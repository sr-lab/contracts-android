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
#DIR="/mnt/c/Users/salty/Desktop/ProjetoFinal/contracts-android/contractstudy/ExtractContracts/"

REPOS_INPUT_FOLDER = os.getenv('CLONED-REPOS-FOLDER').join("/original")
REPOS_OUTPUT_FOLDER = "dataset"


def allocateVersions():
    AppsList = [f for f in listdir(REPOS_INPUT_FOLDER) if isfile(join(REPOS_INPUT_FOLDER, f))]  
    print(AppsList)
    for app in AppsList:
        wordList = app.split("-")
        wordList.pop(-1)
        wordList.pop(0)
        path = '-'.join(wordList)
        try:
            mkdir( rf'{REPOS_OUTPUT_FOLDER}{path}')
        except: 
            print("This directory already exists!")
        try:
            if os.path.isfile(rf'{REPOS_INPUT_FOLDER}/{app}'):
                rename(rf'{REPOS_INPUT_FOLDER}/{app}',rf'{REPOS_OUTPUT_FOLDER}{path}/{app}')
        except:
            print("This app already exists!")


def makeFinalVersions():
    dirList = [f for f in listdir(REPOS_OUTPUT_FOLDER) if not isfile(join(REPOS_OUTPUT_FOLDER, f))]  
    print(dirList)
    for dir in dirList:
        appDir = listdir(REPOS_OUTPUT_FOLDER + dir)
        appDir.sort()
        countVersion = 1
        for app in appDir:
            wordList = app.split("-");
            wordList[-1] = str(countVersion) + ".0.0"
            print(f"{wordList[-2]}-{wordList[-1]}")
            try:
                rename(rf'{REPOS_OUTPUT_FOLDER}{dir}/{app}',rf'{REPOS_OUTPUT_FOLDER}{dir}/{dir}-{wordList[-1]}.zip')
            except: print("This version already exists!")
            countVersion += 1  


script = """
sudo apt-get update 
sudo apt-get install zip -y
#DIRL="../../../scripts/f-droid/github_repos"
#DIR_ORGINAL="C:/Users/salty/Desktop/ProjetoFinal/contracts-android/scripts/f-droid/original/"
DIR="C:/Users/salty/Desktop/ProjetoFinal/contracts-android/scripts/f-droid/original"
DIR_ORGINAL="original/"
cd repos # change to use filePaths.env var.
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