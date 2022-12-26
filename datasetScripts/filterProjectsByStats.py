#################### ---------------- IMPORTS ---------------- ####################

from glob import glob
from multiprocessing import current_process
from operator import truediv
from xml.dom import minidom
from github import Github
from github import RateLimitExceededException, UnknownObjectException
import pandas as pd
import time
import os
from dotenv import load_dotenv

load_dotenv()
load_dotenv("filePaths.env")
load_dotenv("config.env")

GITHUB_ACCESS_TOKEN = os.getenv('GITHUB-ACCESS-TOKEN')
INPUT_FILE = os.getenv('NON-DUPLICATED-PROJECTS-LIST-FILE')
OUTPUT_FILE = os.getenv('FILTERED-PROJECTS-LIST-FILE')
PROJECTS_STATS_FILE = os.getenv('PROJECTS-STATS-FILE')
PAGINATION_OFFSET = os.getenv('FILTER-PAGINATION-OFFSET', False)
PAGINATION_LIMIT = os.getenv('FILTER-PAGINATION-LIMIT', False)
JAVA_LANGUAGE_ACCEPT = os.getenv('JAVA-PROJECTS-ANALYSIS', 'True')
KOTLIN_LANGUAGE_ACCEPT = os.getenv('KOTLIN-PROJECTS-ANALYSIS', 'True')

GITHUB_PREFIX = ['https://github.com/',
				'http://github.com/',
				'https://www.github.com/',
				'http://www.github.com/']

currentPaginationIndex = 0
validProjectCount = 0
javaProjectsCount = 0
kotlinProjectsCount = 0
invalidLanguageErrorCount = 0
archivedErrorCount = 0
inactiveErrorCount = 0
notFoundErrorCount = 0
repoStats = []
repoURLs = []

def logNumberOfItemsToFetch():
	if (PAGINATION_LIMIT == False):
		print("Fetching ALL items...")
	else:
		print("INFO: Fetching " + str(PAGINATION_LIMIT) + " items...")
 
def checkRequestOffsetReached():
    if (PAGINATION_OFFSET == False):
        return True
    return (str(PAGINATION_OFFSET) <= str(currentPaginationIndex))
 
def getRepoName(urlName):
	print(urlName)
	prefix = GITHUB_PREFIX[0]
	for githubPrefix in GITHUB_PREFIX:
		if (urlName.startswith(githubPrefix)):
			prefix = githubPrefix
	splittedString = urlName.split(prefix)
	if(len(splittedString) < 1):
		return ""
	return splittedString[1].replace("\n", "")

def validateRepo(url):
	repoName = getRepoName(url)
	repo = git.get_repo(repoName)

	if (isRepoLanguageValid(repo) == False):
		return

	if (isRepoArchived(repo) == True):
		return

	if (isRepoActive(repo) == True):
		return

	closedPulls = repo.get_pulls(state='closed')
	totalClosedPulls = closedPulls.totalCount
	mergedPulls = list(filter(lambda x: x.merged, closedPulls))
	ratioMergedPerClosedPulls = computeRatioMergedPerClosedPulls(closedPulls, totalClosedPulls, mergedPulls)
	addValidatedRepoToArrays(url, repo, mergedPulls, totalClosedPulls, ratioMergedPerClosedPulls)


def isRepoLanguageValid(repo):
	global javaProjectsCount
	global kotlinProjectsCount
	global invalidLanguageErrorCount
    
	if(repo.language == "Java"):
		return isJavaProjectDesired()
	elif(repo.language == "Kotlin"):
		return isKotlinProjectDesired()
	else:
		print("WARNING: Project ignored since it is neither Java nor Kotlin project.")
		invalidLanguageErrorCount += 1
		return False

def isJavaProjectDesired():
    global javaProjectsCount
    if (JAVA_LANGUAGE_ACCEPT.upper() == 'TRUE'):
        javaProjectsCount += 1
        return True
    else:
        print("WARNING: Project ignored since it is written in Java.")
        return False

def isKotlinProjectDesired():
    global kotlinProjectsCount
    if (KOTLIN_LANGUAGE_ACCEPT.upper() == 'TRUE'):
        kotlinProjectsCount += 1
        return True
    else:
        print("WARNING: Project ignored since it is written in Kotlin.")
        return False

def isRepoArchived(repo):
	global archivedErrorCount
	if(repo.archived):
		archivedErrorCount += 1
		print("WARNING: Project ignored since it is read only.")
		return True
	return False

def isRepoActive(repo):
	global inactiveErrorCount  
	if(repo.pushed_at.year < 2018): # commit in last two years.
		inactiveErrorCount += 1
		print("WARNING: Project ignored since it is too old.")
		return

def computeRatioMergedPerClosedPulls(closedPulls, totalClosedPulls, mergedPulls):
	if(closedPulls.totalCount > 0):
		if totalClosedPulls == 0:
			return 0
		else:
			return len(mergedPulls) / totalClosedPulls

def addValidatedRepoToArrays(urlName, repo, mergedPulls, totalClosedPulls, percentage):
	global validProjectCount
	global repoStats
	validProjectCount  += 1
	repoStats.append({
			"APPLICATION NAME": str(repo.full_name),
			"GITHUB LINK": str(urlName),
			"LANGUAGE": str(repo.language),
			"WATCHERS": repo.subscribers_count,
			"STARS": repo.stargazers_count,
			"FORKS": repo.forks_count,
			"CONTRIBUTORS": repo.get_contributors().totalCount,
			"DATE OF LAST COMMIT": str(repo.pushed_at),
			"TOTAL MERGED PULL REQUESTS": len(mergedPulls),
			"TOTAL CLOSED PULL REQUESTS": totalClosedPulls,
			"% OF PULL REQUESTS ACCEPTED": percentage
	})
	repoURLs.append(urlName)
 
def saveStatsToOutputFile():
     global repoStats
     df = pd.DataFrame(repoStats)
     df.to_csv(PROJECTS_STATS_FILE )
     
def saveRepoURLToRegistry():
	registryFile = open(OUTPUT_FILE, 'w')
	for url in repoURLs:
		registryFile.write(url)
	registryFile.close()
	print("SUCCESS: Filtered Github projects' urls were saved to " + OUTPUT_FILE)

def checkIfProjectsNumberReachedLimit():
	global validProjectCount
	if (PAGINATION_LIMIT == False):
		return False
	return (str(validProjectCount) == str(PAGINATION_LIMIT))

def printResultLogs():
	print("Java Project Count:")
	print(javaProjectsCount)
	print("Kotlin Project Count:")
	print(kotlinProjectsCount)
	print("NEITHER JAVA NOR KOTLIN ERRORS:")
	print(invalidLanguageErrorCount)
	print("READ ERRORS:")
	print(archivedErrorCount)
	print("TOO OLD ERRORS:")
	print(inactiveErrorCount)
	print("PROJECT NOT FOUND:")
	print(notFoundErrorCount)
 
 
if __name__ == "__main__":
    
	logNumberOfItemsToFetch()
	git = Github(GITHUB_ACCESS_TOKEN)
	i = 0

	for repoURL in open(INPUT_FILE, "r"):
		print("=====================")		
		print("[" + str(i) + "]")
		i += 1
  
		if (checkRequestOffsetReached() == False):
			currentPaginationIndex += 1
			continue
					
		try:
			validateRepo(repoURL)
		except RateLimitExceededException:
			print('INFO: Waiting for an hour... ')
			time.sleep(1800)
			time.sleep(1800)
			try:
				validateRepo(repoURL)
				if (checkIfProjectsNumberReachedLimit()):
					break
				continue
			except:
				if (checkIfProjectsNumberReachedLimit()):
					break
				continue
		except UnknownObjectException:
			print("ERROR: Project ignored since it does not exist.")
			notFoundErrorCount += 1
			continue
		
		if (checkIfProjectsNumberReachedLimit()):
			break

	printResultLogs()
	saveRepoURLToRegistry()
	saveStatsToOutputFile()
