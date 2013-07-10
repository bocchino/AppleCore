" Vim syntax file
" Language:    AppleCore Virtual Machine (AVM)
" Author:      Robert L. Bocchino Jr.
" Last change: July 2013

" Remove any old syntax stuff
syn clear

setl tabstop=8
setl shiftwidth=2
setl expandtab

map * 0i* ------------------------------------- 

" String constants
syn match avmStringConst "\".*\"" contained

" Comments
syn match avmComment	"^\*.*"

" Character constants
syn match avmCharConst	"'." contained

" Labels
syn region avmLine matchgroup=avmLabel start="^[[:alnum:]\.]*[\t ]\+" end="$" contains=avmInst oneline keepend
syn match avmLabel "^[[:alnum:]\.]\+[\t ]*$"

syn region avmInst matchgroup=avmMnemonic start="..." end="$" contains=avmStringConst,avmCharConst contained keepend oneline

hi link avmComment	Comment
hi link avmLabel	Function
hi link avmMnemonic	Keyword
hi link avmStringConst	String
hi link avmCharConst  	Character

let b:current_syntax = "avm"
