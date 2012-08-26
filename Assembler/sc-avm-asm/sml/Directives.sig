signature DIRECTIVES =
sig
    
    datatype directive =
	     AS of string
	   | AT of string
	   | BS of Expression.t
	   | DA of Expression.t list
	   | EQ of Expression.t
	   | HS of int list   
	   | IN of string
	   | OR of Expression.t
	   | TF of string
	   | Ignored

    val parse : Substring.substring -> directive option
						
end
