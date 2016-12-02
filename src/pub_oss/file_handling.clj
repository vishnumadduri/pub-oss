; file handling helper functions
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.file-handling
  (:use [utils.security]
        [utils.gen-utils])
  (:import java.security.MessageDigest))


(defn read-binary-file
  [file]
  (let [filesize (.length file)
        buf (byte-array filesize)]
    (with-open [in-reader (clojure.java.io/input-stream file)]
      (.read in-reader buf)
      buf)))


(defn write-binary-file
  [filename content]
  (with-open [out-writer (clojure.java.io/output-stream filename)]
            (.write out-writer content)))


(defn read-all-files-within
  "read content of all (source archive) files within
   given directory and return hash map with the content
   sha1 digest and filename."
  [dir]
  (let [files (filter #(.isFile %) (.listFiles dir))]
    (map #(let [content (read-binary-file %)]
            {:filename (.getCanonicalPath %)
             :content content
             :sha1 (sha1-hex content)
             }) files)))


(defn write-all-files-within
  "inverse to read-all-files-within, target-dir specifies destintion
   where to copy the source archive files to."
  [container target-dir]
  (map #(let [bodyname (last (first (re-seq #"^(.+)/([^/]+)$" (:filename %))))
              content (:content %)]
          (write-binary-file (str target-dir "/" bodyname) content)) container))


(defn package-verification-code
  "calculate pacakge verification code over all files according
   to algorithm 4.7.4 in SPDX Specification 1.0"
  [container]
  (let [c (sort-by :sha1 container)
        l (map #(format "%s %s\n" (:sha1 %) (:filename %)) c)
        s (apply str l)]
    (sha1-hex (.getBytes s))))


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

  (doall x)



  (def x (java.io.File. "/mnt/ssd1/ol/lte-workspace2/peikertools/pub_oss/src/pub_oss"))

  (def x (read-all-files-within
          (java.io.File. "/mnt/ssd1/ol/lte-workspace2/apps_proc/oe-core/build/tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi/GPLv2/minicaller-1.0-r0/")))

  (dorun (map #(println (:filename %)) x))

  (def y (extract-source-dir-meta-data x))

  )
