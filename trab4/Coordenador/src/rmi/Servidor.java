/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author a1013343
 */
public class Servidor {

    public static void main(String args[]) {

        try {
            Servidor obj = new Servidor();
//            InterfaceServidor stub = (InterfaceServidor) UnicastRemoteObject.exportObject((Remote) obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1098);

            InterfaceServidorImpl serventeServidor = new InterfaceServidorImpl();

            registry.rebind("HelloDime", serventeServidor);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
