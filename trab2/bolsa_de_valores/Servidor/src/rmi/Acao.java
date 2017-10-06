/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author a1013343
 */
public class Acao {

    String nome;
    List<InterfaceCliente> interessados;
    HashMap<InterfaceCliente, HashMap> compradores;
    HashMap<InterfaceCliente, HashMap> vendedores;

    public Acao(String nome) {
        this.nome = nome;
        interessados = new ArrayList<>();
        compradores = new HashMap<>();
    }
    
    public void addInteressados(InterfaceCliente cliente){
        this.interessados.add(cliente);
    }
    
    public void mudaPreco(float novoPreco) throws RemoteException{
        //this.preco = novoPreco;
        for(InterfaceCliente i: interessados){
            i.notificar(novoPreco);
        }
    }
 
}
