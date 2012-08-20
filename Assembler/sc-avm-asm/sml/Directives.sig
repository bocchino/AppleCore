signature DIRECTIVES =
sig
    
    datatype directive =
	     AS of string
	   | AT of string
	   | BS of Operands.expr
	   | DA of Operands.expr list
	   | EQ of Operands.expr
	   | HS of int list   
	   | IN of string
	   | OR of Operands.expr
	   | TF of string
		   
    val parse : Substring.substring -> directive option
						
end
