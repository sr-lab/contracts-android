import requests
import time
import re
import os
from dotenv import load_dotenv

load_dotenv()
load_dotenv("filePaths.env")
load_dotenv("config.env")

GITHUB_ACCESS_TOKEN = os.getenv('GITHUB-ACCESS-TOKEN')
INPUT_FILE = os.getenv('FILTERED-PROJECTS-LIST-FILE')
OUTPUT_FILE = os.getenv('PROJECTS-VERSIONS-FILE')
PAGINATION_OFFSET = os.getenv('VERSIONS-FILE-OFFSET', False)
PAGINATION_LIMIT = os.getenv('VERSIONS-FILE-LIMIT', False)

s = requests.Session()
headers = {'Authorization': 'token ' + str(GITHUB_ACCESS_TOKEN)}

URLsToSave = []

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
    versions = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
    result = versions.json()
    try:
        projectFirstVersion = result[0]["tag_name"]
        projectLastVersion = result[len(result) -1]["tag_name"]
        return [projectLastVersion, projectFirstVersion]
    except:
        return getTags(originRepo)

def getTags(repo):
    repo = repo + "tags"
    tags = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
    result = tags.json()
    try:
        projectFirstVersion =  result[0]["name"]
        projectLastVersion = result[len(result) -1]["name"]
        return [projectLastVersion, projectFirstVersion]
    except:
        return ["None", "None"]
    
def createRepoVersionURLString(strippedLine):
    patt = re.compile('(\s*)/releases(\s*)')
    link = patt.sub('\\1\\2', strippedLine)
    return f"https://github.com/{link}"
 
def addVersionURL(baseURL, version):
    global URLsToSave
    versionURL = createURLWithVersion(baseURL, version)
    URLsToSave.append(versionURL)
 
def createURLWithVersion(baseURL, version):
    return f"{baseURL};{version}"
  
def saveVersionURLsToOutputIfValid():
    global URLsToSave
    outputFile = open(OUTPUT_FILE, 'w+')
    for URL in URLsToSave:
        if (validateURLVersionFormat(URL)):
            if (validateURLIsNotDuplicated(URL)):
                outputFile.write(URL + "\n")
                time.sleep(1)
    outputFile.close()
  
def validateURLVersionFormat(url):
    regexTextAtBeginning = ";(.*(((\d+).?(\d+.)*(\*|\d+)?))|None)$"
    regexTextAtEnd = ";(v?(((\d+).?(\d+.)*(\*|\d+)?).*)|None)$"
    matchAtBeginning = re.search(regexTextAtBeginning, url)
    matchAtEnd = re.search(regexTextAtEnd, url)
    if (matchAtBeginning == False and matchAtEnd == False):
        print("WARNING: URL was ignored since it has not valid version: " + url)
    return (matchAtBeginning or matchAtEnd)

def validateURLIsNotDuplicated(url):
    global URLsToSave
    return (url in URLsToSave)

def checkIfProjectsNumberReachedLimit(currentPaginationIndex):
    if (PAGINATION_LIMIT == False):
        return False
    return (str(currentPaginationIndex) == str(PAGINATION_LIMIT)) 

def main():
    print("INFO: Fetching first and last versions of each project...")
    currentPaginationIndex = 0
    links = readURLsFromInputFile()
    for line in links:
        if (checkRequestOffsetReached(currentPaginationIndex) == False):
            currentPaginationIndex += 1
            continue
        strippedLine = line.strip()
        versions = getVersions(strippedLine)
        strippedLine = createRepoVersionURLString(strippedLine)
        addVersionURL(strippedLine, versions[1])
        addVersionURL(strippedLine, versions[0])
        if (checkIfProjectsNumberReachedLimit(currentPaginationIndex)):
            break  
        currentPaginationIndex += 1
    
    saveVersionURLsToOutputIfValid()
    print(f"Projects' versions were saved in file: " + OUTPUT_FILE)

if __name__ == "__main__":
    main()