import requests
import time

s = requests.Session()
headers = {'Authorization': 'token GITHUB TOKEN'}

def get_versions(repo):
	print( repo, end=">")
	data = s.get(f"https://api.github.com/repos/{repo}", headers=headers)
	resultado = data.json()

	if len(resultado) == 0:
		return "None", "None"

	first_version =  resultado[0]["tag_name"]
	last_version = resultado[len(resultado) -1]["tag_name"]
	print(f"{last_version}, {first_version}")
	return last_version, first_version

def main():
	source_file = open('linksToGetVersions.txt')
	out_file = open('verFinal.txt', 'w+')
	for line in source_file.readlines():
		stripped_line = line.strip()
		last, first = get_versions(stripped_line)
		out_file.write(f"{stripped_line}/{last} \n")
		time.sleep(1)
		out_file.write(f"{stripped_line}/{first} \n")
		time.sleep(1)


	source_file.close()
	out_file.close()

if __name__ == "__main__":
    main()