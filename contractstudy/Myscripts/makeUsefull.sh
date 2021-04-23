#!/bin/bash

COUNTER=0

for i in *
do
	IFS='-'
	read -a strarr <<< "$i"
	echo "${strarr[1]}"
	mkdir "/mnt/c/Users/salty/contracts-android/contractstudy/ExtractContractsFinal/${strarr[1]}"
	mv "$i" "/mnt/c/Users/salty/contracts-android/contractstudy/ExtractContractsFinal/${strarr[1]}"
done
