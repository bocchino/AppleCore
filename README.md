The AppleCore Programming Language, v1.0
========================================
Copyright (C) 2011-12 by Rob Bocchino

1\. Introduction and Rationale
------------------------------

The goal of AppleCore is to provide a "low-level high-level" language
for writing programs that run on the Apple II series of computers.  A
cross-compiler that runs on Mac OS X systems is currently available.
For more information on the design and goals of AppleCore, see
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

To use the AppleCore installation, you need the following:

1.  An Apple Macintosh computer running OS X 10.4 or later, with XCode
    command line tools installed.

2.  A Java 6 Virtual Machine and javac compiler.

3.  The Virtual ][ emulator, available at
    http://www.virtualii.com/.

4.  A Standard ML of New Jersey (SML/NJ) installation, available via
    macports (http://www.macports.org) or via the developers
    (http://www.smlnj.org/dist/working/110.74/index.html).

4\. Mac Setup
-------------

To set up your Mac to use AppleCore, do the following:

1.  If necessary, install Virtual ][ and SML/NJ.

2.  Download the AppleCore distribution from the git repository:

    `git checkout git://github.com/bocchino/AppleCore.git`

3.  Set the environment variable APPLECORE to point to the top-level
    directory of the AppleCore distribution.

4.  Include ${APPLECORE}/Compiler/bin, ${APPLECORE}/Assembler/bin, and
    ${APPLECORE}/Scripts in your UNIX PATH.

5.  Build the assembler, compiler, and examples:

    `cd ${APPLECORE}`
    `make`

It should build the without any errors.  If not, fix the problem and
try again until it works.

5\. Writing AppleCore Programs
------------------------------

Currently the best documentation for the AppleCore language is the
spec (${APPLECORE}/Docs/AppleCore-Spec-v1.0.pdf).  However, like most
language specs it's a bit dry and conveys all the details without
enough worked examples.  Unfortunately there's no tutorial
documentation yet.  However, after browsing the spec to get the
general idea of what's going on, you should be able to read the
examples in ${APPLECORE}/Examples to get a better idea of how to write
programs in AppleCore.

6\. Compiling AppleCore Programs
--------------------------------

To compile an AppleCore program, you must carry out the following
steps:

1.  Run the AppleCore compiler (acc) to translate one or more AppleCore
    source files FILE.ac into AppleCore Virtual Machine (AVM) assembly
    files FILE.avm.

2.  Run the AppleCore assembler (sc-avm-asm) to translate the AVM
    assembly files into binary files that can be run on the Apple II.

To see how this is done, go to the directory
`${APPLECORE}/Examples/HelloWorld`.  First, peek at the source program
ac/HELLO.WORLD.ac.  That's the program we'll compile.  Next, type
`make clean` and then `make`.  A directory `obj` should appear
containing two files:

1.  `HELLO.WORLD.OBJ` containing the binary code for the
    compiled program.

2.  `_AppleDOSMappings.plist` that tells Virtual ][ how to interpret
    the OS X file as an Apple II DOS file.

If you would like to see the assembled output listing, then say `make
OPTS=-l`.  That tells the assembler to list the assembly to the
standard output (which can be redirected to a file).  

Notice that the assembled program is quite a bit longer than the
source program!  That's because some library and runtime code has been
assembled into the final program.

Notice also that the compiler and the assembler both require options
indicating where to find included files.  Those options are specified
in the file ${APPLECORE}/HelloWorld/Makefile.  See sections 8 and 9
below for more information about these options.

7\. Running AppleCore Programs
------------------------------

To run a compiled AppleCore program, start up Virtual ][ and boot DOS
3.3.  Drag the output directory into one of the emulator's virutal
disk drives and use the directory as a normal DOS 3.3 disk.  

For example, to run the "hello world" program, drag
`${APPLECORE}/Examples/HelloWorld/obj` into one of the drives, say
drive 1.  Then say `BRUN HELLO.WORLD.OBJ` at the BASIC prompt.  The
Apple II should respond by printing

   `HELLO, WORLD!`

to the console.

The nice thing about Virtual ][ is that it lets you treat OS X
directories (with the proper mappings list) as DOS 3.3 disks.  This
makes it easy to transfer files between the Mac and the emulator.You
can also use a utility such as Apple Commander
(http://applecommander.sourceforge.net/) to construct DOS 3.3 disk
images, if you like.

8\. The AppleCore Compiler (acc)
-------------------------------

acc accepts exactly one source file name SF (including UNIX path info)
on the command line, translates that file, and writes the results to
standard output.  In the future, more flexible options (e.g.,
compiling multiple files in one acc command) may be provided.

acc accepts the following command-line options:

  - `-decls=DF1:...:DFn`

    Before translating SF, parse files DF1 through DFn and get the
    declarations out of them.  This allows SF to refer to functions,
    constants, data, or variables declared in any of the DFi, which is
    essential for separate compilation.

  - `-include`

    Translate SF in include mode (see Section 5.1 of the AppleCore
    specification).  If no -include appears on the command line, then
    the default is top-level mode.

  - `-tf=TF`

    Instruct the assembler to write the output to file TF.  (TF stands
    for "text file," which is how the assembler refers to its file
    output.)  If no -tf= appears on the command line, then the default
    name is generated by stripping .AC from the end of SF (if it is
    there, ignoring case) and adding .OBJ.

  - `-origin=OR`

    Instruct the assembler to assemble the file with origin
    address OR.  The origin address may be given in positive decimal,
    negative decimal, or hexadecimal preceded by $.  If no -origin=
    appears on the command line, then for top level mode the default
    origin is $803.  That puts the start of the program in the main
    storage area for programs and data, just above the three bytes
    signaling an empty BASIC program.  For include mode the default is
    to use the origin implied by the point in the program where the
    file is included.

These compiler options handle most common cases. Finer control over
what goes where in memory can be achieved by compiling everything in
include mode and writing a short assembly language program to glue the
pieces together.  You might do this if the whole program won't fit in
memory, or if you need the program to occupy discontinuous parts of
memory (e.g., to wrap it around the hi-res graphics pages).  See
${APPLECORE}/Examples/Chain for an example of how to do this.

9\. The SC-AVM-ASM Assembler
----------------------------

The assembler is called sc-avm-asm because its format is based on the
SC Macro Assembler.  This was my favorite assembler back when I
actually wrote assembly code on the Apple II.  Also, an earlier
version of the AppleCore tool chain actually used this assembler (and
required you to do final assembly on the Apple II).  

The SC Macro Assembler is an impressive piece of engineering, but it
suffers from the inherent limitations of the Apple II.  The current
assembler makes it much easier to use AppleCore, because (1) you can
do everything using UNIX tools and scripts, (2) you aren't constrained
by the Apple II's memory limitations, and (3) nested .IN directives
are allowed.

The assembler assembles three kinds of mnemonics, using the SC Macro
Assembler format:

1.  Native 6502 instructions

2.  AppleCore virtual machine (AVM) instructions

3.  A subset of the SC Macro Assembler directives.  Most directives
    are supported, except for the ones that don't make sense when
    cross-assembling (e.g., the .TA directive, which specifies where
    in memory to put the assembled code).  Macros and private labels
    are not supported.

You can get documentation on the SC Macro Assembler format and
directives here: http://stjarnhimlen.se/apple2/.  

acc accepts the following command-line options:

  - `-d outdir`

    Set the output directory to outdir.  All output files are written
    to that directory, and a new mappings list is created in the
    directory to describe the files.  Any existing mappings list, and
    any existing files with the same name as an outpuf file, is
    destroyed.  If no -d option is specified, the default is the
    current working directory.

  - `-i p1:...:pn`

    Search paths p1 through pn (in that order) for AVM files to
    include when encountering an .IN directive.

  - `-l`

    List the assembly file to stdout.

  - `-o outfile`

    Set the output file to outfile.  The file is interpreted relative
    to the outdir; that is, the output is written to outdir/outfile.
    The default generated by stripping .AVN from the end of the input
    file name (if it is there, ignoring case) and adding .OBJ.

Please note the following:

  - The .TF (text file) assembler directive works just like in the SC
    Macro Assembler, so the initial output file (default or set by -o)
    is overridden by any .TF directive(s) encountered.

  - Because DOS file names must be upper case, the assembler converts
    all file names (default or specified by -o or .TF) to upper case.

The neat thing about the .TF directive is that it lets you assemble a
single logical program into multiple output files.  This is handy, for
example, when your whole program won't fit into memory.  See
${APPLECORE}/Examples/Chain and ${APPLECORE}/Examples/BelowTheBasement
for examples of the .TF directive in action.

9\. The AppleCore Virtual Machine
---------------------------------

The AppleCore compiler compiles AppleCore source files to byte code
for the AppleCore Virtual Machine (AVM).  The AVM code is then
interpreted by the AVM runtime.  This makes the code very compact.
For more details on how this works, see the AVM specification
(${APPLECORE}/Docs/AVM-Spec-v1.0.pdf).

10\. Integration with BASIC and DOS
-----------------------------------

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

11\. Shell Editor
-----------------

As part of the AppleCore release, I've included a nifty little shell
editor that's fun to use from the monitor or BASIC prompt and is
automatically invoked by any program that does a JSR to $FD6F (GETLN1)
to get line input.  It works quite nicely with the old monitor and the
flashing cursor!  For more details, see the documentation in
${APPLECORE}/DOS3.3/README.txt and the source code in
${APPLECORE}/ShellEditor.
