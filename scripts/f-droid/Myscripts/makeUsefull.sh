#!/bin/bash

COUNTER=0
DIR="../../../contractstudy/ExtractContracts/"  #Dir ExtractContracts na dir "contracts-android/contractstudy"

for i in *
do
	IFS='-'
	read -a strarr <<< "$i"
	echo "${strarr[*]}"
	CAMINHO=${i##*${strarr[0]}-}
	CAMINHO=${CAMINHO%%-${strarr[-1]}*}
	echo "$CAMINHO"
	mkdir -p "$DIR$CAMINHO"
	mv "$i" "$DIR$CAMINHO"
done

