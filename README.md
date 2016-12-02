# SPDX Generator and Source Code Extractor for Open Embedded / Poky

The SPDX  generator software allows  the automatic extraction  of open
source software packages together with the generation of corresponding
SPDX meta data for the OpenEmbedded based Linux distributions
[http://openembedded.org](http://openembedde.org).

> "The  Software  Package  Data  Exchange (SPDX®)  specification  is a
standard  format  for  communicating  the  components,  licenses,  and
copyrights  associated  with  a  software package.  An  SPDX  file  is
associated with a particular software package and contains information
about that package in the SPDX format.", from [http://spdx.org](http://spdx.org).

### Motivation

While recent versions of the OpenEmbedded/Yocto build system come with
extensions for  the generation of  SPDX files, these cannot  always be
easily used since many OE distributions  are either too old or include
extensions  to  the build  system  which  are simply  incompatible  to
bitbake's SPDX  generator class. Addressing  these issues is  an quite
complex task and usually requires in-depth knowledge of the underlying
bitbake respecively  OpenEmbedded build  system. Bitbake itself  is in
the range of 30.000 lines of  Python code and so being able to understand
and to debug just its core functionality is rather difficult.

It seemed to be much easier - at least at first - to implement the XML
generator from  scratch and simply use  OpenEmbedded's build artefacts
as input for it. This approach basically worked well until we received
another  software  release from  a  SoC  vendor  which came  with  new
extensions  to  the OpenEmedded  build  system  that even  broke  some
of  bitbake's base  class  functionality used  for  the extraction  of
fundamental  package license  data and  and associcated  source codes.
This in turn  enforced us to significantly extend  the existing simple
approach. It  basically enforced us  to reimplement some  of bitbake's
recipe parser functionality,  resulting in a larger,  more complex and
possibly undependable code base. So please use it with care and double
check the results!

The benefit  of the new  approach is  that the present  SPDX generator
utility is now with its second major iteration widely independent from
bitbake's python code base. A successfully compiled OpenEmbedded based
Linux distribution shall  be everything that is needed to  use it. But
there  do exist  some restrictions  due to  the layout  of the  recipe
files.  In example  the parser  is not  capable of  evaluating complex
Python expressions  used in some  dependency or source  URI definition
variables. Luckily most  of the open source recipes files  do not rely
on this or appropriate workarounds have been already introduced by us.
But where  possible it might  be more safe to  fall back to  the first
implementation which is based on bitbakes archiver class. Refer to the
branch 'version-1' of this repository for this.

### Requirements

The  present  tool  itself  is written  in  the  [Clojure  programming
language](http://clojure.org)  which requires  a Java  virtual machine
and some additional libraries for operation.

Furthermore it can be exclusively used  on Unix hosts since it envokes
some system tools  directly e.g. 'cp', 'tar' and 'git'  and thus it is
not '100%  pure Java'. We  generally recommend  to run pub-oss  on the
same  machine used  for the  actual build  which has  to be  completed
successfully first.


### Compiling pub-oss

You need  the clojure build  tool leinignen for  compilation. Download
the lein script file from Github

    $ cd ~/bin
    $ wget http://github.com/technomancy/leiningen/raw/stable/bin/lein
    $ chmod +x lein

and type

    $ lein self-install

The following commands will generate and stand-alone jar file:

    $ lein uberjar

Refer also to [Zef's Leiningen page](http://zef.me/2470/building-clojure-projects-with-leiningen)
for more specific information about build options.


### Invocation

The following  command will start the spdx generation

    $ java -jar pub-oss-<xyz>-standalone.jar [--build-dir bitbake's build direcotry]
                                             [--image bitbake image recipe name]

with the following optional arguments:

* build-dir: directory where the bitbake command is invoked from (e.g. oe-core/build)
* image: name of the bitbake recipe used for the generation the file system image


### Configuration

OpenEmbedded/Bitbake comes  with extensive configuration  which cannot
be  completely   and  reliably  parsed.  Instead   we  define  crucial
information  in example  for  the specification  of preferred  package
providers  and versions  and so  on within  a dedicated  configuration
file. We provide the default configuration used for our project within
the file  'pub-oss/src/bb/config.clj'. Refer  to the comments  in this
file for  more specific  information. It is  possible to  overload the
default  configuration dynamically  at startup  by specifying  another
config file with the optional argument specifier '-c'. The contents of
the file 'config.clj' can be used as a starting point.

### Output of SPDX Container, Open Source Packages and Overview file

The  program  'pub-oss'  generates  the  following  output  files  for
deployment:

* a gnu-zipped tar archive for each open source package
* a SPDX (XML) container file providing meta information of each package
* a list of all open source packages in html format (content.html)

These  files are  by  default  located wihtin  the  same directory  as
bitbake's build artefacts  under e.g. under 'build/tmp-eglibc/deploy'.
The   concrete  directories   are  specified   in  the   configuration
(config.clj). You might need to change them to the directories used in
your platform/distribution.


## Licence

This software stands under the terms of the
[GNU General Public Licence](http://www.gnu.org/licenses/gpl.html).

2014-2016, Otto Linnemann


## Resources and links

Thanks to all  the giants whose shoulders we stand  on. And the giants
these giants  stand on... And special  thanks to Rich Hickey  (and the
team) for Clojure. Really, thanks!

* Clojure: http://clojure.org
* Leiningen: https://github.com/technomancy/leiningen
