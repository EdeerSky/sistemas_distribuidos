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

    void registrarInteresse(String nomeAcao, InterfaceCliente referenciaCliente) throws RemoteException;

    float consulta(String nome) throws RemoteException;

    void compra(String nomeAcao, int quantidade, float precoMaximo, InterfaceCliente referenciaCliente) throws RemoteException;

    void venda(String nomeAcao, int quantidade, float precoMinimo, InterfaceCliente referenciaCliente) throws RemoteException;
}
