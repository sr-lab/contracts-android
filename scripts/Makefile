setup: requirements.txt
	pip install -r requirements.txt

get-fdroid-dataset: get-fdroid-index clean-projects-list filter-projects get-all-versions-files get-dataset

get-fdroid-index:
	python3 getFDroidIndex.py

clean-projects-list: remove-non-github-urls remove-duplicates 

remove-duplicates: 
	python3 removeDuplicatedURL.py

remove-non-github-urls: 
	python3 removeNonGithubURL.py

filter-projects: output/projects-list.txt
	python3 filterProjectsByStats.py

get-all-versions-files: get-versions get-links-to-versions

get-versions: output/projects-list.txt
	python3 createVersionsFile.py

get-links-to-versions: output/projects-list.txt
	python3 createLinksToVersionsFile.py

get-final-links-to-versions: output/link-to-versions.txt
	python3 setFinalLinksToVersions.py

get-dataset: clone-projects prepare-dataset

clone-projects: output/projects-list.txt
	python3 cloneProjects.py

prepare-dataset: output/projects
	python3 prepareDataset.py

force-clean-output:
	rm -rf output

