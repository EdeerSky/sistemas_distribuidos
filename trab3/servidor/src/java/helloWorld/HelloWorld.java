/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloWorld;

import java.util.ArrayList;
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

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of HelloWorld
     */
    public HelloWorld() {
        System.out.println("comecando codigo");
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
        String tipo = null;
        String nomeAcao = null;
        String preco = null;
        String[] partes = comando.split(":");

        if (partes.length >= 3) {
            idCliente = partes[0].trim();
            tipo = partes[1].trim();
            nomeAcao = partes[2].trim();
            preco = partes[3].trim();

            if (idCliente != null && tipo != null && nomeAcao != null & preco != null) {

                if (tipo.equals("compra")) {
                    return compra(idCliente, nomeAcao, preco);
                }
                if (tipo.equals("venda")) {
                    return venda(idCliente, nomeAcao, preco);
                }
                if (tipo.equals("consulta")) {
                    return consulta(idCliente, nomeAcao, preco);
                }
            }
        }

        return formarHtml("Erro, bem vindo!\n O request deve ser no formato seuID:comando:nomeAcao:preco");
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

        return formarHtml("isso é uma compra, bem vindo");
    }

    private String venda(String idCliente, String nomeAcao, String preco) {

        return formarHtml("isso é uma venda, bem vindo");
    }

    private String consulta(String idCliente, String nomeAcao, String preco) {
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

        return formarHtml("isso é uma consulta, bem vindo");
    }

}
