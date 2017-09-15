/*
 * comando pra ver ip multicast:
 * netstat -g
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    int privateKey;
    int publicKey;

    private boolean peercomum;
    private ArrayList<PeerData> peerList;


    public Peer() throws InterruptedException {

        try {
            id = (int) (Math.random() * 7000 + 1025);
            myIp = "localhost";

            peercomum = true;
            peerList = new ArrayList<>();
            InetAddress group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            NameAnnouncer na = new NameAnnouncer(id, ipMulti, portaMulti);
            this.start();

            String msg = "oi meu id e=:=" + id;
            byte[] m = msg.getBytes();

            enviarMsgMulticast(msg);
            // ligando recebedor de comandos
            

            while (true) {
                long now = System.currentTimeMillis();
                if(now - timeOfLastIndexPing > 6000 && indexIp != "0"){
                    System.out.println("indexador morreu !!!!!!!!!! e agora? eleição");
//                    peerList.remove(new Integer(indexPort));
                    //retirando da lista
                    for (Iterator i = peerList.iterator(); i.hasNext(); ) {
                        Object element = i.next();

                        if (((PeerData)element).port == indexPort) {
                           i.remove();
                        }
                    }
                    indexIp = "";
                    indexPort = 0;
                    eleicao();
                }
//                Thread.sleep(1000);
//                System.out.println("indexIp="+indexIp);
//                System.out.println("peerList.size="+peerList.size());
                if (indexIp == "0" && peerList.size() >= 3) {
                    eleicao();
                }
                
                //
                

                if (msg.trim().compareToIgnoreCase("sair") == 1) {
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void eleicao() {
        if (!peerList.isEmpty()) {
            Integer voto = Collections.max(peerList).port;
            
            //enviarMsgMulticast("voto=:=" + voto.toString());
            if (voto == id) {
                enviarMsgMulticast("sou indexador id=:=" + id);
                IndexAnnouncer ia = new IndexAnnouncer(id, ipMulti, portaMulti);
                indexIp = myIp;
                indexPort = id;
            }

        } else {
            System.out.println("Sem candidatos pra eleição ainda!");
        }
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
                System.out.println("Received:" + recieved);
                String[] parts = recieved.split("=:=");

                if (parts[0].equals("oi meu id e")) {
                    int portRecebido = Integer.parseInt(parts[1].trim());
                    boolean achou = peerList.contains(new PeerData(portRecebido));
                    if (!achou) {
                        peerList.add(new PeerData (portRecebido));
                        String msg = "oi meu id e=:=" + id;
                        Thread.sleep(10);
                        enviarMsgMulticast(msg);
                    } else {
                        System.out.println("achei na lista já!");
                    }
                    System.out.println(peerList);

                }
                if (parts[0].equals("sou indexador id")) {
                    if (indexPort != Integer.parseInt(parts[1].trim())) {
                        System.out.println("Indexador Mudou!!-----");
                        indexIp = "localhost";
                        indexPort = Integer.parseInt(parts[1].trim());
                        timeOfLastIndexPing = System.currentTimeMillis();
                    } else {
                        timeOfLastIndexPing = System.currentTimeMillis();
                    }
                    indexIp = "localhost";
                    indexPort = Integer.parseInt(parts[1].trim());
                }

            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (InterruptedException ex) {
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

    public void enviarMsgUnicast(String msg, String ip, int port) {
        Socket su = null;
        try {
            int serverPort = 7896;
            su = new Socket(ip, port);
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
}
