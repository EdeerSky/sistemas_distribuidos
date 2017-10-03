/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author a1013343
 */
public class Acao {

    String nome;
    float preco;
    int vendedorId;
    List<InterfaceCliente> interessados;

    public Acao(String nome, float preco, int vendedorId, List<InterfaceCliente> interessados) {
        this.nome = nome;
        this.preco = preco;
        this.vendedorId = vendedorId;
        this.interessados = interessados;
    }
    
    public void addInteressados(InterfaceCliente cliente){
        this.interessados.add(cliente);
    }
    
    public void mudaPreco(float novoPreco) throws RemoteException{
        this.preco = novoPreco;
        for(InterfaceCliente i: interessados){
            i.notificar(novoPreco);
        }
    }
 
}
