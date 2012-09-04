AppleCore/DOS3.3
================

This directory contains the following Apple II DOS 3.3 disk images:

1.  EXAMPLES.v2d: Object code for the programs in AppleCore/Examples,
    except for Mini Adventure, Edify, and Below the Basement.

2.  MINI.ADVENTURE.v2d: Object code for the program "Mini Adventure"
    in AppleCore/Examples/MiniAdventure.

3.  EDIFY.v2d: Object code for the program "Edify" in
    AppleCore/Examples/Edify.

4.  BTB.v2d: Object code for the program "Below the Basement" in
    AppleCore/Examples/BelowTheBasement.

5.  SHELL.EDITOR.FP.dsk and SHELL.EDITOR.INT.dsk: A shell editor
    that's a bit nicer than the native Apple II one, plus ROM images
    patched to work with the editor.  

    If you boot SHELL.EDITOR.FP.dsk, you'll get Applesoft BASIC with
    the shell editor.  You then have the option to load the 1977 ROM
    and/or INT BASIC, if you wish.

    If you boot SHELL.EDITOR.INT.dsk, then you just get INT BASIC with
    the old ROM.

    The editor resides in $D000 RAM bank 1, so it works seamlessly
    with whatever is in bank 2 (usually Applesoft or INT BASIC).
    Also, once installed the editor is invoked automatically by
    AppleCore programs that ask for input via MON.GETLN.


