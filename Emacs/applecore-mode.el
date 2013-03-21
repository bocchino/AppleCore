;; AppleCore mode

(require 'generic-x)

(autoload 'caps-lock-mode "caps-lock"
  "Toggle to capitalize typed characters.

     \(fn &optional ARG)"
  'interactive)

(autoload 'global-caps-lock-mode "caps-lock"
  "Toggle Global Caps Lock Mode.

     \(fn &optional ARG)"
  'interactive)

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
    ("DATA[[:space:]]+\\([A-Za-z0-9_]+\\)[[:space:]]+\.+;" . 
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
  '(ac-setup)
  ;; Doc string
  "Major mode for AppleCore source files"
)

(defun ac-setup ()
  (setq-default indent-tabs-mode nil)
  (setq-default tab-width 2)
  (setq-default indent-line-function 'ac-indent)
  (local-set-key (kbd "M-c") 'ac-separator)
  (caps-lock-mode 1))

(defun ac-separator ()
  (interactive)
  (beginning-of-line)
  (insert "# -------------------------------------"))

(defun ac-indent ()
  (let ((want-relative 0))
    (save-excursion
      (beginning-of-line)
      (if (looking-at "^[ \t]*\\(CONST\\|DATA\\)")
	  (setq want-relative 1)))
    (if (= want-relative 1)
	(indent-relative)
      (ac-indent-decl))))
    
(defun ac-indent-decl ()
  (let (prev curr)
    (save-excursion
      (forward-line -1)
      (setq prev (current-indentation)))
    (setq curr (current-indentation))
    (if (< curr prev)
	(indent-line-to prev)
      (indent-line-to (+ curr tab-width)))))

(provide 'applecore-mode)
