signature OPERANDS =
sig

    datatype term =
	   Number of int
	 | Label of Labels.label
	 | Character of char
	 | Star
    
    datatype expr =
	     Term of term
	   | Add of term * expr
	   | Sub of term * expr
	   | Mul of term * expr
	   | Div of term * expr

    val parseHexString : Substring.substring -> int list
    val parseTerm : Substring.substring -> (term * Substring.substring) option					  
    val parseExpr : Substring.substring -> (expr * Substring.substring) option
    val parseExprArg : Substring.substring -> expr
					      
    val parseList : (Substring.substring -> ('a * Substring.substring) option)
		    -> Substring.substring
		    -> ('a list * Substring.substring) option
    val parseNumberArg : Substring.substring -> IntInf.int

end

