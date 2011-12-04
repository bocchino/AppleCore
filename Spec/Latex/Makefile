all : AppleCore-Spec-v1.0.pdf

%.pdf : %.tex
	pdflatex $<
	pdflatex $<

clean ::
	rm -f *.pdf *.log *.aux