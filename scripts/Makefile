setup: requirements.txt
	pip install -r requirements.txt

all-fdroid: get-fdroid-index clean-repo-registry filter-projects get-all-versions-files clone-repos

get-fdroid-index:
	python3 getFDroidIndex.py

clean-repo-registry: remove-non-github-urls remove-duplicates 

remove-duplicates: 
	python3 removeDuplicatedURL.py

remove-non-github-urls: 
	python3 removeNonGithubURL.py

filter-projects: output/repos-registry.txt
	python3 filterProjectsByStats.py

get-all-versions-files: get-versions get-links-to-versions

get-versions: output/repos-registry.txt
	python3 createVersionsFile.py

get-links-to-versions: output/repos-registry.txt
	python3 createLinksToVersionsFile.py

clone-repos: output/repos-registry.txt
	python3 cloneRepos.py

force-clean:
	rm -rf output
	rm -rf repos

