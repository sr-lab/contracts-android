TARGET = contractstudy
DEPS = contractstudy.tex refs.bib

all: $(TARGET).pdf

%.pdf : %.tex $(DEPS)
	@for X in 1 2 3 ; do \
        ( echo "---------------- Latex ($$X) ----------------" && pdflatex $< && bibtex $*) \
	done

clean:
	rm -f *.bbl *.aux *.dvi *.log $(TARGET).pdf *~ *.blg *.bak
