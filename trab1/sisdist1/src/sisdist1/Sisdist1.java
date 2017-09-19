/*
 * comando pra ver ip multicast:
 * netstat -g

codigos base:
http://www.cdk5.net/ipc/programCode/TCPClient.java
http://www.cdk5.net/ipc/programCode/TCPServer.java
 */
package sisdist1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @authors Jimmy Yuji Tanamati Soares
 * @authors Tomás Abril
 */
public class Sisdist1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {

        Peer c = new Peer();
    }
}

class Peer extends Thread {

    //DataInputStream in;
    //DataOutputStream out;
    //Socket clientSocket;
    /*
    Definição do ip e porta Multicast, que é o mesmo para todos os peers
    */
    String ipMulti = "224.0.0.251";
    int portaMulti = 6789;
    MulticastSocket s = null;

    /*
    myIp - ip local, no caso sempre localhost
    id - porta unicast
    indexIp/Port - guardar as informações do indexador
    timeOfLastIndexPing - para fazer o dT e saber se existe falha no index
    souIndexador - saber se o proprio peer é o index
    */
    private String myIp;
    private int id;
    private String indexIp = "0";
    private int indexPort = 0;
    private long timeOfLastIndexPing = 0;
    private boolean souIndexador;
    /*
    chaves para a criptografia da venda/compra
    */
    PrivateKey privateKey;
    PublicKey publicKey;
    String ALGORITHM_NAME = "RSA";
    String PADDING_SCHEME = "OAEPWITHSHA-512ANDMGF1PADDING";
    String MODE_OF_OPERATION = "ECB"; // This essentially means none behind the scene
    /*
    peerList - Guarda info relevantes dos peers para uso do index
    cmds - lista de comandos recebidos por unicast ao index
    */
    private ArrayList<PeerData> peerList;
    public ArrayList<String> cmds;

    /*
    rq/reqs - thread para enviar comandos unicast
    ia - anuncio do indexador
    */
    Requisitions rq;
    Thread reqs;
    IndexAnnouncer ia;
    
    String multiVenda;

    public Peer() throws InterruptedException {

        try {
            // id é um numero aleatorio no range das portas unicast
            id = (int) (Math.random() * 7000 + 1025);
            System.out.println("Meu id é "+id);
            myIp = "localhost";
            // gera as chaves para criptografia
            keyGenerator();
            souIndexador = false;
            peerList = new ArrayList<>();
            // entra no grupo multicast
            InetAddress group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            // cria a thread para o peer anunciar que está vivo
            NameAnnouncer na = new NameAnnouncer(id, ipMulti, portaMulti, publicKey);
            this.start();

            // ligando recebedor de comandos, mensagens unicast
            cmds = new ArrayList<>();
            Thread uniListener = new Thread(new unicastListener(cmds, id));
            uniListener.start();

            // inicia a thread para enviar comandos unicast
            rq = new Requisitions(indexPort, id);
            reqs = new Thread(rq);
            reqs.start();

            while (true) {
                /* 
                pega o tempo atual e compara com a ultima vez que o indexador
                se anunciou, caso seja longo demais e existir um indexador já eleito,
                -> remove o indexador e promove eleição
                */
                long now = System.currentTimeMillis();
                if (now - timeOfLastIndexPing > 6000 && indexIp != "0") {
                    System.out.println("Falha no indexador -> eleição");
                    //retirando da lista
                    for (Iterator i = peerList.iterator(); i.hasNext();) {
                        Object element = i.next();

                        if (((PeerData) element).port == indexPort) {
                            i.remove();
                        }
                    }
                    indexIp = "";
                    indexPort = 0;
                    eleicao();
                }
                //necessário ou não entra no if abaixo
                Thread.sleep(1000);
                //se tiver ao menos 4 peers conectados e nenhum indexador definido,
                //-> promove eleicao
                if ((peerList.size() >= 4) && (indexPort == 0)) {
                    eleicao();
                }

                // os comandos são processados e retirados da lista, por isso
                // o loop ocorre enquanto houver comandos
                // os comandos são do formato: id=:=venda=:=item=:=preco
                //                             id=:=compra=:=item
                
                while (!cmds.isEmpty()) {
                    String comando = cmds.remove(0);
                    String[] partes = comando.split("=:=", 2);
                    String[] prts = partes[1].split("=:=");
                    Integer idDoComando = Integer.parseInt(partes[0].trim());
                    //adicionando o item anunciado ao peer correspondente
                    if(prts[0].equals("venda")){
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();

                            if ((element).port == idDoComando) {
                                element.addCmd(comando);
                                System.out.println("Produto(s) do peer " + element.port + " > " + element.produtos);
                            }
                        }
                    }
                    //lista de vendedores
                    List<String> vendedores = new ArrayList<>();
                    
                    
                    if(prts[0].equals("compra")) {
                        //para cada peer, verifica se ele possui o item a venda, 
                        //adicionando na lista de vendedores caso tenha com o preço do item
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();
                            
                            for(int loop=0;loop<element.produtos.size(); loop++) {
                                if ((element).produtos.contains(prts[1]+element.produtos.get(loop).substring(prts[1].length()))) { //se contem o item
                                    vendedores.add(element.port+"=:="+element.produtos.get(loop).substring(prts[1].length()+1));
                                }
                            }
                        }
                        //envia msg unicast para o peer interesado com o numero de vendedores do item
                        //enviarMsgUnicast(vendedores.size()+"=:=vendedores",idDoComando); //envia o numero de vendedores do mesmo produto
                        
                        multiVenda = (vendedores.size()+"=:=vendedores");
                        //envia msg unicast para o peer interesado no formato idvendedor=:=preco=:=possui o item
                        vendedores.forEach((element) -> {
                            //final String aux = multiVenda;
                            multiVenda = multiVenda.concat("=:="+element);
                            //enviarMsgUnicast(element+"=:=possui o item",idDoComando); //envia os vendedores do produto + preco
                        });
                        multiVenda = multiVenda.concat("=:="+prts[1]);
                        enviarMsgUnicast(multiVenda, idDoComando);
                        
                    }
                    //comando recebido do peer comprador,
                    if(prts[0].equals("escolhido")) {
                        //prts[1] - id escolhido
                        //prts[2] - nome do item
                        System.out.println("Eu, id "+ idDoComando +" comprarei "+prts[2]+" do "+prts[1]);
                        System.out.println("Confirmar? Enter até confirmar");
                        
                        String ans = new Scanner(System.in).nextLine();
                        if(ans.isEmpty())
                        enviarMsgUnicast(idDoComando+"=:=escolhido=:="+prts[1]+"=:="+prts[2],indexPort);
                    }
                    
                    //comando recebido pelo index para mandar a chave pública do vendedor para o peer
                    String chv = new String();
                    if(prts[0].equals("sendkey")) {
                        //prts[1] - id escolhido
                        //prts[2] - nome do item
                                                
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();

                            if ((element).port == Integer.parseInt(prts[1].trim())) {
                                PublicKey chave = element.publicKey;
                                chv = Base64.getEncoder().encodeToString(chave.getEncoded());
                            }                            
                        }
                        
                        enviarMsgUnicast(indexPort+"=:=startp2p=:="+prts[1]+"=:="+prts[2]+"=:="+chv,idDoComando);
                    }
                    //comando recebido pelo peer para começar o p2p para compra
                    if(prts[0].equals("startp2p")) {
                        //[1] - id escolhido, [2] - nome item, [3] - chave pub
                        byte[] decodedKey = Base64.getDecoder().decode(prts[3].trim());
                        //System.out.println(prts[3]);
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey originalKey = keyFactory.generatePublic(keySpec); 
                        Cipher c = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
                        c.init(Cipher.ENCRYPT_MODE, originalKey);
                        byte[] cipherTextArray = c.doFinal((idDoComando+"=:=buystuff=:="+prts[2]).getBytes());
                        enviarMsgUnicast("encrypted=:="+Base64.getEncoder().encodeToString(cipherTextArray),Integer.parseInt(prts[1].trim()));
                    }
                    
                    //ultimo passo para vender, o vendedor recepe a msg criptografada com o pedido de compra
                    //decrypt, retorna a confirmação e retira o item
                    if(prts[0].equals("decrypt")) {
                        Cipher c = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
                        c.init(Cipher.DECRYPT_MODE, privateKey);
                        byte[] plainText = c.doFinal(Base64.getDecoder().decode(prts[1]));
                        String originalMsg = new String (plainText);
                        //splt[0]-idDoComando, splt[1]-buystuff, splt[2]-nome item
                        //System.out.println(originalMsg);
                        String[] splt = originalMsg.split("=:=", 0);
                        if(splt[1].equals("buystuff")) {
                            enviarMsgUnicast("end=:=Você comprou "+splt[2]+" de "+id,Integer.parseInt(splt[0].trim()));
                            enviarMsgUnicast(id+"=:=remove=:="+splt[2],indexPort);
                            System.out.println("Eu vendi o item "+splt[2]+ " para o "+splt[0]);
                            rq.removeSold(splt[2].trim());
                            
                        }
                    }
                    
                    //o index processa esse comando para retirar o item que foi vendido da lista
                    if(prts[0].equals("remove")) {
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();

                            for(int loop=0;loop<element.produtos.size(); loop++) {
                                if ((element).produtos.contains(prts[1]+element.produtos.get(loop).substring(prts[1].length()))) { //se contem o item
                                    if(element.port == idDoComando){
                                    element.produtos.remove((prts[1]+element.produtos.get(loop).substring(prts[1].length())));
                                    System.out.println("Produto(s) do peer " + element.port + " > " + element.produtos);
                                    rq.removeSold(prts[1].trim());
                                    }
                                }
                            }
                        }   
                    }
                    
                    Thread.sleep(100);
                }
                
            }

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void eleicao() {   
        //retirando da lista os peers que sairam
        //System.out.println(peerList);
        for (Iterator i = peerList.iterator(); i.hasNext();) {
            PeerData element = (PeerData) i.next();
            if (!element.isAlive()) {
                i.remove();
            }
        }
        //irá eleger o peer com o maior número de port unicast
        Integer voto = Collections.max(peerList).port;

        /*
        se for o proprio peer, inicia o anuncio de que é o index,
        arruma os parâmetros locais e da um update na Thread rq,
        que envia msgs unicast ao index
        */
        if (voto.equals(id)) {
            enviarMsgMulticast("sou indexador id=:=" + id);
            ia = new IndexAnnouncer(id, ipMulti, portaMulti);
            indexIp = myIp;
            indexPort = id;
            rq.updateIndex(indexPort);
            souIndexador = true;
        }
    }

    // thread para escutar msgs Multicast
    @Override
    public void run() {
        try {			               
            //System.out.println("comecando a escutar...");
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                String recieved = new String(messageIn.getData());
                String[] parts = recieved.split("=:=", 0);
                //escuta o keep alive dos peers normais
                //oi meu id e=:=id=:=publicKey
                if (parts[0].equals("oi meu id e")) {
                    int portRecebido = Integer.parseInt(parts[1].trim());
                    boolean achou = peerList.contains(new PeerData(portRecebido));
                    //se não esta na peerList, decodifica a publicKey do peer de
                    //String para objeto PublicKey e então adiciona a peerList
                    //o id e sua chave publica
                    if (!achou) {
                        // decode the base64 encoded string
                        byte[] decodedKey = Base64.getDecoder().decode(parts[2].trim());
                        // rebuild key using SecretKeySpec
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey originalKey = keyFactory.generatePublic(keySpec);
                        //System.out.println(Base64.getEncoder().encodeToString(originalKey.getEncoded()));
                        peerList.add(new PeerData(portRecebido, originalKey));
                        String msg = "oi meu id e=:=" + portRecebido + "=:=" + Base64.getEncoder().encodeToString(originalKey.getEncoded());
                        enviarMsgMulticast(msg);
                    } else {
                    //caso já tenha o peer adicionado, da um update do tempo de
                    //vida e checa se esta operante, caso não esteja é removido
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();

                            if (((PeerData) element).port == portRecebido) {
                                ((PeerData) element).updateTime();
                            }
                            if (!element.isAlive()) {
                                i.remove();
                            }
                        }
                    }

                }
                
                //escuta o keep alive do indexador
                //sou indexador id=:=id
                if (parts[0].equals("sou indexador id")) {
                    int indexPortRecebido = Integer.parseInt(parts[1].trim());
                    // se mais de um peer acha que é indexador
                    if (souIndexador && id != indexPortRecebido) {
                        if (id < indexPortRecebido) {
                            //deixar o outro ser o indexador
                            System.out.println("Vou deixar o outreo ser index");
                            souIndexador = false;
                            ia.on = false;
                        }
                    }
                    if (indexPort != indexPortRecebido) {
                        //System.out.println("Indexador Mudou!!-----");
                    }
                    indexIp = "localhost";
                    indexPort = indexPortRecebido;
                    rq.updateIndex(indexPortRecebido);
                    timeOfLastIndexPing = System.currentTimeMillis();

                }

            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                //clientSocket.close();
                s.close();
            } catch (Exception e) {/*close failed*/
            }
        }

    }

    // função para enviar msg multicast
    public void enviarMsgMulticast(String msg) {
        InetAddress group;
        try {
            group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            byte[] m = msg.getBytes();
            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, portaMulti);
            s.send(messageOut);
//            s.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // função para enviar msg unicast
    public void enviarMsgUnicast(String msg, int port) {
        Socket su = null;
        try {
            su = new Socket("localhost", port);
            DataOutputStream outuni = new DataOutputStream(su.getOutputStream());
            outuni.writeUTF(msg);
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (su != null) {
                try {
                    su.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }

    // função para gerar as chaves da criptografia
    public void keyGenerator() {
        int RSA_KEY_LENGTH = 2048;
        String ALGORITHM_NAME = "RSA";
        KeyPair rsaKeyPair;
        try {
            // Generate Key Pairs
            KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance(ALGORITHM_NAME);
            rsaKeyGen.initialize(RSA_KEY_LENGTH);
            rsaKeyPair = rsaKeyGen.generateKeyPair();
            publicKey = rsaKeyPair.getPublic();
            privateKey = rsaKeyPair.getPrivate();
        } catch (Exception e) {
            System.out.println("Exception while encryption/decryption");
            e.printStackTrace();
        }
    }
}
