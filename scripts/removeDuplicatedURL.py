import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

REPOS_REGISTRY = os.getenv('REPOS-REGISTRY')

lines_seen = []

def collectNotDuplicatedLines():
    global lines_seen
    for line in open(REPOS_REGISTRY, "r"):
        if line not in lines_seen: 
            lines_seen.append(line)

def saveNotDuplicatedToOutputFile():
    outfile = open(REPOS_REGISTRY, "w")
    for url in lines_seen:
        outfile.write(url)
    outfile.close() 
    print("Duplicates were removed and new repos' URL registry was saved to: " + REPOS_REGISTRY)

if __name__ == "__main__": 
    collectNotDuplicatedLines()
    saveNotDuplicatedToOutputFile()

