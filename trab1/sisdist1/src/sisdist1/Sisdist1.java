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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

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
    MulticastSocket s = null;

    private int id;
    private int indexIp = 0;
    private int indexPort = 0;
    int privateKey;
    int publicKey;
    
    private boolean peercomum;
    private ArrayList<Integer> peerList;

    public Peer() {

        try {
            id = (int) (Math.random() * 7000 +1025);

            peercomum = true;
            peerList = new ArrayList<>();
            InetAddress group = InetAddress.getByName("224.0.0.251");
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            this.start();

            //Scanner scanner = new Scanner(System.in);
//          String msg = scanner.nextLine();
            String msg = "oi, meu id e=:=" + id;
            byte[] m = msg.getBytes();
            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
            s.send(messageOut);

            while (true) {
                if(indexIp == 0){
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
    
    public void eleicao(){
        int voto = Collections.max(peerList);
        
    }



    // thread para escutar
    public void run() {
        try {			                 // an echo server

            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                String recieved = new String(messageIn.getData());
                System.out.println("Received:" + recieved);
                
                String[] parts = recieved.split("=:=");
                System.out.println(parts[1]);
                peerList.add(Integer.parseInt(parts[1]));
                
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
}
