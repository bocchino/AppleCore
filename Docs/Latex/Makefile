all : AppleCore-Spec-v1.0.pdf AVM-Spec-v1.0.pdf

%.pdf : %.tex defs.tex
	pdflatex $<
	pdflatex $<

clean ::
	rm -f *.pdf *.log *.aux