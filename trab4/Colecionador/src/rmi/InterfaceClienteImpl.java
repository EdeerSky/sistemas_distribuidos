/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//saopaulo:qte:idT:a
//rio:qte:idT:r
//rio:qte
public class InterfaceClienteImpl extends UnicastRemoteObject implements InterfaceCliente {

    String nome;
    File db;
    List<String> minhaColecao;
    File dbTmp;

    InterfaceClienteImpl(InterfaceServidor referenciaServidor) throws RemoteException {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Digite o nome do cliente:");
            this.nome = bufferRead.readLine();
        } catch (IOException ex) {
            Logger.getLogger(InterfaceClienteImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        referenciaServidor.sayHello(nome, this);

        //criando ARQUIVO DE CARTAS
        db = LogHelper.generateCardCollection(nome);
        minhaColecao = LogHelper.readdb(db);
        //criando db temporaria
        LogHelper.copyFile(db, dbTmp);

        //colocar interface em um loop aqui
    }

    @Override
    public String echo(String texto) throws RemoteException {
        System.out.println("Recebi>" + texto);
        return null;
    }

    @Override
    public List getCards() throws RemoteException {
        return minhaColecao;
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
        //procura no dpTmp as linhas que possuem dados temporarios
        //faz a operacao no db final

        return "";
    }

    @Override
    public String abortTransaction(int idT) throws RemoteException {
        //apaga os indicadores de operação do dpTmp
        
        return "";
    }

    @Override
    public String trocarCartao(int idT, Card aRetirar, Card aReceber) throws RemoteException {
        //coloca as mudanças no aquivo temporario
        LogHelper.retirarCartaoTmp(dbTmp, aRetirar.nome, idT);
        LogHelper.colocarCartaoTmp(dbTmp, aReceber.nome, idT);
        return "";
    }

}
