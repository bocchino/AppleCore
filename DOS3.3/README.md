AppleCore/DOS3.3
================

This directory contains the following Apple II DOS 3.3 disk images:

1.  EXAMPLES.v2d: Object code for the programs in AppleCore/Examples,
    except for Below the Basement.

2.  BTB.v2d: Object code for the program "Below the Basement" in
    AppleCore/Examples/BelowTheBasement.

3.  SHELL.EDITOR.FP.dsk: Applesoft version of the shell editor.

Booting from any of the three images provides the shell editor:

  - Booting from disks 1 or 2 gives you Integer BASIC with the 1977
    ROM and the solid flashing cursor.

  - Booting from SHELL.EDITOR.FP.dsk gives you Applesoft BASIC with
    the Autostart ROM and the shell editor.  You then have the option
    to load the 1977 ROM and/or Integer BASIC, if you wish.

The editor resides in $D000 RAM bank 1, so it works seamlessly with
whatever is in bank 2 (usually Applesoft or Integer BASIC).  Also,
once it is installed, the editor is automatically invoked by AppleCore
(or BASIC or other) programs that ask for input via MON.GETLN.

I constructed these disk images using the utility Copy II Plus
(http://www.vectronicsappleworld.com/appleii/internet.html#copy).

