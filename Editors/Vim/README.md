AppleCore/Editors/Vim 
=====================

This directory contains a Vim syntax file for editing AppleCore source
files. The syntax file provides the following features:

1.  Syntax highlighting.

2.  Comment support: Pressing * moves the point to the beginning of the
line and inserts a comment character, a space, and 37 dashes.  This is
useful for AppleCore-style comments, which are inspired by the old SC Macro
Assembler style of comment.  These comments really stand out, and they show at
a glance how the program is divided into groups of related code.  See the
programs in $APPLECORE/Programs for examples.

3.  Indentation support based on Vim's cindent feature.

To use the modes, include the following lines in your .vimrc file:

   `au! Syntax ac source $APPLECORE/Editors/Vim/ac.vim`

   `au BufRead,BufNewFile *.{ac,ach} set filetype=ac`

This assumes that the environment variable APPLECORE is properly set; see the
main README file.

