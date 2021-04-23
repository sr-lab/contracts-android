import re

f1 = open("verFinal.txt", "r")
f2 = open("versoesDownload.txt", "w")
lines = f1.readlines()

for i in lines:
	patt = re.compile('(\s*)/releases(\s*)')
	link = patt.sub('\\1\\2', i)
	f2.write(f"https://github.com/{link}")

f1.close()
f2.close()
