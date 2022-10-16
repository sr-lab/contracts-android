import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FILE = os.getenv('PROJECTS-LIST-FILE')
OUTPUT_FILE = os.getenv('PROJECTS-LINK-TO-VERSIONS-FILE')

sourceFile = open(INPUT_FILE)
outputFile = open(OUTPUT_FILE, 'w+')
for line in sourceFile.readlines():
		x = line.split("/")
		outputFile.write(f"{x[-2]}/{x[-1].strip()}/releases\n")
print("SUCCESS: Links to projects versions were saved to " + OUTPUT_FILE)