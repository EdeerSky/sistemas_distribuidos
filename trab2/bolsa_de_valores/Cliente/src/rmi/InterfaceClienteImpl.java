/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InterfaceClienteImpl extends UnicastRemoteObject implements InterfaceCliente {

    InterfaceClienteImpl(InterfaceServidor referenciaServidor) throws RemoteException {
        //
        while (true) {
            //interface com usuario aqui

        }

    }

    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

    @Override
    public void notificar(String mensagem) throws RemoteException {
        String[] tipo = mensagem.split(":");

        if (tipo[0].trim().equals("novoPreco")) {
            System.out.println("O preco da ação mudou. Novo preço = " + tipo[1].trim());
        }
        if (tipo[0].trim().equals("voceVendeu")) {
            System.out.println("Venda Efetuada " + mensagem);
        }
        if (tipo[0].trim().equals("voceComprou")) {
            System.out.println("Compra Efetuada " + mensagem);
        }

    }
}
