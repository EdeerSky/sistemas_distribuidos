/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author a1013343
 */
@Path("helloworld")
public class HelloWorld {

    List<Acao> acoes;
    int id;
    HashMap<Integer, String> transacoes;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of HelloWorld
     */
    public HelloWorld() {
        System.out.println("comecando codigo");
        id = 1;
        transacoes = new HashMap<>();
        //criando ações
        acoes = new ArrayList<>();
        //colocando algumas acoes padrão
        acoes.add(new Acao("macdonalds"));
        acoes.add(new Acao("waynecorp"));
        acoes.add(new Acao("exxon"));
        acoes.add(new Acao("bitcoin"));
        acoes.add(new Acao("petrobras"));
        acoes.add(new Acao("jimmycorp"));

        //uma thread que fica mudando os valores das acoes
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    for (Acao a : acoes) {
                        float novoPreco = a.precoDeMercado * 0.9f;
                        novoPreco += (float) (Math.random() * (a.precoDeMercado * 0.2f));
                        if (Math.random() > 0.5d) {
                            a.mudaPreco(novoPreco);
                        }
                    }
                }
            }
        };
        t.start();
    }

    @Path("{id}")
    @GET
    @Produces("text/html")
    public String recebeGet(@PathParam("id") String comando) {
        //A url para acessar o recurso passa a ser:
        //http://localhost:8080/jersey-tutorial/bandas/{id}

        String idCliente = null;
        String idTransacao = null;
        String tipo = null;
        String nomeAcao = null;
        String preco = null;
        String[] partes = comando.split(":");

        tipo = partes[0].trim();
        if (tipo.equals("compra")) {
            idCliente = partes[1].trim();
            nomeAcao = partes[2].trim();
            preco = partes[3].trim();
            return compra(idCliente, nomeAcao, preco);
        }

        if (tipo.equals("venda")) {
            idCliente = partes[1].trim();
            nomeAcao = partes[2].trim();
            preco = partes[3].trim();
            return venda(idCliente, nomeAcao, preco);
        }
        if (tipo.equals("consulta")) {
            nomeAcao = partes[2].trim();
            return consulta(nomeAcao);
        }
        if (tipo.equals("check")) {
            idTransacao = partes[1].trim();
            return consultaT(idTransacao);
        }

        return formarHtml("Erro, bem vindo!\n O request deve ser no formato comando:seuID:nomeAcao:preco");
    }

    /**
     * Retrieves representation of an instance of helloWorld.HelloWorld
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHtml() {
        return "<html lang=\"en\"><body><h1>Hello, World!!</body></h1></html>";
    }

    /**
     * PUT method for updating or creating an instance of HelloWorld
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_HTML)
    public void putHtml(String content) {
    }

    private String formarHtml(String texto) {
        return "<html lang=\"en\"><head><meta charset=\"UTF-8\"></head><body><h1>"
                + texto
                + "</body></h1></html>";
    }

    private String compra(String idCliente, String nomeAcao, String preco) {
        int idDessaTransacao = id++;
        float precoMaximo = Float.parseFloat(preco);
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                // colocando na lista de compradores
                a.compradores.add(new DataCliente(Integer.parseInt(idCliente), precoMaximo, "nao ok", idDessaTransacao));
                transacoes.put(idDessaTransacao, "Cliente " + idCliente + "requisitou uma compra da acao " + nomeAcao + " pelo preço " + preco);

                //tentando fazer par comprador/vendedor
                if (!a.vendedores.isEmpty()) {
                    for (DataCliente vendedor : a.vendedores) {

                        float precoVendedor = vendedor.preco;
                        int idVendedor = vendedor.idCliente;

                        if (precoVendedor <= precoMaximo) {
                            //efetuar Venda
                            float precoFinal = (precoMaximo + precoVendedor) / 2;

                            String mensagem = nomeAcao + " pelo preço " + precoFinal;

                            //notifica o cliente que a compra foi efetuada, e passa o preço final e nome
                            String p = "" + precoFinal + "";
                            String msgOriginal = transacoes.get(idDessaTransacao);
                            transacoes.put(id, msgOriginal + "\n Transação concluida com cliente " + vendedor.idCliente + ", preco Final é de " + p);

                            //notifica o vendedor que a venda foi efetuada, e passa o preço final e nome
                            String pp = "" + precoFinal + "";
                            String msgOriginal2 = transacoes.get(vendedor.idTransacao);
                            transacoes.put(vendedor.idTransacao, msgOriginal2 + "\n Transação concluida com cliente " + idCliente + ", preco Final é de " + pp);

                            //removendo da lista de compradores/vendedores dessa ação as referencias
                            //retira o item que acabou de ser adicionado
                            a.compradores.remove(a.compradores.size() - 1);
                            a.vendedores.remove(vendedor);

                            break;
                        }
                    }
                }
            }
        }
        if (!flag) {
            return formarHtml("0:Ação não existe, bem vindo");
        }

        return formarHtml(idDessaTransacao + ":isso é uma compra, bem vindo");
    }

    private String venda(String idCliente, String nomeAcao, String preco) {

        return formarHtml("isso é uma venda, bem vindo");
    }

    private String consulta(String nomeAcao) {
        String resposta = "";
        boolean flag = false;
        for (Acao a : acoes) {
            if (a.nome.equals(nomeAcao)) {
                flag = true;
                resposta = String.valueOf(a.precoDeMercado);
            }
        }
        if (!flag) {
            System.out.println("Ação nao existe!");
            //TODO:  criar a acao aqui
        }

        return formarHtml(nomeAcao + " tem preco de " + resposta);
    }

    private String consultaT(String idTransacao) {

        return formarHtml("check de estado da transacao, bem vindo");
    }

}
