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
        referenciaServidor.sayHello("cliente1", this);
    }

    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

}
