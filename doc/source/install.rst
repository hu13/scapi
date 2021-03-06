.. _install:

Installation
============

Scapi is simple enough to install, the installation varies on different operating systems. Scapi currently supports Linux_, `Mac OS X`_, and Windows_.

.. _Linux:

Prerequisites on Linux
---------------------

There are a few prerequisites you must install before being able to compile scapi on your machine.

1.  Install `git`_
2.  Install `java`_, `junit4`_, and `ant`_
3.  Install the `gcc`_ compiler environment: `gcc`, `make`, `ar`, `ld`, etc. Under Ubuntu you can simply run ``sudo apt-get install build-essential``. 

On Ubuntu environment is should look like: ::

  $ sudo apt-get update
  $ sudo apt-get install git
  $ sudo apt-get install default-jre
  $ sudo apt-get install default-jdk
  $ sudo apt-get install build-essential
  $ sudo apt-get install ant
  $ sudo apt-get install junit4
  
.. note::
  Notice that scapi should compile with gcc 4.7 or higher (if you also compile the c++ version - see specific instruction in c++ section)
  

.. _`Mac OS X`:

Prerequisites on Mac OS X
------------------------

On Mac OS X, `git`_ is usually preinstalled, and so are `java`_ and the `gcc`_ compiler environment.
However, `ant`_ is not preinstalled, and you must install it via `homebrew`_: ::

  $ brew install ant

Prerequisites OS X 10.9 (Mavericks) and above
--------------------------------------------

Starting in OS X Mavericks, apple has switched the default compiler from `gcc`_ to `clang`_.
Since Scapi's buildsystem is based on gcc, you must install gcc manually using homebrew: ::

  $ brew tap homebrew/versions
  $ brew install gcc49

.. note::

  Homebrew compiles gcc from source, and it can take a lot of time (usually around 20-50 minutes).
  **DO NOT** stop the shell process even though it seems to be stuck, it's not stuck.
  

Installing Scapi from Source (On UNIX-based Operating Systems)
--------------------------------------------------------------

In order to install scapi: ::

  $ git clone git://github.com/cryptobiu/scapi.git
  $ cd scapi
  $ git submodule init
  $ git submodule update
  $ sudo make prefix=/usr
  $ sudo make install prefix=/usr
  
In case you want to install the latest java build as well and not use the pre-compiled header: ::

  $ sudo make compile-scapi
  
.. note::

  Seems that JDK 1.8 introduced a change of behavior regarding JNI include file locations.
  Details on the problem and workaround can be found in the following link: `jdk-1-8-on-linux`_

.. _Windows:

Instructions for Windows
------------------------

We currently do not have a makefile for windows, but we intend to add one in the near future.

In order to use SCAPI on windows, we included precompiled assets on the **assets/** dir.

Needed Files
~~~~~~~~~~~~

.. _here:

1. ScapiWin-{version}.jar (Scapi)
2. bcprov-{version}.jar (Bouncy Castle)
3. commons-exec-{version}.jar (Apache Commons utilities)
4. activemq-all-{version}.jar (Apache ActiveMQ ™ is an open source messaging and Integration Patterns server)
5. Precompiled DLLs under win32Dlls (for 32-bit windows) or under x64Dlls (for 64-bit windows)
6. msvcp100.dll, msvcr100.dll, msvcp120.dll and msvcr120.dll(Microsoft DLLs used by the native code)

In order to install SCAPI
~~~~~~~~~~~~~~~~~~~~~~~~~

On Eclipse:

1. Configure build path: go to Libraries tab, and add external JARS. 

   a. Add ScapiWin-{version}.jar.
   b. Add bcprov-{version}.jar.
   c. Add commons-exec-{version}.jar.
   d. Add activemq-all-{version}.jar
   
2. Configure build path: go to Source tab and locate the Native Library Location section.

   a. Add the lib folder where you have the precompiled DLLs (assets/win32Dlls or assets/x64Dlls).
3. Place the msvcp100.dll and msvcr100.dll in [C:]\Windows\System32 folder if they are missing there.

.. _git: http://git-scm.org/
.. _java: http://java.com/
.. _ant: http://ant.apache.org/
.. _gcc: http://gcc.gnu.org/
.. _clang: http://clang.llvm.org/
.. _homebrew: http://brew.sh/
.. _jdk-1-8-on-linux: http://stackoverflow.com/questions/24996017/jdk-1-8-on-linux-missing-include-file
.. _junit4: http://packages.ubuntu.com/precise/devel/junit4
