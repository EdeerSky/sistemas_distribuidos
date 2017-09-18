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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author aaaaaaaaaaaaaaaaaa
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

    DataInputStream in;
    DataOutputStream out;
    //Socket clientSocket;
    String ipMulti = "224.0.0.251";
    int portaMulti = 6789;
    MulticastSocket s = null;

    private String myIp;
    private int id;
    private String indexIp = "0";
    private int indexPort = 0;
    private long timeOfLastIndexPing = 0;

    PrivateKey privateKey;
    PublicKey publicKey;

    private boolean souIndexador;
    private ArrayList<PeerData> peerList;
    public ArrayList<String> cmds;


    Requisitions rq;
    Thread reqs;
    IndexAnnouncer ia;

    public Peer() throws InterruptedException {

        try {
            id = (int) (Math.random() * 7000 + 1025);
            myIp = "localhost";
            //SecuredRSAUsage cryp = new SecuredRSAUsage();
            //privateKey = cryp.getPrivateKey();
            //publicKey = cryp.getPublicKey();
            keyGenerator();
            souIndexador = false;
            peerList = new ArrayList<>();
            InetAddress group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            NameAnnouncer na = new NameAnnouncer(id, ipMulti, portaMulti, publicKey);
            this.start();

//            String msg = "oi meu id e=:=" + id;
//            byte[] m = msg.getBytes();
//
//            enviarMsgMulticast(msg);
            // ligando recebedor de comandos
            cmds = new ArrayList<>();
            Thread uniListener = new Thread(new unicastListener(cmds, id));
            uniListener.start();

            rq = new Requisitions(indexPort, id);
            reqs = new Thread(rq);
            reqs.start();

            while (true) {
                long now = System.currentTimeMillis();

                if (now - timeOfLastIndexPing > 6000 && indexIp != "0") {
                    System.out.println("indexador morreu !!!!!!!!!! e agora? eleição");
//                    peerList.remove(new Integer(indexPort));
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
                Thread.sleep(1000);
//                System.out.println("indexIp="+indexIp);
                //System.out.println("peerList.size="+peerList.size()+" e indexPort= "+indexPort + " " +peerList);
                if ((peerList.size() >= 3) && (indexPort == 0)) {
                    eleicao();
                    System.out.println("tentei eleicao");
                }

                while (!cmds.isEmpty()) {
                    String comando = cmds.remove(0);
                    String[] partes = comando.split("=:=", 2);
                    System.out.println("parte 0, depois 1");
                    System.out.println(partes[0]);
                    System.out.println(partes[1]);
                    Integer idDoComando = Integer.parseInt(partes[0].trim());
                    //adicionando o comando ao peer correspondente
                    for (Iterator i = peerList.iterator(); i.hasNext();) {
                        PeerData element = (PeerData) i.next();

                        if ((element).port == idDoComando) {
//                            element.addCmd(partes[1]);
                            element.addCmd(comando);
                            System.out.println("Produtos do peer " + element.port + " > " + element.produtos);
                        }
                    }
                    List<Integer> vendedores = new ArrayList<Integer>();
                    String[] prts = partes[1].split("=:=");
                    System.out.println(prts[0]);
                    System.out.println(prts[1]);
                    
                    if(prts[0].equals("compra")) {
                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            PeerData element = (PeerData) i.next();
                            
                            if ((element).produtos.contains(prts[1])) {
    //                            element.addCmd(partes[1]);
                                vendedores.add(element.port);
                                //loop++;
                                //enviarMsgUnicast(element.port+"=:=possui o item",idDoComando);
                                //System.out.println("Produtos do peer " + element.port + " > " + element.produtos);
                            }else{
                                enviarMsgUnicast(element.port+"=:=não possui o item",idDoComando);
                            }
                        }
                        for(Iterator i = vendedores.iterator(); i.hasNext();) {
                            Integer element = (Integer) i.next();
                            enviarMsgUnicast(element+"=:=possui o item",idDoComando);
                        }
                    }
                    
                    Thread.sleep(100);
                }
                /*
                while(!compras.isEmpty()) {
                    String cmp = compras.remove(0);
                    String[] spt = cmp.split("=:=");
                    for (Iterator i = peerList.iterator(); i.hasNext();) {
                        PeerData element = (PeerData) i.next();

                        if ((element).produtos.contains(spt[1])) {
    //                            element.addCmd(partes[1]);
                            enviarMsgUnicast(element.port+"=:=possui o item",Integer.parseInt(spt[0].trim()));
                                //System.out.println("Produtos do peer " + element.port + " > " + element.produtos);
                        }else{
                            enviarMsgUnicast(element.port+"=:=não possui o item",Integer.parseInt(spt[0].trim()));
                        }
                    }
                    Thread.sleep(50);
                }
                */
                //
            }

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void eleicao() {
        //if (!peerList.isEmpty()) {

        //retirando da lista os peers que sairam
        System.out.println(peerList);
        for (Iterator i = peerList.iterator(); i.hasNext();) {
            PeerData element = (PeerData) i.next();
            if (!element.isAlive()) {
                i.remove();
            }
        }

        Integer voto = Collections.max(peerList).port;
        //System.out.println("Estou aqui!!!");
        //enviarMsgMulticast("voto=:=" + voto.toString());

        if (voto.equals(id)) {
            //System.out.println("Escolhido");
            enviarMsgMulticast("sou indexador id=:=" + id);
            ia = new IndexAnnouncer(id, ipMulti, portaMulti);
            indexIp = myIp;
            indexPort = id;
            rq.updateIndex(indexPort);
            souIndexador = true;
        } else {
        }

        //}
    }

    // thread para escutar
    @Override
    public void run() {
        try {			                 // an echo server
            System.out.println("comecando a escutar...");
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                String recieved = new String(messageIn.getData());
                //System.out.println("Received:" + recieved);
                String[] parts = recieved.split("=:=", 0);

                if (parts[0].equals("oi meu id e")) {
                    int portRecebido = Integer.parseInt(parts[1].trim());
                    boolean achou = peerList.contains(new PeerData(portRecebido));
                    if (!achou) {
                        // decode the base64 encoded string
                        byte[] decodedKey = Base64.getDecoder().decode(parts[2].trim());
                        // rebuild key using SecretKeySpec
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey originalKey = keyFactory.generatePublic(keySpec);
                        //System.out.println(Base64.getEncoder().encodeToString(originalKey.getEncoded()));
                        peerList.add(new PeerData(portRecebido, originalKey));
//                        peerList.add(new PeerData(portRecebido, originalKey));

                        String msg = "oi meu id e=:=" + portRecebido + "=:=" + Base64.getEncoder().encodeToString(originalKey.getEncoded());
//                        Thread.sleep(10);
                        enviarMsgMulticast(msg);
                    } else {
//                        System.out.println("achei na lista já!");

                        for (Iterator i = peerList.iterator(); i.hasNext();) {
                            Object element = i.next();

                            if (((PeerData) element).port == portRecebido) {
                                ((PeerData) element).updateTime();
                                //if(((PeerData)element).produtos)
//                               System.out.println(((PeerData) element).produtos);
                            }
                        }
                    }

                }

                if (parts[0].equals("sou indexador id")) {
                    int indexPortRecebido = Integer.parseInt(parts[1].trim());
                    //System.out.println("Received:" + recieved);
                    // se mais de um peer acha que é indexador
                    if (souIndexador && id != indexPortRecebido) {
                        if (id < indexPortRecebido) {
                            //deixar o outro ser o indexador
                            System.out.println("Vou deixar o outreo ser index");
                            souIndexador = false;
                            ia.on = false;
//                            rq.turnOff();
//                            reqs.stop();
//                            reqs.interrupt();
                        }
                    }
                    if (indexPort != indexPortRecebido) {
                        System.out.println("Indexador Mudou!!-----");
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

    public void keyGenerator() {
        int RSA_KEY_LENGTH = 512;
        String ALGORITHM_NAME = "RSA";
        //String PADDING_SCHEME = "OAEPWITHSHA-512ANDMGF1PADDING";
        // String MODE_OF_OPERATION = "ECB"; // This essentially means none behind the scene
        KeyPair rsaKeyPair;
        //PublicKey publicKey;
        //PrivateKey privateKey;
        try {

            // Generate Key Pairs
            KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance(ALGORITHM_NAME);
            rsaKeyGen.initialize(RSA_KEY_LENGTH);
            rsaKeyPair = rsaKeyGen.generateKeyPair();
            publicKey = rsaKeyPair.getPublic();
            privateKey = rsaKeyPair.getPrivate();

            //String encryptedText = rsaEncrypt(shortMessage, publicKey);
            //String decryptedText = rsaDecrypt(Base64.getDecoder().decode(encryptedText), privateKey) ;
            //System.out.println("Encrypted text = " + encryptedText) ;
            //System.out.println("Decrypted text = " + decryptedText) ;
        } catch (Exception e) {
            System.out.println("Exception while encryption/decryption");
            e.printStackTrace();
        }
    }
}
