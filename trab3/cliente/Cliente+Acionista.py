
# coding: utf-8

# In[ ]:

import requests
from IPython.display import clear_output
import random
import os

myId = random.randint(100, 999)
myTransactions = []

def insertChar(mystring, position, chartoinsert ):
    mystring   =  mystring[:position] + chartoinsert + mystring[position:] 
    return mystring 

def rmv_str(test_str):
    ret = ''
    skip1c = 0
    skip2c = 0
    skip3c = 0
    for i in test_str:
        if i == '[':
            skip1c += 1
        elif i == '<':
            skip2c += 1
        elif i == '{':
            skip3c += 1
        elif i == ']' and skip1c > 0:
            skip1c -= 1
        elif i == '>' and skip2c > 0:
            skip2c -= 1
        elif i == '}' and skip3c > 0:
            skip3c -= 1
        elif skip1c == 0 and skip2c == 0 and skip3c == 0:
            ret += i
    return ret

print('Digite "comandos" para saber os comandos disponíveis, "clear" para limpar a saída. Seu Id é', myId)
while(1):
    entrada = input("Digite o comando: ")
    if(entrada !='comandos' and entrada != 'clear' and entrada != 'idlist'):
        if('check' not in entrada):
            entrada = entrada.replace(" ", ":")
            entrada = insertChar(entrada, entrada.find(':'), ':'+str(myId))
            saida = 'http://localhost:8080/servidor/webresources/helloworld/' + entrada
            r = requests.get(saida)
            if('consulta' not in entrada):
                s = rmv_str(r.text)
                myTransactions.append(int(''.join(filter(str.isdigit, s))))
        elif('check' in entrada):
            entrada = entrada.replace(" ", ":")
            saida = 'http://localhost:8080/servidor/webresources/helloworld/' + entrada
            r = requests.get(saida)
        print(saida)
        print(rmv_str(r.text).encode("windows-1252").decode("utf-8"))
    if(entrada=='comandos'):
        print('Comandos disponíveis:')
        print(' - Compra de ações       ->  compra nomeAção Preço')
        print(' - Venda de ações        ->  venda nomeAção Preço')
        print(' - Consulta de preço     ->  consulta nomeAção')
        print(' - Checar status         ->  check idTransação')
        print(' - Checar ids transações ->  idlist')
    if(entrada=='idlist'):
        print(myTransactions)
    if(entrada=='clear'):
        clear_output()
        os.system('cls')
        os.system('clear')
        print('Digite "comandos" para saber os comandos disponíveis ou "clear" para limpar a saída. Seu Id é', myId)


# In[ ]:



