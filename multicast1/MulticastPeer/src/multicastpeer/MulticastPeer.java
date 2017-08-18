/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicastpeer;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class MulticastPeer {

    public static void main(String args[]) {
        while (true) {
            Connection c = new Connection();
        }
    }
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    MulticastSocket s = null;

    public Connection() {

        try {
            InetAddress group = InetAddress.getByName("224.0.0.251");
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            this.start();
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String msg = scanner.nextLine();
                byte[] m = msg.getBytes();
                DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
                s.send(messageOut);
                if (msg.compareToIgnoreCase("sair") == 1) {
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {			                 // an echo server

            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                System.out.println("Received:" + new String(messageIn.getData()));
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }

    }
}
