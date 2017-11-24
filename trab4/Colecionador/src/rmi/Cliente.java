/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author a1013343
 */
public class Cliente {

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry(1098);

            InterfaceServidor interfaceServidor = (InterfaceServidor) registry.lookup("HelloDime");

            InterfaceClienteImpl serventeCliente = new InterfaceClienteImpl(interfaceServidor);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
