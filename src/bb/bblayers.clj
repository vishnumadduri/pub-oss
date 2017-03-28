; Utitlities for parsing the bitbake layers
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.bblayers
  (:use [utils.gen-utils]
        [bb.bb-syntax]
        [bb.config]
        [org.satta.glob])
  (:require [clojure.string :as str]))


;; association of clojure and bitbake keywords
(def ^:private h-clj-bb
  {::machine "MACHINE"
   ::basemachine "BASEMACHINE"
   ::buildname "BUILDNAME"
   ::topdir "TOPDIR"
   ::dl-dir "DL_DIR"
   ::tmpdir "TMPDIR"
   ::workdir "WORKDIR"
   ::deploydir "DEPLOY_DIR"
   ::pv "PV"
   ::pn "PN"
   ::bpn "BPN"
   ::xorg-pn "XORG_PN"
   ::jdk-pn "JDKPN"
   ::inc-pr "INC_PR"
   ::filename-type "FILENAME_TYPE"
   ::python-majmin "PYTHON_MAJMIN"
   ::gnu-mirror "GNU_MIRROR"
   ::debian-mirror "DEBIAN_MIRROR"
   ::sourceforge-mirror "SOURCEFORGE_MIRROR"
   ::xorg-mirror "XORG_MIRROR"
   ::gnome-mirror "GNOME_MIRROR"
   ::icedtea-uri "ICEDTEA_URI"
   ::kernelorg-mirror "KERNELORG_MIRROR"})

;; reverse association of clojure and bitbake keywords
(def ^:private h-bb-clj
  (reduce
   (fn [res elem]
     (assoc res (val elem) (key elem)))
   {}
   h-clj-bb))


(defn- clj->bb
  "maps clojure key to bitbake's key string
   example: (clj->bb ::machine) -> \"MACHINE\""
  [cljkey]
  (h-clj-bb cljkey))


(defn- bb->clj
  "maps bitbake key string to clojure keyword
   example: (bb->clj \"MACHINE\") -> ::machine"
  [bbkey]
  (h-bb-clj bbkey))


(defn- version-str-to-vec
  "transforms  a given  version  into  a vector  of  integer  numbers where  the
  elements correspond to the number groups separated by the dot character."
  [s]
  (map
   (fn [part-str]
     (Integer. (or (re-find #"\d+" part-str) "0")))
   (str/split s #"[.]" )))


(defn- compare-version-strs
  "function compares  version string  xx.yy.zz where xx,  yy, zz  denote version
  numbers. The  numbers must be positive  or zero. There is  not constraint with
  respect to total  amout of groups separate by the  dot character. The function
  returns a  postive value  when a is  higher then  b, 0 if  there are  equal or
  negative when b is higher the a."
  [a b]
  (cond (= a b) 0
        (= a "git") 1
        (= b "git") -1
        :else
        (let [[a b] (map version-str-to-vec [a b])
              [a b] (if (> (count a) (count b))
                      [a (concat b (repeat 0))]
                      [(concat a (repeat 0)) b])
              ver-pairs (partition 2 (interleave a b))
              pair-comp-vec (map #(let [[x y] %] (- x y)) ver-pairs)]
          (or
           (loop [e (first pair-comp-vec) r (rest pair-comp-vec)]
             (if (not= 0 e) e
                 (recur (first r) (rest r))))
           0))))


(comment
  (version-strcmakecmakecmake-to-vec "v1.2.3.4.5")
  (compare-version-strs "1.3.3" "1.3.3.0")
  (compare-version-strs "1.3.4" "1.3.3.0")
  (compare-version-strs "1.3.2" "1.3.3.0")
  (compare-version-strs "1.4.2" "1.3.3.0")
  (compare-version-strs "git" "1.3.3.0")
  (compare-version-strs "1.3.3.0" "git")
  )


(defn- load-bbfile-with-subst
  "load bbfile and subsitute all bitbake expressions ${XYZ}
   refer to update-content-with-bb-vars."
  [filename bb]
  (let [s (slurp filename)
        s (subst-python-multiline-strings s)]
    (update-content-with-bb-vars s bb clj->bb)))


(defn- parse-and-update-bb-vars
  "takes multi-line  string and  retrieves updates  bitbake variable  hash table
  (bb) with all keys defined in this module"
  [bb s]
  (reduce
   (fn [res [clj-key bb-key]]
     (let [bb-val (value-of-string-assignment s bb-key)]
       (if (empty? bb-val) res (assoc res clj-key bb-val))))
   bb
   h-clj-bb))


(comment
  (def testline "test1\ntest2\nMACHINE=\"MDM9640\"\nBASEMACHINE=${MACHINE}\netc.")
  (update-content-with-bb-vars
   testline
   (parse-and-update-bb-vars bb testline)
   clj->bb))


(defn- search-filename-in-layers
  "searches for given filename under the directory patterns
   layers -> { <prio1> {::bbfiles <patterns>} <prio2> ... }
   in case no qualified filename is given (no directory path) or under
   layers -> { <prio1> {::layerdir <patterns>} <prio2> ... }
   when the filename includes a  relative path. Substitutes early search results
   with those eventually found in layers with higher priority. "
  [bb layers filename]
  (let [name-includes-path (path-of-filename filename)
        pattern-list (reduce
                      (fn [res [prio h]]
                        (let [patterns
                              (if name-includes-path
                                [(str (::layerdir h) "/" filename)]
                                (seq (map
                                      #(str (path-of-filename %) "/" filename)
                                      (::bbfiles h))))]
                          (apply conj res patterns)))
                      (list #_res)
                      layers)]
    (loop [l pattern-list]
      (when-let [p (first l)]
        (let [g (glob p)]
          (if (not-empty g)
            (.getAbsolutePath (first g))
            (recur (rest l))))))))


(comment
  (search-filename-in-layers bb layers "mdm-image.bb")
  (search-filename-in-layers bb layers "recipes/images/mdm-image.bb")
  (search-filename-in-layers bb layers "mdm-image2.bb")
  )


(defn- read-bb-recipe-file
  "read bitbake recipe file from file system and substitute include data

   The function  reads the recipe <file>  given as java.io.File object  from the
   file system and  subsitute all 'require', 'inherit'  and 'include' statements
   with  the content  of  the include  file for  simplified  parsing. Since  the
   included files  can refer to different  bitbake layer directories we  have to
   search for these under all layer directories which have to be declared within
   the hash-map  bb under  the key  ::layerdir in  ascending order  according to
   their priority."
  [evt bb classes layers layer file]
  (let [filename (.getAbsolutePath file)
        bb (assoc bb ::workdir "") ;; working directory is treated differently
        content (load-bbfile-with-subst filename bb)
        include-regex #"(?![#\"])(?:[ \t]*)(require|include|inherit)(?:[\W][ \t]*)(.*)"
        parse-recipe-progress-cb (::parse-recipe-progress-cb evt)
        subst-fn (fn [s]
                   (subst-lines-with-matching-regexp
                    s
                    include-regex
                    (fn [match]
                      (let [[inc-files action] (reverse match)
                            inc-files (str/split inc-files #"[\s]+")]
                        (reduce
                         (fn [res inc-file]
                           (let [inc-file2
                                 (if (= action "inherit")
                                   (classes inc-file)
                                   (search-filename-in-layers bb layers inc-file))]
                                        ; (println "BPN: " (::bpn bb) "include -> " inc-file2)
                             (if inc-file2
                               (try
                                 (load-bbfile-with-subst inc-file2 bb)
                                 (catch java.io.FileNotFoundException e
                                   (do
                                        ;(println "*** could not open file " inc-file2 " error!")
                                     (str res inc-file2 "\n")) ))
                               (do
                                 (when (= action "require")
                                   (println (format "*** file %s %ss %s which could not be parsed!"
                                                    filename action inc-file)))
                                 (str res "# include " inc-file " NOT AVAILABLE!\n")))))
                         "" ;; res
                         inc-files)))))
        ;; recursively substitute include files
        content (loop [content (subst-fn content)]
                  (if (string-has-lines-of-regex? content include-regex)
                    (recur (subst-fn content))
                    content))
        bb (parse-and-update-bb-vars bb content)
        bb (assoc bb ::workdir "") ;; working directory is treated differently
        content (update-content-with-bb-vars content bb clj->bb)]
    (when parse-recipe-progress-cb (parse-recipe-progress-cb file))
    content))


(defn- get-bb-layers
  "extracts enabled bitbake layer directory names out of configuration
   file 'conf/bblayers.conf' in give build directory"
  [bb-build-dir-name]
  (let [bb-layers-conf (slurp (str bb-build-dir-name "/conf/bblayers.conf"))]
    (split-values-of-string-assignment bb-layers-conf "BBLAYERS")))


(defn- load-bb-layer-classes
  "load all class files of a layer and return a hash map with the
   class names as keys and the file content as values"
  [bb layer-dir-name]
  (let [class-file-pattern (str layer-dir-name "/classes/*.bbclass")
        class-files (glob class-file-pattern)]
    (reduce
     (fn [res class-file]
       (let [class-id (->> class-file str (re-matches #"(.*)[/](.*).bbclass") last)]
         (assoc res class-id (load-bbfile-with-subst class-file bb))))
     {}
     class-files)))


(defn- get-bb-layer-conf
  "extracts bitbake layer configuration data from bitbake layer root directory
   which in example can be extracted with function get-bb-layers.

   example:
   (get-bb-layer-conf .../apps_proc/oe-core/meta) ->
      {::priority 5 ::bblayers ['oe-core/meta/recipes-*/*/*.bb']}"
  [bb layer-dir-name]
  (try
    (let [layer-conf (load-bbfile-with-subst (str layer-dir-name "/conf/layer.conf") bb)]
      (if layer-conf
        (let [layer-conf (str/replace layer-conf #"\$[{]?LAYERDIR[}]?" layer-dir-name)
              bbclasses (load-bb-layer-classes bb layer-dir-name)
              bbfiles (split-values-of-string-assignment layer-conf "BBFILES")
              bbfile-collection (last (value-of-string-assignment layer-conf "BBFILE_COLLECTIONS"))
              bbfile-priority (Integer.
                               (first (value-of-string-assignment
                                       layer-conf
                                       (str "BBFILE_PRIORITY_" bbfile-collection))))]
          {::priority bbfile-priority ::bbfiles bbfiles ::layerdir layer-dir-name
           ::classes bbclasses})))
    (catch java.io.FileNotFoundException e {::priority 0 ::bbfiles {}})))


(defn- read-recipes-for-pattern
  "read all bitbake recipes which matches one file pattern
   and return hash map where each key corresponds to the recipe identifier
   and each value corresponds to a sorted hash map in descending order
   of version number and recipe file content (bb or bbappend files)

   example: (read-recipes-for-pattern 'oe-core/meta/recipes-*/*/*.bb') ->

     { 'libnml' {'4.1' {::bb '...' ::bbappend '...'}, '3.1' {::bb '...'}}]
       'cmake'  { '3.8.1' { ... }, '3.8.0' { ... } }  }"
  [evt bb classes layers layer bbfile-pattern]
  (let [bb-files (glob bbfile-pattern)
        v (reduce
           (fn [res bb-file]
             (let [full-qualified-name (.getAbsoluteFile bb-file)
                   file-name (.getName bb-file)
                   [_ pn _ pv ext] (re-matches #"([A-Za-z0-9-+-.]*)(_(.*))?[.](bb|bbappend)" file-name)
                   pv (or pv "1.0")
                   ext (keyword "bb.bblayers" ext)
                   bpn (str/replace pn #"(nativesdk-|-cross|-native|lib32-|lib64-)" "")
                   bb (assoc bb ::pv pv ::pn pn ::bpn bpn)]
               (if (contains? *skipped-packages* bpn)
                 res
                 (update-in res [pn]
                            (fn [versions]
                              ;; (if versions (println "versions -> " versions ", type ->" (type versions)))
                              (let [versions (or versions
                                                 (sorted-map-by (fn [a b] (compare-version-strs b a))))]
                                (assoc versions pv
                                       {ext
                                        (read-bb-recipe-file evt bb classes layers layer full-qualified-name)
                                        ::pn pn ::bpn bpn ::pv pv})))))))
           {#_res}
           bb-files)]
    v))


(defn- read-recipes-for-patterns
  "same as read-recipes-for-pattern but takes vector of file patterns as input

   The function is required since one bitbake layer can declare several
   file patterns e.g. for extension like '*.bb' and '*.bbappend'

   example (read-recipes [.../oe-core/.../recipes/*/*.bb .../oe-core/.../recipes/*/*.bbappend])
     -> delivers merged resutl of  read-recipes-for-pattern."
  [evt bb classes layers layer]
  (let [bb-files (::bbfiles layer)
        layer-recipes (map (partial read-recipes-for-pattern evt bb classes layers layer) bb-files)]
    (reduce
     (fn [res elem]
       (merge-with (partial merge-with merge) res elem))
     layer-recipes)))


(defn- read-layer-configuration-sorted-by-priority
  "reads bitbake layer configuration in ascending order

   The   function   takes  the   open   embedded   build  directory   as   input
   argument,  reads  and  parses  the  bitbake  layer  configuration  data  from
   <build-dir>/conf/bblayers.conf  and returns  a sorted  hash map  in ascending
   order  where  the keys  are  the  layer  priorities  and values  provide  the
   corresponding layer definitions in form of another clojure hash map.

   example: (read-layer-configuration-sorted-by-priority '.../apps_proc/oe-core/build') ->
              {1 {::priority 1, ::bbfiles [ '.../*.bb', ... ],
               5 {::priority 5, ::bbfiels [ ... ], ... }"
  [bb build-dir]
  (reduce
   (fn [result layer-dir]
     (let [layer-conf (get-bb-layer-conf bb layer-dir)
           fract-hash (fn [s] (/ (reduce #(+ %1 (.hashCode %2)) 0.0 s) (* 255 (count s))))
           priority (::priority layer-conf)
           ;; trick to extend layers with same priority by fractional number based on
           ;; some hash value of the directory to ensure unique hash keys
           priority (if (result priority) (+ priority (fract-hash layer-dir)) priority)]
       (assoc result priority layer-conf)))
   (sorted-map-by <)
   (get-bb-layers build-dir)))


(defn- recipe-progress-ind
  "progress indicator callback"
  [progress-ind-atom recipe]
  (println (format "%4d/%4d, parsing %s ..."
                   (::parsed-recipes @progress-ind-atom)
                   (::tot-recipes @progress-ind-atom)
                   (.getName recipe)))
  (swap! progress-ind-atom #(update-in % [::parsed-recipes] inc)))


;;;
;;; ---------- Public API ----------
;;;

(defn get-bb-local-conf
  "read bitbake's local configuration data out of file 'conf/local.conf"
  [bb-build-dir-name]
  (let [bb-local-conf (slurp (str bb-build-dir-name "/conf/local.conf"))
        [bb-local-conf machine] (subst-val-pair bb-local-conf (clj->bb ::machine))
        bb-local-conf  (subst-bb-var bb-local-conf (clj->bb ::topdir) bb-build-dir-name)
        [bb-local-conf dldir] (subst-val-pair bb-local-conf (clj->bb ::dl-dir))
        [bb-local-conf tmpdir] (subst-val-pair bb-local-conf (clj->bb ::tmpdir))
        [bb-local-conf buildname] (subst-val-pair bb-local-conf (clj->bb ::buildname))]
    (merge *default-config*
           {::buildname buildname
            ::machine machine
            ::basemachine machine
            ::dl-dir dldir
            ::tmpdir tmpdir
            ::topdir bb-build-dir-name})))


(defn read-layers
  "reads recipes from all bitbake layers

   The   function   takes  the   open   embedded   build  directory   as   input
   argument,  reads  and  parses  the  bitbake  layer  configuration  data  from
   <build-dir>/conf/bblayers.conf  and returns  a hash  map of  key value  pairs
   where the key is the recipe identifier  (name) and value is a sorted hash map
   in descending order of versions numbers and recipe file content.

   By convention  bitbake selects recipe files  from the layer with  the highest
   priority and highest version number. There are some tricky situation when the
   bb and bbappend files are distributed  over several layers. We handle that by
   relacing overlapping recipe  definitions of lower layers with  those found in
   higher layers.  In case the main  bitbake recipe definition (bb)  file is not
   defined in  one layer, instead  of overwriting we  merge the bb  and bbappend
   files toghether.

   example: (read-layers '.../apps_proc/oe-core/build') ->
              { 'readline' { '6.2' {::bb ... ::bbappend ... }, '5.2' {::bb ...} },
                 ... }"
  [bb build-dir]
  (let [layers (read-layer-configuration-sorted-by-priority bb build-dir)
        classes (reduce
                 (fn [res [prio layer]]
                   (def l layer)
                   (merge res (::classes layer)))
                 {#_res}
                 layers)
        all-layer-dirs (mapcat #(::bbfiles (val %)) layers)
        tot-recipes (count (mapcat glob all-layer-dirs))
        progress-ind-atom (atom {::tot-recipes tot-recipes ::parsed-recipes 0})
        evt {::parse-recipe-progress-cb (partial recipe-progress-ind progress-ind-atom)}]
    (reduce
     (fn [result [prio layer]]
       (merge-with
        (fn [a b]
          (let [ac (-> a vals first)
                bc (-> b vals first)]
            (if (::bb bc)
              b                         ;; main bitbake file of higher layer defined -> replace
              (merge-with merge a b)))) ;; main bitbake file not defined -> merge
        result (read-recipes-for-patterns evt bb classes layers layer)))
     {#_result}
     layers)))


(defn filter-default-recipes
  "takes the result from function read-layers and extract default recipes

   Most of bitake's package dependencies refer to the most recent version of
   required packages and do not explicitly specify a distinctive pacakge version
   This parsing function takes advantage out of this fact, cuts of
   all recipes version after the highest version number and delivers a hash map
   of package identifier and flat package file content.

   example: (filter-default-recipes (read-layers '.../apps_proc/oe-core/build')) ->
              { 'readline' {  {::bb ::pv '6.2 } },
                 ... }"
  ([read-layers]
   (filter-default-recipes read-layers {}))
  ([read-layers preferred-versions]
   (reduce
    (fn [res [recipe-id versions]]
      (let [req-preferred (preferred-versions recipe-id)
            found-preferred (when req-preferred (versions req-preferred))
            content (or found-preferred (val (first versions)))
            bb (::bb content)
            bbappend (::bbappend content)
            joint (str bb (if bbappend (str "\n" bbappend)))
            content (-> content (dissoc ::bbappend) (assoc ::bb joint))]
        (assoc res recipe-id content)
        ))
    {#_res}
    read-layers)))


(defn read-default-recipes
  "reads flat bitbake recipes file content from all layers

   The function  takes the build directory  '.../apps_proc/oe-core/build' and an
   optional bitbake configuration hash-map and  delivers a hash map with bitbake
   package identifiers as  keys and associated package data data  as values. The
   packages data is provided as a hash map with the keys ::bb which provides the
   flattened file content and the ::pv  which provides the version number of the
   package.

   The   optional  hash-map   argument  bb   allows  the   extension  respectily
   redefinition of incorporated bitbake variables."
  ([build-dir] (read-default-recipes build-dir {}))
  ([build-dir bb]
   (let [bb (merge (get-bb-local-conf build-dir) bb)]
     (filter-default-recipes
      (read-layers bb build-dir)))))


(comment

  (def build-dir "/mnt/ssd1/ol/ltenad2_work1/apps_proc/oe-core/build")
  (def build-dir "/Users/ol/Entwicklung/Peiker/opennad/apps_proc/oe-core/build")
  (def build-dir "/Volumes/dev/opennad-mdm9640/apps_proc/oe-core/build")

  (def bb (get-bb-local-conf build-dir))
  (def layer-conf (read-layer-configuration-sorted-by-priority bb build-dir))

  (def l (read-layers bb build-dir))

  (println (l "linphone"))

  (l "icu")
  (l "udev")
  (l "readline")
  ((l "readline") "6.2")
  ((l "readline") "5.2")


  (def ld (filter-default-recipes l {"readline" "5.2"}))
  (def ld (filter-default-recipes l))
  (def ld (filter-default-recipes l {"readline" "7.3"})) ;; non-existing
  (::pv (ld "readline"))

  (def ld (read-default-recipes build-dir))

  (ld "icu")
  (println (::bb (ld "icu")))
  (ld "udev")

  (map #(do (println %) (println "________________________")) ld)
  )
