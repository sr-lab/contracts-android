from os import listdir
from os import rename
from os.path import isfile, join
import re


list = [f for f in listdir("C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts") if not isfile(join("C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts", f))]
print(list)
for x in list:
	list2 = listdir("C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/" + x)
	v = 1
	for x2 in list2:
		l = x2.split("-");
		l[-1] = str(v) + ".0.0"
		print(f"{l[-2]}-{l[-1]}")
		try:
			rename(rf'C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/{x}/{x2}',rf'C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/ExtractContracts/{x}/{x}-{l[-1]}.zip')
		except: print("já existe")
		v += 1

