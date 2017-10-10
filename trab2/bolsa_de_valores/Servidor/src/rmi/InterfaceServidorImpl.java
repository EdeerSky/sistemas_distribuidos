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
import java.util.Iterator;
import java.util.List;

public class InterfaceServidorImpl extends UnicastRemoteObject implements InterfaceServidor {

    List<Acao> acoes;

    InterfaceServidorImpl() throws RemoteException {
        acoes = new ArrayList<>();
        //TODO: criar uma thread que fica mudando os valores das acoes
    }

    @Override
    public void registrarInteresse(String nomeAcao, InterfaceCliente referenciaCliente) throws RemoteException {
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                a.addInteressados(referenciaCliente);
            }
        }
        if (!flag) {
            System.out.println("Ação nao existe!");
        }
    }

    @Override
    public float consulta(String nome) throws RemoteException {
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nome)) {
                flag = true;
                return a.precoDeMercado;
            }
            if (!flag) {
                System.out.println("Ação nao existe!");
                //TODO:  criar a acao aqui
            }
        }
        return -1;
    }

    @Override
    public void compra(String nomeAcao, int quantidade, float precoMaximo, InterfaceCliente referenciaCliente) throws RemoteException {
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                // colocando na lista de compradores
                HashMap<Integer, Float> tmp = new HashMap<>();
                tmp.put(quantidade, precoMaximo);
                a.compradores.put(referenciaCliente, tmp);

                //tentando fazer par comprador/vendedor
                if (!a.vendedores.isEmpty()) {
                    for (HashMap.Entry<InterfaceCliente, HashMap<Integer, Float>> vendedor : a.vendedores.entrySet()) {
                        //vendedor.getKey();
                        //vendedor.getValue());
                        HashMap<Integer, Float> temp = vendedor.getValue();
                        for (HashMap.Entry<Integer, Float> qtdp : temp.entrySet()) {
                            float precoVendedor = qtdp.getValue();
                            if (precoVendedor < precoMaximo) {
                                //efetuar Venda

                            }
                        }
                    }
                }

                break;
            }
            if (!flag) {
                System.out.println("Ação nao existe!");
            }
        }
    }

    @Override
    public void venda(String nomeAcao, int quantidade, float precoMinimo, InterfaceCliente referenciaCliente) throws RemoteException {
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                // colocando na lista de compradores
                HashMap<Integer, Float> tmp = new HashMap<>();
                tmp.put(quantidade, precoMinimo);
                a.vendedores.put(referenciaCliente, tmp);
                //encontrar par comprador/vendedor

                break;
            }
            if (!flag) {
                System.out.println("Ação nao existe!");
            }
        }
    }

}
