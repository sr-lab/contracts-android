#!/bin/bash

COUNTER=0

for i in *
do
	IFS='-'
	read -a strarr <<< "$i"
	echo "${strarr[*]}"
	CAMINHO=${i##*${strarr[0]}-}
	CAMINHO=${CAMINHO%%-${strarr[-1]}*}
	echo "$CAMINHO"
	mkdir "/mnt/c/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/$CAMINHO"
	mv "$i" "/mnt/c/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/$CAMINHO"
done

