/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InterfaceServidorImpl extends UnicastRemoteObject implements InterfaceServidor {

    InterfaceServidorImpl() throws RemoteException {
    }

    @Override
    public String sayHello(String nomeCliente, InterfaceCliente referenciaCliente) throws RemoteException {
        System.out.println("tem gente se conectando comigo");
        referenciaCliente.echo(nomeCliente + "< to mandando isso do servidor");
        return null;
    }

}
