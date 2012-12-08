TEXFILES = ${wildcard *.tex}
MULTIHTML = multihtml
HACHAOPTS = -tocbis
TARGET=AppleCore-Spec-v1.0

all: $(TARGET).pdf

$(TARGET).pdf : ${TEXFILES} #$(TARGET).ps
	pdflatex $<
	pdflatex $<

clean:
		rm -f *.aux *.bbl *.log *.toc *.blg *~
		rm -Rf *.dvi *.ps *.pdf *.out

publish: ${TARGET}.pdf
	cp $< ../../

.PRECIOUS: $(TARGET).pdf

.phony: clean 
