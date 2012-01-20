TEXFILES = ${wildcard *.tex}
MULTIHTML = multihtml
HACHAOPTS = -tocbis
TARGET=AVM-Spec-v1.0

all: $(TARGET).pdf ${MULTIHTML}/index.html

${TARGET}.dvi : ${TEXFILES}
	-rm -f *.aux
	latex $(TARGET).tex
	latex $(TARGET).tex

$(TARGET).ps : $(TARGET).dvi
	dvips -o $@ $< -t letter

$(TARGET).pdf : $(TARGET).ps
	ps2pdf $<

${TARGET}.html : ${TEXFILES} ${BIBFILES}
	rm -f *.aux *.haux
	hevea -s $(TARGET).tex
	hevea -fix -O $(TARGET).tex

${MULTIHTML}/index.html : $(MULTIHTML) $(TARGET).html
	hacha $(HACHAOPTS) -o $(MULTIHTML)/index.html $(TARGET).html

$(MULTIHTML) :
	mkdir $@

clean:
		rm -f *.h{aux,bbl,toc} *.aux *.bbl *.log *.toc *.blg *~
		rm -Rf *.dvi *.ps *.pdf *.html $(MULTIHTML) *.out

.PRECIOUS: $(TARGET).pdf $(TARGET).html

.phony: clean 
