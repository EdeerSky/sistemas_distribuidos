/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author a1013343
 */
public interface InterfaceServidor extends Remote {

    String sayHello(String nomeCliente, InterfaceCliente referenciaCliente) throws RemoteException;

    String updateTransactionState(int idT, String estado) throws RemoteException;

    String listAllCards() throws RemoteException;

    String startTransaction(String nomeCliente, InterfaceCliente referenciaCliente, Card offered, Card wanted, String nomeOutro) throws RemoteException;

}
