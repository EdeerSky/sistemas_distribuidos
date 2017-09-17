/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
//                String recebido;
                // setup tcp server
//                Socket skt = new Socket("localhost", myPort);
                DatagramSocket ds = new DatagramSocket(myPort);
//                DatagramSocket sds = new DatagramSocket();
                DatagramPacket rdp, sdp;
                String msg;
                while (true) {
                    byte[] buf = new byte[100];
                    rdp = new DatagramPacket(buf, buf.length);
                    ds.receive(rdp);
                    msg = new String(buf);
                    msg = msg.trim();
                    System.out.println("Mensagem recebida por Unicast: " + msg);
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
