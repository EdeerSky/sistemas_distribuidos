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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a
 */
public class Sisdist1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

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

    int privateKey;
    int publicKey;

    private boolean peercomum;
    private ArrayList<Integer> peerList;
    private ArrayList<ArrayList<Integer>> voteList;

    public Peer() {

        try {
            id = (int) (Math.random() * 7000 + 1025);
            myIp = "localhost";

            peercomum = true;
            peerList = new ArrayList<>();
            InetAddress group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            this.start();

            //Scanner scanner = new Scanner(System.in);
//          String msg = scanner.nextLine();
            String msg = "oi meu id e=:=" + id;
            byte[] m = msg.getBytes();
//            System.out.println(msg);
//            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
//            s.send(messageOut);
            enviarMsgMulticast(msg);

            while (true) {
                if (indexIp == "0" && peerList.size() > 3) {
                    eleicao();
                }

                if (msg.compareToIgnoreCase("sair") == 1) {
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void eleicao() {
        if (!peerList.isEmpty()) {
            Integer voto = Collections.max(peerList);
            //enviarMsgMulticast("voto=:=" + voto.toString());
            if (voto == id) {
                enviarMsgMulticast("indexador=:=" + id);
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
//                System.out.println(parts[0]);
                if (parts[0].equals("oi meu id e")) {
                    System.out.println(parts[1]);
                    
                    int portRecebido = Integer.parseInt(parts[1]);
                    System.out.println(portRecebido);
                    boolean achou = peerList.contains(portRecebido);
                    if(!achou){
                        peerList.add(portRecebido);
                        String msg = "oi meu id e=:=" + id;
                        enviarMsgMulticast(msg);
                    }else{
                        System.out.println("achei na lista já!");}
                    
                }
                if (parts[0].equals("indexador")) {
                    indexIp = "localhost";
                    indexPort = Integer.parseInt(parts[1]);
                }

            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
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
