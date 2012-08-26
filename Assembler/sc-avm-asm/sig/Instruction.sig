signature INSTRUCTION =
sig

    datatype t =
	     Native of Native.instruction
	   | Directive of Directives.directive
	   | AppleCore of AppleCore.instruction
			
    val parse : Substring.substring -> t option

end
