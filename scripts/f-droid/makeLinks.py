source_file = open('github_unique_repos.txt')
out_file = open('linksToGetVersions.txt', 'w+')
for line in source_file.readlines():
		x = line.split("/")
		out_file.write(f"{x[-2]}/{x[-1].strip()}/releases\n")

