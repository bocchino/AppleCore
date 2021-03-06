;; AVM mode

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
  'avm-mode
  nil
  nil
  ;; Other highlighting
  '(("^\\*.*$" . 'font-lock-comment-face)
    ("^[A-Za-z0-9\\.]*[\t ]+[A-Za-z0-9\\.]+ [^[:space:]]*[\t ]+\\(.+\\)$" . 
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
  '(avm-setup)
  ;; Doc string
  "Major mode for AppleCore Virtual Machine assembly files"
)

(defun avm-setup ()
  (setq-default indent-tabs-mode nil)
  (setq-default tab-width 8)
  (setq indent-line-function 'avm-indent-relative)
  (local-set-key (kbd "M-c") 'avm-separator)
  (caps-lock-mode 1))

(defun avm-separator ()
  (interactive)
  (beginning-of-line)
  (insert "* -------------------------------------"))

(defun avm-indent-relative ()
  (let ((prev (preceding-char)))
    (when (or 
	   (not (looking-at "[^[:space:]]"))
	   (string-match "[ \t]" (string prev)))
      (indent-relative))))


(provide 'avm-mode)
