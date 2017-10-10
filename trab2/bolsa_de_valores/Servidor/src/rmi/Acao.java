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

    public String nome;
    public float precoDeMercado;
    public List<InterfaceCliente> interessados;
    //.................................\/dentro desse hashmap tem <quantidade, preco>
    public HashMap<InterfaceCliente, HashMap<Integer, Float>> compradores;
    public HashMap<InterfaceCliente, HashMap<Integer, Float>> vendedores;

    public Acao(String nome) {
        this.nome = nome;
        interessados = new ArrayList<>();
        compradores = new HashMap<>();
        vendedores = new HashMap<>();
        precoDeMercado = (float) (10 + Math.random() * (100 - 10));
    }
    
    public void addInteressados(InterfaceCliente cliente){
        this.interessados.add(cliente);
    }
    
    public void mudaPreco(float novoPreco) throws RemoteException{
        //this.preco = novoPreco;
        for(InterfaceCliente i: interessados){
            i.notificar("novopreco:" + String.valueOf(novoPreco));
        }
    }
 
}
