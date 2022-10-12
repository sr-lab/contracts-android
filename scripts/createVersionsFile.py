import requests
import time
import re
import os
from dotenv import load_dotenv

load_dotenv()
load_dotenv("filePaths.env")
load_dotenv("config.env")

GITHUB_ACCESS_TOKEN = os.getenv('GITHUB-ACCESS-TOKEN')
INPUT_FILE = os.getenv('REPOS-REGISTRY')
OUTPUT_FILE = os.getenv('REPOS-VERSIONS-FILE')
PAGINATION_OFFSET = os.getenv('VERSIONS-FILE-OFFSET', False)
PAGINATION_LIMIT = os.getenv('VERSIONS-FILE-LIMIT', False)

s = requests.Session()
headers = {'Authorization': 'token ' + str(GITHUB_ACCESS_TOKEN)}

def readURLsFromInputFile():
    sourceFile = open(INPUT_FILE)
    links = []
    for line in sourceFile.readlines():
        x = line.split("/")
        links.append(f"{x[-2]}/{x[-1].strip()}/\n")
    sourceFile.close()
    return links

def checkRequestOffsetReached(currentPaginationIndex):
    if (PAGINATION_OFFSET == False):
        return True
    return (str(PAGINATION_OFFSET) <= str(currentPaginationIndex))

def getVersions(repo):
    originRepo = repo 
    repo = repo + "releases"
    print(repo, end=">")
    versions = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
    result = versions.json()
    try:
        projectFirstVersion = result[0]["tag_name"]
        projectLastVersion = result[len(result) -1]["tag_name"]
        print(f"{projectLastVersion}, {projectFirstVersion}")
        return [projectLastVersion, projectFirstVersion]
    except:
        return getTags(originRepo)

def getTags(repo):
    repo = repo + "tags"
    print( repo, end=">")
    tags = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
    result = tags.json()
    try:
        projectFirstVersion =  result[0]["name"]
        projectLastVersion = result[len(result) -1]["name"]
        print(f"{projectLastVersion}, {projectFirstVersion}")
        return [projectLastVersion, projectFirstVersion]
    except:
        return ["None", "None"]
    
def createRepoVersionURLString(strippedLine):
    patt = re.compile('(\s*)/releases(\s*)')
    link = patt.sub('\\1\\2', strippedLine)
    return f"https://github.com/{link}"

def saveRepoVersionURLToOutput(outputFile, strippedLine, versions):
    outputFile.write(f"{strippedLine};{versions[1]} \n")
    time.sleep(1)
    outputFile.write(f"{strippedLine};{versions[0]} \n")
    time.sleep(1) 
    
def checkIfProjectsNumberReachedLimit(currentPaginationIndex):
    if (PAGINATION_LIMIT == False):
        return False
    return (str(currentPaginationIndex) == str(PAGINATION_LIMIT)) 

def main():
    currentPaginationIndex = 0
    links = readURLsFromInputFile()
    outputFile = open(OUTPUT_FILE, 'w+')
    for line in links:
        if (checkRequestOffsetReached(currentPaginationIndex) == False):
            currentPaginationIndex += 1
            continue
        strippedLine = line.strip()
        versions = getVersions(strippedLine)
        strippedLine = createRepoVersionURLString(strippedLine)
        saveRepoVersionURLToOutput(outputFile, strippedLine, versions)
        if (checkIfProjectsNumberReachedLimit(currentPaginationIndex)):
            break
        currentPaginationIndex += 1
    print(f"Projects' versions were saved in file: " + OUTPUT_FILE)
    outputFile.close()

if __name__ == "__main__":
    main()