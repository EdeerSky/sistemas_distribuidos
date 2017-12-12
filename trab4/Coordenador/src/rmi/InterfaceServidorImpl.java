/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceServidorImpl extends UnicastRemoteObject implements InterfaceServidor {

    List<Clientes> clientes;

    InterfaceServidorImpl() throws RemoteException {
        clientes = new ArrayList<>();
    }

    @Override
    public String sayHello(String nomeCliente, InterfaceCliente referenciaCliente) throws RemoteException {
        System.out.println(nomeCliente + " se conectando comigo");
        clientes.add(new Clientes(referenciaCliente, nomeCliente));
        return "";
    }

    @Override
    public String updateTransactionState(int idT, String estado) throws RemoteException {
        //cliente está dizendo que está pronto ou que deu erro
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String listAllCards() throws RemoteException {
        //um cliente pediu pra ver as casrtas de todo mundo
        //pegar listas de cartas de todos e devolver para o cliente que pediu
        String resposta = "";
        for (Clientes cl : clientes) {
            resposta += "\n" + cl.nome + ": " + cl.referencia.getCards();
        }
        return resposta;
    }

    @Override
    public String startTransaction(String nomeCliente, InterfaceCliente referenciaCliente, Card offered, Card wanted, String nomeOutro) throws RemoteException {
        //cliente quer iniciar uma troca
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
