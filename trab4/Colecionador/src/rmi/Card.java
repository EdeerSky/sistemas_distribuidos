/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

/**
 *
 * @author a1013343
 */
public class Card {

    String nome;
    Integer quantidade;

    public Card(String nome, Integer quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return nome + ":" + quantidade + "\n";
    }
}
