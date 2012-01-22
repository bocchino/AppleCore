AppleCore/DOS3.3
================

This directory contains the following Apple II DOS 3.3 disk images:

1. ACC.v2d: Source code and object code for the programs in
   AppleCore/Examples/ac.

2. MINI.ADVENTURE.v2d: Source code and object code for the program
   "Mini Adventure" in AppleCore/Examples/MINI.ADVENTURE.

3. BOOTABLE.DISK.dsk: A disk you can use to boot DOS, before running
   programs on the other disks.  The other two disks have no DOS image
   (to conserve space), so if you try to boot them the system will
   crash.

Please note:

1. The v2d format is proprietary to the Virtual II emulator, so the
   first two disk images require Virtual II.  However, you can make
   your own images that run on a different emulator as explained in
   AppleCore/README.txt.

2. On the first two disks, the object code files have type B, and
   their names end in .OBJ.  The source files have type I.

3. If you boot from the Bootable Disk, then you will be able to run
   the object code.  However, to inspect the source code, you'll need
   the S-C Macro Assembler.

