import requests
import os
from dotenv import load_dotenv
from xml.dom import minidom

load_dotenv("filePaths.env")

URL = "https://f-droid.org/repo/index.xml"
F_DROID_INDEX_FILE = os.getenv('F-DROID-INDEX-FILE')
OUTPUT_FILE = os.getenv('F-DROID-PROJECTS-LIST-FILE')

def downloadFDroidIndex():
    response = requests.get(URL, stream = True)
    with open(F_DROID_INDEX_FILE, "wb") as file:
        for chunk in response.iter_content(chunk_size=1024):
            if chunk:
                file.write(chunk)
    print("SUCCESS: F-Droid index was downloaded and saved to " + F_DROID_INDEX_FILE)

def getFDroidIndexSourceTag():
	indexFile = minidom.parse(F_DROID_INDEX_FILE)
	sourceTag = indexFile.getElementsByTagName('source')
	print("INFO: Total items in Index: " + str(sourceTag.length))
	return sourceTag

def saveProjectsURLsToRegistry():
    registryFile = open(OUTPUT_FILE, 'a')
    for url in getFDroidIndexSourceTag():
        if(url.firstChild != None):
            repoURL = url.firstChild.data
            registryFile.write(repoURL + "\n")
    registryFile.close()
    print("SUCCESS: F-Droid repos' urls were saved to " + OUTPUT_FILE)

if __name__ == "__main__":
    os.makedirs(os.path.dirname(F_DROID_INDEX_FILE), exist_ok=True)
    downloadFDroidIndex()
    saveProjectsURLsToRegistry()


