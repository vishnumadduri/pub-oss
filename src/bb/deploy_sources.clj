; Utitlities for parsing the bitbake recipes
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.deploy-sources
  (:use [utils.gen-utils]
        [bb.config :as conf]
        [clojure.java.shell :only [sh]])
  (:require
   [clojure.string :as str]))


(defn sh-cmd
  "executes command line in the system shell

   uses the  shell function of clojure.java.shell  but does not need  to specify
   the arguments as a vector"
  [command-line]
  (def command-line command-line)
  (let [res (sh "/bin/sh" "-c" command-line)]
    (def res res)
    (:exit res)))


(defn is-path-directory?
  "checks wether the full qualified path name is a directory"
  [path]
  (.isDirectory (java.io.File. path)))


(defn- deploy-source-dir
  "transfer source directory from working directory to deploy directory
   copy everything followed by the invocation 'make clean' afterwords."
  [src-dir package-dir]
  (let [dirs (map #(str src-dir "/" %) ["package" "packages-split" "pkgdata" "image"])]
    (sh-cmd (format "cp -RL %s %s" src-dir package-dir))
    ;; check whether we are in top level work directory and remove bitbake's
    ;; build directories if this is the case
    (if (every? is-path-directory? dirs)
      (dorun (map #(sh-cmd (format "rm -rf %s" %)) dirs)))
    (sh-cmd (format "rm -rf %s/.git; true" package-dir))
    ;; invoke clean only for non-excluded packages;
    ;; otherwise remove at least object files
    (if (not-any?
           #(substring? (body-of-filename package-dir) %)
           *packages-without-make-clean*)
      (sh-cmd (format "cd %s && (make clean 2>&1 > /dev/null; true)" package-dir))
      (sh-cmd (format "cd %s && (find . -name \"*.o\" -type f -delete)" package-dir)))))


(defn- package-deploy-dir
  "creates a gnu-zipped tar archive and removes original directory"
  [deploy-dir deploy-package]
  (sh-cmd (format "cd %s && tar cfz %s.tgz %s && rm -rf %s 2>&1 > /dev/null"
                  deploy-dir deploy-package deploy-package deploy-package)))


(defn deploy-source
  "deploy source code of a project as compressed tar archive

   src-dir:       full qualified directory name of the source files
   deploy-dir:    full qualified destination directory name
   deploy-package file body name of the generated tar archive."
  [src-dir deploy-dir deploy-package]
  (cond
    (not (is-path-directory? src-dir))
    (println-err (format "cannot access deployment source dir: %s" src-dir))

    (not (is-path-directory? deploy-dir))
    (println-err (format "cannot access deployment target dir: %s" deploy-dir))

    :else
    (if (= 0 (deploy-source-dir src-dir (str deploy-dir "/" deploy-package)))
      (if (= 0 (package-deploy-dir deploy-dir deploy-package))
        (str deploy-package ".tgz")
        "zip-error")
      "copy-error")))


(defn deploy-kernel-source
  "deploy source code of the Linux Kernel as a compressed tar archive

   git-src-dir:   full qualified directory name of the kernel source repository
   deploy-dir:    full qualified destination directory name
   deploy-package file body name of the generated tar archive."
  [git-src-dir deploy-dir deploy-package]
  (cond
    (not (is-path-directory? git-src-dir))
    (println-err (format "cannot access deployment source dir: %s" git-src-dir))

    (not (is-path-directory? deploy-dir))
    (println-err (format "cannot access deployment target dir: %s" deploy-dir))

    :else
    (if (= 0 (sh-cmd (format "git clone --no-hardlinks %s %s/%s" git-src-dir deploy-dir deploy-package)))
      (if (= 0 (package-deploy-dir deploy-dir deploy-package))
        (str deploy-package ".tgz")
        "zip-error")
      "copy-error")))


(defn clean-and-create-deployment-directories
  "cleans and recreate all deployment diretories"
  [build-dir]
  (let [mkdir-force-fn (fn [d] (sh-cmd (format "rm -rf %s && mkdir -p %s" d d)))]
    (dorun
     (map
      #(mkdir-force-fn (str build-dir (*default-config* %)))
      [::conf/deploy-os-dir
       ::conf/deploy-spdx-dir]))))
