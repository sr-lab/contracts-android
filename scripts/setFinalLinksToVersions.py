import re
import os
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FILE = os.getenv('PROJECTS-LINK-TO-VERSIONS-FILE')
OUTPUT_FILE = os.getenv('PROJECTS-LINK-TO-VERSIONS-FINAL-FILE')

inputFile = open(INPUT_FILE, "r")
outputFile = open(OUTPUT_FILE, "w")

lines = inputFile.readlines()
inputFile.close()
for i in lines:
    patt = re.compile('(\s*)/releases(\s*)')
    link = patt.sub('\\1\\2', i)
    outputFile.write(f"https://github.com/{link}")
outputFile.close()