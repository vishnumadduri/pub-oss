(defproject pub-oss "2.0.0-SNAPSHOT"
  :description "Extracts Open Source Code by OE generated Directory Structure"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/tools.cli "0.3.1"]
                 [commons-io/commons-io "2.4"]
                 [clj-glob "1.0.0"]]
  :source-paths ["src"]
  :main pub-oss.core
  :aot [pub-oss.core])
