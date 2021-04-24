from os import listdir
from os import rename
from os.path import isfile, join
import re


list = [f for f in listdir("C:/Users/salty/contracts-android/contractstudy/ExtractContractsFinalF") if not isfile(join("C:/Users/salty/contracts-android/contractstudy/ExtractContractsFinalF", f))]
print(list)
for x in list:
	list2 = listdir("C:/Users/salty/contracts-android/contractstudy/ExtractContractsFinalF/" + x)
	v = 1
	for x2 in list2:
		l = x2.split("-");
		l[-1] = str(v) + ".0.0"
		print(f"{l[-2]}-{l[-1]}")
		try:
			rename(rf'C:/Users/salty/contracts-android/contractstudy/ExtractContractsFinalF/{x}/{x2}',rf'C:/Users/salty/contracts-android/contractstudy/ExtractContractsFinalF/{x}/{l[-2]}-{l[-1]}.zip')
		except: print("já existe")
		v += 1