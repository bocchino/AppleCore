AppleCore/Emacs
===============

This directory contains Emacs generic modes that provide syntax
highlighting for AppleCore source and assembly files.
Auto-indentation is not supported at this time.

To use the modes, include the following lines in your .emacs file:

   (add-to-list 'load-path (concat (getenv "APPLECORE") "/Emacs"))

   (require 'applecore-mode)

   (require 'avm-mode)

This assumes that the environment variable APPLECORE is properly set;
see the main README file.

