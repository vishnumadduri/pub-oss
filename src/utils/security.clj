; some helpers functions for common hashes
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns utils.security
  (:import java.security.MessageDigest))

(defn get-hash [type data]
  (.digest (java.security.MessageDigest/getInstance type) data))

(defn sha1-hash [data]
 (get-hash "sha1" data))

(defn str2hex [data]
  (apply str (map #(format "%02x" (bit-and % 0xff)) data)))

(defn sha1-hex [data]
  (-> data sha1-hash str2hex))