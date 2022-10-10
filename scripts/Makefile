setup: requirements.txt
	pip install -r requirements.txt

all-fdroid: get-fdroid-index get-fdroid-stats clone-repos

get-fdroid-index:
	python3 getFDroidIndex.py

get-fdroid-stats: output/fdroid-index.xml
	python3 getFDroidStats.py

clone-repos: output/repos-registry.txt
	python3 cloneRepos.py

remove-duplicates:
	python3 removeDuplicateURLs.py

force-clean:
	rm -rf output
	rm -rf repos

