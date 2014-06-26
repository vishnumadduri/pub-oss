; additional meta information for common open source packages
;
; by Otto Linnemann
; (C) 2014, GNU General Public Licence

(ns pub-oss.package-addon-info
  (:use [utils.gen-utils]))

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
   :binutils-cross {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv3" :summary "The Binutils package contains a linker, an assembler, and other tools for handling object files."}
   :binutils {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv3" :summary "A GNU collection of binary utilities"}

   :busybox {:download-location "http://www.busybox.net/downloads/busybox-1.19.3.tar.bz2" :copyright-text "GPLv2" :summary "Tiny versions of many common UNIX utilities in a single small executable."}
   :cairo {:download-location "http://cairographics.org/download"
           :copyright-text "Copyright 1999 Tom Tromey&#xD;Copyright 2002, 2003 University of Southern California, Information&#xD;  Sciences Institute  (ISI)&#xD;Copyright 2000, 2002, 2004, 2005 Keith Packard&#xD;Copyright 2004 Calum Robinson&#xD;Copyright 2004 Richard D. Worth&#xD;Copyright 2004, 2005 Red Hat, Inc.&#xD;Copyright 2004 David Reveman"
           :summary "Cairo is a 2D graphics library with support for multiple output devices. Currently supported output targets include the X Window System, Quartz, Win32, image buffers, PostScript, PDF, and SVG file"}
   :coreutils {:download-location "http://www.gnu.org/software/coreutils" :copyright-text "GPLv3+" :summary "The GNU Core Utilities provide the basic file, shell and text manipulation utilities. These are the core utilities which are expected to exist on every system."}
   :cups {:download-location "ftp://ftp.easysw.com/pub/cups/14/cups-14-source.tar.bz2" :copyright-text "GPLv2 LGPLv2" :summary "An Internet printing system for Unix"}
   :dbus-glib {:download-location "http://www.freedesktop.org/Software/dbus" :copyright-text "AFL-2 | GPLv2+" :summary "High level language (GLib) binding for D-Bus"}
   :dbus {:download-location "http://dbus.freedesktop.org/releases/dbus/dbus-1.4.16.tar.gz" :copyright-text "Academic Free
License version 2.1, or the GNU General Public License version 2" :summary "D-Bus message bus"}
   :dnsmasq {:download-location "http://www.thekelleys.org.uk/dnsmasq/dnsmasq-2.55.tar.gz" :copyright-text "GPL" :summary "Dnsmasq is a lightweight, easy to configure DNS forwarder and DHCP server."}
   :e2fsprogs {:download-location "http://e2fsprogs.sourceforge.net" :copyright-text "GPLv2 & LGPLv2 & BSD & MIT" :summary "Ext2 Filesystem Utilities"}
   :eglibc-initial {:download-location "http://www.eglibc.org/home" :copyright-text "GPLv2 & LGPLv2.1" :summary "Embedded GLIBC (GNU C Library)"}
   :eglibc {:download-location "http://www.eglibc.org/home" :copyright-text "GPLv2 & LGPLv2.1" :summary "Embedded GLIBC (GNU C Library)"}
   :elfutils {:download-location "https://fedorahosted.org/elfutils" :copyright-text "(GPL-2+ & Elfutils-Exception)" :summary "A collection of utilities and DSOs to handle compiled objects."}
   :exosip4 {:download-location "http://www.linphone.org" :copyright-text "LGPLv2" :summary "exoSIP, the eXtended osip library"}
   :exosip {:download-location "http://www.linphone.org" :copyright-text "LGPLv2" :summary "exoSIP, the eXtended osip library"}
   :expat {:download-location "http://expat.sourceforge.net" :copyright-text "MIT" :summary "A stream-oriented XML parser library."}
   :fastjar {:download-location "http://savannah.nongnu.org/projects/fastjar" :copyright-text "GPLv2" :summary "jar replacement written in C."}
   :fontconfig {:download-location "http://www.fontconfig.org" :copyright-text "MIT-style & MIT & PD" :summary "Generic font configuration library"}
   :freetype {:download-location "http://www.freetype.org" :copyright-text "FreeType | GPLv2+" :summary "Freetype font rendering library"}
   :gdbm {:download-location "http://www.gnu.org/software/gdbm" :copyright-text "GPLv3" :summary "GNU dbm is a set of database routines that use extensible hashing."}
   :gdb {:download-location "http://www.gnu.org/software/gdb" :copyright-text "GPLv3+" :summary "gdb - GNU debugger"}
   :gdk-pixbuf {:download-location "http://ftp.acc.umu.se/pub/GNOME/sources/gdk-pixbuf/2.24/gdk-pixbuf-2.24.0.tar.bz2" :copyright-text "LGPLv2" :summary "Image loading library for GTK+"}
   :gettext {:download-location "http://www.gnu.org/software/gettext/gettext.html" :copyright-text "GPL-3+ & LGPL-2.1+" :summary "Utilities and libraries for producing multi-lingual messages."}
   :giflib {:download-location "http://sourceforge.net/projects/giflib" :copyright-text "MIT" :summary "shared library for GIF images"}
   :glib {:download-location "http://ftp.gnome.org/pub/gnome/sources/glib/2.32/glib-2.32.4.tar.xz" :copyright-text "LGPLv2+ & BSD & PD" :summary "A general-purpose utility library"}
   :gmp {:download-location "http://www.swox.com/gmp" :copyright-text "LGPLv3&GPLv3" :summary "GNU multiprecision arithmetic library"}
   :gnujaf {:download-location "http://ftp.gnu.org/gnu/classpathx/activation-1.1.1.tar.gz" :copyright-text "GPL-2.0-with-GCC-exception" :summary "Provides a mean to type data and locate components suitable for performing various kinds of action on it."}
   :gnumail {:download-location "http://ftp.gnu.org/gnu/classpathx/mail-1.1.2.tar.gz" :copyright-text "GPL-2.0-with-GCC-exception" :summary "GNU's free implementation of the JavaMail API specification"}
   :gnutls {:download-location "ftp://ftp.gnutls.org/pub/gnutls/gnutls-2.12.14.tar.bz2" :copyright-text "GPLv3+ & LGPLv2.1+" :summary "GNU Transport Layer Security Library"}
   :gtk+ {:download-location "http://download.gnome.org/sources/gtk+/2.24/gtk+-2.24.8.tar.bz2" :copyright-text "LGPLv2 & LGPLv2+ & LGPLv2.1+" :summary "Multi-platform toolkit for creating GUIs"}
   :hostap-git-r3 {:download-location "http://hostap.epitest.fi/releases/hostap-utils-0.2.0.tar.gz" :copyright-text "Copyright (c) 2002-2012, Jouni Malinen <j@w1.fi> and contributors" :summary "wpa_supplicant and hostapd"}
   :inetlib {:download-location "ftp://ftp.gnu.org/gnu/classpath/inetlib-1.1.1.tar.gz" :copyright-text "GPL-2.0-with-GCC-exception" :summary "A Java library of clients for common internet protocols"}
   :initscripts {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv2" :summary "SysV init scripts"}
   :inputproto {:download-location "http://www.x.org" :copyright-text "MIT-X" :summary "X protocol headers: 2.0.2"}
   :iproute2 {:download-location "http://www.linuxfoundation.org/collaborate/workgroups/networking/iproute2" :copyright-text "GPLv2+" :summary "TCP / IP networking and traffic control utilities"}
   :iptables {:download-location "http://netfilter.org/projects/iptables/files/iptables-1.4.12.2.tar.bz2" :copyright-text "GPLv2+" :summary "Tools for managing kernel packaet filtering capabilities"}
   :kbd {:download-location "ftp://ftp.kernel.org/pub/linux/utils/kbd/kbd-1.15.2.tar.bz2" :copyright-text "GPLv2+" :summary "This package contains keytable files and keyboard utilities"}
   :kbproto {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XKB: X Keyboard extension headers"}
   :keymaps {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv2" :summary "Keyboard maps"}
   :libasrc {:download-location proj-download-location :copyright-text "GPLv2" :summary "asynchronous sample rate conversion of audio data"}
   :libcap {:download-location "ftp://ftp.kernel.org/pub/linux/libs/security/linux-privs/libcap2/libcap-2.22.tar.bz2" :copyright-text "BSD | GPLv2" :summary "support for getting/setting POSIX.1e capabilities"}
   :libffi {:download-location "ftp://sourceware.org/pub/libffi/libffi-3.0.10.tar.gz" :copyright-text "MIT" :summary "A portable foreign function interface library"}
   :libgcrypt {:download-location "ftp://ftp.gnupg.org/gcrypt/libgcrypt/libgcrypt-1.5.0.tar.gz" :copyright-text "GPLv2+ & LGPLv2.1+" :summary "A general purpose cryptographic library based on the code from GnuPG"}
   :libgpg-error {:download-location "ftp://ftp.gnupg.org/gcrypt/libgpg-error/libgpg-error-1.10.tar.bz2" :copyright-text "GPLv2+ & LGPLv2.1+" :summary "a small library that defines common error values for all GnuPG components"}
   :libice {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "ICE: Inter-Client Exchange library"}
   :lib-libmnl {:download-location "git://git.netfilter.org/libmnl" :copyright-text "LGPLv2.1+" :summary "Minimalistic user-space library oriented to Netlink developers"}
   :libol {:download-location "http://www.balabit.com/downloads/files/libol/0.3/libol-0.3.18.tar.gz" :copyright-text "GPLv2" :summary "A tiny C support library"}
   :libpthread-stubs {:download-location "http://xcb.freedesktop.org/dist/libpthread-stubs-0.3.tar.bz2" :copyright-text "MIT" :summary "Library that provides weak aliases for pthread functions"}
   :libsm {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "SM: Session Management library"}
   :libtasn1 {:download-location "ftp://ftp.gnu.org/gnu/libtasn1/libtasn1-2.11.tar.gz" :copyright-text "GPLv3+ & LGPLv2.1+" :summary "Library for ASN.1 and DER manipulation"}
   :libtool-cross {:download-location "ftp://ftp.gnupg.org/libtool/libtool-2.4.2.tar.gz" :copyright-text "GPLv2 & LGPLv2.1" :summary "Generic library support script"}
   :libtool {:download-location "ftp://ftp.gnupg.org/libtool/libtool-2.4.2.tar.gz" :copyright-text "GPLv2 & LGPLv2.1" :summary "Generic library support script"}
   :liburcu {:download-location "http://lttng.org/files/urcu/userspace-rcu-0.6.7.tar.bz2" :copyright-text "LGPLv2.1+ & MIT-style" :summary "The userspace read-copy update library by Mathieu Desnoyers"}
   :libusb1 {:download-location "http://sourceforge.net/projects/libusb" :copyright-text "LGPLv2.1+" :summary "Userspace library to access USB (version 1.0)"}
   :libusb-compat {:download-location "http://sourceforge.net/projects/libusb" :copyright-text "LGPLv2.1+" :summary "libusb-0.1 compatible layer for libusb1, a drop-in replacement that aims to look, feel and behave exactly like libusb-0.1"}
   :libx11 {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style & BSD" :summary "Xlib: C Language X Interface library"}
   :libxau {:download-location "http://www.x.org" :copyright-text "MIT-X" :summary "Xau: X Authority Database library"}
   :libxcb {:download-location "http://xcb.freedesktop.org/dist/libxcb-1.8.tar.bz2" :copyright-text "MIT-X" :summary "XCB: The X protocol C binding library"}
   :libxdmcp {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XDMCP: X Display Manager Control Protocol library"}
   :libxext {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XExt: X Extension library"}
   :libxinerama {:download-location "http://www.x.org" :copyright-text "MIT" :summary "Xinerama: Xinerama extension library"}
   :libxi {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "XI: X Input extension library"}
   :libxml2 {:download-location "ftp://xmlsoft.org/libxml2/libxml2-2.7.8.tar.gz" :copyright-text "MIT" :summary "XML C Parser Library and Toolkit"}
   :libxrender {:download-location "git://anongit.freedesktop.org/xorg/lib/libXrender" :copyright-text "MIT-style" :summary "XRender: X Rendering Extension library"}
   :libxt {:download-location "http://www.x.org" :copyright-text "MIT" :summary "X Toolkit Intrinsics library"}
   :libxtst {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XTest: X Test extension library"}
   :linphone {:download-location "http://linphone.org/download"
              :copyright-text "GPLv2"
              :summary "This is Linphone, a free (GPL) video softphone based on the SIP protocol."}
   :linux-libc-headers {:download-location "ftp://ftp.kernel.org/pub/linux/kernel/v3.1/linux-3.1.tar.bz2" :copyright-text "GPLv2" :summary "Sanitized set of kernel headers for the C library's use."}
   :linux-quic {:download-location "ftp://ftp.kernel.org" :copyright-text "GPLv2" :summary "QuIC Linux Kernel"}
   :lk-git-r3 {:download-location "https://www.codeaurora.org/gitweb/quic/la?p=kernel/lk.git" :copyright-text "MIT" :summary "Little Kernel bootloader"}
   :logrotate {:download-location "https://fedorahosted.org/releases/l/o/logrotate/logrotate-3.7.9.tar.gz" :copyright-text "GPLv2" :summary "Rotates, compresses, removes and mails system log files"}
   :lttng-ust {:download-location "git://git.lttng.org/lttng-ust.git;protocol=git" :copyright-text "LGPLv2.1+ & BSD" :summary "Linux Trace Toolkit Userspace Tracer 2.0"}
   :lzo {:download-location "http://www.lzop.org/download/lzop-1.03.tar.gz" :copyright-text "GPLv2+" :summary "Real-time file compressor"}
   :mediastreamer2 {:download-location "http://www.linphone.org" :copyright-text "LGPLv2" :summary "Mediastreamer2 - the multimedia streaming engine"}
   :minicaller {:download-location proj-download-location :copyright-text "GPLv2" :summary "A primitive SIP gateway from IP to PSTN"}
   :ncurses {:download-location "ftp://ftp.gnupg.org/ncurses/ncurses-5.9.tar.gz" :copyright-text "MIT" :summary "The New Curses library"}
   :netbase {:download-location "ftp://ftp.debian.org/debian/pool/main/n/netbase/netbase_4.47.tar.gz" :copyright-text "GPLv2" :summary "Basic TCP/IP networking support"}
   :openjdk-7-jre-25b30 {:download-location "http://icedtea.classpath.org" :copyright-text "GPL-2.0-with-classpath-exception" :summary "Java runtime based upon the OpenJDK- and Icedtea Project"}
   :opkg {:download-location "http://opkg.googlecode.com/files/opkg-0.1.8.tar.gz" :copyright-text "GPLv2+" :summary "Open Package Manager"}
   :ortp {:download-location "http://www.linphone.org" :copyright-text "LGPLv2" :summary "oRTP, a Real-time Transport Protocol (RTP, RFC3550) library"}
   :osip4 {:download-location "http://ftp.gnu.org/osip" :copyright-text "LGPLv2" :summary "oSIP, a LGPL implementation of SIP according to RFC3261"}
   :osip {:download-location "http://ftp.gnu.org/osip" :copyright-text "LGPLv2" :summary "oSIP, a LGPL implementation of SIP according to RFC3261"}
   :pango {:download-location "http://www.pango.org" :copyright-text "LGPLv2.0+" :summary "Framework for layout and rendering of internationalized text"}
   :perl {:download-location "http://www.cpan.org/src/5.0/perl-5.14.2.tar.gz" :copyright-text "Artistic|GPL" :summary "Perl is a popular scripting language."}
   :pixman {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style & PD" :summary "Pixman: Pixel Manipulation library"}
   :popt {:download-location "http://rpm5.org/files/popt/popt-1.16.tar.gz" :copyright-text "MIT" :summary "The popt library for parsing command line options."}
   :readline {:download-location "http://ftp.gnu.org/readline/readline-6.2.tar.gz" :copyright-text "GPLv3+" :summary "The GNU Readline library provides a set of functions for use by applications that allow users to edit command lines as they are typed in. Both Emacs and vi editing modes are available. The Readline library includes additional functions to maintain a list of previously-entered command lines, to recall and perhaps reedit those lines, and perform csh-like history expansion on previous commands."}
   :recordproto {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XRecord: X Record extension headers"}
   :renderproto {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "X.org RenderProto protocol headers."}
   :run-postinsts {:download-location "ftp://ftp.debian.org/debian/pool/main/d/dpkg/dpkg_1.15.8.7.tar.bz2" :copyright-text "GPLv2.0+" :summary "Package maintenance system for Debian."}
   :shared-mime-info {:download-location "http://freedesktop.org/~hadess/shared-mime-info-0.91.tar.xz" :copyright-text "LGPLv2+" :summary "shared MIME database and spec"}
   :sipgateway {:download-location proj-download-location :copyright-text "GPLv2" :summary "A primitive SIP gateway from IP to PSTN"}
   :socat {:download-location "http://www.dest-unreach.org/socat/download/socat-1.7.2.0.tar.bz2;name=src" :copyright-text "GPL-2.0+-with-OpenSSL-exception" :summary "Socat is a relay for bidirectional data transfer between two independent data channels."}
   :syslog-ng {:download-location "http://www.balabit.com/downloads/files/syslog-ng/sources/3.4.7/source/syslog-ng_3.4.7.tar.gz" :copyright-text "GPL LGPL" :summary "Alternative system logger daemon"}
   :sysvinit-inittab {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "GPLv2" :summary "Inittab for sysvinit"}
   :sysvinit {:download-location "http://download.savannah.gnu.org/releases-noredirect/sysvinit/sysvinit-2.88dsf.tar.bz2" :copyright-text "GPLv2+" :summary "System-V like init."}
   :tinylogin {:download-location "GPLv2" :copyright-text "http://www.angstrom-distribution.org/unstable/sources/tinylogin-1.4.tar.bz2" :summary "Tiny versions of many common login, authentication and related utilities."}
   :useradd-peiker {:download-location proj-download-location :copyright-text "MIT" :summary "Example recipe for using inherit useradd"}
   :util-linux {:download-location "ftp://ftp.kernel.org/pub/linux/utils/util-linux/v${MAJOR_VERSION}/util-linux-${PV}.tar.bz2" :copyright-text "GPLv2+ & LGPLv2.1+ & BSD" :summary "A suite of basic system administration utilities."}
   :util-macros {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "X autotools macros"}
   :wireless-tools-1_29-r2 {:download-location "http://www.hpl.hp.com/personal/Jean_Tourrilhes/Linux/wireless_tools.29.tar.gz" :copyright-text "GPLv2 & (LGPL | MPL | BSD)" :summary "Tools for the Linux Standard Wireless Extension Subsystem"}
   :xcb-proto {:download-location "http://xcb.freedesktop.org/dist/xcb-proto-1.7.tar.bz2" :copyright-text "MIT-X" :summary "XCB: The X protocol C binding headers"}
   :xcmiscproto {:download-location "http://www.x.org" :copyright-text "MIT-style" :summary "XC-MISC: X XC-Miscellaneous extension headers"}
   :xextproto {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "XExt: X Extension headers"}
   :xf86bigfontproto {:download-location "http://www.x.org" :copyright-text "MIT" :summary "XFree86-BIGFONT: XFree86 Big Font extension headers"}
   :xineramaproto {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "Xinerama: Xinerama extension headers"}
   :xproto {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "Xlib: C Language X interface headers"}
   :xtrans {:download-location "http://www.x.org" :copyright-text "MIT & MIT-style" :summary "XTrans: X Transport library"}
   :shadow {:download-location "http://pkg-shadow.alioth.debian.org/releases/shadow-4.1.4.3.tar.bz2" :copyright-text "BSD | Artistic" :summary "Tools to change and administer password and group data"}
   :liblinenoise {:download-location "http://github.com/antirez/linenoise" :copyright-text "BSD" :summary "A minimal, zero-config, BSD licensed, readline replacement used in Redis, MongoDB, and Android."}
   :zip {:download-location "ftp://ftp.info-zip.org/pub/infozip/src/zip30.tgz" :copyright-text "BSD-3-Clause" :summary "Archiver for .zip files"}
   :shadow-sysroot {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "BSD | Artistic" :summary "Shadow utils requirements for useradd.bbclass"}
   :openssl {:download-location "http://www.openssl.org/source/openssl-1.0.0f.tar.gz" :copyright-text "openssl" :summary "Secure Socket Layer"}
   :libpcap {:download-location "http://www.tcpdump.org/release/libpcap-1.2.0.tar.gz" :copyright-text "BSD" :summary "Interface for user-level network packet capture"}
   :open-source-kernel-tests-git-r2 {:download-location "http://git.openembedded.org/openembedded-core" :copyright-text "BSD" :summary "Open Source kernel tests"}
   :cronie {:download-location "https://fedorahosted.org/releases/c/r/cronie/cronie-1.4.8.tar.gz" :copyright-text "ISC & BSD" :summary "Cron daemon for executing programs at set times"}
   :eventlog {:download-location "http://www.balabit.com/downloads/files/syslog-ng/sources/3.2.2/source/eventlog_0.2.12.tar.gz" :copyright-text "BSD" :summary "Replacement syslog API"}
   :reboot-daemon {:download-location "none" :copyright-text "BSD" :summary "Rebooter daemon"}
   :lib-ril {:download-location "https://android.googlesource.com/platform/hardware/ril" :copyright-text "Apache" :summary "Android RIL library"}
   :system-core-git-r6 {:download-location "https://android.googlesource.com/platform/system/core" :copyright-text "Apache-2.0" :summary "Android system/core components"}
   :powerapp-git-r5 {:download-location "https://android.googlesource.com/platform/hardware/qcom/power" :copyright-text "Apache-2.0" :summary "Powerapp tools"}
   :rild {:download-location "https://android.googlesource.com/platform/hardware/ril" :copyright-text "Apache" :summary "Android RIL daemon"}
   :ril-include {:download-location "https://android.googlesource.com/platform/hardware/ril" :copyright-text "Apache" :summary "Android RIL library"}
   :dhcpcd {:download-location "http://roy.aydogan.net/dhcpcd/dhcpcd-5.2.10.tar.bz2" :copyright-text "BSD" :summary "dhcpcd is an RFC2131-, RFC2132-, and RFC1541-compliant DHCP client daemon. It gets an IP address and other information from the DHCP server, automatically configures the network interface, and tries to renew the lease time according to RFC2131 or RFC1541."}
   :loc-api-test {:download-location proj-download-location :copyright-text "BSD" :summary "GPS Location API Test"}
   :strace {:download-location "http://sourceforge.net/projects/strace" :copyright-text "BSD" :summary "strace is a system call tracing tool."}
   :bzip2 {:download-location "http://www.bzip.org/1.0.6/bzip2-1.0.6.tar.gz" :copyright-text "BSD-4-Clause" :summary "Very high-quality data compression program."}
   :tcpdump {:download-location "http://www.tcpdump.org/release/tcpdump-4.1.1.tar.gz" :copyright-text "BSD" :summary "A sophisticated network protocol analyzer"}
   :pimd-git-r1 {:download-location "git://github.com/troglobit/pimd.git;protocol=git" :copyright-text "BSD" :summary "PIMD - Multicast Routing Daemon"}
   :ocf-linux-20100325-r3 {:download-location "http://sourceforge.net/projects/ocf-linux/files/ocf-linux" :copyright-text "BSD" :summary "Install required headers to enable OCF Linux support"}
   :flex {:download-location "http://sourceforge.net/projects/flex" :copyright-text "BSD" :summary "Flex (The Fast Lexical Analyzer)"}
   :ril-java {:download-location proj-download-location :copyright-text "AL2.0" :summary "RIL Java library"}
   :speex {:download-location "http://downloads.us.xiph.org/releases/speex/speex-1.2rc1.tar.gz" :copyright-text "BSD" :summary "Speech Audio Codec"}
   :loc-api-git-r3 {:download-location "none" :copyright-text "BSD" :summary "GPS Location API"}
   :libhardware-git-r4 {:download-location "https://www.codeaurora.org/cgit/quic/la/platform/hardware/libhardware" :copyright-text "Apache-2.0" :summary "hardware libhardware headers"}
   :alsa-intf-git-r3 {:download-location "https://www.codeaurora.org/cgit/quic/la/platform/vendor/qcom-opensource/omx/mm-audio" :copyright-text "Apache-2.0" :summary "ALSA Framework Library"}
   :jpeg-8c-r3 {:download-location "http://www.ijg.org/files/jpegsrc.v8c.tar.gz" :copyright-text "BSD-3-Clause" :summary "libjpeg is a library for handling the JPEG (JFIF) image format."}
   :libogg {:download-location "http://downloads.xiph.org/releases/ogg/libogg-1.3.0.tar.gz" :copyright-text "BSD" :summary "Ogg bitstream and framing libary"}

   })


(defn pkg-add-on-info
  "provide additional meta information for given
   open source software package"
  [package-name]
  (let [package-name (if (initstring? package-name "linux-quic") "linux-quic" package-name)]
    ((keyword package-name) pkg-add-on-info-data )))


