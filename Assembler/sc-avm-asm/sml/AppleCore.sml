structure AppleCore : INSTRUCTION =
struct

open Error

datatype size =
         Signed of int
       | Unsigned of int

datatype constant =
         Label of Label.t
       | Literal of IntInf.int
                     
datatype t =
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

fun parseUnsigned substr =
    Numbers.normalize 256 (Numbers.parseArg substr)
    
fun parseSigned substr =
    case Numbers.parse (Substring.dropl Char.isSpace substr) of
        SOME (num,rest) =>
        let
            val num' = Numbers.normalize 256 num
        in
            case Substring.getc rest of
                SOME (c,_) => if (Char.toUpper c) = #"S" 
                              then Signed num'
                              else Unsigned num'
              | NONE => Unsigned num'
        end
      | _ => raise AssemblyError BadAddress

fun parseConstant substr =
    let
        val constant = (Substring.dropl Char.isSpace substr)
    in
        case Numbers.parse constant of
            SOME (n,_) => Literal n
          | NONE => (case Label.parseExpr constant of
                         SOME (l,_) => Label l
                       | NONE => raise AssemblyError BadAddress)
    end
                   
fun parseMV (instr,delim) substr =
    case Numbers.parse (Substring.dropl Char.isSpace substr) of
        SOME (var,rest) => 
        let
            val (d,rest') = Substring.splitAt(Substring.dropl Char.isSpace rest,2) 
                handle Subscript => raise AssemblyError BadAddress
        in
            if (Substring.string d) = delim
            then instr (Numbers.normalize 256 var,Expression.parseArg rest')
            else raise AssemblyError BadAddress
        end
      | _ => raise AssemblyError BadAddress
                   
                   
fun parse substr =
    let
        val (mem,rest) = Substring.splitl (not o Char.isSpace) substr 
    in
        case (Substring.translate (Char.toString o Char.toUpper) mem) of
            "BRF" => SOME (BRF (Expression.parseArg rest))
          | "BRU" => SOME (BRU (Expression.parseArg rest))
          | "CFD" => SOME (CFD (Expression.parseArg rest))
          | "CFI" => SOME CFI  
          | "ADD" => SOME (ADD (parseUnsigned rest))
          | "ANL" => SOME (ANL (parseUnsigned rest))
          | "DCR" => SOME (DCR (parseUnsigned rest))
          | "DSP" => SOME (DSP (parseUnsigned rest))
          | "ICR" => SOME (ICR (parseUnsigned rest))
          | "ISP" => SOME (ISP (parseUnsigned rest))
          | "MTS" => SOME (MTS (parseUnsigned rest))
          | "MTV" => SOME (parseMV (MTV,"<-") rest)
          | "NEG" => SOME (NEG (parseUnsigned rest))
          | "NOT" => SOME (NOT (parseUnsigned rest))
          | "ORL" => SOME (ORL (parseUnsigned rest))
          | "ORX" => SOME (ORX (parseUnsigned rest))
          | "PHC" => SOME (PHC (parseConstant rest))
          | "PVA" => SOME (PVA (parseUnsigned rest))
          | "RAF" => SOME (RAF (parseUnsigned rest))
          | "SHL" => SOME (SHL (parseUnsigned rest))
          | "STM" => SOME (STM (parseUnsigned rest))
          | "SUB" => SOME (SUB (parseUnsigned rest))
          | "TEQ" => SOME (TEQ (parseUnsigned rest))
          | "VTM" => SOME (parseMV (VTM,"->") rest)
          | "DIV" => SOME (DIV (parseSigned rest))
          | "EXT" => SOME (EXT (parseSigned rest))
          | "MUL" => SOME (MUL (parseSigned rest))
          | "SHR" => SOME (SHR (parseSigned rest))
          | "TGE" => SOME (TGE (parseSigned rest))
          | "TGT" => SOME (TGT (parseSigned rest))
          | "TLE" => SOME (TLE (parseSigned rest))
          | "TLT" => SOME (TLT (parseSigned rest))
          | _ => NONE
    end

fun includeIn inst file = file

local
    fun unsignedSize sz =
        if sz >= 1 andalso sz <= 7
        then 1 else 2
    fun signedSize' sz =
        if sz >= 1 andalso sz <= 3
        then 1 else 2
    fun signedSize (Signed sz) = signedSize' sz
      | signedSize (Unsigned sz) = signedSize' sz
    fun constantSize (Label label) = 2
      | constantSize (Literal n) = Numbers.sizeOf n
    fun sizeOf (BRF expr) = 3
      | sizeOf (BRU expr) = 3
      | sizeOf (CFD expr) = 3
      | sizeOf (CFI) = 1
      | sizeOf (ADD sz) = unsignedSize sz
      | sizeOf (ANL sz) = unsignedSize sz
      | sizeOf (DCR sz) = unsignedSize sz
      | sizeOf (DSP sz) = unsignedSize sz
      | sizeOf (ICR sz) = unsignedSize sz
      | sizeOf (ISP sz) = unsignedSize sz
      | sizeOf (MTS sz) = unsignedSize sz
      | sizeOf (MTV (sz,exp)) = 1 + (unsignedSize sz)
      | sizeOf (NEG sz) = unsignedSize sz
      | sizeOf (NOT sz) = unsignedSize sz
      | sizeOf (ORL sz) = unsignedSize sz
      | sizeOf (ORX sz) = unsignedSize sz
      | sizeOf (PHC c) = 
        let
            val sz = constantSize c
        in 
            sz + (unsignedSize sz)
        end
      | sizeOf (PVA sz) = unsignedSize sz
      | sizeOf (RAF sz) = unsignedSize sz
      | sizeOf (SHL sz) = unsignedSize sz
      | sizeOf (STM sz) = unsignedSize sz
      | sizeOf (SUB sz) = unsignedSize sz
      | sizeOf (TEQ sz) = unsignedSize sz
      | sizeOf (VTM (sz,exp)) = 1 + (unsignedSize sz)
      | sizeOf (DIV sz) = signedSize sz
      | sizeOf (EXT sz) = signedSize sz
      | sizeOf (MUL sz) = signedSize sz
      | sizeOf (SHR sz) = signedSize sz
      | sizeOf (TGE sz) = signedSize sz
      | sizeOf (TGT sz) = signedSize sz
      | sizeOf (TLE sz) = signedSize sz
      | sizeOf (TLT sz) = signedSize sz
in
fun pass1 (label,inst) ({sourceLine,address},map) = 
    (inst,address + (sizeOf inst),map)
end

fun instBytes (inst,addr,map) = 
    let
        fun bytes expr =
            Numbers.bytes (Expression.evalAsAddr (addr,map) expr)
        fun unsigned opcode size =
            if size >= 1 andalso size <= 7 then
                [opcode + size]
            else 
                [opcode,size]
        fun signed opcode size =
            let
                val (opcode,size) = case size of
                                        Signed size => (opcode + 4,size)
                                      | Unsigned size => (opcode,size)
            in
                if size >= 1 andalso size <= 3 then
                    [opcode + size]
                else
                    [opcode,size]
            end
        fun MV opcode offset expr =
            let
                val MVaddr = Expression.evalAsAddr (addr,map) expr
            in
                if (Numbers.isZeroPage MVaddr) then
                    (unsigned opcode offset) @ [Numbers.lowByte MVaddr]
                else
                    raise AssemblyError RangeError
            end
        fun pushConstant (Label label) =
            let
                val constVal = case Label.lookup (map,label) of
                                    SOME n => n
                                  | NONE   => raise AssemblyError UndefinedLabel
            in
                0x6A :: (Numbers.bytes constVal)
            end
          | pushConstant (Literal literal) =
            let
                val constBytes = Numbers.constBytes literal
                val constSize = Numbers.sizeOf literal
            in
                (unsigned 0x68 constSize) @ constBytes
            end
    in
        case inst of
            BRF expr => 0x01 :: (bytes expr)
          | BRU expr => 0x02 :: (bytes expr)
          | CFD expr => 0x03 :: (bytes expr)
          | CFI => [0x04]
          | ADD size => unsigned 0x08 size
          | ANL size => unsigned 0x10 size
          | DCR size => unsigned 0x18 size
          | DSP size => unsigned 0x20 size
          | ICR size => unsigned 0x28 size
          | ISP size => unsigned 0x30 size
          | MTS size => unsigned 0x38 size
          | MTV (offset,expr) => MV 0x40 offset expr
          | NEG size => unsigned 0x48 size
          | NOT size => unsigned 0x50 size
          | ORL size => unsigned 0x58 size
          | ORX size => unsigned 0x60 size
          | PHC constant => pushConstant constant
          | PVA size => unsigned 0x70 size
          | RAF size => unsigned 0x78 size
          | SHL size => unsigned 0x80 size
          | STM size => unsigned 0x88 size
          | SUB size => unsigned 0x90 size
          | TEQ size => unsigned 0x98 size
          | VTM (offset,expr) => MV 0xA0 offset expr
          | DIV size => signed 0xA8 size
          | EXT size => signed 0xB0 size
          | MUL size => signed 0xB8 size
          | SHR size => signed 0xC0 size
          | TGE size => signed 0xC8 size
          | TGT size => signed 0xD0 size
          | TLE size => signed 0xD8 size
          | TLT size => signed 0xE0 size
    end

fun pass2 (output,sourceLine,inst,addr,map) =
    let
        val bytes = instBytes (inst,addr,map)
    in
        (Output.addBytes (output,bytes),
         Printing.formatLine (SOME addr,bytes,File.data sourceLine))
    end
end
