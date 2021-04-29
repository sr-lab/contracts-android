import requests
import time
import re

GITHUB_REPOS_FILE="github_unique_repos.txt"
OUTPUT_FILE="versions_file.txt"
NUMBER_PROJECTS=200

s = requests.Session()
headers = {'Authorization': 'token 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'}

def get_versions(repo):
	orig_repo = repo 
	repo = repo + "releases"
	print(repo, end=">")
	data = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
	resultado = data.json()

	if len(resultado) == 0:
		return get_tags(orig_repo)

	first_version =  resultado[0]["tag_name"]
	last_version = resultado[len(resultado) -1]["tag_name"]
	print(f"{last_version}, {first_version}")
	return last_version, first_version

def get_tags(repo):
	repo = repo + "tags"
	print( repo, end=">")
	data = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
	resultado = data.json()

	if len(resultado) == 0:
		return "None", "None"

	first_version =  resultado[0]["name"]
	last_version = resultado[len(resultado) -1]["name"]
	print(f"{last_version}, {first_version}")
	return last_version, first_version



def main():

    source_file = open(GITHUB_REPOS_FILE)
    links = []
    for line in source_file.readlines():
        x = line.split("/")
        links.append(f"{x[-2]}/{x[-1].strip()}/\n")

    top_links = links[:NUMBER_PROJECTS]
    print(f"Processing top {NUMBER_PROJECTS} projects...")
    source_file.close()
    out_file = open(OUTPUT_FILE, 'w+')
    for line in top_links:
        stripped_line = line.strip()
        last, first = get_versions(stripped_line)

        # add the github link to each line and remove '/releases'
        patt = re.compile('(\s*)/releases(\s*)')
        link = patt.sub('\\1\\2', stripped_line)
        stripped_line = f"https://github.com/{link}"

        out_file.write(f"{stripped_line};{last} \n")
        time.sleep(1)
        out_file.write(f"{stripped_line};{first} \n")
        time.sleep(1)

    print(f"File {OUTPUT_FILE} created.")
    out_file.close()

if __name__ == "__main__":
    main()
