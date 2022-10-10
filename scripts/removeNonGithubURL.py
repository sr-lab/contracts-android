import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

REPOS_REGISTRY_FILE = os.getenv('REPOS-REGISTRY-FILE')

GITHUB_PREFIX = ['https://github.com/',
				'http://github.com/',
				'https://www.github.com/',
				'http://www.github.com/']

GITHUB_STANDARD_BASE_URL = GITHUB_PREFIX[0]

#################### ---------------- AUXILIARY METHODS ---------------- ####################

def replaceGithubBaseURLWithStandard(originalURL, originalPrefix):
    return originalURL.replace(originalPrefix, GITHUB_STANDARD_BASE_URL)


#################### ---------------- MAIN ---------------- ####################

validatedLines = set() 
for line in open(REPOS_REGISTRY_FILE, "r"):
    for githubPrefix in GITHUB_PREFIX:
        if(line.startswith(githubPrefix)):
            standarizedURL = replaceGithubBaseURLWithStandard(line, githubPrefix)
            validatedLines.add(standarizedURL)             

outfile = open(REPOS_REGISTRY_FILE, "w")
for url in validatedLines:
    outfile.write(url)
outfile.close()

print("Only Github URLs were saved to: " + REPOS_REGISTRY_FILE)