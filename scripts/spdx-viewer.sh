#!/bin/sh
# invoke spdx viewer application for all files in local
# directory.

SPDXJARFILE=$HOME/bin/spdx-tools-1.2.5-jar-with-dependencies.jar
OUTFILE=spdx_viewer_output.txt
ERRFILE=spdx_viewer_errors.txt

rm -rf $OUTFILE $ERRFILE
echo "SPDX Viewer Output generated on $(date)" > $OUTFILE
echo "=============================================================" >> $OUTFILE
echo "List of Errors of SPX Viewer Invocation, generated on $(date)" > $ERRFILE
echo "===================================================================================" >> $ERRFILE

for pdxfilename in *.spdx
do
    echo "generating spdx viewer output for file $pdxfilename ..."
    java -jar $SPDXJARFILE SPDXViewer $pdxfilename >> $OUTFILE 2>> $ERRFILE
done