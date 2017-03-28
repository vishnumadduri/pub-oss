; Utitlities for parsing the bitbake layers
;
; addon configuration data resolved inside bitbake's build system
;
; by Otto Linnemann
; (C) 2016, GNU General Public Licence

(ns bb.config)


;; default configuration keys
;; make sure they match to your bitbake configuration

(def ^:dynamic *default-config*
  {:bb.bblayers/tmpdir              "/tmp-eglibc"
   :bb.bblayers/workdir             "/tmp-eglibc/work/armv7a-vfp-neon-oe-linux-gnueabi"
   :bb.bblayers/workdir-sys         "/tmp-eglibc/work/mdm9640-oe-linux-gnueabi"
   :bb.bblayers/deploydir           "/tmp-eglibc/deploy"

   :bb.bblayers/gnu-mirror          "ftp://ftp.gnu.org/gnu"
   :bb.bblayers/debian-mirror       "ftp://ftp.debian.org/debian/pool"
   :bb.bblayers/sourceforge-mirror  "http://downloads.sourceforge.net"
   :bb.bblayers/xorg-mirror         "http://xorg.freedesktop.org/releases"
   :bb.bblayers/gnome-mirror        "http://ftp.gnome.org/pub/GNOME/sources"
   :bb.bblayers/icedtea-uri         "http://icedtea.wildebeest.org/download/source"
   :bb.bblayers/kernelorg-mirror    "http://kernel.org/pub"

   :bb.bblayers/filename-type       "qti"
   :bb.bblayers/python-majmin       "2.7"
   ::deploy-os-dir                  "/tmp-eglibc/deploy/open-source/sources"
   ::deploy-spdx-dir                "/tmp-eglibc/deploy/open-source/spdx"})


;; watch out for preferred providers of your build configuration
;; fo the distribition. These are currently not parsed out of
;; distribution configuration files as in the QTI example
;; meta-msm/conf/distro/msm.conf

(def ^:dynamic *preferred-providers*
  {"jpeg"                      "libjpeg-turbo"
   "virtual/kernel"            "linux-quic"
   "virtual/libx11"            "libx11"
   "virtual/java-initial"      "icedtea8-native"})


;; watch out for preferred versions of your build configuration
;; for the distribution. These are currently not parsed out of
;; distribution configuration files as in the QTI example
;; meta-msm/conf/distro/msm.conf

(def ^:dynamic *preferred-versions*
  {"readline"                  "5.2"
   "iptables"                  "1.4.19.1"
   "inputproto"                "2.3"
   "gettext"                   "0.16.1"})


;; build recipe and directory of linux kernel git repository
;; the kernel gets an extra treatment

(def ^:dynamic *linux-kernel*
  {::bb-recipe "linux-quic"
   ::git-src "/../../kernel"})


;; these packages  are skipped  in example  libiconv is left  out because  it is
;; already provided by eglibc, refer to recipes-support/libiconv/libiconv_1.14.bb

(def ^:dynamic *skipped-packages*
  #{"libiconv"})


;; regular expression for filtering software packages with open source licenses

(def ^:dynamic *opensource-licenses*
  #"[L]?GPL[v]?[1-3]?[+]?|LGPLv(.*)only|(GPL(.*)exception)|LGPLv(.*)[+]|BSD|NewBSD|BSD-3-Clause|MIT|MIT-style|MIT-X|Apache[-]?[1-2]?(.0)?|AL[-]?[1-2]?(.0)?|AFL|Artistic(.*)GPL.*|PD|bz2|bzip2|Sleepycat|openssl|ICU|Zlib|Libpng|ISC|MPL(.*)|FreeType|\([L]?GPL.*\)|[L]?GPL.*")


;; The following set declares licenses under the spdx tag (refer to function
;; transform-to-spdx-license) whose distribution is critical due to concrete
;; terms of the licensing text e.g. GPLv3 with respect to Tivoization

(def ^:dynamic *prohibited-spdx-licenses*
  #{"GPLv3" "GPL-3.0+" "LGPLv3"})


;; For some packages e.g. perl the invocation of 'make clean' causes an infinite loop
;; We better to not execute this operation for these.

(def ^:dynamic *packages-without-make-clean*
  ["perl"])
