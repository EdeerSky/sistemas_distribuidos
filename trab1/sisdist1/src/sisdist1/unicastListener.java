/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author samot
 */
public class unicastListener implements Runnable {
    
    int myPort;
    ArrayList<String> cmds;
    int vendedores=0;
    
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
                    Connection c = new Connection(clientSocket, cmds, vendedores);
                    if(c.getVendors()!=0) vendedores = c.getVendors();
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
    ArrayList<String> comandos;
    int vend;
    public Connection(Socket aClientSocket, ArrayList<String> c, int vendedores) {
        try {
            clientSocket = aClientSocket;
            comandos = c;
            vend = vendedores;
            //porta = prt;
            in = new DataInputStream(clientSocket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }
    
    public void run() {
        try {			                 // an echo server

            String data = in.readUTF();	                  // read a line of data from the stream
            System.out.println("\nrecebi por unicast:" + data);
            //TODO cod pra criptografar aqui
            //comando de venda-> venda=:=produto=:=preco
            //comando de compra-> compra=:=produto
            String[] splitado = data.split("=:=", 0);
            //long timeToAnswer = System.currentTimeMillis(); //se mais de 1 tiver o item, tem que dar tempo de todos responderem
            //comandos.add(data);
            if (splitado[1].equals("venda") || splitado[1].equals("compra")) {
                System.out.println("unicast listener receebeu> " + data);
                comandos.add(data);
            }
            else if (splitado[1].equals("vendedores")) { //para saber quantos peers estao vendendo o item
                vend = Integer.parseInt(splitado[0].trim());
            }
            else if (splitado[2].equals("possui o item")) { //contar os peers, nao funciona
                vend--; System.out.println("Num de vend: "+String.valueOf(vend));
                //consegue chegar aqui, recebendo algo do tipo
                //7180=:=2=:=possui o item, id, preço..
                // precisa comparar esses 2 preços, mas como?
            }
            //System.out.println("Num de vend: "+String.valueOf(vend));
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
    
    public int getVendors() {
        return vend;
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
}
