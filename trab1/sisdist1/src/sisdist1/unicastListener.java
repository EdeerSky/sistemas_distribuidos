/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author samot
 */
public class unicastListener implements Runnable {

    int myPort;
    ArrayList<String> cmds;

    public unicastListener(ArrayList<String> commands, int port) {
        cmds = commands;
        myPort = port;
    }

    @Override
    public void run() {
        if (myPort > 1) {
            try {
                ServerSocket listenSocket = new ServerSocket(myPort);
                while (true) {
                    Socket clientSocket = listenSocket.accept();
                    Connection c = new Connection(clientSocket);
                }
            } catch (IOException ex) {
                Logger.getLogger(unicastListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void updatePort(int port) {
        myPort = port;
    }

}

class Connection extends Thread {

    DataInputStream in;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {			                 // an echo server

            String data = in.readUTF();	                  // read a line of data from the stream
            System.out.print("recebi por unicast:" + data);
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
