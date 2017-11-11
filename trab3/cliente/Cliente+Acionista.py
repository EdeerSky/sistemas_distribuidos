
# coding: utf-8

# In[ ]:

import requests
from IPython.display import clear_output

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
    
while(1):
    entrada = input("Digite o comando: ")
    checkentry = entrada.split(":")
    if(checkentry[0] != 'consulta' or checkentry[0] != 'compra' or checkentry[0] != 'venda' or checkentry[0] != 'check'):
        if(entrada=='comandos'):
            print('Comandos disponíveis:')
            print(' - Compra de ações   ->  compra:seuID:nomeAção:Preço')
            print(' - Venda de ações    ->  venda:seuID:nomeAção:Preço')
            print(' - Consulta de preço ->  consulta:seuID:nomeAção')
            print(' - Checar status     ->  check')
    else:
        saida = 'http://localhost:8080/servidor/webresources/helloworld/' + entrada
        r = requests.get(saida)
        print(saida)
        print(rmv_str(r.text))
    if(entrada=='clear'):
        clear_output()


# In[ ]:



