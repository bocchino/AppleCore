signature APPLECORE =
  sig

  datatype size =
    Signed of int
  | Unsigned of int

  datatype instruction =
    BRF of Parser.expr
  | BRU of Parser.expr
  | CFD of Parser.expr
  | CFI
  | ADD of int
  | ANL of int
  | DCR of int
  | DSP of int
  | ICR of int
  | ISP of int
  | MTS of int
  | MTV of int
  | NEG of int
  | NOT of int
  | ORL of int
  | ORX of int
  | PHC of int
  | PVA of int
  | RAF of int
  | SHL of int
  | STM of int
  | SUB of int
  | TEQ of int
  | VTM of int * Parser.expr
  | DIV of size
  | EXT of size
  | MUL of size
  | SHR of size
  | TGE of size
  | TGT of size
  | TLE of size
  | TLT of size

  val parseInstruction : Substring.substring -> instruction option

  end
