The AppleCore Programming Language, v1.0
========================================
Copyright (C) 2011-12 by Rob Bocchino

1\. Introduction and Rationale
------------------------------

The goal of AppleCore is to provide a "low-level high-level" language
for writing programs that run on the Apple II series of computers.  A
cross-compiler that runs on UNIX systems is currently available.  For
more information on the design and goals of AppleCore, see
Docs/AppleCore-Spec-v1.0.pdf.

2\. License
-----------

This software and the accompanying documentation are provided free for
general use, _provided that attribution is maintained_.  Any public
copying or distribution of this work or any derivative works thereof
must clearly state the original author's contribution.

Of course it is hoped this software works as intended.  However, it's
still under development and bugs can always occur.  Therefore, this
software is provided "as is" without any warranty, express or implied,
that it conforms to its specification or otherwise does anything in
particular, other than what it actually does do when you run it.

3\. System Requirements
-----------------------

Currently AppleCore works on UNIX-like systems with the git version
control system, a Java 6 compiler, and a Java 6 VM installed.  It has
been tested on Mac OS X Lion.  In the future, other systems may be
supported.  If you are enterprising, you should be able to get the
tools to work on Windows systems by installing Cygwin and writing some
simple scripts to translate UNIX class paths to Windows class paths.

To run AppleCore programs, you will also need an Apple II emulator
and/or a way to transfer files to an actual Apple II.  If you are
running Mac OS X, then I recommend Virtual ][, available here:

     http://www.virtualii.com/

In addition to providing an amazingly full-featured Apple II emulation
(including sounds that mimic the real thing), Virtual ][ supports file
transfer from a Mac to a real Apple II and vice versa.

Finally, to assemble AppleCore programs on the Apple II, you need the
S-C Macro Assembler, which is available here as a disk image that
works in the emulator (I use version 2.0 for DOS 3.3):

     http://stjarnhimlen.se/apple2/

Hereafter I'll use the terms "UNIX system" to refer to the system you
are using to run the AppleCore tools in this repository and "Apple II
system" to the Apple II environment (either emulated or actual) that
you are using to run AppleCore programs.

4. UNIX System Setup
--------------------

To set up your UNIX system to use AppleCore, do the following:

1.  Download the AppleCore distribution from the git repository:

    `git checkout git://github.com/bocchino/AppleCore.git`

2.  Set the environment variable APPLECORE to point to the top-level
    directory of the AppleCore distribution.

3.  Include ${APPLECORE}/Compiler/bin, ${APPLECORE}/Assembler/bin, and
    ${APPLECORE}/Scripts in your UNIX PATH.

Once you've done all that, you can test the implementation:

    cd ${APPLECORE}/Examples
    make

It should build the examples without any errors.  If not, fix the
problem and try again until it works.

5. APPLE II SYSTEM SETUP

To set up your Apple II system, you need to make a DOS 3.3 disk with
the AppleCore Virtual Machine (AVM) runtime files on it, as well as
any library files that you need to include in your programs.  If you
use Virtual ][ you can just use the disk image
${APPLECORE}/DOS3.3/LIB.v2d.  If you boot DOS 3,3, drag that image
into the virtual disk drive, and type CATALOG, you should see the
files AVM.PROLOGUE, AVM.1, AVM.2, AVM.3.BINOP, AVM.4.UNOP, and
AVM.5.BUILT.IN already on the disk.  Also, the LIB.v2d disk contains
library files such as files STRING and IO that are included by some of
the examples.

If you are not using Virtual ][, or if you wish to make your own disk
with these files on it, then you will need to build these files and
transfer them to the Apple II yourself.  First, build the runtime
files on your UNIX system: go to ${APPLECORE}/Runtime and type 'make'.
The build will create a directory exec with the AVM runtime files in
it.  

Next, start up the S-C Macro Assembler on the Apple II system and get
the exec directory contents onto the Apple II.  In Virtual ][, this
can be done simply and easily by dragging the OS X folder containing
the file into a drive on the virtual Apple II.  Virtual ][ will ask
you to provide a file type for each imported file.  Be sure to specify
type T (text) for EXEC files; the default type B (binary) won't work.
You need to do this only the first time you drag the directory to the
virtual drive; once you do it, Virtual ][ "remembers" the file type
information by storing it in the UNIX directory.  Another option is to
use a tool such as Apple Commander
(http://applecommander.sourceforge.net/) to transfer the files one by
one, but I find this method to be more awkward.

Once the exec directory contents are on the Apple II, for each FILE in
the directory, do the following:

 - EXEC FILE.EXEC to read the file into the S-C Macro Assembler's
   memory.  (By convention, DOS 3.3 file names are all uppercase; in
   particular, FILE.exec becomes FILE.EXEC when you drag a directory
   into Virtual ][.)

 - SAVE FILE to save the file to the disk.

Note that in Virtual ][ this process goes MUCH faster if you select
"maximum speed" from the speed control knob on the tool bar.

If you want to compile programs that depend on other AppleCore files
(e.g., the ones in ${APPLECORE}/Lib/ac) or assembly files (e.g., the
ones in ${APPLECORE}/Lib/avm), then you need to add those files to the
disk as well.  Section 7 says a bit more about this.

6. WRITING APPLECORE PROGRAMS

Currently the best documentation for the AppleCore language is the
spec (${APPLECORE}/Docs/AppleCore-Spec-v1.0.pdf).  However, like most
language specs it's a bit dry and conveys all the details without
enough worked examples.  Unfortunately there's no tutorial
documentation yet.  However, after browsing the spec to get the
general idea of what's going on, you should be able to read the
examples in ${APPLECORE}/Examples to get a better idea of how to write
programs in AppleCore.

7. COMPILING APPLECORE PROGRAMS

To compile an AppleCore program, you must carry out the following
steps:

a. Run the AppleCore compiler (acc) to translate one or more AppleCore
   source files FILE.ac into AppleCore Virtual Machine (AVM) assembly
   files FILE.avm.

b. Run the AppleCore assembler (avm-asm) to translate each AVM
   assembly file into a native 6502 assembly file FILE.asm.

c. For each AVM file, run the ASM to EXEC translator (asm2exec) to
   generate a file FILE.exec that can be imported into the S-C Macro
   Assembler on the Apple II using the EXEC command.

d. Get the FILE.EXEC files onto the Apple II system, as described in
   Section 5.

e. For each FILE.EXEC file, issue the command EXEC FILE.EXEC in the
   S-C Macro Assembler.  This creates an assembly file from the EXEC
   file.  Save the assembly file FILE to the disk.

f. Assemble the top-level FILE (see Section 5.1 of the AppleCore spec)
   by loading it into the assembler and issuing the command ASM to the
   S-C Macro Assembler.  Any assembly files that FILE depends on
   (including the AVM runtime, see Section 5 of this document) must be
   available on the disk, or the assembler will complain that it can't
   find the files.

The result of all this should be a file called FILE.OBJ on the Apple
II disk.  Issue the command BRUN FILE.OBJ to run the program.

For a simple example, try compiling any of the programs in
${APPLECORE}/Examples.  Examples/Redbook/RodsColorPattern might be a
good one to start with:

- In that directory, type 'make'.  The build system automatically does
  steps (a) through (c) for you.

- With the S-C Macro Assembler running, drag the directory 'exec' into
  drive 1 (step d) and say 'EXEC RODS.COLOR.PATTERN.EXEC,D1' at the
  prompt (step e).  If you want, save the file RODS.COLOR.PATTERN to
  the exec directory itself or to a different disk image.

- Put the LIB.v2d disk (or equivalent) in drive 2.

- Say 'ASM'.  After assembly is finished, the file
  RODS.COLOR.PATTERN.OBJ should be written to drive 1.

For another example, try assembling Examples/Redbook/Mastermind.  This
one is a bit more complex, because there are three source files you
need to get onto the drive 1 disk: MASTERMIND, MASTERMIND.1, and
MASTERMIND.2.

Note that these programs (and all programs in the Examples directory)
are configured to expect the source files for the program itself on
the disk in drive 1, and the LIB.v2d disk (or equivalent) in drive 2.
Unfortunately, when the assembler can't find a file that it needs, it
just halts and says FILE NOT FOUND; it doesn't specify which file is
missing.  However, you can figure this out by looking at the generated
assembly file for the top-level source file: for every directive .IN
FILE appearing in the assembly file, the file FILE must be present on
the disk in the specified drive.  For example, if the top-level file
contains the directive .IN GRAPHICS,D2, then the assembly file
GRAPHICS must be present on the disk in drive 2 in order to do the
assembly.  You can also look at the top-level AppleCore source file:
the needed files are just the AVM runtime files listed in Section 5 of
this document, together with any files specified in the source file
via an INCLUDE declaration.  The AVM runtime files must be in the
drive specified on the compiler command line, as explained in the next
section.  The INCLUDE declarations specify the drive directly (e.g.,
INCLUDE "GRAPHICS,D2").

8. ACC COMPILER OPTIONS

Currently acc accepts exactly one source file name SF (including
UNIX path info) on the command line, translates that file, and writes
the results to standard output.  In the future, more flexible options
(e.g., compiling multiple files in one acc command) may be provided.

Currently acc accepts the following command-line options.  In the
future, more options may be provided:

-decls=DF1:...:DFn - Before translating SF, parse files DF1 through
    DFn and get the declarations out of them.  This allows SF to refer
    to functions, constants, data, or variables declared in any of the
    DFi, which is essential for separate compilation.  The Makefile
    ${APPLECORE}/Examples/Makefile illustrates how this is done.

-include - Translate SF in include mode (see Section 5.1 of the
    AppleCore specification).  If no -include appears on the command
    line, then the default is top-level mode.

-tf=TF - Instruct the assembler to write the output to file TF.  (TF
    stands for "text file," which is how the S-C Macro Assembler
    refers to its file output.)  If no -tf= appears on the command
    line, then the default name is generated by stripping .AC from the
    end of SF (if it is there, ignoring case) and adding .OBJ.

-origin=OR - Instruct the assembler to assemble the file with origin
    address OR.  The origin address may be given in positive decimal,
    negative decimal, or hexadecimal preceded by $.  If no -origin=
    appears on the command line, then for top level mode the default
    origin is $803.  That puts the start of the program in the main
    storage area for programs and data, just above the three bytes
    signaling an empty BASIC program.  For include mode the default is
    to use the origin implied by the point in the program where the
    file is included.

-avm-slot=S - Instruct the assembler to look for the AVM source files
    in slot S (default 6).

-avm-drive=D - Instruct the assembler to look for the AVM source fiels
    in drive D (default 1).

These compiler options handle most common cases. Finer control over
what goes where in memory can be achieved by compiling everything in
include mode and writing a short assembly language program to glue the
pieces together.  You might do this if the whole program won't fit in
memory, or if you need the program to occupy discontinuous parts of
memory (e.g., to wrap it around the hi-res graphics pages).  See
${APPLECORE}/Examples/Chain for an example of how to do this.

9. THE APPLECORE VIRTUAL MACHINE

The AppleCore compiler compiles AppleCore source files to byte code
for the AppleCore Virtual Machine (AVM).  The AVM code is then
interpreted by the AVM runtime.  This makes the code very compact.
For more details on how this works, see the AVM specification
(${APPLECORE}/Docs/AVM-Spec-v1.0.pdf).

10. INTEGRATION WITH BASIC AND DOS

For the most part, loading and running AppleCore programs should work
seamlessly with BASIC and DOS (except that loading an AppleCore
program clobbers any resident BASIC program, of course).  AppleCore
programs patch DOS while they are running to make error handling work
less insanely, but they always restore DOS to its usual ways on exit,
even via control-reset.  AppleCore programs can be run from the
monitor prompt, the Integer BASIC prompt, or the AppleSoft prompt.

The one exception is that to maintain compatibility with whatever the
environment is doing with control-reset, AppleCore programs always
exit via a JMP to the location stored in the reset vector ($3F2-$3F3)
on program startup.  When DOS 3.3 is booted, this is typically $9DBF
(DOS warm start).  Thus, in most cases, if you want to load a BASIC
program after running an AppleCore program, you should first say INT
or FP (or 3D3G or CONTROL-B from inside the monitor) to reset the
BASIC environment.  Otherwise you may get an OUT OF MEMORY or MEM FULL
error when you attempt to load the BASIC program.

Currently AppleCore works with DOS 3.3 and DOS 3.3 only; ProDOS is not
supported.  I've no plans to change that any time soon.  I'm most
interested in the "old school" Apple II with Woz's original monitor,
integer BASIC, and the 40 column display installed.  The only newer
firmware features I really care about are booting DOS (essential) and
lower-case characters (nice, but not essential).  Other features like
80-character displays, double hi-res graphics, alternate character
sets, auxiliary memories, etc. are nice, but they are also a headache
to program and destroy the simplicity and elegance of Woz's original
design.

11. SHELL EDITOR

As part of the AppleCore release, I've included a nifty little shell
editor that's fun to use from the monitor or BASIC prompt and is
automatically invoked by any program that does a JSR to $FD6F (GETLN1)
to get line input.  It works quite nicely with the old monitor and the
flashing cursor!  For more details, see the documentation in
${APPLECORE}/DOS3.3/README.txt and the source code in
${APPLECORE}/ShellEditor.
