structure Expression : EXPRESSION = 
struct

open Char
open Error
open LabelMap
open Substring

structure Term = struct

datatype t =
	 (* A concrete operand *)
	 Number of int
       (* A symbolic operand *)
       | Label of Label.t
       (* A character such as 'A *)
       | Character of char
       (* A star indicating the current address *)
       | Star

(* Parse a term from a substring *)	 
fun parse substr =
    case Numbers.parse substr of
	SOME (n,substr') => SOME (Number (Numbers.normalize 65536 n),substr')
      |  _ => 
	 (case Label.parse substr of
	      SOME (l,substr') => SOME (Label l,substr')
	    | _ => 
	      (case Substring.getc substr of
	           SOME(#"'",substr') =>
		   (case Substring.getc substr' of
		       SOME (c,substr'') => SOME (Character c,substr'')
		     | _                 => raise BadAddress)
		 | SOME(#"*",substr') => SOME (Star,substr')
		 | _ => NONE))

(* Evaluate a term, given bindings for labels and for * *)
fun eval (labelMap:map,starAddr:int) (term:t) =
    case term of
	Number n => term
      | Label l => 
	(case lookup (labelMap,l) of
	     SOME n => Number n
	   | NONE   => term)
      | Character c => Number (ord c)
      | Star => Number starAddr

(* Report whether a term represents a zero-page address *)
fun isZeroPage (term:t) =
    case term of
	Number n => Numbers.isZeroPage n
      | _ => false
    
end
	    
open Term

(* An expression is a single term or a binary operation
   comprising a term and an expression *)
datatype t =
	 Term of Term.t
       | Add of Term.t * t
       | Sub of Term.t * t
       | Mul of Term.t * t
       | Div of Term.t * t
		
fun parse substr =
    let fun binop oper t substr =
	    case parse substr of
		SOME (e,substr'') => SOME (oper(t,e),substr'')
              | _                 => raise BadAddress
    in
	case Term.parse (dropl isSpace substr) of
	    SOME (t,substr') =>
	    (case getc (dropl isSpace substr') of
		SOME (#"+",substr'') => binop Add t substr''
              | SOME (#"-",substr'') => binop Sub t substr''
              | SOME (#"*",substr'') => binop Mul t substr''
              | SOME (#"/",substr'') => binop Div t substr''
	      | _                    => SOME (Term t,substr'))
          | _ => NONE
    end
    
fun parseListRest parse results substr =
    case getc (dropl isSpace substr) of
	SOME (#",",substr') =>
	(case parse (dropl isSpace substr') of
	    SOME (result, substr'') => 
	    parseListRest parse (result :: results) substr''
          | _ => raise BadAddress)
      | _ => SOME (List.rev results,substr)
	     
fun parseArg substr =
    case parse substr of
	SOME (e,_) => e
      | _          => raise BadAddress
			    
fun parseList parse substr =
    case parse (dropl isSpace substr) of
	SOME (result,substr') => parseListRest parse [result] substr'
      | _ => SOME ([],substr)

fun eval (labelMap:map,starAddr:int) (expr:t) =
    let 
	fun evalBinop(lhs,constr,oper,rhs) =
	    let
		val lhs = Term.eval (labelMap,starAddr) lhs
		val rhs = eval (labelMap,starAddr) rhs
	    in
		case (lhs,rhs) of
		    (Number n,Term (Number n')) =>
		    Term (Number (oper(n,n')))
		  | _ => constr (lhs,rhs)
	    end
    in
	case expr of
	    Term term => Term (Term.eval (labelMap,starAddr) term)
	  | Add (expr,term) => evalBinop(expr,Add,op+,term)
	  | Sub (expr,term) => evalBinop(expr,Sub,op-,term)
	  | Mul (expr,term) => evalBinop(expr,Mul,op*,term)
	  | Div (expr,term) => evalBinop(expr,Div,op div,term)
    end

fun evalAsAddr (labelMap:map,starAddr:int) (expr:t) =
    case eval (labelMap,starAddr) expr of
	Term (Number n) => n
      | _ => raise UndefinedLabel

fun isZeroPage (expr:t) =
    case expr of
	Term term => Term.isZeroPage term
      | _ => false

end
