import os
import time
from dotenv import load_dotenv

load_dotenv("filePaths.env")

INPUT_FILE = os.getenv('PROJECTS-VERSIONS-FILE')
OUTPUT_FOLDER = os.getenv('CLONED-PROJECTS-FOLDER')
PAGINATION_OFFSET = os.getenv('VERSIONS-FILE-OFFSET', False)
PAGINATION_LIMIT = os.getenv('VERSIONS-FILE-LIMIT', False)

SLEEP_SECONDS = 10
SLEEP_INTERVAL = 10

reposCount = 0

os.system("mkdir -p " + OUTPUT_FOLDER)

with open(INPUT_FILE) as file:
    for line in file:
        
        repoSplitBySlash = line.split("/")
        ownerName = repoSplitBySlash[3]
        repoName = repoSplitBySlash[4]
        version = ((repoSplitBySlash[5])[1:])[:-1]
        outputRepoName = OUTPUT_FOLDER + "/" + str(reposCount) + "-" + ownerName + "-" + repoName + "-" + version
        repoURL = ((line.split(";"))[0])[:-1]
        
        print("[" + str(reposCount) + "] Cloning " + outputRepoName)
                
        if os.path.exists(outputRepoName[:-1]) == True:
            print("INFO: The directory alread exists. No actions performed.")
        elif (version == "None"):
            os.system("git clone " + repoURL + " " + outputRepoName)
            print("SUCCESS: Done.")
        else:
            os.system("git clone -b " + version + " --single-branch " + repoURL + " " + outputRepoName)
            print("SUCCESS: Done.")
        
        reposCount += 1
        
        if (reposCount % SLEEP_INTERVAL == 0):
            print("INFO: Sleeping for " + str(SLEEP_SECONDS) + " seconds...")
            time.sleep(SLEEP_SECONDS)