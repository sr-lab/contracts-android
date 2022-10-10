import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

REPOS_UNFILTERED_REGISTRY_FILE = os.getenv('REPOS-UNCLEANED-REGISTRY-FILE')
REPOS_REGISTRY_FILE = os.getenv('REPOS-REGISTRY-FILE')

lines_seen = set() 
outfile = open(REPOS_REGISTRY_FILE, "w")
for line in open(REPOS_UNFILTERED_REGISTRY_FILE, "r"):
    if line not in lines_seen: 
        outfile.write(line)
        lines_seen.add(line)
outfile.close()

print("Duplicates were removed and new repos' URL registry was saved to: " + REPOS_REGISTRY_FILE)