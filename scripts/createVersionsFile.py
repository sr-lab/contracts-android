import requests
import time
import re
import os
from dotenv import load_dotenv

load_dotenv()
load_dotenv("filePaths.env")

GITHUB_ACCESS_TOKEN = os.getenv('GITHUB-ACCESS-TOKEN')
INPUT_FILE = os.getenv('REPOS-REGISTRY')
OUTPUT_FILE = os.getenv('REPOS-VERSIONS-FILE')
NUMBER_PROJECTS=200

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

def limitNumberOfProjects(links):
    print(f"Processing top {NUMBER_PROJECTS} projects...")
    return links[:NUMBER_PROJECTS]

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

def main():
    links = readURLsFromInputFile()
    topLinks = limitNumberOfProjects(links)
    outputFile = open(OUTPUT_FILE, 'w+')
    for line in topLinks:
        strippedLine = line.strip()
        versions = getVersions(strippedLine)
        strippedLine = createRepoVersionURLString(strippedLine)
        saveRepoVersionURLToOutput(outputFile, strippedLine, versions)
    print(f"File {OUTPUT_FILE} created.")
    outputFile.close()

if __name__ == "__main__":
    main()