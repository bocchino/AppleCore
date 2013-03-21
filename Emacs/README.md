AppleCore/Emacs
===============

This directory contains Emacs major modes for editing AppleCore source
and assembly files. The modes provide the following features:

1.  Syntax highlighting.

2.  A caps lock minor mode, which is on by default.  It can be turned
    off by toggling `caps-lock-mode`.

3.  Minimal indentation support, as follows:

    - AVM files, and CONST and DATA declarations in AppleCore files,
      use Emacs-standard relative indentation. Pressing TAB inserts
      enough space to move the cursor position to the start of the
      next word in the previous line, or to the next tab stop if there
      is no word in the previous line. This indentation style is
      useful for for lining up labels and declarations.

    - For other declarations in AppleCore files, inserting space in
      the middle of the line doesn't make sense; and in the case of
      statement blocks, the indentation should occur from the left.
      Therefore these declarations use the following simple strategy:
      TAB indents the entire line by the current tab width (the
      default is 2) unless the current line is to the left of the
      previous line, in which case the line is pushed over to match
      the previous line.

This simple indentation scheme seems to work fairly well. Implementing
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

