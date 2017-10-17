/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class InterfaceClienteImpl extends UnicastRemoteObject implements InterfaceCliente {

    InterfaceClienteImpl(InterfaceServidor referenciaServidor) throws RemoteException {

        //imprime os comandos possíveis e fica em loop lendo e executando de acordo
        while (true) {
            //interface com usuario aqui
            System.out.println(">> Comandos possiveis: <<\n"
                    + "| interesse,nomeAcao    |\n"
                    + "| venda,nomeAcao,preco  | \n"
                    + "| compra,nomeAcao,preco |\n"
                    + "| consulta,nomeAcao     |\n"
                    + ">> ------------------- <<");
            Scanner scan = new Scanner(System.in);
            String comando = scan.nextLine();

            String[] parts = comando.split(",");
            if (parts[0].trim().equals("interesse")) {
                String acao = parts[1].trim();
                referenciaServidor.registrarInteresse(acao, this);
            }
            if (parts[0].trim().equals("venda")) {
                String acao = parts[1].trim();
                Float preco = Float.valueOf(parts[2].trim());
                referenciaServidor.venda(acao, 1, preco, this);
            }
            if (parts[0].trim().equals("compra")) {
                String acao = parts[1].trim();
                Float preco = Float.valueOf(parts[2].trim());
                referenciaServidor.compra(acao, 1, preco, this);
            }
            if (parts[0].trim().equals("consulta")) {
                String acao = parts[1].trim();
                float precoConsultado = referenciaServidor.consulta(acao);
                if (precoConsultado >= 0) {
                    System.out.println("O preço da ação " + acao + " é " + precoConsultado);
                }
            }
        }

    }

    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

    /* 
        mensagem Mensagem recebida do servidor
        
     */
    @Override
    public void notificar(String mensagem) throws RemoteException {
        String[] tipo = mensagem.split(":");

        if (tipo[0].trim().equals("novoPreco")) {
            System.out.println("O preco da ação mudou. " + tipo[1].trim());
        }
        if (tipo[0].trim().equals("voceVendeu")) {
            System.out.println("Venda Efetuada " + mensagem);
        }
        if (tipo[0].trim().equals("voceComprou")) {
            System.out.println("Compra Efetuada " + mensagem);
        }

    }
}
