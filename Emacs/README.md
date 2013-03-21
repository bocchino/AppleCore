AppleCore/Emacs
===============

This directory contains Emacs major modes for editing AppleCore source
and assembly files. The modes provide the following features:

1.  Syntax highlighting.

2.  A caps lock minor mode, which is on by default.  It can be turned
    off by toggling `caps-lock-mode`.

3.  For AppleCore files, minimal tab indentation (relative indentation
    works just fine for AVM files).  In applecore mode, pressing TAB
    indents the current line by the tab width (the default is two)
    unless the current line is to the left of the previous line, in
    which case the current line is indented to line up with the
    previous line.

The minimal indentation strategy works surprisingly well for entering
code.  Of course there are many things it won't do: for example, it
won't automatically un-indent closing braces.  However, implementing a
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

