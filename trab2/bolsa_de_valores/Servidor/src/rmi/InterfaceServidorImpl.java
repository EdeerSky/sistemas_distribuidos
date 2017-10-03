/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class InterfaceServidorImpl extends UnicastRemoteObject implements InterfaceServidor {

    List<Acao> acoes;

    InterfaceServidorImpl() throws RemoteException {
        acoes = new ArrayList<>();
    }

    @Override
    public void registrarInteresse(String nomeAcao, InterfaceCliente referenciaCliente) throws RemoteException {
        boolean flag = false;
        for(Acao a : acoes) {
            if (a.nome.equals(nomeAcao)){
                flag = true;
                a.addInteressados(referenciaCliente);
            }
        }
        if(!flag){
            System.out.println("Ação nao existe!");
        }
    }

    @Override
    public float consulta(String nome) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compra(String nomeAcao, int quantidade, float precoMaximo, InterfaceCliente referenciaCliente) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int venda(String nomeAcao, int quantidade, float precoMinimo, InterfaceCliente referenciaCliente) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
