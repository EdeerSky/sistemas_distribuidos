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
import java.util.logging.Level;
import java.util.logging.Logger;

public class InterfaceServidorImpl extends UnicastRemoteObject implements InterfaceServidor {

    List<Acao> acoes;

    InterfaceServidorImpl() throws RemoteException {
        acoes = new ArrayList<>();
        //colocando algumas acoes padrão
        acoes.add(new Acao("macdonalds"));
        acoes.add(new Acao("waynecorp"));
        acoes.add(new Acao("exxon"));
        acoes.add(new Acao("bitcoin"));
        acoes.add(new Acao("petrobras"));

        //uma thread que fica mudando os valores das acoes
        Thread t = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(InterfaceServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (Acao a : acoes) {
                        float novoPreco = a.precoDeMercado * 0.9f;
                        novoPreco += (float) (Math.random() * (a.precoDeMercado * 0.2f));
                        if (Math.random() > 0.5d) {
                            try {
                                a.mudaPreco(novoPreco);
                            } catch (RemoteException ex) {
                                Logger.getLogger(InterfaceServidorImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        };
        t.start();
    }

    //refistra o interesse do acionista em uma ação
    @Override
    public void registrarInteresse(String nomeAcao, InterfaceCliente referenciaCliente) throws RemoteException {
        boolean flag = false;
        for (Acao a : acoes) {
            //procurando a ação interessada na lista de ações
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                a.addInteressados(referenciaCliente);
            }
        }

        if (!flag) {
            System.out.println("Ação nao existe!");
        }
    }

    //retorna o preço de mercado de uma ação sabendo o nome dela
    @Override
    public float consulta(String nome) throws RemoteException {
        if (nome.equals("todos")) {
            System.out.println("Todas as ações existentes: ");
            for (Acao a : acoes) {
                System.out.println(a.nome);
            }
        } else {
            boolean flag = false;
            for (Acao a : acoes) {
                if (a.nome.equals(nome)) {
                    flag = true;
                    return a.precoDeMercado;
                }
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
                        HashMap<Integer, Float> temp = vendedor.getValue();
                        // é um for mas na verdade só tem um elemento
                        for (HashMap.Entry<Integer, Float> qtdp : temp.entrySet()) {
                            float precoVendedor = qtdp.getValue();
                            if (precoVendedor <= precoMaximo) {
                                //efetuar Venda
                                float precoFinal = (precoMaximo + precoVendedor) / 2;
//                                String mensagem = nomeAcao + ":" + precoFinal + ":" + quantidade;
                                String mensagem = nomeAcao + " pelo preço " + precoFinal;
                                InterfaceCliente referenciaVendedor = vendedor.getKey();
                                //notifica o cliente que a compra foi efetuada, e passa o preço final e nome
                                referenciaCliente.notificar("voceComprou: " + mensagem);
                                //notifica o vendedor que a venda foi efetuada, e passa o preço final e nome
                                referenciaVendedor.notificar("voceVendeu: " + mensagem);

                                //removendo da lista de compradores/vendedores dessa ação as referencias
                                a.compradores.remove(referenciaCliente);
                                a.vendedores.remove(referenciaVendedor);
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

                //tentando fazer par comprador/vendedor
                if (!a.compradores.isEmpty()) {
                    for (HashMap.Entry<InterfaceCliente, HashMap<Integer, Float>> comprador : a.compradores.entrySet()) {
                        //vendedor.getKey();
                        //vendedor.getValue());
                        HashMap<Integer, Float> temp = comprador.getValue();
                        // é um for mas na verdade só tem um elemento
                        for (HashMap.Entry<Integer, Float> qtdp : temp.entrySet()) {
                            float precoComprador = qtdp.getValue();
                            if (precoComprador >= precoMinimo) {
                                //efetuar Venda
                                float precoFinal = (precoComprador + precoMinimo) / 2;
//                                String mensagem = nomeAcao + ":" + precoFinal + ":" + quantidade;
                                String mensagem = nomeAcao + " pelo preço " + precoFinal;
                                InterfaceCliente referenciaComprador = comprador.getKey();

                                referenciaCliente.notificar("voceVendeu: " + mensagem);
                                referenciaComprador.notificar("voceComprou: " + mensagem);

                                a.vendedores.remove(referenciaCliente);
                                a.compradores.remove(referenciaComprador);
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

}
