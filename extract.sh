#!/bin/sh
# generate blacklist (packes which are not part of the image and
# export SPDX and open source archive for publishing
# 2014-06-23 @peiker/ol

# create whitelist

IMAGE="9615-cdp-ltenad"
WHITELIST="whitelist"

# These packages come with inconsistent naming which is the reason that
# they are not assigned to native build packages. Because of licensing
# issues (GPLv3) we have to exclude them explicitly
EXLUDE_PACKAGE_REGEXP_LST="bash\|binutils-cross\|binutils\|coreutils\|gdbm\|gmp\|gnutls\|libtasn1"

GPLv2_SOURCE_DIR=tmp-eglibc/deploy/sources/arm-oe-linux-gnueabi/GPLv2

PUBOSSDIR=$(pwd)
cd ../../apps_proc/oe-core
source build/conf/set_bb_env.sh
bitbake -g $IMAGE
cat pn-depends.dot | grep -v -e '-native' | grep -v digraph | grep -v $IMAGE | grep -v -e '}' | \
    grep -v -e $EXLUDE_PACKAGE_REGEXP_LST | \
    awk '{print gensub("\"","","g",$1)}' | sort | uniq > $PUBOSSDIR/$WHITELIST

# copy kernel sources to avoid delivery of symbolic link
cd $GPLv2_SOURCE_DIR
cd linux-quic-git-*

if [ -f symlinks_resolved ];
then
    echo "symbolic link for kernel sources already resolved"
else
    echo "resolve symbolic link for kernel sources by repacking"
    tar xvfz linux-quic-git*.tar.gz
    tar cvfhz linux-quic.tar.gz --exclude '.git/*' linux-quic
    rm linux-quic-git*.tar.gz
    touch symlinks_resolved
fi

# publish source archive files, patches and SPDX containers
cd $PUBOSSDIR
java -jar target/pub-oss-1.0.0-SNAPSHOT-standalone.jar --whitelist $WHITELIST

