#!/bin/bash

### Script counts LOC metric for .zip files in the "data" subdirectories, 
### generating CSVs in the "results" dir.
### 
### Needs the `cloc` utility (https://github.com/AlDanial/cloc, "cloc" package in Linux distros)ll .
###
### Author: 2016 Premek Brada <brada@kiv.zcu.cz>

DATADIR=data
OUTDIR=results
OUTNAME=cloc-counts
TOTNAME=total-loc

CURDIR=`pwd`

rm ${OUTDIR}/cloc*.csv
> ${TOTNAME}.csv

echo "Counting LOC in..."
cd ${DATADIR}
for d in [0-z]* ; do 
  cd $d
  echo "$d"
  cloc *.zip --out=${CURDIR}/${OUTDIR}/${OUTNAME}-$d.csv
  cd ..
done

echo "Generating total..."
cd ${CURDIR}/${OUTDIR}

grep --no-filename Language *.csv | head -1 > ${TOTNAME}.csv
grep "Java " cloc*.csv | sed "s/cloc-counts-//" | sed "s/.csv:Java//" >> ${TOTNAME}.csv

echo "Done."
