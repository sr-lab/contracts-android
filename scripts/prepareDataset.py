from os import listdir
from os import rename
from os import mkdir
from os import rmdir
from os.path import isfile, join
import shutil
import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FOLDER = os.getenv('CLONED-PROJECTS-FOLDER')
OUTPUT_FOLDER = os.getenv('DATASET-PROJECTS-FOLDER')

def checkPathIsValidFile(path):
    if (len(path.split("-")) == 1):
        print("WARNING: Bad file/directory detected - " + path + " - it is going to be ignored.")
        return False
    return True

def zipDirectory(outputPath, inputPath):
    shutil.make_archive(outputPath, 'zip', inputPath)
    
def organizeProjectsZipsFolders():
    AppsList = [f for f in os.listdir(OUTPUT_FOLDER) if isfile(join(OUTPUT_FOLDER, f))] 
    for app in AppsList:
        if (checkPathIsValidFile(app) == False):
            continue
        path = getApplicationName(app)
        createParentDirectoryForRepo(path)
        moveRepoVersionToRepoParentDirectory(app, path)

def getApplicationName(directoryName):
    wordList = directoryName.split("-")
    wordList.pop(-1)
    wordList.pop(0)
    return '-'.join(wordList)

def createParentDirectoryForRepo(path):
    try:
        mkdir( rf'{OUTPUT_FOLDER}/{path}')
    except: 
        return 

def moveRepoVersionToRepoParentDirectory(app, path):
    try:
        if os.path.isfile(rf'{OUTPUT_FOLDER}/{app}'):
            rename(rf'{OUTPUT_FOLDER}/{app}',rf'{OUTPUT_FOLDER}/{path}/{app}')
    except:
        return

def updateZipNameWithStandarizedVersionNumber():
    dirList = [f for f in listdir(OUTPUT_FOLDER) if not isfile(join(OUTPUT_FOLDER, f))]  
    print(dirList)
    for dir in dirList:
        appDir = listdir(OUTPUT_FOLDER + "/" + dir)
        appDir.sort()
        countVersion = 1
        for app in appDir:
            if (checkPathIsValidFile(app) == False):
                continue
            splittedName = makeSplittedNameWithNewVersion(app, countVersion)
            try:
                rename(rf'{OUTPUT_FOLDER}/{dir}/{app}',rf'{OUTPUT_FOLDER}/{dir}/{dir}-{splittedName[-1]}.zip')
            except: 
                return
            countVersion += 1  

def makeSplittedNameWithNewVersion(directoryName, countVersion):
    wordList = directoryName.split("-")
    wordList[-1] = str(countVersion) + ".0.0"
    return wordList

def main():
    for f in os.listdir(INPUT_FOLDER):
        if (checkPathIsValidFile(f) == True):
            zipDirectory(OUTPUT_FOLDER+"/"+f, INPUT_FOLDER+"/"+f)
          
    organizeProjectsZipsFolders()
    updateZipNameWithStandarizedVersionNumber()
    print("SUCCESS: Cloned projects were organized and zipped to folder " + OUTPUT_FOLDER)


if __name__ == "__main__":
    main()
    