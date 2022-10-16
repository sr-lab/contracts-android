#!/bin/bash

DIR="../../../contractstudy/ExtractContracts/"

COUNTER=0

for i in *
do
	IFS='-'
	read -a strarr <<< "$i"
	echo "${strarr[*]}"
	CAMINHO=${i##*${strarr[0]}-}
	CAMINHO=${CAMINHO%%-${strarr[-1]}*}
	echo "$CAMINHO"
	mkdir "$DIR/$CAMINHO"
	mv "$i" "$DIR/$CAMINHO"
done

