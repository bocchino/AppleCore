signature INSTRUCTIONS =
sig

    exception InvalidMnemonic of string
    
    datatype instruction =
	     Native of Native.instruction
	   | Directive of Directives.directive
	   | AppleCore of AppleCore.instruction
			
    val parse : Substring.substring -> instruction option

end
