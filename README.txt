THE APPLECORE PROGRAMMING LANGUAGE, V1.0
BY ROB BOCCHINO

1. INTRODUCTION AND RATIONALE

The goal of AppleCore is to provide a "low-level high-level" language
for writing programs on the Apple II series of computers.  A cross-
compiler from Java on UNIX systems is currently available.  For more
information on the design and goals of AppleCore, see
Spec/AppleCore-Spec-v1.0.pdf.

2. LICENSE

This software and the accompanying documentation are provided free for
general use, PROVIDED THAT ATTRIBUTION IS MAINTAINED.  Any public
copying or distribution of this work or any derivative works thereof
must clearly state the original author's contribution.

Hopefully this software works as intended.  However, it's still under
development and bugs can always occur.  Therefore, this software is
provided "as is" without any warranty, express or implied, that it
conforms to its specification or otherwise does anything in
particular, other than what it actually does do when you run it.

3. SYSTEM REQUIREMENTS

Currently AppleCore works on UNIX-like systems with the git version
control system, a Java 6 compiler, and a Java 6 VM installed.  It has
been tested on Mac OS Lion.  In the future, other systems may be
supported.  If you are enterprising, you should be able to get the
tools to work on Windows systems by installing Cygwin and writing some
simple scripts to translate UNIX class paths to Windows class paths.

To run AppleCore programs, you will also need an Apple II emulator
and/or a way to transfer files to an actual Apple II.  If you are
running Mac OS X, then I recommend Virtual II, available here:

     http://www.virtualii.com/

In addition to providing an amazingly full-featured Apple II emulation
(including sounds that mimic the real thing), Virtual II supports file
transfer from a real Apple II to a Mac and vice versa.  Other
emulators may be available for Windows and Linux machines, but I
haven't used them (after all, this is APPLECore!).

Finally, to assemble AppleCore programs on the Apple II, you need the
S-C Macro Assembler, which is available here as a disk image that
works in the emulator (I use version 2.0 for DOS 3.3):

     http://stjarnhimlen.se/apple2/

Hereafter I'll use the terms "UNIX system" to refer to the system you
are using to run the AppleCore tools in this repository and "Apple II
system" to the Apple II environment (either emulated or actual) that
you are using to run AppleCore programs.

4. UNIX SYSTEM SETUP

To set up your UNIX system to use AppleCore, do the following:

a. Download the AppleCore distribution from the git repository:

   git checkout git://github.com/bocchino/AppleCore.git

b. Set the environment variable APPLECORE to point to the top-level
   directory of the AppleCore distribution.

c. Include ${APPLECORE}/Compiler/bin, ${APPLECORE}/Assembler/bin, and
   ${APPLECORE}/Scripts in your UNIX PATH.

Once you've done all that, you can test the implementation:

   cd ${APPLECORE}/Examples
   make

It should build the examples without any errors.  If not, fix the
problem and try again until it works.

5. APPLE II SYSTEM SETUP

To set up your Apple II system, you need to make a DOS 3.3 disk with
the AppleCore Virtual Machine (AVM) runtime files on it.  If you use
Virtual II, the easiest way to do this is to drag the disk image
${APPLECORE}/DOS3.3/ACC.v2d into the virtual disk drive.  You should
see the files AVM.PROLOGUE, AVM.1, AVM.2, AVM.3.BINOP, AVM.4.UNOP, and
AVM.5.BUILT.IN already on the disk.  Also, the ACC.v2d disk contains
files STRING and IO that are included by some of the examples.
Finally, some examples are there (such as HELLO.WORLD).

If you are not using Virtual II, or if you wish to make your own disk
with these files on it, then you will need to build these files and
transfer them to the Apple II yourself.  First, build the runtime
files on your UNIX system: go to ${APPLECORE}/Runtime and type 'make'.
The build will create a directory exec with the AVM runtime files in
it.  

Next, start up the S-C Macro Assembler on the Apple II system and get
the exec directory contents onto the Apple II.  In Virtual II, this
can be done simply and easily by dragging the OS X folder containing
the file into a drive on the virtual Apple II.  Another option is to
use a tool such as AppleCommander
(http://applecommander.sourceforge.net/) to transfer the files one by
one, but this is more awkward.

Once the exec directory contents are on the Apple II, for each FILE in
the directory, do the following:

 - EXEC FILE.EXEC to read the file into the S-C Macro Assembler's
   memory.
 - SAVE FILE to save the file to the disk.

(Note that in Virtual II this process goes MUCH faster if you select
"maximum speed" from the speed control knob on the tool bar.)    

6. WRITING APPLECORE PROGRAMS

Currently the best documentation for the AppleCore language is the
spec (${APPLECORE}/Spec/AppleCore-Spec-v1.0.pdf).  However, like any
spec it's a bit dry and conveys all the details without enough worked
examples.  Unfortunately there's no tutorial documentation yet.
However, after browsing the spec to get the general idea of what's
going on, you should be able to read the examples in

     ${APPLECORE}/Examples/ac
     ${APPLECORE}/Lib/ac
     ${APPLECORE}/Test/Good/ac

to get a better idea of how to write programs in AppleCore.  More and
larger examples will be forthcoming.

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

e. For each FILE.EXEC file, issue the command EXEC in the S-C Macro
   Assembler.  This creats an assembly file from the EXEC file.  If
   you wish, save the assembly file FILE so it is available on the
   Apple II in assembly format.  (This step saves doing the EXEC every
   time you want to assemble against the file; it is particularly
   useful for library files that do not change as you are working on
   other files that need them.)

f. Assemble the top-level FILE (see Section 5.1 of the AppleCore spec)
   by issuing the command ASM to the S-C Macro Assembler.  Any
   assembly files that FILE depends on (including the AVM runtime, see
   Section 5 of this document) must be available on the disk, or the
   assembler will complain that it can't find the files.

The result of all this should be a file called FILE.OBJ on the Apple
II disk.  Issue the command BRUN FILE.OBJ to run the program.

To see an example of this process, navigate to ${APPLECORE}/Test/Good
and type 'make'.  After everything builds, drag the exec directory
into the Apple II.  Then you should be able to do steps d-f above on
any of the files you wish.  If the assembler complains that some files
are missing, then put those files on the disk as described in Section
5 and try again.

8. ACC COMPILER OPTIONS

Currently, acc accepts exactly one source file name SF (including
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
    line, then the default name is generated by stripping .ac from the
    end of SF (if it is there) and adding .obj.

-origin=OR - Instruct the assembler to assemble the file with origin
    address OR.  The origin address may be given in positive decimal,
    negative decimal, or hexadecimal preceded by $.  If no -orign=
    appears on the command line, then the default is $803.

9. THE APPLECORE VIRTUAL MACHINE

The AppleCore compiler compiles AppleCore source files to byte code
for the AppleCore Virtual Machine (AVM).  The AVM code is then
interpreted by the AVM runtime.  This makes the code very compact.
For more details on how this works, see the AVM specification
(${APPLECORE}/Spec/AVM-Spec-v1.0.pdf).
