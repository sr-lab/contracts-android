#!/bin/bash

COUNTER=0

for i in *
do
	let COUNTER++
	if [ $COUNTER -lt 200 ] ;
	then
		mv "$i" "/mnt/c/Users/salty/contracts-android/contractstudy/ExtractContracts/$COUNTER"
	fi
done
