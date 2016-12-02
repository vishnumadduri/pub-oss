; Utitlities for parsing the bitbake layers
;
; Python/Bitbake variable sustitutions
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.bb-syntax
  (:use [utils.gen-utils])
  (:require [clojure.string :as str]))


(defn value-of-string-assignment
  "parse ASCII  blob for string assignment  to variable as found  in most script
   languages enviromnets such shell, python, perl VAR = \"string expression\" and
   returns vector of found elements.

   Uses the following assignment extensions taken from Python:

    x  = \"arg1\"
    x += \"arg2\" -> x = [\"arg1\" \"arg2\"]

    x  = \"arg1\"
    x ?= \"arg2\" -> x = [\"arg1\"]

    x  = \"arg1\"
    x  = \"arg2\" -> x = [\"arg2\"]"
  [s var]
  (let [assign-matcher (re-pattern
                        (str "([\\s]*)(" var "[ \\t]*[+:.?]?=[ \\t]*)\\\"(.*)\\\"([\\S\\s]*)"))
        match (fn [res s]
                (let [arg (second (reverse (re-matches assign-matcher s)))]
                  (if arg
                    (condp #(substring? %2 %1) s
                      "+=" (conj res arg)
                      "?=" (if (empty? res) [arg] res)
                      [arg])
                    res)))]
    (reduce match [#_res] (str/split s #"\n"))))


(defn split-values-of-string-assignment
  "same as value-of-string-assignment but splits separated by blanks
   to another vector entry."
  [s var]
  (let [val (value-of-string-assignment s var)]
    (if (empty? val)
      val
      (str/split (reduce #(str %1 " " %2) val) #" "))))


(comment
  (value-of-string-assignment "TEST=\"123\"\nTEST=\"456\"" "TEST") ;; -> returns  [ \"456\" ]
  (value-of-string-assignment "TEST=\"123\"\nTEST?=\"456\"" "TEST") ;; -> returns  [ \"123\" ]
  (value-of-string-assignment "TEST?=\"456\"" "TEST") ;; -> returns  [ \"456\" ]
  (value-of-string-assignment "TEST+=\"123\"\nTEST+=\"456\"" "TEST") ;; -> returns  [ \"123\" \"456\" ]
  (value-of-string-assignment "TEST+=\"123\"\nTEST+=\"456\"" "TESTX") ;; -> returns  []

  (split-values-of-string-assignment "TEST+=\"123\"\nTEST+=\"456\"" "TEST") ;; -> returns  [ \"123\" \"456\" ]
  (value-of-string-assignment "TEST+=\"123\"\nTEST=\"456\"" "TEST") ;; -> returns \"456\"

  (value-of-string-assignment "TEST=\"123 456\"" "TEST") ;; -> returns \"123 456\"
  (value-of-string-assignment "XTEST=\"123 456\"" "TEST") ;; -> returns \"123 456\"
  (split-values-of-string-assignment "TEST=\"123 456\"" "TEST") ;; -> returns \"123\" \"456\"
  )



(defn subst-lines-with-regexp
  "substitute substrings with  regular expression in each  line with replacement
   string or regex

   example: (subst-lines-with-regexp
              \"topdir=${DIR}\"
              #\"[$][{]?DIR[}]?\"
              \"/usr/share\") -> topdir=\"/usr/share\""
  [s regex replace-fn]
  (let [lines (str/split s #"\n")
        lines (map
               (fn [l] (str/replace l regex replace-fn))
               lines)]
    (reduce #(str %1 %2 "\n") "" lines)))


(defn subst-lines-with-matching-regexp
  "substitute substrings with  regular expression in each  line with replacement
  regex  when  this  fully  matches  to  the line.  This  is  required  for  the
  replacement  of include  lines which  otherwise  would be  replaced even  when
  the  command is  mentioned  in  a comment.  Refer  to read-bb-recipe-file  for
  application scenario. "
  [s regex replace-fn]
  (let [lines (str/split s #"\n")
        lines (map
               (fn [l]
                 (if (re-matches regex l)
                   (str/replace l regex replace-fn)
                   l))
               lines)]
    (reduce #(str %1 %2 "\n") "" lines)))


(defn replace-comment-by-emtpy-lines
  [s]
  (subst-lines-with-regexp s #"^[\s]*[#].*" ""))


(defn subst-bb-var
  "substitute bitbake variable evaluation expression by given value"
  [s var value]
  (let [value (str/replace value #"[$]" "\\\\\\$")
        value (str/replace value #"[{]" "\\\\\\{")
        value (str/replace value #"[}]" "\\\\\\}")]
    (subst-lines-with-regexp s (re-pattern (str "[$][{]?" var "[}]?")) value)))


(defn subst-val-pair
  "read value of bitbake variable defintion and substitue later occurences
   (subst-val-pair \"TST=\"1.0\"\n include ${TST}\" \"TST\" ) ->
     \"... include \"1.0\" \" "
  [c var]
  (let [[value] (value-of-string-assignment c var)]
    [(subst-bb-var c var value) value]))


(defn update-content-with-bb-vars
  "subsitute all bitbake expressions ${XYZ} in given multi-line string

   takes string 's' and hash map for clojure namespace keys e.g.
   {::pn \"perl\" ::pv \"1.2.3\" ...} and replaces all corresponding
   bitbake variables with corresponding key value. In the given example
   the expression ${PN} will be replaced by \"perl\", ${PV} by \"1.2.3\"
   and so on."
  [content bb mapfn-clj->bb]
  (let [;; replace line continuation chars '\' by ''
        content (str/replace content #"[\\][\s]*[\n][\s]*" "")]
    (reduce (fn [res [k v]]
              (let [v (if (sequential? v) (first v) v)
                    cljk (mapfn-clj->bb k)]
                (if cljk (subst-bb-var res cljk v) res)))
            content bb)))


(defn subst-python-multiline-strings
  "substitutes a multiline string python constant by a standard string definition"
  [s]
  (str/replace s
               #"([\"]{3})([^\"]+)([\"]{3})"
               (fn [ml]
                 (let [ml-arg (second (reverse ml))
                       ml-arg (str/replace ml-arg #"[\n]" " ")]
                   (str "\"" ml-arg "\"")))))
