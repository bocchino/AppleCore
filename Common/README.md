AppleCore Common Makefiles
==========================
Copyright (C) 2013 by Rob Bocchino

This directory provides predefined variables and targets for setting
up AppleCore makefiles with a minimum of effort.  To use the files,
find the pattern described below that most closely corresponds to what
you want to do, and copy or modify it as necessary.  Each of the
patterns is illustrated by one or more builds in the AppleCore
release.

1\. Single top-level AppleCore source files
-------------------------------------------

**Single program:** If you want to compile a single program comprising
a single AppleCore source file (not counting the AppleCore library
files, which are precompiled and automatically linked in during
assembly), then do the following:

1. Give the source file the suffix ac and put it in a directory
   ac, e.g., ac/PROGRAM.ac.

2. At the same level as the ac directory, write a makefile that
   includes $(APPLECORE)/Common/Defs, then defines a variable
   ACC-DECLS providing the argument to -decls on the acc command line,
   then includes $(COMMON)/Makefile.ac.

*Example:* $APPLECORE/Programs/Games/Snake. Notice that the Makefile
also includes the line

  `ACC += -native`

which instructs acc to generate native code. To generate interpreted
AVM code, you would take that line out.

Issuing make does the following:

1. Compiles ac to a new directory avm, reading definitions from the
   files listed in ACC-DECLS.

2. Compiles avm to a new directroy obj.  The AppleCore libraries
   and runtime are automatically made available during this step.

Directory obj contains a compiled binary PROGRAM.OBJ which you can
load directly into Virtual ][.

**Multiple programs:** Multiple programs, each consisting of a single
file, can be compiled this way.  For example, if directory ac contains
PROGRAM1.ac and PROGRAM2.ac, both of which are complete AppleCore
programs, then issuing make will create an obj directory containing
the compiled binaries PROGRAM1.OBJ and PROGRAM2.OBJ.

*Example:* $APPLECORE/Test/Good

**Handwritten assembly files:** If your program includes handwritten
assembly files, then see item 3 below.

2\. Including AppleCore source files
------------------------------------

**Single program:** If you want to compile a single program comprising
several AppleCore source files (again not counting the AppleCore
library files), then do the following:

1. Give the top-level source file the suffix ac and put it in a
   directory ac, e.g., ac/TOP.LEVEL.ac.

2. Write a makefile as in step 2 of item 1 above, except that it
   includes $(COMMON)/Makefile.ac.including instead of
   $(COMMON)/Makefile.ac.

3. At the same level as the ac directory, create a directory Include,
   and in that directory do the following:

   a. Give the included AppleCore files the suffix ac and put them in
      a directory ac, e.g., Include/ac/INCLUDED.ac.

   b. Write a makefile as in step 2 above, except that it includes
      $(COMMON)/Makefile.ac.included.

*Example:* $APPLECORE/Programs/Games/Snake

Issuing make in the top-level directory does the following:

1. Compiles Include/ac to Include/avm.

2. Compiles ac to avm.

3. Compiles avm to obj.  

The directory obj contains the compiled binary TOP.LEVEL.OBJ.

**Multiple programs:** Multiple top-level programs can be compiled
this way, as described in item 1 above.

**Handwritten assembly files:** If your program includes handwritten
assembly files, then see item 3 below.

3\. Including handwritten assembly files
----------------------------------------

If you need a routine that runs very fast, then you can write most of
your program in AppleCore but include one or more handwritten assembly
files.  To do that, do the following:

1. At the top level, create directories AppleCore and Assembly for the
   AppleCore and assembly parts of your program.

2. In the Assembly directory, create a directory avm that includes
   your assembly files with the suffix avm, e.g., ASSEMBLY.avm.

3. In the AppleCore directory, proceed as in item 1 or 2 above.

*Example:* $APPLECORE/Programs/Graphics/RodsColorPattern

Invoking make in the AppleCore directory produces a file obj with the
generated object code.

4\. Top-level handwritten assembly files
----------------------------------------

If you need finer-grained control over the layout of parts of your
program than is provided by the acc compiler options, then you can
write a top-level assembly program that includes one or more assembly
files (either compiled from AppleCore, or handwritten, or both).  In
that case, do the following:

1. At the top level, create directories AppleCore and Assembly for the
   AppleCore and assembly parts of your program.

2. In the Assembly directory, create a directory avm that includes
   your top-level assembly file with the suffix avm, e.g.,
   TOP.LEVEL.avm.

3. In the Assembly directory, write a makefile that includes
   $(APPLECORE)/Common/Defs, then contains the line

   `TARGET=TOP.LEVEL`

   then includes $(COMMON)/Makefile.avm.  Change TOP.LEVEL to your
   actual program name, of course.

4. Set up the AppleCore directory so that it compiles your AppleCore
   files in include mode, as in the Include directory described in
   item 2 above.

*Examples:* $APPLECORE/Programs/Examples/Chain,
$APPLECORE/Programs/Games/BelowTheBasement

Running make in AppleCore, then running make in Assembly creates a
file Assembly/obj with the compiled object code.  As shown in the
examples, you can write a simple top-level makefile with a single
target that carries out both of these steps.


