import os
import time
from dotenv import load_dotenv

load_dotenv("filePaths.env")

REPOS_REGISTRY = os.getenv('REPOS-REGISTRY')
OUTPUT_FOLDER = os.getenv('CLONED-REPOS-FOLDER')

SLEEP_SECONDS = 10
SLEEP_INTERVAL = 10

reposCount = 1

os.system("mkdir " + OUTPUT_FOLDER)

with open(REPOS_REGISTRY) as file:
    for line in file:
        
        os.system("cd " + OUTPUT_FOLDER + "; git clone " + line)
                
        if (reposCount % SLEEP_INTERVAL == 0):
            print("Sleeping for " + str(SLEEP_SECONDS) + " seconds...")
            time.sleep(SLEEP_SECONDS)