#!/bin/bash

for dir in * #list directories in the form "/tmp/dirname/"
do
	dir=${dir%*/} # remove the trailing "/"
	zip -r ${dir##*/}.zip ${dir##*/} #print everything after the final "/"
done

#etc.
