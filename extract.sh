#!/bin/sh
# generate blacklist (packes which are not part of the image and
# export SPDX and open source archive for publishing
# 2014-03-20 @peiker/ol

# create whitelist
IMAGE="9615-cdp-ltenad"
WHITELIST="whitelist"

PUBOSSDIR=$(pwd)
cd ../../apps_proc/oe-core
source build/conf/set_bb_env.sh
bitbake -g $IMAGE
cat pn-depends.dot | grep -v -e '-native' | grep -v digraph | grep -v $IMAGE | grep -v -e '}' |awk '{print gensub("\"","","g",$1)}' | sort | uniq > $PUBOSSDIR/$WHITELIST
cd $PUBOSSDIR

# publish source archive files, patches and SPDX containers
java -jar target/pub-oss-1.0.0-SNAPSHOT-standalone.jar --whitelist $WHITELIST

