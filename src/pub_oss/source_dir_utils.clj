; Utitlities for parsing the directory created by
; bitbake's sourcepkg class files
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.source-dir-utils
  (:use [utils.gen-utils]))


(defn- oss-license-predicate
  "returns true when given File Object points to Open Source Directory."
  [dir]
  (let [full-dir-name (str dir)
        content-dir-name (last (first (re-seq #"^(.+)/([^/]+)$" full-dir-name)))
        oss-licenses #{"GPL" "BSD" "MIT" "APACHE" "OPENSSL" "AL2"}]
    (some (partial substring? (.toUpperCase content-dir-name)) #{"GPL" "MIT"})))


(defn- get-pred-dir-list
  "returns all subdirectories in given dir which predicate"
  [dir pred]
  (filter #(and (.isDirectory %) (pred %)) (.listFiles dir)))


(defn- get-package-data-within
  "returns all relevant data for a given os packages subdirectory
   as hash map."
  [dir]
  (let [directories (filter #(.isDirectory %) (.listFiles dir))]
    (map #(let [full-dir-name (str %)
                content-dir-name (last (first (re-seq #"^(.+)/([^/]+)$" full-dir-name)))
                package-name (first (re-seq #"^\w[A-Za-z-_0-9]+[A-Za-z][^-_.]*" content-dir-name))
                version (first (first (re-seq #"\d+(\.\d+)+" content-dir-name)))]
            {:source-dir %
             :package-name package-name
             :licence-concluded (last (first (re-seq #"^(.+)/([^/]+)$" (str dir))))
             :version-info version
             :creation-date (str (new java.util.Date))
             })
         directories)))


(comment "regular expression examples for extracting package name"
         (re-seq #"^\w[A-Za-z-_0-9]+[A-Za-z][^-_.]*" "abc-1.0.1.tar.gz")
         (re-seq #"^\w[A-Za-z-_0-9]+[A-Za-z][^-_.]*" "abc-def-1.0.1.tar.gz")
         (re-seq #"^\w[A-Za-z-_0-9]+[A-Za-z][^-_.]*" "abc2-def22-1.0.1.tar.gz"))



(defn get-all-os-packages
  "returns a hash map of all open source packages including most relevant information."
  [oe-main-directory-name  oss-main-directory-name]
  (let [oss-dirs (get-pred-dir-list (java.io.File. oss-main-directory-name) oss-license-predicate)]
    (mapcat get-package-data-within oss-dirs)))






(comment

  (def r (get-all-os-packages "oe-main-dir" "../../apps_proc/oe-core/build/tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi"))

  (println r)


  (def p (get-package-data-within
          (java.io.File. "../../apps_proc/oe-core/build/tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi/GPL-2.0-with-GCC-exception")))

  (doseq [e p] (println (str e)))


  (def oss-dirs (get-directories-within
                 (java.io.File. "../../apps_proc/oe-core/build/tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi")
                 oss-license-predicate
                 ))

  (doseq [d oss-dirs] (println (str d)))

  )
