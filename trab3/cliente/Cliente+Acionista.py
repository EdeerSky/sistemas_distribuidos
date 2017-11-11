
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

print('Digite "comandos" para saber os comandos disponíveis ou "clear" para limpar a saída')
while(1):
    entrada = input("Digite o comando: ")
    if(entrada !='comandos' and entrada != 'clear'):
        saida = 'http://localhost:8080/servidor/webresources/helloworld/' + entrada
        r = requests.get(saida)
        #print(saida)
        print(rmv_str(r.text))
    if(entrada=='comandos'):
        print('Comandos disponíveis:')
        print(' - Compra de ações   ->  compra:seuID:nomeAção:Preço')
        print(' - Venda de ações    ->  venda:seuID:nomeAção:Preço')
        print(' - Consulta de preço ->  consulta:seuID:nomeAção')
        print(' - Checar status     ->  check:idTransação')
    if(entrada=='clear'):
        clear_output()
        print('Digite "comandos" para saber os comandos disponíveis ou "clear" para limpar a saída')


# In[ ]:



