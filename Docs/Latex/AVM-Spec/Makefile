TEXFILES = ${wildcard *.tex}
MULTIHTML = multihtml
HACHAOPTS = -tocbis
TARGET=AVM-Spec-v1.0

all: $(TARGET).pdf

$(TARGET).pdf : ${TEXFILES} #$(TARGET).ps
	pdflatex $<
	pdflatex $<
	cp $@ ../../

clean:
		rm -f *.aux *.bbl *.log *.toc *.blg *~
		rm -Rf *.dvi *.ps *.pdf *.out

.PRECIOUS: $(TARGET).pdf

.phony: clean 
