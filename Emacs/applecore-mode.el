;; AppleCore mode

(require 'generic-x)

(define-generic-mode 
  ;; Mode name
  'applecore-mode
  ;; Comment delimiter
  '("#")          
  ;; Keywords
  '("AND"  "CONST" "DATA" "DECR"     
    "ELSE" "FN"    "IF"   "INCLUDE"
    "INCR" "NOT"   "OR"   "RETURN"
    "SET"  "VAR"   "XOR"  "WHILE")
  ;; Other highlighting
  '(("CONST[[:space:]]+\\([A-Za-z0-9_]*\\)" .
     (1 'font-lock-variable-name-face))
    ("VAR[[:space:]]+\\([A-Za-z0-9_]*\\)" . 
     (1 'font-lock-variable-name-face))
    ("DATA[[:space:]]+\\([A-Za-z0-9_]+\\)[[:space:]]+.*;" . 
     (1 'font-lock-variable-name-face))
    ("FN[[:space:]]+\\([A-Za-z0-9_]*\\)" . 
     (1 'font-lock-function-name-face))
    ("'.'" . 'font-lock-constant-face)
    ("\\$[[:xdigit:]]*" . 'font-lock-constant-face)
    ("^[[:digit:]]+" . 'font-lock-constant-face)
    ("[^_A-Za-z0-9]\\([[:digit:]]+\\)" .
     (1 'font-lock-constant-face)))
  ;; File extensions
  '("\\.ach?$")
  nil
  ;; Doc string
  "AppleCore syntax highlighting"
)

(provide 'applecore-mode)
