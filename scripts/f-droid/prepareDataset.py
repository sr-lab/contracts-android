from os import listdir
from os import rename
from os import mkdir
from os import replace
from os.path import isfile, join
import re
import shutil
import os

COUNTER=0
#DIR="../../../contractstudy/ExtractContracts/"
#DIR_ORGINAL="../../../scripts/f-droid/original"
DIR="/mnt/c/Users/salty/Desktop/ProjetoFinal/contracts-android/contractstudy/ExtractContracts/"
DIR_ORGINAL="/mnt/c/Users/salty/Desktop/ProjetoFinal/contracts-android/scripts/f-droid/github_repos/original"

def allocateVersions():
    AppsList = [f for f in listdir(DIR_ORGINAL) if isfile(join(DIR_ORGINAL, f))]  
    print(AppsList)
    for app in AppsList:
        wordList = app.split("-")
        wordList.pop(-1)
        wordList.pop(0)
        path = '-'.join(wordList)
        try:
            mkdir( rf'{DIR}{path}')
        except: 
            print("já existe esta diretoria")
        try:
            if os.path.isfile(rf'{DIR_ORGINAL}/{app}'):
                rename(rf'{DIR_ORGINAL}/{app}',rf'{DIR}{path}/{app}')
        except:
            print("já existe esta app")


def makeFinalVersions():
    dirList = [f for f in listdir(DIR) if not isfile(join(DIR, f))]  
    print(dirList)
    for dir in dirList:
        appDir = listdir(DIR + dir)
        appDir.sort()
        countVersion = 1
        for app in appDir:
            wordList = app.split("-");
            wordList[-1] = str(countVersion) + ".0.0"
            print(f"{wordList[-2]}-{wordList[-1]}")
            try:
                rename(rf'{DIR}{dir}/{app}',rf'{DIR}{dir}/{dir}-{wordList[-1]}.zip')
            except: print("já existe versão")
            countVersion += 1  


script = """
sudo apt-get update # To get the latest package lists
sudo apt-get install zip -y

#DIRL="../../../scripts/f-droid/github_repos"
#DIR_ORGINAL="C:/Users/salty/Desktop/ProjetoFinal/contracts-android/scripts/f-droid/original/"
DIR="C:/Users/salty/Desktop/ProjetoFinal/contracts-android/scripts/f-droid/original"
DIR_ORGINAL="original/"
cd github_repos
for dir in * #list directories in the form "/tmp/dirname/"
do
    if [ "$dir" != "original" ]
    then
        echo "$dir"
	    dir=${dir%*/} # remove the trailing "/"
        echo "${DIR_ORGINAL}${dir##*/}.zip ${dir##*/}"
	    zip -r ${DIR_ORGINAL}${dir##*/}.zip ${dir##*/} #print everything after the final "/"
    fi
done
"""

os.system("bash -c '%s'" % script)
allocateVersions()
makeFinalVersions()