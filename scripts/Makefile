setup: requirements.txt
	pip install -r requirements.txt

all-fdroid: get-fdroid-index clean-repo-registry filter-projects clone-repos

get-fdroid-index:
	python3 getFDroidIndex.py

filter-projects: output/repos-registry.txt
	python3 filterProjectsByStats.py

clone-repos: output/repos-registry.txt
	python3 cloneRepos.py

clean-repo-registry: remove-non-github-urls remove-duplicates 

remove-duplicates: 
	python3 removeDuplicatedURL.py

remove-non-github-urls: 
	python3 removeNonGithubURL.py

force-clean:
	rm -rf output
	rm -rf repos

