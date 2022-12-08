import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FILE = os.getenv('F-DROID-PROJECTS-LIST-FILE')
OUTPUT_FILE = os.getenv('GITHUB-PROJECTS-LIST-FILE')
GITHUB_PREFIX = ['https://github.com/',
				'http://github.com/',
				'https://www.github.com/',
				'http://www.github.com/']
GITHUB_STANDARD_BASE_URL = GITHUB_PREFIX[0]

validatedLines = []

def checkIfURLIsGithub():
    global validatedLines
    for line in open(INPUT_FILE, "r"):
        for githubPrefix in GITHUB_PREFIX:
            if(line.startswith(githubPrefix)):
                standarizedURL = standardizeGithubBaseURL(line, githubPrefix)
                validatedLines.append(standarizedURL)     

def standardizeGithubBaseURL(originalURL, originalPrefix):
    return originalURL.replace(originalPrefix, GITHUB_STANDARD_BASE_URL)

def saveValidatedURLsToOutputFile(): 
    outfile = open(OUTPUT_FILE, "w")
    for url in validatedLines:
        outfile.write(url)
    outfile.close() 
    print("SUCCESS: Only Github URLs were saved to: " + OUTPUT_FILE)


if __name__ == "__main__":        
    checkIfURLIsGithub()
    saveValidatedURLsToOutputFile()
