import json
from os import listdir
from os.path import isfile, join
from operator import itemgetter

fil = open("AndroidXanotaçõesFinal.txt", "a")
dir =[f for f in listdir('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts') if isfile(join('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts', f))]
file1 = open('ListaAndroidXanno.txt', 'r')
Lines = file1.readlines()
lista = []

for line in Lines:
    lista.append(line[:-1])
    
print(lista)
listC = []


for x in dir:
    with open('C:/Users/salty/Desktop/plsF/contracts-android/contractstudy/out/contracts/' + x) as f:
        data = json.load(f)
        for row in data:
            if(row["type"] in lista):
                #list.append([row["type"], row["condition"], row["version"]])
                flag = 0
                for aux in range(len(listC)):
                    #print(aux)
                    if aux != 0 and listC[aux][0] == row["type"]:
                        listC[aux][1] = listC[aux][1] + 1
                        flag = 1
                        break
                
                if flag == 0:
                    listC.append([row["type"], 1, row["version"]])


listaAux = sorted(listC, key=itemgetter(1))
listaAux.reverse()
for pr in listaAux:
    fil.write(str(pr) + "\n")
    print(str(pr)) 

fil.close()             
file1.close()
