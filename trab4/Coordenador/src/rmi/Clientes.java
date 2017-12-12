/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

/**
 *
 * @author samot
 */
public class Clientes {

    public InterfaceCliente referencia;
    public String nome;

    public Clientes(InterfaceCliente referencia, String nome) {
        this.referencia = referencia;
        this.nome = nome;
    }

}
