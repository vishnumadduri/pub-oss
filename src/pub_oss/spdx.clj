; Creation of SPDX Container Data
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.spdx
  (:use [utils.xml-utils]
        [clojure.data.xml])
  )

(defn- simple-date-format [format-str date]
  (.format (java.text.SimpleDateFormat. format-str) date))

(defn- spdx-date-format
  "formats java date objects according to SPDX rules"
  [date]
  (let [dstr (simple-date-format "yyyy-MM-dd" date)
        tstr (simple-date-format "HH:mm:ss" date)]
    (str dstr "T" tstr "Z")))


(defn transform-to-spdx-license
  "returns the spdx license tag for a given Yocto license ID"
  [yocto-license-id]
  (let [yocto2spdx-license-hash
        {
         "AFL-2GPLv2+" "AFL-2.0"
         "AL2.0" "Apache-2.0"
         "Apache" "Apache-1.0"
         "Apache-2.0" "Apache-2.0"
         "ArtisticGPL" "Artistic-1.0"
         "BSD" "BSD-2-Clause"
         "BSD-3-Clause" "BSD-3-Clause"
         "BSDArtistic" "BSD-2-Clause"
         "BSDGPLv2" "BSD-2-Clause"
         "FreeTypeGPLv2+" "FTL"
         "GPL" "GPL-2.0"
         "GPL2" "GPL-2.0"
         "GPL-2.0-with-classpath-exception" "GPL-2.0-with-classpath-exception"
         "GPL-2.0-with-GCC-exception" "GPL-2.0-with-GCC-exception"
         "GPL-3+LGPL-2.1+" "GPL-3.0"
         "GPLLGPL" "GPL-1.0"
         "GPLv1" "GPL-1.0"
         "GPLv1+" "GPL-1.0+"
         "GPLv2" "GPL-2.0"
         "GPLv2+" "GPL-2.0+"
         "GPLv2LGPLMPLBSD" "GPL-2.0"
         "GPLv2LGPLv2" "GPL-2.0"
         "GPLv2+LGPLv2.1+" "GPL-2.0"
         "GPLv2LGPLv2.1" "GPL-2.0"
         "GPLv2+LGPLv2.1+BSD" "GPL-2.0"
         "GPLv2LGPLv2BSDMIT" "GPL-2.0"
         "GPLv3" "GPL-3.0"
         "GPLv3+" "GPL-3.0+"
         "GPLv3+LGPLv2.1+" "GPL-3.0+"
         "ISCBSD" "ISC"
         "LGPLv2" "LGPL-2.0"
         "LGPLv2+" "LGPL-2.0+"
         "LGPLv2.0+" "LGPL-2.0+"
         "LGPLv2.1+" "LGPL-2.0+"
         "LGPLv2.1+GPLv2+" "LGPL-2.0+"
         "LGPLv2.1GPLv2+" "LGPL-2.0+"
         "LGPLv2+BSDPD" "LGPL-2.0+"
         "LGPLv2LGPLv2+" "LGPL-2.0+"
         "LGPLv2LGPLv2+LGPLv2.1+" "LGPL-2.0+"
         "LGPLv3GPLv3" "LGPL-3.0"
         "Libpng" "Libpng"
         "MIT" "MIT"
         "MITMIT-style" "MIT"
         "MITMIT-styleBSD" "MIT"
         "MITMIT-stylePD" "MIT"
         "MIT-style" "MIT"
         "MIT-styleMITPD" "MIT"
         "MIT-X" "MIT"
         "MPL-1LGPLv2.1" "MPL-1.0"
         "MPL-2.0" "MPL-2.0"
         "openssl" "OpenSSL"
         "Sleepycat" "Sleepycat"
         "Zlib" "Zlib"}
        spdx-lic-tag (yocto2spdx-license-hash yocto-license-id)]
    (if spdx-lic-tag
      (str "http://spdx.org/licenses/" spdx-lic-tag)
      (do (println "no valid open source license declared for tag " spdx-lic-tag)
          "UNSUPPORTED-LICENSE"))))


(defn create-spdx
  "create an spdx xml entety with the given keys:"
  [& {:keys [creator-org
             creator-person
             creator-tool
             creation-date
             package-name
             version-info
             package-file-name
             package-archive-file-name
             download-location
             package-verification-code
             licence-concluded
             copyright-text
             summary
             package-checksum-value
             license-comments
             patch-file-name
             patch-file-checksum-value]
      :or {creator-org "peiker acustic"
           creator-person "Otto Linnemann"
           creator-tool "SPDX Generator and Source Code Extractor for Open Embedded / Poky, https://github.com/linneman/pub-oss"
           creation-date (spdx-date-format (new java.util.Date))
           copyright-text "NOASSERTION"}
      :as args}]
  (let [licence-concluded (transform-to-spdx-license licence-concluded)]
    (element :rdf:RDF
             {"xmlns:rdf" "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
              "xmlns" "http://spdx.org/rdf/terms#"
              "xmlns:rdfs" "http://www.w3.org/2000/01/rdf-schema#"}
             (element :SpdxDocument {:rdf:about "http://www.peiker.de"}
                      (element :specVersion {} "SPDX-1.0")
                      (element :dataLicense {}
                               (element :License {:rdf:about "http://spdx.org/licenses/PDDL-1.0"}
                                        (element :LicenseId {} "PDDL-1.0")))
                      (element :creationInfo {}
                               (element :CreationInfo {}
                                        (element :creator {} (str "Organization: " creator-org))
                                        (element :creator {} (str "Person: " creator-person))
                                        (element :creator {} (str "Tool: " creator-tool))
                                        (element :created {} creation-date)))
                      (element :describesPackage {}
                               (element :Package {:rdf:about "http://www.spdx.org/tools#SPDXANALYSIS?package"}
                                        (element :name {} package-name)
                                        (element :versionInfo {} version-info)
                                        (element :packageFileName {} package-file-name)
                                        (element :downloadLocation {} download-location)
                                        (element :packageVerificationCode {}
                                                 (element :PackageVerificationCode {}
                                                          (element :packageVerificationCodeValue {} package-verification-code)))
                                        (element :licenseConcluded {:rdf:resource licence-concluded})
                                        (element :licenseInfoFromFiles {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                        (element :licenseDeclared {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                        (element :copyrightText {} copyright-text)
                                        (element :summary {} summary)
                                        (when package-archive-file-name (element :hasFile {:rdf:nodeID "A0"}))
                                        (when patch-file-name (element :hasFile {:rdf:nodeID "PATCHFILE"}))))
                      (when package-archive-file-name
                       (element :referencesFile {}
                                 (element :File {:rdf:nodeID "A0"}
                                          (element :fileName {} package-archive-file-name)
                                          (element :fileType {} "ARCHIVE")
                                          (element :checksum {}
                                                   (element :Checksum {}
                                                            (element :checksumValue {} package-checksum-value)
                                                            (element :algorithm {} "SHA1")))
                                          (element :licenseConcluded {:rdf:resource licence-concluded})
                                          (element :licenseInfoInFile {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                          (element :licenseComments {} license-comments)
                                          (element :copyrightText {} copyright-text))))
                      (when patch-file-name
                        (element :referencesFile {}
                                 (element :File {:rdf:nodeID "PATCHFILE"}
                                          (element :fileName {} patch-file-name)
                                          (element :fileType {} "SOURCE")
                                          (element :checksum {}
                                                   (element :Checksum {}
                                                            (element :checksumValue {} patch-file-checksum-value)
                                                            (element :algorithm {} "SHA1")))
                                          (element :licenseConcluded {:rdf:resource licence-concluded})
                                          (element :licenseInfoInFile {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                          (element :licenseComments {} license-comments)
                                          (element :copyrightText {} copyright-text))))))))




(comment

  (create-spdx)
  (println (emit-str (create-spdx)))

  (write-xml "check.xml" (create-spdx
                          :creator "Otto Linnemann"
                          :creation-date "2014-03-18"
                          :package-file-name "linphone-1.0.1.tar.gz"
                          :package-checksum-value "ffa0242da1bcada"
                          :patch-patch-file-name "linphone-1.0.1.patches"
                          :patch-file-checksum-value "0241af1dcfa1"))


  )
