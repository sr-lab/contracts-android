setup: requirements.txt
	pip install -r requirements.txt

all-fdroid: get-fdroid-index clean-repo-registry get-versions clone-repos

clean-repo-registry: remove-non-github-urls remove-duplicates filter-projects

get-fdroid-index:
	python3 getFDroidIndex.py

remove-duplicates: 
	python3 removeDuplicatedURL.py

remove-non-github-urls: 
	python3 removeNonGithubURL.py

filter-projects: output/repos-registry.txt
	python3 filterProjectsByStats.py

get-versions: output/repos-registry.txt
	python3 createVersionsFile.py

clone-repos: output/repos-registry.txt
	python3 cloneRepos.py

force-clean:
	rm -rf output
	rm -rf repos

