signature INSTRUCTIONS =
  sig

  datatype instruction =
    Native of Native.instruction
  | Directive of Directives.directive
  | AppleCore of AppleCore.instruction

  end
