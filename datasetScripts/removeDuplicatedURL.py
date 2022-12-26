import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FILE = os.getenv('GITHUB-PROJECTS-LIST-FILE')
OUTPUT_FILE = os.getenv('NON-DUPLICATED-PROJECTS-LIST-FILE')

lines_seen = []

def collectNotDuplicatedLines():
    global lines_seen
    for line in open(INPUT_FILE, "r"):
        if line not in lines_seen: 
            lines_seen.append(line)

def saveNotDuplicatedToOutputFile():
    outfile = open(OUTPUT_FILE, "w")
    for url in lines_seen:
        outfile.write(url)
    outfile.close() 
    print("SUCCESS: Duplicates were removed and new repos' URL registry was saved to: " + OUTPUT_FILE)

if __name__ == "__main__": 
    collectNotDuplicatedLines()
    saveNotDuplicatedToOutputFile()

