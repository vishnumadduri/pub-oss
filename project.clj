(defproject pub-oss "1.0.0-SNAPSHOT"
  :description "Extracts Open Source Code by OE generated Directory Structure"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.xml "0.0.7"]
                 [swank-clojure "1.4.3"]]
  :source-paths ["src"]
  :main pub-oss.core
  :aot [pub-oss.core])
