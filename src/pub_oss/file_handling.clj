; file handling helper functions
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.file-handling
  (:use [utils.security]
        [utils.gen-utils])
  (:import java.security.MessageDigest))

; (sha1-hex "abc")

(defn read-all-files-within
  "read content of all (source archive) files within
   given directory and return hash map with the content
   sha1 digest and filename."
  [dir]
  (let [files (filter #(.isFile %) (.listFiles dir))]
    (map #(let [content (slurp %)]
            {:filename (.getCanonicalPath %)
             :content content
             :sha1 (sha1-hex content)}) files)))


(defn write-all-files-within
  "inverse to read-all-files-within, target-dir specifies destintion
   where to copy the source archive files to."
  [container target-dir]
  (map #(let [bodyname (last (first (re-seq #"^(.+)/([^/]+)$" (:filename %))))
              content (:content %)]
          (with-open [out-writer (clojure.java.io/writer (str target-dir "/" bodyname))]
            (.write out-writer content))) container))


(defn extract-source-dir-meta-data
  "open-embedded source pkg class generates for each oss packages
   a source archive file and one archive with all packages.
   Unfortunately the naming conventions are a little bit unprecise.
   This function will apply several checks and assign source
   and package archives to the keys used within spdx utilities."
  [container]
  (when (and (> 3 (count container)) (<= 0 (count container))) ; must be one or two files
    (apply merge
           (map #(let [filename (:filename %)
                       bodyname (last (first (re-seq #"^(.+)/([^/]+)$" filename)))
                       sha1 (:sha1 %)
                       is-patch-archive? (and
                                          (substring? bodyname "patch")
                                          (not (substring? bodyname "prepatch")))]
                   (if-not is-patch-archive?
                     {:package-archive-file-name bodyname
                      :package-checksum-value sha1}
                     {:patch-file-name bodyname
                      :patch-file-checksum-value sha1}))
                container))))


(comment

  (def y (write-all-files-within x "/tmp"))

  (dorun y )


  (def x (read-all-files-within
          (java.io.File. "/mnt/ssd1/ol/lte-workspace2/peikertools/pub_oss/src/pub_oss")))

  (println x)



  (def x (java.io.File. "/mnt/ssd1/ol/lte-workspace2/peikertools/pub_oss/src/pub_oss"))

  (def x (read-all-files-within
          (java.io.File. "/mnt/ssd1/ol/lte-workspace2/apps_proc/oe-core/build/tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi/GPLv2/minicaller-1.0-r0/")))

  (def y (extract-source-dir-meta-data x))

  )




