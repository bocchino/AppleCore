;; AVM mode

(require 'generic-x)

(define-generic-mode 
  ;; Mode name
  'avm-mode
  nil
  nil
  ;; Other highlighting
  '(("^\\*.*$" . 'font-lock-comment-face)
    ("^[A-Za-z0-9\\.]*[\t ]+[A-Za-z0-9\\.]+ [^[:space:]]+[\t ]+\\(.+\\)$" . 
     (1 'font-lock-comment-face))
    ("^[A-Za-z0-9\\.]+" . 'font-lock-function-name-face)
    ("^[[:space:]]+\\([A-Za-z0-9\\.]+\\)" . 
     (1 'font-lock-keyword-face))
    ("^[A-Za-z0-9\\.]+[[:space:]]+\\([A-Za-z0-9\\.]+\\)" . 
     (1 'font-lock-keyword-face))
    ("^[A-Za-z0-9\\.]*[\t ]+\\.HS[[:space:]]+\\([[:xdigit:]]+\\)" .
     (1 'font-lock-constant-face))
    ("\\$[[:xdigit:]]*" . 'font-lock-constant-face)
    ("[^A-Za-z0-9\\.]\\([[:digit:]]+\\)" .
     (1 'font-lock-constant-face))
    ("'." . 'font-lock-constant-face))

  ;; File extension
  '("\\.avm$")
  nil
  ;; Doc string
  "AppleCore Virtual Machine syntax highlighting"
)

(provide 'avm-mode)
