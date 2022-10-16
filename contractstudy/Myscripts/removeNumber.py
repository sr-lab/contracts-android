from os import listdir
from os import rename
from os.path import isfile, join
import re


dir = '../ExtractContracts'
list = [f for f in listdir(dir) if not isfile(join(dir, f))]
print(list)
for x in list:
	list2 = listdir(dir + "/" + x)
	v = 1
	for x2 in list2:
		l = x2.split("-");
		l[-1] = str(v) + ".0.0"
		print(f"{l[-2]}-{l[-1]}")
		try:
			rename(rf'{dir}/{x}/{x2}',rf'{dir}/{x}/{x}-{l[-1]}.zip')
		except: print("já existe")
		v += 1

