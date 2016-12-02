; Utitlities for parsing the bitbake recipes
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.recipe-parser
  (:use [utils.gen-utils]
        [utils.security]
        [bb.bb-syntax]
        [bb.deploy-sources]
        [bb.config :as conf]
        [bb.bblayers :as layers]
        [org.satta.glob]
        [pub-oss.file-handling]
        [pub-oss.spdx]
        [utils.xml-utils]
        [bb.gen-html-readme]
        [clojure.set :only [map-invert]])
  (:require
   [clojure.string :as str]))


;; association of clojure and bitbake keywords
(def ^:private h-clj-bb
  {::depends "DEPENDS"
   ::rdepends "RDEPENDS"
   ::image-install "IMAGE_INSTALL"
   ::description "DESCRIPTION"
   ::homepage "HOMEPAGE"
   ::section "SECTION"
   ::license "LICENSE"
   ::provides "PROVIDES"
   ::src-uri "SRC_URI"
   ::pr "PR"
   ::s "S"})

;; reverse association of clojure and bitbake keywords
(def ^:private h-bb-clj
  (reduce
   (fn [res elem]
     (assoc res (val elem) (key elem)))
   {}
   h-clj-bb))


(defn- clj->bb
  "maps clojure key to bitbake's key string
   example: (clj->bb ::depends) -> \"DEPENDS\""
  [cljkey]
  (h-clj-bb cljkey))


(defn- bb->clj
  "maps bitbake key string to clojure keyword
   example: (bb->clj \"RDEPENDS\") -> ::rdepends"
  [bbkey]
  (h-bb-clj bbkey))


(defn- remove-virtual-prefixes
 "removes the prefix virtual/ which we do not
  need for dealing with dependencies in the given application.
  It is already dealed with by the preferred providers."
 [v]
 (mapt #(str/replace % #"virtual\/" "") v))


(defn- check-and-correct-source-dir
  "according  to  imperfect  parsing  (e.g.   no  python)  of  bitbake's  recipe
  definitions  we  cannot fully  rely  that  identified source  directories  are
  correct. So we check all of them whether they exist and if not we try to guess
  them heuristicly. "
  [build-dir bb bpn pv prel s] ;; -> /trunk
  (let [base-dirs [(::layers/workdir bb) (::layers/workdir-sys bb)]
        base-dirs (map #(str build-dir %) base-dirs)
        s (or s (str "/" bpn "-" pv))
        ;; bloody hack to cope with QTI's localgit class which rewrites variable S!
        s (if (substring? s (::layers/workdir bb)) ;; we recognize this when workdir has been already defined
            (str "/" bpn) ;; in this case just use base package name
            s) ;; otherwise use what is defined in recipe or by default declare source directory
        checked-dirs (map (fn [base-dir] (str base-dir "/" bpn "/" pv "-" prel s)) base-dirs)
        glb-dirs (map (fn [base-dir] (str base-dir "/" bpn "/" s "*")) base-dirs)
        glb-dirs (concat glb-dirs (map (fn [base-dir] (str base-dir "/" bpn )) base-dirs))
        glb-dirs (concat glb-dirs (map (fn [base-dir] (str base-dir "/" bpn "*/*" s "*")) base-dirs))]
    (if-let [found (some (fn [dir] (when (is-path-directory? dir) dir)) checked-dirs)]
      found
      (some (fn [g]
              (let [g (glob g)]
                (when-not (empty? g) (.getAbsolutePath (first g)))))
            glb-dirs))))


(defn- parse-bb-recipe
  "parses bitbake recipe content data

   function takes the bitbake recipe hash map as provided
   by read-default-recipes defined in ns bblayers."
  [build-dir bb recipe-hash]
  (try
    (let [vec2str (fn [v] (when v (.trim (reduce #(str %1 " " %2) "" v))))
          bbfile (::layers/bb recipe-hash)
          bbappend (::layers/bbappend recipe-hash)
          bbfile (str bbfile (if bbappend (str "\n" bbappend)))
          bpn (::layers/bpn recipe-hash)
          pv (::layers/pv recipe-hash)
          h (reduce
             (fn [res clj-key]
               (let [v (split-values-of-string-assignment bbfile (clj->bb clj-key))]
                 (assoc res clj-key v)))
             recipe-hash
             (keys h-clj-bb))
          h (update-in h [::pr] #(if (empty? %) "r0" (first %)))
          [s] (::s h)
          s (check-and-correct-source-dir build-dir bb bpn pv (::pr h) s)]
      ;; we do not need extract the sources for all packages
      ;; therefore when we do not find the source folder yet, it is not
      ;; necessarily an error.
      (when-not s (comment println-err (format "\tsource not found for package: %s!" bpn)))
      (-> h
          (update-in [::description] vec2str)
          (update-in [::license] vec2str)
          (update-in [::depends] remove-virtual-prefixes)
          (update-in [::rdepends] remove-virtual-prefixes)
          (assoc-in [::s] s)))
    (catch Exception e
      (def build-dir build-dir)
      (def bb bb)
      (def recipe-hash recipe-hash)
      (throw (Exception.
              (format "exception occured while parsing recipe: %s->\n\n%s\n"
                      (str (::layers/bpn recipe-hash) "-" (::layers/pv recipe-hash))
                      (dissoc recipe-hash ::layers/bb ::layers/bbappend)))))))


(defn parse-bb-recipes
  "reads and parses all bitbake recipes from given build directory

   The  function takes  the  build  directory '.../apps_proc/oe-core/build'  and
   delivers a hash map with various section described in the following:
     ::bb      -> provides the local configuration, refer to get-bb-local-conf
     ::all     -> provides package data for all versions:
                   { 'readline' { '6.2' {::bb ... ::bbappend ... },
                                  '5.2' {::bb ...} }, ... }
     ::default -> provides package data for most recent (default) version:
                   { 'readline' {  {::bb ::pv '6.2 } }, ... }"
  [build-dir]
  (let [bb (get-bb-local-conf build-dir)
        all (read-layers bb build-dir)
        all (mapt
             (fn [[h versions]]
               (let [versions2
                     (mapt (fn [[ver recipe]]
                             [ver (parse-bb-recipe build-dir bb recipe)]) versions)]
                 [h versions2]))
             all)
        default (filter-default-recipes all *preferred-versions*)
        preferred-providers (mapt (fn [[k v]] {k (default v)}) *preferred-providers*)
        default-and-preferred-providers (merge default preferred-providers)]
    {::bb bb ::all all ::default default-and-preferred-providers}))


(defn- bb-recipe-dependencies-walker
  "helper function  which recursively iterates  the dependency tree for  a given
  recipe refer to function bb-recipe-dependencies for usage description.

  be aware  that some dependency  tags might  reference the same  package under
  different tags (default and versioned) causing these recipies to appear twice
  within the result. The function bb-recipe-dependencies takes care of that."
  [dependencies recipies recipe]
  (let [default (::default recipies)
        all (::all recipies)
        dep (map recipe [::image-install ::depends ::rdepends])
        dep (reduce (fn [res elem] (apply conj res elem)) #{} dep)]
    (reduce
     (fn [res d]
       (let [[_ dpn _ dpv] (re-matches #"([A-Za-z0-9+.]*)(-(.*))?" d)]
         (if (substring? d "-native")
           (do (println "\tinfo: native dependeny " d " ignored!") res)
           (if-let [drecipe (or (default d) (get-in all [dpn dpv]))]
             (if (or (contains? res d))
               res
               (bb-recipe-dependencies-walker (assoc res d drecipe) recipies drecipe))
             (do (println "\twarning: recipe " (::layers/pn recipe) " dependency " d " missing!") res)))))
     dependencies ;; init
     dep)))


(defn bb-recipe-dependencies
  "returns dependencies for given bitbake recipe

   The function  takes the  hash map  of all  parsed recipies  as first  and the
   recipe for which the dependencies shall be determined and delivers the result
   as set of recipe keys."
  [recipies recipe]
  (let [deps (bb-recipe-dependencies-walker {} recipies recipe)]
    (map-invert (map-invert deps))))


(defn filter-license-with-fn
  "filter license tags in argument hash  with recipe key, content as returned by
  the function bb-recipe-dependencies according to provided filter function"
  [recipies-hash filter-fn]
  (reduce
   (fn [res [k v]]
     (if (filter-fn (::license v))
       (assoc res k v)
       res))
   {}
   recipies-hash))


(defn filter-open-source-licenses
  "filter license tags in argument hash  with recipe key, content as returned by
  the function bb-recipe-dependencies according to open source licenses"
  [recipies-hash]
  (let [regex (re-pattern (str "(?i)\\s(" *opensource-licenses* ")\\s"))]
    (filter-license-with-fn recipies-hash #(re-find regex (str " " % " ")))))


(defn- pkg-sha1
  "read content of deployed package file return content sha1 digest"
  [filename]
  (let [content (read-binary-file filename)]
    (sha1-hex content)))


(defn deploy-recipe
  "depoys source code for one recipe"
  [bb build-dir recipe-hash]
  (if-let [s (::s recipe-hash)]
    (let [src-dir s
          deploy-dir (str build-dir (::conf/deploy-os-dir bb))
          deploy-package (str (::layers/bpn recipe-hash) "-" (::layers/pv recipe-hash))
          deployed-pkg-file (deploy-source src-dir deploy-dir deploy-package)]
      (-> recipe-hash
          (assoc-in [::deployed-file] deployed-pkg-file)
          (assoc-in [::deployed-file-sha1] (pkg-sha1 (str deploy-dir "/" deployed-pkg-file)))))
    (println-err "\tno source found for package: " (::layers/bpn recipe-hash))))


(defn deploy-recipes
  "deploys source code for all recipe hashes"
  [bb build-dir recipies-hashes]
  (mapt
   (fn [[k v]]
     (println (format "deploying %s ..." k))
     {k (merge v (deploy-recipe bb build-dir v))})
   recipies-hashes))


(defn deploy-kernel
  "deploys kernel source code"
  [bb build-dir kernel-recipe-hash]
  (println (format "deploying the Linux Kernel ..."))
  (let [src-dir (str build-dir (::conf/git-src *linux-kernel*))
        deploy-dir (str build-dir (::conf/deploy-os-dir *default-config*))
        deployed-pkg-file (deploy-kernel-source src-dir deploy-dir
                                                (str (::layers/bpn kernel-recipe-hash) "-"
                                                     (::layers/pv kernel-recipe-hash)))]
    (-> kernel-recipe-hash
        (assoc-in [::deployed-file] deployed-pkg-file)
        (assoc-in [::deployed-file-sha1] (pkg-sha1 (str deploy-dir "/" deployed-pkg-file))))))


(defn- regularize-license-string
  "bitbake strips out  blank and ampersand characters out of  license string for
  compliance to filename conventions. We do the same"
  [s]
  (str/replace s #"[ &]" ""))


(defn create-spdxfiles
  "creates and deploys spdx container files for the given recipe hashes"
  [bb build-dir recipies-hashes]
  (dorun
   (map (fn [[k v]]
          (let [dir (str build-dir (::conf/deploy-spdx-dir *default-config*))
                pn  (::layers/pn v)
                license (regularize-license-string (::license v))]
            (println (format "creating SPDX for %s, license %s ..." pn license))
            (write-xml (str dir "/" pn ".spdx")
                       (create-spdx
                        :package-name (::layers/pn v)
                        :version-info (::layers/pv v)
                        :package-archive-file-name (::deployed-file v)
                        :package-checksum-value (::deployed-file-sha1 v)
                        :licence-concluded license
                        :download-location (first (::src-uri v))
                        :summary (::description v)))))
        recipies-hashes)))


(defn filter-prohibited-licenses
  "returns recipe hashes under critical open source licenses"
  [recipies-hash]
  (filter-license-with-fn
   recipies-hash
   (fn [l]
     (contains? conf/*prohibited-spdx-licenses* (regularize-license-string l)))))


(defn reverse-deps-of-one-recipe
  "finds reverse dependencies for given hash of recipies
   and given package id"
  [recipies id]
  (set
   (keys
    (filter
     (fn [[k v]]
       (let [deps (set (::depends v))]
         (contains? deps id)))
     (dissoc recipies id)))))


(defn print-recursive-reverse-deps-for-one-package-id
  "print recursive dependencies of one package"
  [recipies id]
  (print "<-" id)
  (let [deps (reverse-deps-of-one-recipe recipies id)]
    (when-not (empty? deps)
      (dorun
       (for [dep deps]
         (do (print-recursive-reverse-deps-for-one-package-id recipies dep))))
      (println))))


(defn print-recursive-reverse-deps-for-packages
  "print recursive dependencies for all given package id's"
  [recipies ids]
  (dorun
   (map #(print-recursive-reverse-deps-for-one-package-id recipies %) ids)))


(defn gen-readme
  "creates html content document which includes a nicly readible
   table with an overview of all open source pacakges"
  [full-qual-readme-filename build-version image-name recipes]
  (let [html-content
        (str
         (html-doc-header build-version image-name)
         (html-pkg-table-doc-header)
         (reduce
          (fn [res [k v]]
            (let [package (::layers/pn v)
                  version (::layers/pv v)
                  license (::license v)
                  description (::description v)]
              (str res
                   (html-pkg-table-entry-line package version license description))))
          ""
          recipes)
         (html-pkg-table-doc-footer)
         (html-doc-footer))]
    (spit full-qual-readme-filename html-content)))


(defn execute-bb-open-source-extraction
  "main function of bitbake driven extraction of open source packages

   Invoke with build directory pointing to 'apps_proc/oe-core/build'
   and body name of the linux image recibe file which has been used
   to generate the file system image e.g. '9640-cdp-ltenad'. The function
   deploys source code and spdx container files under the subdirectory
   'deploy' under the given build directory."
  [build-dir image-recipe-name]
  (let [_ (println "\nparse bitbake layers ...")
        all-recipies (parse-bb-recipes build-dir)
        bb (::bb all-recipies)
        default-recipies (::default all-recipies)
        image-recipes (default-recipies image-recipe-name)
        d-image-recipies (bb-recipe-dependencies all-recipies image-recipes)
        os-recipes (filter-open-source-licenses d-image-recipies)
        _ (clean-and-create-deployment-directories build-dir)
        _ (println "\ndeploy source tarballs ...")

        kernel-recipe-key (::conf/bb-recipe *linux-kernel*)
        kernel-recipies-hash (default-recipies kernel-recipe-key)
        kernel-recipies-hash (deploy-kernel bb build-dir kernel-recipies-hash)

        os-recipes (dissoc os-recipes kernel-recipe-key)
        deployed-os-recipes (deploy-recipes bb build-dir os-recipes)
        deployed-os-recipes (assoc deployed-os-recipes kernel-recipe-key kernel-recipies-hash)

        _ (println "\ncreate spdx containers ...")
        _ (create-spdxfiles bb build-dir deployed-os-recipes)
        prohibited (filter-prohibited-licenses deployed-os-recipes)
        _ (gen-readme
           (str build-dir (::conf/deploy-os-dir *default-config*) "/../content.html" )
           (::layers/buildname bb)
           image-recipe-name
           (sort deployed-os-recipes))]
    (when-not (empty? prohibited)
      (println "\nThe following dependencies to prohibited licenses are currently used:")
      (print-recursive-reverse-deps-for-packages d-image-recipies (keys prohibited)))))




;;; experimentation enviromnet for step by step execution respectively
;;; evaluation of Clojure data structures
;;;
;;; use the following approach
;;;
;;;  1. make sure you have a complete Open Embedded build of the Linux stuff (apps_proc)
;;;  2. set build-dir to its location, refer to examples
;;;  3. start recipe parsing by evaluation function parse-bb-recipes bound to symbol z
;;;  4. extract bitbake environment and bind to to symbol bb
;;;  5. extract default recipies which provide only the highest respectively prevered
;;;     package number and bind it to z2
;;;  6. given the hash table in z2 you can now play around in the REPL
;;;  7. extract and bind the app_proc image recipe to the symbol ltenad-dev or ltenad-prod
;;;  8. evaluate all image dependencies by binding to d-ltenad-prev or d-ltenad-prod
;;;  9. filter packages with open source licenses and bind to os-recipies
;;; 10. evaluate clean-and-create-deployment-directories with definition build-dir
;;; 11. start source code deployment by the evaluation of the function deployed-os-recipies
;;; 12. start creation of SPDX XML container files by evauating function create-spdxfiles
;;; 13. filter for packages in the image under prohibited open source license (filter-prohibited-licenses)
;;; 14. pring out the reverse dependencies for these packages by invoking the function
;;;     print-recursive-reverse-deps-for-packages
;;;
(comment

  (def build-dir "/mnt/ssd1/ol/ltenad2_work1/apps_proc/oe-core/build")
  (def build-dir "/Users/ol/Entwicklung/Peiker/opennad/apps_proc/oe-core/build")
  (def build-dir "/Volumes/dev/opennad-mdm9640/apps_proc/oe-core/build")

  (def z (parse-bb-recipes build-dir))
  (def bb (::bb z))


  (keys z)

  (((::default z) "readline") ::layers/pv)
  (keys ((::all z) "readline"))
  (((::default z) "prelink") ::layers/pv)
  (keys ((::all z) "prelink"))
  (keys ((::all z) "osip"))
  (keys ((::all z) "osip4"))
  (((::default z) "osip") ::layers/pv)
  (((::default z) "osip4") ::layers/pv)

  (def z2 (::default z))

  (println (z2 "perl"))
  (println (z2 "alsaucm"))
  (keys (z2 "alsaucm"))
  (::s (z2 "alsaucm"))
  (::license (z2 "alsaucm"))
  (::license (z2 "elfutils"))
  (::depends (z2 "alsaucm"))
  (::rdepends (z2 "alsaucm"))
  (def alsaucm (z2 "alsaucm"))
  (keys alsaucm)
  (::layers/pv alsaucm)
  (::layers/bpn alsaucm)
  (::layers/pn alsaucm)
  (::s alsaucm)
  (::pr alsaucm)

  (def opkg (z2 "opkg"))
  (def openssh (z2 "openssh"))
  (keys openssh)
  (def base-files (z2 "base-files"))
  (def system-conf (z2 "system-conf"))
  (def lk (z2 "opkg"))
  (println (::layers/bb lk))
  (def s (::s opkg))

  (def alsa-intf (z2 "alsa-intf"))
  (keys alsa-intf)
  (::s alsa-intf)
  (::layers/pv alsa-intf)


  (def ltenad-dev (z2 "9640-cdp-ltenad-dev"))
  (def ltenad-prod (z2 "9640-cdp-ltenad"))

  (println  (z2 "9640-cdp-ltenad-dev"))
  (::image-install  (z2 "9640-cdp-ltenad-dev"))

  (::src-uri (z2 "perl"))

  (bb-recipe-dependencies #{} z alsaucm)
  (bb-recipe-dependencies z alsaucm)
  (def d-ltenad-dev (bb-recipe-dependencies z ltenad-dev))
  (def d-ltenad-prod (bb-recipe-dependencies z ltenad-prod))


  (contains? d-ltenad-dev "alsaucm")
  (contains? d-ltenad-dev "ssc")
  (contains? d-ltenad-dev "gettext")
  (contains? d-ltenad-dev "osip")
  (first d-ltenad-dev)

  (map (fn [[k v]]
         (println (format "PN: %s, PV: %s LICENSE: %s\nDESCRIPTION: %s\nURI: %s\nS: %s\n__________________"
                          (::layers/pn v)
                          (::layers/pv v)
                          (::license v)
                          (::description v)
                          (::src-uri v)
                          (::s v))))
       (sort d-ltenad-dev))


  (def os-recipies (filter-open-source-licenses d-ltenad-prod))
  (map (fn [[k v]] (println k (::license v))) os-recipies)

  (os-recipies "linux-quic")
  (def os-recipies (dissoc os-recipies "linux-quic"))

  (def kernel-recipe-key (::conf/bb-recipe *linux-kernel*))
  (def kernel-recipies-hash (z2 kernel-recipe-key))
  (def kernel-recipies-hash (deploy-kernel bb build-dir kernel-recipies-hash))


  (clean-and-create-deployment-directories build-dir)

  (deploy-recipe bb build-dir (os-recipies "ssc"))
  (deploy-recipe bb build-dir (os-recipies "readline"))
  (deploy-recipe bb build-dir (os-recipies "libxml2"))
  (deploy-recipe bb build-dir (os-recipies "icu"))
  (deploy-recipe bb build-dir (os-recipies "perl"))

  (deploy-recipe bb build-dir base-files)
  (deploy-recipe bb build-dir openssh)
  (deploy-recipe bb build-dir system-conf)

  (def deployed-os-recipies (deploy-recipes bb build-dir os-recipies))
  (def deployed-os-recipies (assoc deployed-os-recipies kernel-recipe-key kernel-recipies-hash))

  (::deployed-file (deployed-os-recipies "opkg"))
  (::deployed-file-sha1 (deployed-os-recipies "opkg"))
  (::license (deployed-os-recipies "opkg"))

  (::s (os-recipies "ethernet-neutrino"))

  (create-spdxfiles bb build-dir deployed-os-recipies)

  (def prohibited (filter-prohibited-licenses deployed-os-recipies))
  (keys prohibited)

  (reverse-deps-of-one-recipe z2 "gdbm")
  (reverse-deps-of-one-recipe d-ltenad-dev "gdbm")
  (reverse-deps-of-one-recipe d-ltenad-prod "gdbm")
  (reverse-deps-of-one-recipe d-ltenad-prod "perl")
  (reverse-deps-of-one-recipe d-ltenad-dev "coreutils")

  (print-recursive-reverse-deps-for-one-package-id d-ltenad-prod "gdbm")
  (print-recursive-reverse-deps-for-one-package-id d-ltenad-prod "coreutils")

  (print-recursive-reverse-deps-for-packages d-ltenad-prod (keys prohibited))

  (gen-readme
   (str build-dir (::conf/deploy-os-dir *default-config*) "/../content.html" )
   (::layers/buildname bb)
   "9640-cdp-ltenad"
   (sort os-recipies))


  (execute-bb-open-source-extraction build-dir "9640-cdp-ltenad")
  )
