from os import listdir
from os import rename
from os import mkdir
from os import rmdir
from os.path import isfile, join
import shutil
import os
import re
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FOLDER = os.getenv('CLONED-PROJECTS-FOLDER')
OUTPUT_FOLDER = os.getenv('DATASET-PROJECTS-FOLDER')
OUTPUT_FILE = os.getenv('DATASET-PROJECTS-LIST')

def zipDirectory(outputPath, inputPath):
    shutil.make_archive(outputPath, 'zip', inputPath)
    
def organizeProjectsZipsFolders():
    AppsList = [f for f in os.listdir(OUTPUT_FOLDER) if isfile(join(OUTPUT_FOLDER, f))] 
    for app in AppsList:
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
    for directory in dirList:
        appDir = listdir(OUTPUT_FOLDER + "/" + directory)
        appDir.sort()
        if (directory[0].endswith("None.zip") or len(appDir) == 1):
            prepareProjectForRename(directory, appDir)
        else:
            prepareProjectWithTwoVersionsForRename(directory, appDir)   

def prepareProjectForRename(folder, appDir):
    renameProjectsFromSameFolder(folder, appDir, 0)

def prepareProjectWithTwoVersionsForRename(folder, appDir):
    versions = []
    appNames = []
    latestVersionIndex = 0
    for appName in appDir:
        version = extractVersionFromProjectName(appName)
        if (version != None):
            versions.append(version)
            appNames.append(appName)
    print(appDir)
    latestVersionIndex = getIndexForLatestVersion(versions)
    renameProjectsFromSameFolder(folder, appNames, latestVersionIndex)

def extractVersionFromProjectName(appName):
    regex = r'(\d)((\.|-)\d+)?((\.|-)\d+)?((\.|-)\d+)?((\.|-)\d+)?'
    extracts = re.findall(regex, appName)
    if (extracts == None or len(extracts) == 0):
        return None
    version = extracts[-1]
    return version

def getIndexForLatestVersion(versions):
    versionNumbers = cleanVersions(versions)         
    version1 = versionNumbers[0]
    version1Length = len(version1)
    version2 = versionNumbers[1]
    version2Length = len(version2)
    maxLength = maxNumber(version1Length, version2Length)
    try:
        for i in range(maxLength):
            if (int(version1[i]) > int(version2[i])):
                return 0
            elif (int(version1[i]) < int(version2[i])):
                return 1
            else:
                return 0
    except IndexError:
        return 0
    
def cleanVersions(versions):
    versionsNumbers = []
    for version in versions:
        versionNumbers = []
        for element in version:
            if (element != "." and element != "-" and element != ""):
                strippedElement = element.replace(".", "")
                strippedElement = strippedElement.replace("-", "")
                versionNumbers.append(strippedElement)
        versionsNumbers.append(versionNumbers)
    return versionsNumbers

def maxNumber(num1, num2):
    if num1 >= num2:
        return num1
    else:
        return num2

def renameProjectsFromSameFolder(folder, appNames, lastVersionIndex):
    try:
        print("=====================")
        print(appNames)
        print(lastVersionIndex)
        index = 0
        for name in appNames:
            if (index == lastVersionIndex):
                rename(rf'{OUTPUT_FOLDER}/{folder}/{name}',rf'{OUTPUT_FOLDER}/{folder}/{folder}-{"1"}.zip')
            else:
                rename(rf'{OUTPUT_FOLDER}/{folder}/{name}',rf'{OUTPUT_FOLDER}/{folder}/{folder}-{"0"}.zip')
            index += 1
    except: 
        return    
    
    
def listDataset():
    dirListInDataset = listdir(OUTPUT_FOLDER)  
    projects = []
    for directory in dirListInDataset:
        appDir = listdir(OUTPUT_FOLDER + "/" + directory)
        firstElement = appDir[0]
        removeExtension = firstElement.rsplit(".", 1)[0]
        authorProjectName = removeExtension.rsplit("-", 1)[0]
        projects.append(str(authorProjectName) + " : " + str(len(appDir)) + "\n")
        
    sortedProjects = sorted([string.lower() for string in projects])
    registryFile = open(OUTPUT_FILE, 'w')
    for project in sortedProjects: 
        registryFile.write(project)
    registryFile.close()

if __name__ == "__main__":
    print("prepareDateset.py: Preparing dataset...")
    for f in os.listdir(INPUT_FOLDER):
        zipDirectory(OUTPUT_FOLDER+"/"+f, INPUT_FOLDER+"/"+f)        
    organizeProjectsZipsFolders()
    updateZipNameWithStandarizedVersionNumber()
    listDataset()
    print("SUCCESS: Cloned projects were organized and zipped to folder " + OUTPUT_FOLDER)
    