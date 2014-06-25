#!/bin/sh
# scan current directory for spdx files with GPL3 license terms
# GPL3 is usually not allowed for commercial projects because
# it requires to grant users access to the hardware

for pdxfilename in *.spdx
do
    if grep -e "GPL3\|GPLv3\|LGPL3\|LGPLv3\|GPL-3\|LGPL-3" $pdxfilename > /dev/null
    then
        echo $pdxfilename " is under the terms of GPLv3"
    fi
done
