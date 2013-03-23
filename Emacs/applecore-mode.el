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
  (let ((select 0))
    (save-excursion
      (beginning-of-line)
      (if (looking-at "^[ \t]*\\(CONST\\|DATA\\)")
	  (setq select 1))
      (if (looking-at "^[ \t]*\\(FN\\|INCLUDE\\)")
	  (setq select 2)))
    (when (= select 0)
      (ac-indent-decl))
    (when (= select 1)
      (ac-indent-relative))))
    
(defun ac-indent-decl ()
  (let (prev curr)
    (save-excursion
      (forward-line -1)
      (setq prev (current-indentation)))
    (setq curr (current-indentation))
    (beginning-of-line)
    (if (looking-at "[ \t]*\\}")
	(indent-line-to (max 0 (- prev tab-width)))
      (if (< curr prev)
	(indent-line-to prev)
	(indent-line-to (+ curr tab-width))))))

(defun ac-indent-relative ()
  (if (looking-at "[^[:space:]]")
    (progn
      (re-search-backward "\\(^\\|[ \t]\\)")
      (when (looking-at "[ \t]")
	(forward-char 1))
      (when (not (looking-at "\\(CONST\\|DATA\\)"))
	(indent-relative)))
    (progn
      (when (looking-at ".*[^[:space:]]")
	(progn
	  (re-search-forward "[^[:space:]]")
	  (backward-char 1)))
      (indent-relative))))

(provide 'applecore-mode)
