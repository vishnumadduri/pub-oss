; additional meta information for common open source packages
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.package-addon-info)

(def ^{:private true } proj-download-location "http://url-to-be-defined/")

(def ^{:private true } pkg-add-on-info-data
  {
   :alsa-lib {:download-location "ftp://ftp.alsa-project.org/pub/lib/alsa-lib-1.0.24.tar.bz2" :copyright-text "LGPLv2.1 & GPLv2+" :summary "Alsa sound library"}
   :atk {:download-location "http://download.gnome.org/sources/atk/2.2/atk-2.2.0.tar.bz2" :copyright-text "LGPLv2 & LGPLv2+" :summary "An accessibility toolkit for GNOME."}
   :attr {:download-location "http://download.savannah.gnu.org/releases/acl/attr-2.4.46.src.tar.gz" :copyright-text "LGPLv2.1+ & GPLv2+" :summary "utilities for access control list"}
   :audiorouter-git-r1 {:download-location (str proj-download-location "audiorouter-git-r1.tar.gz") :copyright-text "GPL2" :summary "Audio Routing for for ECall/AssistCall Voice Audio"}
   :base-files {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv2" :summary "Miscellaneous files for the base system."}
   :base-passwd {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv2+" :summary "Base system master password/group files."}
   :bash {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv3+" :summary "An sh-compatible command language interpreter."}
   :bigreqsproto {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "MIT-X" :summary "X protocol headers"}
   :binutils-cross {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv3" :summary ""}
   :binutils {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv3" :summary "A GNU collection of binary utilities"}

   :busybox {:download-location "" :copyright-text "" :summary ""}
   :cairo {:download-location "http://cairographics.org/download"
           :copyright-text "Copyright 1999 Tom Tromey&#xD;Copyright 2002, 2003 University of Southern California, Information&#xD;  Sciences Institute  (ISI)&#xD;Copyright 2000, 2002, 2004, 2005 Keith Packard&#xD;Copyright 2004 Calum Robinson&#xD;Copyright 2004 Richard D. Worth&#xD;Copyright 2004, 2005 Red Hat, Inc.&#xD;Copyright 2004 David Reveman"
           :summary "Cairo is a 2D graphics library with support for multiple output devices. Currently supported output targets include the X Window System, Quartz, Win32, image buffers, PostScript, PDF, and SVG file"}
   :coreutils {:download-location "" :copyright-text "" :summary ""}
   :cups {:download-location "" :copyright-text "" :summary ""}
   :dbus-glib {:download-location "" :copyright-text "" :summary ""}
   :dbus {:download-location "" :copyright-text "" :summary ""}
   :dnsmasq {:download-location "" :copyright-text "" :summary ""}
   :e2fsprogs {:download-location "" :copyright-text "" :summary ""}
   :eglibc-initial {:download-location "" :copyright-text "" :summary ""}
   :eglibc {:download-location "" :copyright-text "" :summary ""}
   :elfutils {:download-location "" :copyright-text "" :summary ""}
   :exosip4 {:download-location "" :copyright-text "" :summary ""}
   :exosip {:download-location "" :copyright-text "" :summary ""}
   :expat {:download-location "" :copyright-text "" :summary ""}
   :fastjar {:download-location "" :copyright-text "" :summary ""}
   :fontconfig {:download-location "" :copyright-text "" :summary ""}
   :freetype {:download-location "" :copyright-text "" :summary ""}
   :gdbm {:download-location "" :copyright-text "" :summary ""}
   :gdb {:download-location "" :copyright-text "" :summary ""}
   :gdk-pixbuf {:download-location "" :copyright-text "" :summary ""}
   :gettext {:download-location "" :copyright-text "" :summary ""}
   :giflib {:download-location "" :copyright-text "" :summary ""}
   :glib {:download-location "" :copyright-text "" :summary ""}
   :gmp {:download-location "" :copyright-text "" :summary ""}
   :gnujaf {:download-location "" :copyright-text "" :summary ""}
   :gnumail {:download-location "" :copyright-text "" :summary ""}
   :gnutls {:download-location "" :copyright-text "" :summary ""}
   :gtk+ {:download-location "" :copyright-text "" :summary ""}
   :inetlib {:download-location "" :copyright-text "" :summary ""}
   :initscripts {:download-location "" :copyright-text "" :summary ""}
   :inputproto {:download-location "" :copyright-text "" :summary ""}
   :iproute2 {:download-location "" :copyright-text "" :summary ""}
   :iptables {:download-location "" :copyright-text "" :summary ""}
   :kbd {:download-location "" :copyright-text "" :summary ""}
   :kbproto {:download-location "" :copyright-text "" :summary ""}
   :keymaps {:download-location "" :copyright-text "" :summary ""}
   :libasrc {:download-location "" :copyright-text "" :summary ""}
   :libcap {:download-location "" :copyright-text "" :summary ""}
   :libffi {:download-location "" :copyright-text "" :summary ""}
   :libgcrypt {:download-location "" :copyright-text "" :summary ""}
   :libgpg-error {:download-location "" :copyright-text "" :summary ""}
   :libice {:download-location "" :copyright-text "" :summary ""}
   :lib-libmnl {:download-location "" :copyright-text "" :summary ""}
   :libol {:download-location "" :copyright-text "" :summary ""}
   :libpthread-stubs {:download-location "" :copyright-text "" :summary ""}
   :libsm {:download-location "" :copyright-text "" :summary ""}
   :libtasn1 {:download-location "" :copyright-text "" :summary ""}
   :libtool-cross {:download-location "" :copyright-text "" :summary ""}
   :libtool {:download-location "" :copyright-text "" :summary ""}
   :liburcu {:download-location "" :copyright-text "" :summary ""}
   :libusb1 {:download-location "" :copyright-text "" :summary ""}
   :libusb-compat {:download-location "" :copyright-text "" :summary ""}
   :libx11 {:download-location "" :copyright-text "" :summary ""}
   :libxau {:download-location "" :copyright-text "" :summary ""}
   :libxcb {:download-location "" :copyright-text "" :summary ""}
   :libxdmcp {:download-location "" :copyright-text "" :summary ""}
   :libxext {:download-location "" :copyright-text "" :summary ""}
   :libxinerama {:download-location "" :copyright-text "" :summary ""}
   :libxi {:download-location "" :copyright-text "" :summary ""}
   :libxml2 {:download-location "" :copyright-text "" :summary ""}
   :libxrender {:download-location "" :copyright-text "" :summary ""}
   :libxt {:download-location "" :copyright-text "" :summary ""}
   :libxtst {:download-location "" :copyright-text "" :summary ""}
   :linphone {:download-location "http://linphone.org/download"
              :copyright-text "GPLv2"
              :summary "This is Linphone, a free (GPL) video softphone based on the SIP protocol."}
   :linux-libc-headers {:download-location "" :copyright-text "" :summary ""}
   :linux-quic-git-f21d9f99c5989e009d532f01231a807068483f07-r3 {:download-location "" :copyright-text "" :summary ""}
   :lk-git-r3 {:download-location "" :copyright-text "" :summary ""}
   :logrotate {:download-location "" :copyright-text "" :summary ""}
   :lttng-ust {:download-location "" :copyright-text "" :summary ""}
   :lzo {:download-location "" :copyright-text "" :summary ""}
   :mediastreamer2 {:download-location "" :copyright-text "" :summary ""}
   :minicaller {:download-location "" :copyright-text "" :summary ""}
   :ncurses {:download-location "" :copyright-text "" :summary ""}
   :netbase {:download-location "" :copyright-text "" :summary ""}
   :openjdk-7-jre-25b30 {:download-location "" :copyright-text "" :summary ""}
   :opkg {:download-location "" :copyright-text "" :summary ""}
   :ortp {:download-location "" :copyright-text "" :summary ""}
   :osip4 {:download-location "" :copyright-text "" :summary ""}
   :osip {:download-location "" :copyright-text "" :summary ""}
   :pango {:download-location "" :copyright-text "" :summary ""}
   :perl {:download-location "" :copyright-text "" :summary ""}
   :pixman {:download-location "" :copyright-text "" :summary ""}
   :popt {:download-location "" :copyright-text "" :summary ""}
   :readline {:download-location "" :copyright-text "" :summary ""}
   :recordproto {:download-location "" :copyright-text "" :summary ""}
   :renderproto {:download-location "" :copyright-text "" :summary ""}
   :run-postinsts {:download-location "" :copyright-text "" :summary ""}
   :shared-mime-info {:download-location "" :copyright-text "" :summary ""}
   :sipgateway {:download-location "" :copyright-text "" :summary ""}
   :socat {:download-location "" :copyright-text "" :summary ""}
   :syslog-ng {:download-location "" :copyright-text "" :summary ""}
   :sysvinit-inittab {:download-location "" :copyright-text "" :summary ""}
   :sysvinit {:download-location "" :copyright-text "" :summary ""}
   :tinylogin {:download-location "" :copyright-text "" :summary ""}
   :useradd-peiker {:download-location "" :copyright-text "" :summary ""}
   :util-linux {:download-location "" :copyright-text "" :summary ""}
   :util-macros {:download-location "" :copyright-text "" :summary ""}
   :wireless-tools-1_29-r2 {:download-location "" :copyright-text "" :summary ""}
   :xcb-proto {:download-location "" :copyright-text "" :summary ""}
   :xcmiscproto {:download-location "" :copyright-text "" :summary ""}
   :xextproto {:download-location "" :copyright-text "" :summary ""}
   :xf86bigfontproto {:download-location "" :copyright-text "" :summary ""}
   :xineramaproto {:download-location "" :copyright-text "" :summary ""}
   :xproto {:download-location "" :copyright-text "" :summary ""}
   :xtrans {:download-location "" :copyright-text "" :summary ""}
   })


(defn pkg-add-on-info
  "provide additional meta information for given
   open source software package"
  [package-name]
  ((keyword package-name) pkg-add-on-info-data ))



