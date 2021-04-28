#!/bin/bash

COUNTER=0
DIR="/mnt/c/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/"  #Dir ExtractContracts na dir "contracts-android/contractstudy"

for i in *
do
	IFS='-'
	read -a strarr <<< "$i"
	echo "${strarr[*]}"
	CAMINHO=${i##*${strarr[0]}-}
	CAMINHO=${CAMINHO%%-${strarr[-1]}*}
	echo "$CAMINHO"
	mkdir "$DIR$CAMINHO"
	mv "$i" "DIR$CAMINHO"
done

