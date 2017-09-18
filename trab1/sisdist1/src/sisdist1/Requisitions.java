/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author a1013343
 */
public class Requisitions implements Runnable {

    int indexPort;
    public boolean on;
    int id;
    ArrayList<String> aVenda;

    public Requisitions(int port, int myId) {
        indexPort = port;
        on = true;
        id = myId;
        aVenda = new ArrayList<>();
    }

    @Override
    public void run() {
        while (on) {
            // receber comandos de compra e venda
            String cmd = "";
            System.out.println("\n digite o comando: ");
            Scanner scan = new Scanner(System.in);
            cmd = scan.nextLine();
            // temos 2 comandos,
            // comprar=:=banana
            // vender=:=banana
//            String[] parts = cmd.split("=:=");
//            if(parts[0].trim() == "vender"){
//                String produto = parts[1].trim();
//                
//            }
            if (indexPort > 1) {
                String aenviar = id + "=:=" + cmd;
                System.out.println(aenviar);
                String[] splitter = cmd.split("=:=",0);
                if(splitter[0].equals("venda"))
                aVenda.add(aenviar);
                enviarMsg(aenviar);
            } else {
                System.out.println("Index not set");
            }

        }
    }

    public void updateIndex(int port) {
        if(indexPort!=port){
            indexPort = port;
            on = true;
            for(Iterator i = aVenda.iterator(); i.hasNext();) {
                String element = (String) i.next();
                enviarMsg(element);
            }
        }
    }

    public void turnOff() {
        on = false;
    }
    
    public ArrayList<String> getVendas() {
        return aVenda;
    }

    public void enviarMsg(String msg) {
        Socket su = null;
        try {
//            int serverPort = 7896;
            su = new Socket("localhost", indexPort);
            DataOutputStream outuni = new DataOutputStream(su.getOutputStream());
            if (on) {
                outuni.writeUTF(msg);
            }
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
