; geneneral utilies mostly for data manipulation
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns utils.gen-utils)


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


(defn substring?
  "true when string needle is substring of string hay"
  [hay needle] (>= (.indexOf hay needle) 0))

(defn initstring?
  "true when string needle is the same as begining of string hay"
  [hay needle] (== (.indexOf hay needle) 0))
