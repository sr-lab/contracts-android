
import json
from os import listdir
from os.path import isfile, join
from operator import itemgetter

fil = open("targetapiFinalV1.txt", "a")
dir =[f for f in listdir('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts') if isfile(join('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts', f))]

listC = []

for x in dir:
    with open('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts/' + x) as f:
        data = json.load(f)
        for row in data:
            if(row["type"] == 'AndroidTargetApi' and row["version"] == '1.0.0'):
                #list.append([row["type"], row["condition"], row["version"]])
                flag = 0
                for aux in range(len(listC)):
                    #print(aux)
                    if aux != 0 and listC[aux][0] == row["condition"]:
                        listC[aux][1] = listC[aux][1] + 1
                        flag = 1
                        break
                
                if flag == 0:
                    listC.append([row["condition"], 1, row["name"], row["version"]])


listaAux = sorted(listC, key=itemgetter(1))
listaAux.reverse()
for pr in listaAux:
    fil.write(str(pr) + "\n")
    print(str(pr)) 

fil.close              


