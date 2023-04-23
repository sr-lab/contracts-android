setup: requirements.txt
	pip install -r requirements.txt

get-fdroid-dataset: get-fdroid-index clean-projects-list filter-projects get-versions get-dataset

get-fdroid-index:
	python3 getFDroidIndex.py

clean-projects-list: remove-non-github-urls remove-duplicates 

remove-non-github-urls: output/0-f-droid-projects.txt
	python3 removeNonGithubURL.py

remove-duplicates: output/1-github-projects.txt
	python3 removeDuplicatedURL.py

filter-projects: output/2-non-duplicated-projects.txt
	python3 filterProjectsByStats.py

get-versions: output/3-filtered-projects.txt
	python3 createVersionsFile.py

get-dataset: clone-projects clean-projects prepare-dataset

clone-projects: output/4-projects-versions.txt
	python3 cloneProjects.py

clean-projects: output/5-projects
	python3 cleanProjects.py

prepare-dataset: output/5-projects
	python3 prepareDataset.py

clean-output:
	rm -rf output

clean-intermediate-output:
	rm -rf output/fdroid-index.xml
	rm -rf output/projects-list.txt
	rm -rf output/projects-stats.csv
	rm -rf output/repos/*
