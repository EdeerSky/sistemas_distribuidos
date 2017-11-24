/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class InterfaceClienteImpl extends UnicastRemoteObject implements InterfaceCliente {

    String nome;
    

    InterfaceClienteImpl(InterfaceServidor referenciaServidor) throws RemoteException {
        referenciaServidor.sayHello("cliente1", this);
        nome = "123";
    }

    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

    @Override
    public List getCards() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String insertCard(Card card) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String removeCard(Card card) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String finishTransaction(int idT) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
