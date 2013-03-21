AppleCore/Emacs
===============

This directory contains Emacs major modes for editing AppleCore source
and assembly files. The modes provide the following features:

1.  Syntax highlighting.

2.  A caps lock minor mode, which is on by default.  It can be turned
    off by toggling `caps-lock-mode`.

3.  Relative indentation.  Pressing TAB lines the cursor up with the
    next word in the previous line or (if there is no word in the
    previous line) with the next tab stop.  For AppleCore files, there
    are five tab stops set at two-space intervals.

Relative indentation works fairly well for this language, and it
requires no implementation.  On the other hand, implementing
full-fledged auto-indentation requires parsing the code, and I have
not been able to find good tool support for this.  Writing a parser
from scratch does not seem warranted at this time.

To use the modes, include the following lines in your .emacs file:

   `(add-to-list 'load-path (concat (getenv "APPLECORE") "/Emacs"))`

   `(require 'applecore-mode)`

   `(require 'avm-mode)`

This assumes that the environment variable APPLECORE is properly set;
see the main README file.

The contents of the file caps-lock.el are copyright (C) 2011 by Aaron
S. Hawley, and are distributed under the terms of the GNU General
Public License v.3.  See the file GNU-GPLv3.txt in this directory.

