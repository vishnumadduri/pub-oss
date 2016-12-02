; Utitlities for parsing the bitbake recipes
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.gen-html-readme
  (:use [utils.gen-utils]
        [bb.config :as conf]
        [pub-oss.file-handling])
  (:require
   [clojure.string :as str]))


(defn html-doc-header
  [build-version image-name]
  (let [now (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date))]
    (format
     "<!DOCTYPE html>
    <head>
      <title>build-version: %s, apps-proc-image: %s</title>
    </head>
    <html>
    <body>
      <center>
        <h1>Published Open Source Software Packages</h1>
        <h2>Valeo peiker Telematics Product Line</h2>
        <h3>SW-Release: %s, Apps-Proc. Image Target: %s</h3>
        <h4>Preliminary Version, published on: %s</h4>
      </center>"
     build-version image-name build-version image-name now)))


(defn html-doc-footer
  []
  "</body>
   </html>")


(defn html-pkg-table-doc-header
  []
  "  <table border=\"1\" style=\"width:100%\" cellpadding=\"2\" cellspacing=\"2\">
       <col width=\"200\">
       <col width=\"120\">
       <col width=\"250\">
       <tr>
         <th>Name</th>
         <th>Version</th>
         <th>License</th>
         <th>Description</th>
       </tr>")


(defn html-pkg-table-doc-footer
  []
  "    </table>")


(defn html-pkg-table-entry-line
  [package version license description]
  (format
   "   <tr>
         <td>%s</td>
         <td>%s</td>
         <td>%s</td>
         <td>%s</td>
       </tr>"
   package version license description))


(comment
  (println
   (html-doc-header "image-name"))

  (println
   (html-pkg-table-doc-header))

  (println
   (html-pkg-table-entry-line "package" "license" "description"))

  (println
   (html-pkg-table-doc-footer))

  (println
   (html-doc-footer))
  )
