" Vim syntax file
" Language:    AppleCore
" Author:      Robert L. Bocchino Jr.
" Last change: July 2013

" Remove any old syntax stuff
syn clear

setl tabstop=2
setl shiftwidth=2
setl expandtab
setl cinwords=IF,ELSE,WHILE,FN
setl cindent

map * 0i# ------------------------------------- 

" String constants
syn match acString	"\".*\""

" Comments
syn match acComment	"#.*"

" Keywords
syn keyword acKeyword	AND CONST DATA DECR
syn keyword acKeyword 	ELSE FN IF INCLUDE
syn keyword acKeyword	INCR NOT OR RETURN
syn keyword acKeyword	SET VAR XOR WHILE

" Numeric constants
" Integer constants
syn match acIntConst 	"\<[[:digit:]]\+\>"
" Hex constants
syn match acHexConst	"\$\<[[:xdigit:]]\+\>"

" Character constants
syn match acCharConst	"'.'"

hi link acString	String
hi link acComment	Comment
hi link acKeyword	Keyword
hi link acIntConst	Number
hi link acHexConst	Number
hi link acCharConst  	Character

let b:current_syntax = "ac"
