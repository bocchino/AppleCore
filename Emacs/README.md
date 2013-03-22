AppleCore/Emacs
===============

This directory contains Emacs major modes for editing AppleCore source
and assembly files. The modes provide the following features:

1.  Syntax highlighting.

2.  A caps lock minor mode, which is on by default.  It can be turned
    off by toggling `caps-lock-mode`.

3.  Comment support: Pressing ESC-c moves the point to the beginning
    of the line and inserts a comment character, a space, and 37
    dashes.  This is useful for AppleCore-style comments, which are
    inspired by the old SC Macro Assembler style of comment.  These
    comments really stand out, and they show at a glance how the
    program is divided into groups of related code.  See the programs
    in $APPLECORE/Programs for examples.

4.  Minimal indentation support, as follows:

  - AVM files, and CONST and DATA declarations in AppleCore files, use
    a modified form of relative indentation. Pressing TAB inserts
    spacing to move the point to the start of the next word in the
    previous line, or to the next tab stop if there is no word in the
    previous line. This indentation style is useful for for lining up
    labels and declarations.

  - In AppleCore files, lines beginning with FN or INCLUDE shouldn't
    ever be indented, so TAB does nothing for these lines.

  - In AppleCore files, lines beginning with a close brace are
    indented to the previous line's indentation minus the current tab
    width (the default is 2).

  - Other lines in AppleCore files use the following simple strategy:
    TAB indents the line by the current tab width unless the current
    line is left of the previous line, in which case the line is
    shifted rightwards to match the previous line.

This simple indentation scheme is already very helpful in writing
AppleCore programs.  Of course it doesn't do everything. Implementing
full-fledged auto-indentation requires parsing, and I have not been
able to find adequate tool support for this.  Writing a parser from
scratch does not seem warranted at this time.

To use the modes, include the following lines in your .emacs file:

   `(add-to-list 'load-path (concat (getenv "APPLECORE") "/Emacs"))`

   `(require 'applecore-mode)`

   `(require 'avm-mode)`

This assumes that the environment variable APPLECORE is properly set;
see the main README file.

The contents of the file caps-lock.el are copyright (C) 2011 by Aaron
S. Hawley, and are distributed under the terms of the GNU General
Public License v.3.  See the file GNU-GPLv3.txt in this directory.

