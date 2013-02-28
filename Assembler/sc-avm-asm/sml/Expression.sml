structure Expression : EXPRESSION = 
struct

open Char
open Error
open Label
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
	 (case Label.parseExpr substr of
	      SOME (l,substr') => SOME (Label l,substr')
	    | _ => 
	      (case Substring.getc substr of
	           SOME(#"'",substr') =>
		   (case Substring.getc substr' of
		       SOME (c,substr'') => SOME (Character c,substr'')
		     | _                 => raise AssemblyError BadAddress)
		 | SOME(#"*",substr') => SOME (Star,substr')
		 | _ => NONE))

(* Evaluate a term, given bindings for labels and for * *)
fun eval (addr,map) term =
    case term of
	Number n => term
      | Label l => 
	(case lookup (map,l) of
	     SOME n => Number n
	   | NONE   => term)
      | Character c => Number (ord c)
      | Star => Number addr

(* Report whether a term represents a zero-page address *)
fun isZeroPage (term:t) =
    case term of
	Number n => Numbers.isZeroPage n
      | _ => false
    
end
	    
open Term

(* An expression is a single term or a binary operation
   comprising an expression and a term. Binary operations
   are left-recursive. *)
datatype t =
	 Term of Term.t
       | Add of t * Term.t
       | Sub of t * Term.t
       | Mul of t * Term.t
       | Div of t * Term.t

(* Parse an expression. Binary operations are left-
   associative. *)
fun parse substr =
    let
        fun parse' e substr =
	    case getc substr of
		SOME (#"+",substr') => binop e Add substr'
	      | SOME (#"-",substr') => binop e Sub substr'
	      | SOME (#"*",substr') => binop e Mul substr'
	      | SOME (#"/",substr') => binop e Div substr'
	      | _                   => SOME (e,substr)
	and binop e oper substr =
	    case Term.parse substr of
		SOME (t,substr') => parse' (oper (e,t)) substr'
	      | _                => raise AssemblyError BadAddress
    in
	case Term.parse (dropl isSpace substr) of
	    SOME (t,substr') => parse' (Term t) substr'
	  | NONE             => NONE
    end

fun parseArg substr =
    case parse substr of
	SOME (e,_) => e
      | _          => raise AssemblyError BadAddress
			    
fun eval (addr,map) expr =
    let 
	fun evalBinop (lhs,constr,oper,rhs) =
	    let
		val lhs = eval (addr,map) lhs
		val rhs = Term.eval (addr,map) rhs
	    in
		case (lhs,rhs) of
		    (Term (Number n),Number n') =>
		    Term (Number (oper(n,n')))
		  | _ => constr (lhs,rhs)
	    end
    in
	case expr of
	    Term term => Term (Term.eval (addr,map) term)
	  | Add (expr,term) => evalBinop(expr,Add,op+,term)
	  | Sub (expr,term) => evalBinop(expr,Sub,op-,term)
	  | Mul (expr,term) => evalBinop(expr,Mul,op*,term)
	  | Div (expr,term) => evalBinop(expr,Div,op div,term)
    end

fun evalAsAddr (addr,map) expr =
    case eval (addr,map) expr of
	Term (Number n) => n
      | _ => raise AssemblyError UndefinedLabel

fun isZeroPage (expr:t) =
    case expr of
	Term term => Term.isZeroPage term
      | _ => false

end
