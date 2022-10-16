#!/bin/bash

sudo apt-get update # To get the latest package lists
sudo apt-get install zip -y

for dir in * #list directories in the form "/tmp/dirname/"
do
	dir=${dir%*/} # remove the trailing "/"
	zip -r ${dir##*/}.zip ${dir##*/} #print everything after the final "/"
done

#etc.
