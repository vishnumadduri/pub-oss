; extracts open source code out of source directory generated
; by sourcepkg class of Poky / Open Embedded Linux Disribution
;
; main function, stand-alone app
;
; by Otto Linnemann
; (C) 2014-2016, GNU General Public Licence

(ns pub-oss.core
  (:require
   [clojure.tools.cli :refer [parse-opts]])
  (:use [bb.recipe-parser]
        [utils.gen-utils :only [get-version]]
        [bb.config :as conf])
  (:gen-class))


; command line interface (leiningen)
(def cli-options
  [["-b" "--build-dir DIR" "full qualified OE build directory name"
    :default "../../apps_proc/oe-core/build"
    :validate [#(.isDirectory (java.io.File. %)) "must be directory"]]
   ["-i" "--image specifier" "bitbake recipe image name without extension"
    :default "9640-cdp-ltenad"]
   ["-c" "--config filename" "optional configuration file"
    :default nil
    :validate [#(.exists (java.io.File. %)) "file or directory must not exist"]]
   ["-h" "--help" "this help string"]])


(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        options (:options opts)
        arguments (:arguments opts)
        summary (:summary opts)
        errors (:errors opts)
        build-dir (:build-dir options)
        image (:image options)
        config (:config options)
        invalid-opts (not-empty errors)
        title-str (str
                   "pub-oss: Parsing and Extraction of Open Source Code out of an OpenEmbedded Build Enviroment\n"
                   (format "      Version: %s, refer to https://github.com/linneman/pub-oss for more information\n" (get-version 'pub-oss))
                   "      (C) 2016, GNU General Public Licence by Otto Linnemann\n")
        start-msg-fmt (str
                       "\t\tStarting source extraction from\n\n"
                       "\topen embedded build directory: %s\n"
                       "\t        for file system image: %s\n"
                       "\t                           to: %s\n\n")]
    (println title-str)
    (when (or (:help options) invalid-opts)
      (println "  Invocation:\n")
      (println summary)
      (System/exit -1))
    (if invalid-opts
      (println errors)
      (do
        (when config (load-file config))
        (println (format start-msg-fmt build-dir image (::conf/deploy-os-dir *default-config*)))
        (dorun (execute-bb-open-source-extraction build-dir image))
        (System/exit 0)))))
