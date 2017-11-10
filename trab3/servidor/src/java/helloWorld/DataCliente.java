/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloWorld;

/**
 *
 * @author a1013343
 */
public class DataCliente {
    public int idCliente;
    public float preco;
    public String status;
    public int idTransacao;

    public DataCliente(int idCliente, float preco, String status, int idTransacao) {
        this.idCliente = idCliente;
        this.preco = preco;
        this.status = status;
        this.idTransacao = idTransacao;
    }
}
