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

If your program consists of a single AppleCore source file (not
counting the AppleCore library files, which are precompiled and
automatically linked in during assembly), then do the following:

1. Give the source file the suffix ac and put it in a directory
   ac, e.g., ac/PROGRAM.ac.

2. At the same level as the ac directory, write a makefile that
   includes the file Defs from this directory, then defines a variable
   ACC-DECLS that provides the argument to -decls on the acc command
   line, then includes Makefile.ac from this directory.

**Example:** ${APPLECORE}/Programs/Games/Snake

Issuing make does the following:

1. Compiles ac to a new directory avm, using ACC-DECLS to find the
   files to include during compilation.

2. Compiles avm to a new directroy obj.  The AppleCore libraries
   and runtime are automatically made available during this step, so
   you don't have to tell the assembler where they are.

Directory obj contains a compiled binary PROGRAM.OBJ which you can
load directly into Virtual ][.

Multiple programs, each consisting of a single file, can be compiled
this way.  For example, if directory ac contains PROGRAM1.ac and
PROGRAM2.ac, both of which are complete AppleCore programs, then
issuing make will create an obj directory containing the compiled
binaries PROGRAM1.OBJ and PROGRAM2.OBJ.

**Example:** ${APPLECORE}/Test/Good

If your program includes handwritten avm files, then see item XX
below.

2\. Top-level and included AppleCore source files
-------------------------------------------------

If your program consists of a top-level AppleCore source file that
includes code compiled from other AppleCore source files (again not
counting the AppleCore library files), then do the following:

1. Give the top-level source file the suffix ac and put it in a
   directory ac, e.g., ac/TOP.LEVEL.ac.

2. Write a makefile as in step 2 of item 1 above, except that it
   includes Makefile.ac.including instead of Makefile.ac from this
   directory.

3. At the same level as the ac directory, create a directory Include,
   and in that directory do the following:

   a. Give the included AppleCore files the suffix ac and put them in
      a directory ac, e.g., Include/ac/INCLUDED.ac.

   b. Write a makefile as in step 2 above, except that it includes
      Makefile.ac.included from this directory.

Issuing make in the top-level directory does the following:

1. Compiles Include/ac to Include/avm.

2. Compiles ac to avm.

3. Compiles avm to obj.  

The directory obj contains the compiled binary TOP.LEVEL.OBJ.

**Example:** ${APPLECORE}/Programs/Games/Snake

Multiple top-level programs can be compiled this way, as described in
item 1 above.

If your program includes handwritten avm files, then see item XX
below.

3\. Included handwritten assembly files
----------------------------------------

Sometimes it's useful to write AVM or 6502 assembly files by hand, if
you need a routine that runs really fast.  To do that, do the
following:

1. At the top level, create directories AppleCore and Assembly for the
   AppleCore and assembly parts of your program.

2. In the Assembly directory, create a directory avm that includes
   your assembly files with the suffix avm, e.g., ASSEMBLY.avm.

3. In the AppleCore directory, Proceed as in item 1 or 2 above, but
   add the following line to the top-level makefile:

   `SC-AVM-ASM += -i ../Assembly/avm`

   That line tells the assembler where to find the included assembly
   files.

**Example:** ${APPLECORE}/Programs/Graphics/RodsColorPattern

4\. Top-level handwritten assembly files
----------------------------------------

TODO

