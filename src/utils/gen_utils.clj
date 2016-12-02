; geneneral utilies mostly for data manipulation
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns utils.gen-utils
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import java.util.Properties))


(defn get-version
  "reads version data from the pom.properties META-INF file
   refer to:
   https://groups.google.com/d/msg/leiningen/7G24ifiYvOA/h6xmjeWaO3gJ"
  [dep]
  (let [path (str "META-INF/maven/" (or (namespace dep) (name dep))
                  "/" (name dep) "/pom.properties")
        props (io/resource path)]
    (when props
      (with-open [stream (io/input-stream props)]
        (let [props (doto (Properties.) (.load stream))]
          (.getProperty props "version"))))))


(defmacro apply-with-keywords
  "applies key word struct map to function f which takes
   keyword arguments.

   Example:
     (defn f
        [& {:keys [a b] :as args}]
        (str a b))

   (f :a 42 :b \"hello\")   ; correspond to
   (apply-with-keywords f {:a 42 :b \"hello\"})"
  [f kw]
  `(apply ~f (mapcat #(vector (key %) (val %)) ~kw)))


(defn mapt
  "same as map but preserves type input collection type

   This works in a similar way like Clojure's map function.
   It takes only one argument as collection and invokes the
   given function with every element of it. Instead of returning
   a list sequence as map it mapt conjoins the results of
   the function to a collection of the same type.

   examples:

     (mapt inc [1 2 3]) -> [3 4 5]

     (mapt
      (fn [[k v]] [k (str v v)])
      (sorted-map-by > 4 \"d\" 1 \"a\" 2 \"b\")) -> {4 dd, 2 bb, 1 aa}"
  [f c]
  (reduce
   (fn [res elem]
     (conj res (f elem)))
   (empty c)
   c))


(comment

  (mapt
   (fn [[k v]] [k (str v v)])
   (sorted-map-by > 4 "d" 1 "a" 2 "b"))

  (mapt
   (fn [[k v]] [(inc k) v])
   (sorted-map-by > 4 "d" 1 "a" 2 "b"))

  (mapt inc [1 2 3])
  )


(defn substring?
  "true when string needle is substring of string hay"
  [hay needle] (>= (.indexOf hay needle) 0))


(defn initstring?
  "true when string needle is the same as begining of string hay"
  [hay needle] (== (.indexOf hay needle) 0))


(defn string-has-lines-of-regex?
  "walks line by line through given string (text)
   and returns the first where the regex applies."
  [s regex]
  (loop [lines (str/split s #"\n")]
    (let [line (first lines)]
      (if line
        (if (re-matches regex line)
          line
          (recur (rest lines)))
        false))))


(comment
  (string-has-lines-of-regex?
   "\n\nrequire test.inc"
   #"([\s]*[^#]?[\s]*(require|include))[ \t](.*[.](?:bb|inc))")

  (string-has-lines-of-regex?
   "\n\nreq test.inc"
   #"([\s]*[^#]?[\s]*(require|include))[ \t](.*[.](?:bb|inc))")
  )


(defn path-of-filename
  "returns the path of a fully qualified filename or nil if no fully
   qualitifed file name is given.

   example: (path-of-filename \"/mnt/ssd1/test.xyz\") -> \"/mnt/ssd1\" "
  [full-qualified-name]
  (nth (re-matches #"(.*)([/].*)$" full-qualified-name) 1))


(defn body-of-filename
  "returns the name without path of a fully qualified filename or nil if no fully
   qualitifed file name is given.

   example: (path-of-filename \"/mnt/ssd1/test.xyz\") -> \"test.xyz\" "
  [full-qualified-name]
  (let [body-name (last (re-matches #"(.*)([/].*)$" full-qualified-name))]
    (subs body-name 1)))


(defn find-first-file-that-exists
  "returns first file that exists in list for file names

   (first-first-file-that-exists [\"xxx\" \"/usr/bin\"]) -> \"/usr/bin\" "
  [file-list]
  (some (fn [fn] (if (.exists (clojure.java.io/as-file fn) ) fn))
        file-list))


(comment
  (find-first-file-that-exists ["xxx" "/usr/bin"])
  )


(defn println-err
  "println to stderr"
  [& args]
  (binding [*out* *err*] (apply println args)))
