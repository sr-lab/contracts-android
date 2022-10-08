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

#################### ---------------- GLOBAL VARIABLES ---------------- ####################

GITHUB_ACCESS_TOKEN = os.getenv('GITHUB-ACCESS-TOKEN')
F_DROID_INDEX_FILE = os.getenv('F-DROID-INDEX-FILE')
F_DROID_STATS_FILE = os.getenv('F-DROID-STATS-FILE')
F_DROID_STATS_OFFSET = os.getenv('F-DROID-STATS-REQUEST-OFFSET', False)
F_DROID_STATS_LIMIT = os.getenv('F-DROID-STATS-REQUEST-LIMIT', False)
REPOS_REGISTRY_FILE = os.getenv('REPOS-REGISTRY-FILE')
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


#################### ---------------- AUXILIARY METHODS ---------------- ####################
 
def getFDroidIndexSourceTag():
	indexFile = minidom.parse(F_DROID_INDEX_FILE)
	sourceTag = indexFile.getElementsByTagName('source')
	print("Total items in Index: " + str(sourceTag.length))
	if (F_DROID_STATS_LIMIT == False):
		print("Fetching ALL items...")
	else:
		print("Fetching " + str(F_DROID_STATS_LIMIT) + " items...")
	return sourceTag
 
def checkRequestOffsetReached():
    if (F_DROID_STATS_OFFSET == False):
        return True
    return (str(F_DROID_STATS_OFFSET) <= str(currentPaginationIndex))
 
def getRepoName(urlName):
	print(urlName)
	splittedString = urlName.split(url_prefix)
	if(len(splittedString) < 1):
		return ""
	return splittedString[1]

def validateRepo(repo_name):

	repo = git.get_repo(repo_name)

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

	addValidatedRepoToArrays(repo, mergedPulls, totalClosedPulls, ratioMergedPerClosedPulls)


def isRepoLanguageValid(repo):
	global javaProjectsCount
	global kotlinProjectsCount
	global invalidLanguageErrorCount
    
	if(repo.language == "Java"):
		return isJavaProjectDesired()
	elif(repo.language == "Kotlin"):
		return isKotlinProjectDesired()
	else:
		print("ERROR1: Neither Java nor Kotlin project.")
		invalidLanguageErrorCount += 1
		return False

def isJavaProjectDesired():
    global javaProjectsCount
    if (JAVA_LANGUAGE_ACCEPT.upper() == 'TRUE'):
        javaProjectsCount += 1
        return True
    else:
        print("Warning: Project ignored since it is written in Java.")
        return False

def isKotlinProjectDesired():
    global kotlinProjectsCount
    if (KOTLIN_LANGUAGE_ACCEPT.upper() == 'TRUE'):
        kotlinProjectsCount += 1
        return True
    else:
        print("Warning: Project ignore since it is written in Kotlin.")
        return False

def isRepoArchived(repo):
	global archivedErrorCount
    
	if(repo.archived):
		archivedErrorCount += 1
		print("ERROR2: READ ONLY")
		return True
	return False

def isRepoActive(repo):
	global inactiveErrorCount
    
    # check repo has commit in the last two years
	if(repo.pushed_at.year < 2018):
		inactiveErrorCount += 1
		print("ERROR3: TOO OLD")
		return

def computeRatioMergedPerClosedPulls(closedPulls, totalClosedPulls, mergedPulls):
	if(closedPulls.totalCount > 0):
		if totalClosedPulls == 0:
			return 0
		else:
			return len(mergedPulls) / totalClosedPulls

def addValidatedRepoToArrays(repo, mergedPulls, totalClosedPulls, percentage):
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
     df.to_csv(F_DROID_STATS_FILE)
     
def saveRepoURLToRegistry():
    registryFile = open(REPOS_REGISTRY_FILE, 'a')
    registryFile.write("\n".join(repoURLs))
    registryFile.close()

def checkIfProjectsNumberReachedLimit():
	global validProjectCount
	if (F_DROID_STATS_LIMIT == False):
		return False
	return (str(validProjectCount) == str(F_DROID_STATS_LIMIT))

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
 
 

#################### ---------------- MAIN ---------------- ####################

sourceTag = getFDroidIndexSourceTag()

git = Github(GITHUB_ACCESS_TOKEN)

for url in sourceTag:
    
	if (checkRequestOffsetReached() == False):
		currentPaginationIndex += 1
		continue
    
	if(url.firstChild != None):
     
		urlName = url.firstChild.data
	
		for url_prefix in GITHUB_PREFIX:
            
			if(urlName.startswith(url_prefix)):
       				
				repo_name = getRepoName(urlName)
    
				if (repo_name == ""):
					continue

				try:
					validateRepo(repo_name)
     
				except RateLimitExceededException:
					print(' Waiting for an hour... ')
					time.sleep(1800)
					time.sleep(1800)
					try:
						validateRepo(repo_name)
						if (checkIfProjectsNumberReachedLimit()):
							break
						continue
					except:
						continue
  
				except UnknownObjectException:
					print("ERROR4: REPOSITORY DOES NOT EXIST")
					notFoundErrorCount += 1
					continue
		
		if (checkIfProjectsNumberReachedLimit()):
			break

printResultLogs()

saveRepoURLToRegistry()

saveStatsToOutputFile()
