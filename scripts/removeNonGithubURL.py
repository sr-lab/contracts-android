import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

REPOS_REGISTRY = os.getenv('REPOS-REGISTRY')
GITHUB_PREFIX = ['https://github.com/',
				'http://github.com/',
				'https://www.github.com/',
				'http://www.github.com/']
GITHUB_STANDARD_BASE_URL = GITHUB_PREFIX[0]

validatedLines = []

def checkIfURLIsGithub():
    global validatedLines
    for line in open(REPOS_REGISTRY, "r"):
        for githubPrefix in GITHUB_PREFIX:
            if(line.startswith(githubPrefix)):
                standarizedURL = standardizeGithubBaseURL(line, githubPrefix)
                validatedLines.append(standarizedURL)     

def standardizeGithubBaseURL(originalURL, originalPrefix):
    return originalURL.replace(originalPrefix, GITHUB_STANDARD_BASE_URL)

def saveValidatedURLsToOutputFile(): 
    outfile = open(REPOS_REGISTRY, "w")
    for url in validatedLines:
        outfile.write(url)
    outfile.close() 
    print("Only Github URLs were saved to: " + REPOS_REGISTRY)


if __name__ == "__main__":        
    checkIfURLIsGithub()
    saveValidatedURLsToOutputFile()
