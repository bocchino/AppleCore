AppleCore/DOS3.3
================

This directory contains the following Apple II DOS 3.3 disk images:

1. EXAMPLES.v2d: Source code and object code for the programs in
   AppleCore/Examples/ac.

2. MINI.ADVENTURE.v2d: Source code and object code for the program
   "Mini Adventure" in AppleCore/Examples/MINI.ADVENTURE.

3. SHELL.EDITOR.dsk: A shell editor that's a bit nicer than the native
   Apple II one, plus older ROM images patched to work with the
   editor.  The editor resides in $D000 RAM bank 1, so it works
   seamlessly with whatever is in bank 2 (usually Applesoft or Integer
   BASIC).  Also, the editor is invoked automatically by AppleCore
   programs that ask for input via MON.GETLN.

Please note:

1. The v2d format is proprietary to the Virtual II emulator, so the
   first two disk images require Virtual II.  However, you can make
   your own images that run on a different emulator as explained in
   AppleCore/README.txt.

2. On these disks object code (i.e., executable) files have type B,
   and their names end in .OBJ.  Source source files have type I.  To
   inspect the source code, you'll need the S-C Macro Assembler.

3. Booting from the first two disks doesn't work, because they have no
   DOS image.  You can boot from the SHELL.EDITOR disk.  Doing that
   installs the editor.


