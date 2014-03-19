; Utitlities for Generating Namespaced XML Data with clojure.data.xml
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns utils.xml-utils
  (:use [clojure.data.xml])
  )

(defn- emit-clojure-string-with-tabs
  "emits  a defrecord  constructor for  a clojure.data.xml  object and
  patches  namespaces  which  are unfortunately  still  not  correctly
  parsed in order to be emit a correctly name spaced xml instance. The
  function  takes  two  arguments,  first the  number  of  tabs  which
  shall  be  initially set  to  0,  2nd  argument  is an  instance  of
  clojure.data.xml.Element.  Pay  attention  to  the  fact  that  this
  function is neither  lazy nor tail recursive  and therefore requires
  signficant resources  (memory/cpu). The  use case is  to be  able to
  transform  an existing  XML template  into a  clojure XML  generator
  where runtime performace is not an issue."
  [nr-tabs data-xml]
  (let [attrs (:attrs data-xml)
        content (:content data-xml)
        tabs (apply str (repeat nr-tabs "\t"))
        fix-ns-key (fn [k] (keyword (subs (clojure.string/replace k #"[/]" ":") 1)))]
    (str "\n" tabs "(element "
         (:tag data-xml) " "
         (if (empty? (:attrs data-xml))
           {}
           {(fix-ns-key (first (keys attrs))) (first (vals attrs))})
         (if (or (> (count content) 1) (not (string? (first content))))
             (doall (apply str (map (partial emit-clojure-string-with-tabs (inc nr-tabs)) content)))
             (str " \"" (first content) "\""))
         ")")))


(defn emit-clojure-string
  "Used    to   emit    a    clojure.data.xml.Element   obtained    by
  clojure.data.xml/parse   to   create    a   clojure   xml   instance
  generator.    For    more     detailed    information    refer    to
  emit-clojure-string-with-tabs"
  [data-xml]
  (emit-clojure-string-with-tabs 0 data-xml))


(defn read-xml
  "read the given xml file and return a clojure.data.xml instance."
  [filename]
  (let [input-xml (java.io.StringReader. (slurp filename))]
    (parse input-xml)))


(defn write-xml
  "write the given xmldata to output file."
  [filename xmldata]
  (with-open [out-writer (clojure.java.io/writer filename)]
    (emit xmldata out-writer)))


(defn xml2clojure
  "Transforms  an  xml  file  to  the  corresponding  clojure.data.xml
  constructor expression. Specifiy in infile the xml data file to read
  and in out file the clojure constructor to generate"
  [infile outfile]
  (let [input-cl (read-xml infile)]
    (with-open [out-writer (clojure.java.io/writer outfile)]
      (dorun (.write out-writer (emit-clojure-string input-cl))))))

(comment example

  (xml2clojure "sample.spdx" "src/oss_cd/spdx2.clj")

  )
