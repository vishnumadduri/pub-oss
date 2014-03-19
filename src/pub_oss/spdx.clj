; Creation of SPDX Container Data
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.spdx
  (:use [utils.xml-utils]
        [clojure.data.xml])
  )


(defn create-spdx
  "create an spdx xml entety with the given keys:"
  [& {:keys [creator
             creation-date
             package-name
             version-info
             package-file-name
             package-archive-file-name
             download-location
             package-virification-code
             licence-concluded
             copyright-text
             summary
             package-checksum-value
             license-comments
             patch-patch-file-name
             patch-file-checksum-value
             ] :as args}]
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
                                      (element :creator {} creator)
                                      (element :created {} creation-date)))
                    (element :describesPackage {}
                             (element :Package {:rdf:about "http://www.spdx.org/tools#SPDXANALYSIS?package"}
                                      (element :name {} package-name)
                                      (element :versionInfo {} version-info)
                                      (element :packageFileName {} package-file-name)
                                      (element :downloadLocation {} download-location)
                                      (element :packageVerificationCode {}
                                               (element :PackageVerificationCode {}
                                                        (element :packageVerificationCodeValue {} package-virification-code)))
                                      (element :licenseConcluded {:rdf:resource licence-concluded})
                                      (element :licenseInfoFromFiles {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                      (element :licenseDeclared {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                      (element :copyrightText {} copyright-text)
                                      (element :summary {} summary)
                                      (element :hasFile {:rdf:nodeID "A0"})))
                    (element :referencesFile {}
                             (element :File {:rdf:nodeID "A0"}
                                      (element :fileName {} package-archive-file-name)
                                      (element :fileType {} "ARCHIVE")
                                      (element :checksum {}
                                               (element :Checksum {}
                                                        (element :checksumValue {} package-checksum-value)
                                                        (element :algorithm {} "SHA1")))
                                      (element :licenseConcluded {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                      (element :licenseInfoInFile {:rdf:resource "http://spdx.org/rdf/terms#noassertion"})
                                      (element :licenseComments {} license-comments)
                                      (element :copyrightText {:rdf:resource "http://spdx.org/rdf/terms#noassertion"}))
                             (when patch-patch-file-name
                               (element :File {:rdfNodeId "PATCHFILE"}
                                        (element :fileName {} patch-patch-file-name)
                                        (element :fileType {} "SOURCE")
                                        (element :checksum {}
                                               (element :Checksum {}
                                                        (element :checksumValue {} patch-file-checksum-value)
                                                        (element :algorithm {} "SHA1")))))))))




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
