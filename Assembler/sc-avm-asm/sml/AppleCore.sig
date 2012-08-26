signature APPLECORE =
sig
  
    datatype size =
	     Signed of int
	   | Unsigned of int
			 
    datatype constant =
	     Label of Label.t
	   | Literal of IntInf.int
		     
    datatype instruction =
	     BRF of Expression.t
	   | BRU of Expression.t
	   | CFD of Expression.t
	   | CFI
	   | ADD of int
	   | ANL of int
	   | DCR of int
	   | DSP of int
	   | ICR of int
	   | ISP of int
	   | MTS of int
	   | MTV of int * Expression.t
	   | NEG of int
	   | NOT of int
	   | ORL of int
	   | ORX of int
	   | PHC of constant
	   | PVA of int
	   | RAF of int
	   | SHL of int
	   | STM of int
	   | SUB of int
	   | TEQ of int
	   | VTM of int * Expression.t
	   | DIV of size
	   | EXT of size
	   | MUL of size
	   | SHR of size
	   | TGE of size
	   | TGT of size
	   | TLE of size
	   | TLT of size
		    
    val parse : Substring.substring -> instruction option

end
