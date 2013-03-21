;;; caps-lock.el --- Caps Lock emulation minor modes

;; Copyright (C) 2011 Aaron S. Hawley

;; Author:  Aaron S. Hawley <Aaron S Hawley gmail com>
;; Keywords: convenience, emulations

;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License as
;; published by the Free Software Foundation, either version 3 of
;; the License, or (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

;; This file is NOT part of Emacs.

;;; Commentary:

;; Emulate Caps Lock key by capitalizing inserted characters.
;; Capitalizes repeatedly inserted characters by a prefix argument.

;; This emulation gives the benefit of not needing to have a Caps Lock
;; key on the keyboard.  A popular keyboard customization for using
;; Emacs is changing Caps Lock to C- (Ctrl).  Additionally, Caps Lock
;; Mode doesn't affect input in the minibuffer.

;; This minor mode can be either buffer local with M-x caps-lock-mode
;; or global with M-x global-caps-lock-mode.  Neither affects the
;; minibuffer since case-sensitive Emacs commands like `M-x' will
;; become useless and key bindings will become less predictable.
;; However, you can enable Caps Lock Mode for a minibuffer with M-x
;; caps-lock-in-minibuffer.  Or, if you have
;; `enable-recursive-minibuffers', run M-x caps-lock-mode while in the
;; minibuffer.  Disable Caps Lock Mode for the minibuffer with M-x
;; caps-lock-in-minibuffer or by running M-x caps-lock-mode again
;; while in the minibuffer.

;;; Known issues:

;; Holding down S- (shift key) while hitting a self inserting key will
;; not disable Caps Lock Mode and insert lowercase character(s).

;; Repeat complex command, `C-x ESC ESC', evaluates commands as Lisp
;; expressions so there's no way for Caps Lock Mode to know it was a
;; self inserting key, or how many inserts were made with a prefix
;; argument.

;;; Install:

;; Put this file in a directory in your `load-path', then add the
;; following code in your .emacs initialization file.

;;     (autoload 'caps-lock-mode "caps-lock"
;;       "Toggle to capitalize typed characters.
;;
;;     \(fn &optional ARG)"
;;       'interactive)
;;
;;     (autoload 'global-caps-lock-mode "caps-lock"
;;       "Toggle Global Caps Lock Mode.
;;
;;     \(fn &optional ARG)"
;;       'interactive)

;; To toggle these modes with a key binding try the following.

;; (global-set-key [f12]   'caps-lock-mode)
;; (global-set-key [S-f12] 'global-caps-lock-mode)

;; If you have other inserting commands that Caps Lock Mode should
;; recognize, add them with the following Lisp.

;;     (eval-after-load 'caps-lock
;;       '(add-to-list 'caps-lock-inserting-commands
;;                     'the-incredible-insert-character-machine))

;; Or by interactively adding them to this customization with
;; M-x customize-variable RET caps-lock-inserting-commands RET

;;; History:

;; Written on October 5, 2011 in Burlington, VT, USA.

;;; Code:

;; (declare-function upcase-char "misc" (arg))

(autoload 'upcase-char "misc"
  "Uppercasify ARG chars starting from point.  Point doesn't move.

\(fn arg)"
  'interactive)

(defvar caps-lock-mode)
(defvar global-caps-lock-mode)

(defvar caps-lock-in-minibuffer nil
  "Whether Caps Lock Mode is enabled in the minibuffer.
See command `caps-lock-mode'.")

;;;###autoload
(define-minor-mode caps-lock-mode
  "Toggle to capitalize typed characters.

Does not capitalize characters inserted by other methods other
than keyboard entry.  Commands that should be capitalized can be
customized with `caps-lock-inserting-commands'.

This mode is buffer local.  See `global-caps-lock-mode'

This mode doesn't apply to the minibuffer.
See the command `caps-lock-in-minibuffer' for that.

See also `upcase-char' and `upcase-region'."
  :lighter " CAPS" :group 'editing-basics
  (when (and global-caps-lock-mode caps-lock-mode)
    (setq caps-lock-mode nil)
    (error "Global Caps Lock Mode already enabled"))
  (if (and (not caps-lock-in-minibuffer) (minibufferp))
      (setq caps-lock-in-minibuffer t)
    (setq caps-lock-in-minibuffer nil))
  (if (or caps-lock-mode caps-lock-in-minibuffer)
      (add-hook 'post-command-hook 'caps-lock-insert-hook)
    ;; Is caps-lock-mode on anywhere?
    (when (null
           (memq t
                 (mapcar
                  (lambda (b)
                    (buffer-local-value 'caps-lock-mode b))
                  (buffer-list))))
      (remove-hook 'post-command-hook 'caps-lock-insert-hook))))

;;;###autoload
(define-minor-mode global-caps-lock-mode
  "Toggle Global Caps Lock Mode.

See command `caps-lock-mode'."
  :global t :lighter " CAPS" :group 'editing-basics
  (if global-caps-lock-mode
      (progn
        ;; Turn off caps lock mode in all buffers.
        (mapc (lambda (b)
                (with-current-buffer b
                  (when caps-lock-mode
                    (caps-lock-mode 0))))
              (buffer-list))
        (when (and caps-lock-mode (minibufferp))
          (setq caps-lock-in-minibuffer t))
        (add-hook 'post-command-hook 'caps-lock-insert-hook))
    (remove-hook 'post-command-hook 'caps-lock-insert-hook)))

(defmacro caps-lock-with-minibuffer (&rest body)
  "Execute the forms in BODY in minibuffer."
  (declare (indent 1) (debug t))
  `(with-current-buffer (window-buffer (minibuffer-window))
     ,@body))

(font-lock-add-keywords 'emacs-lisp-mode '("caps-lock-with-minibuffer"))

;;;###autoload
(defun caps-lock-in-minibuffer ()
  "Toggle Caps Lock Mode in the minibuffer.

See command `caps-lock-mode'."
  (interactive)
  (caps-lock-with-minibuffer
    (if caps-lock-in-minibuffer
        (caps-lock-mode 0)
      (caps-lock-mode 1))))

(defcustom caps-lock-inserting-commands
  '(self-insert-command
    isearch-printing-char
    quoted-insert
    ucs-insert)
  "Commands that insert and Caps Lock Mode should later capitalize.

See command `caps-lock-mode'."
  :group 'editing-basics
  :type '(repeat function))

(defun caps-lock-insert-hook ()
  "Capitalize last inserted character(s)."
  (when (and (or (and caps-lock-in-minibuffer
                       (or (minibufferp) isearch-mode))
                 (and (or caps-lock-mode global-caps-lock-mode)
                      (not (minibufferp))))
             (memq this-command caps-lock-inserting-commands))
    (if isearch-mode
        (let ((last (upcase (substring isearch-string -1))))
          (isearch-del-char)
          (isearch-process-search-string
           last (mapconcat 'isearch-text-char-description last "")))
      (upcase-char (- (prefix-numeric-value current-prefix-arg))))))

(provide 'caps-lock)
;;; caps-lock.el ends here
