from os import listdir
from os import rename
from os.path import isfile, join
import re

DIR="C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/" #Dir ExtractContracts na dir "contracts-android/contractstudy"
list = [f for f in listdir(DIR) if not isfile(join(DIR, f))]
print(list)
for x in list:
	list2 = listdir(DIR + x)
	v = 1
	for x2 in list2:
		l = x2.split("-");
		l[-1] = str(v) + ".0.0"
		print(f"{l[-2]}-{l[-1]}")
		try:
			rename(rf'{DIR}{x}/{x2}',rf'{DIR}{x}/{x}-{l[-1]}.zip')
		except: print("já existe")
		v += 1

