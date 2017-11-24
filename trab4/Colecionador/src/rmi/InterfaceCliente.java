/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author a1013343
 */
public interface InterfaceCliente extends Remote {

    String echo(String texto) throws RemoteException;

    List getCards() throws RemoteException;

    String insertCard(Card card) throws RemoteException;

    String removeCard(Card card) throws RemoteException;

    String finishTransaction(int idT) throws RemoteException;

}
