# SPDX Generator and Source Code Extractor for Open Embedded / Poky

The  SPDX  generator  software  allows  the  automatic  extraction  of
open  source  software  packages   together  with  the  generation  of
corresponding  SPDX meta  data for  the Yocto/Poky  Linux distribution
[http://yoctoproject.org](http://yoctoproject.org).

> "The  Software  Package  Data  Exchange (SPDX®)  specification  is  a
standard  format  for  communicating  the  components,  licenses,  and
copyrights  associated  with  a  software package.  An  SPDX  file  is
associated with a particular software package and contains information
about that package in the SPDX format.", from [http://spdx.org](http://spdx.org).

Instead of  manipulating class  or implementation  files of  the Yocto
build  system bitbake,  the present  spdx generator  software runs  as
standalone application and takes advantage out of the fact that Yoctos
archive  class  already  structures  all  used  open  source  software
packages  as directory  hierachy  with the  various  license types  as
top  level directory.  For more  detailed information  about the  used
directory structure refer to the
[Yocto Project Development Manual](http://www.yoctoproject.org/docs/1.6/dev-manual/dev-manual.html#maintaining-open-source-license-compliance-during-your-products-lifecycle).


The software is written in the [Clojure programming language](http://clojure.org) which requires
a Java virtual machine and some additional libraries for operation.


### Build

You need  the clojure build  tool leinignen for  compilation. Download
the lein script file from Github

    $ cd ~/bin
    $ wget http://github.com/technomancy/leiningen/raw/stable/bin/lein
    $ chmod +x lein

and type

    $ lein self-install

The following commands will generate and stand-alone jar file:

    $ lein uberjar

Refer also to [Zef's Leiningen page](http://zef.me/2470/building-clojure-projects-with-leiningen) for more specific information about build options.


### Invocation
The following  command will start the spdx generation

    $ java -jar pub-oss-<xyz>-standalone.jar [-oe oe-main-dir] [--source-pkg sources-dir] [--pub pub-dir]
          [--blacklist package-blacklist-file] [--whitelist packages-whitelist-file]

with the following optional arguments:

* oe-main-dir: top level directory of Yocto/Poky release, usually "oe-core"
* source dir: input top level directory, provides subdirectory for each OS license
* pubdir: directory or url where open source archive files and sdpx meta data shall be copied to
* package-blacklist-file file with list of packes not to integrate
* package-whitelist-file file with list of packes exclusively to integrate

## Licence
This software stands under the terms of the
[GNU General Public Licence](http://www.gnu.org/licenses/gpl.html).

March 2014, Otto Linnemann

## Resources and links
Thanks to all the giants whose shoulders we stand on. And the giants theses giants stand on...
And special thanks to Rich Hickey (and the team) for Clojure. Really, thanks!

* Clojure: http://clojure.org
* Leiningen: https://github.com/technomancy/leiningen
