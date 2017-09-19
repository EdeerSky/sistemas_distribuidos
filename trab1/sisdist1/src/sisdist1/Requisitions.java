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

/*
TO-DO: retirar itens vendidos do aVenda
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
            while(cmd.isEmpty()){
            Scanner scan = new Scanner(System.in);
            cmd = scan.nextLine();
            }
            if (indexPort > 1) {
                //envia o comando junto com o id automaticamente
                if(cmd.contains("=:=")){
                    String aenviar = id + "=:=" + cmd;
                    String[] splitter = cmd.split("=:=",0);
                    //guarda os itens anunciados caso o index caia
                    if(splitter[0].equals("venda"))
                    aVenda.add(aenviar);
                    enviarMsg(aenviar);
                }
            } else {
                System.out.println("Index not set");
            }

        }
    }

    public void updateIndex(int port) {
        //se o index mudou, manda novamente os itens anunciados para o novo index
        if(indexPort!=port){
            indexPort = port;
            on = true;
            for(Iterator i = aVenda.iterator(); i.hasNext();) {
                String element = (String) i.next();
                enviarMsg(element);
            }
        }
    }
    
    public void removeSold(String prd) {
        
        for(Iterator i = aVenda.iterator(); i.hasNext();) {
            String element = (String) i.next();
            String[] partes = element.split("=:=");
            if(partes[2].trim().equals(prd)){
                //aVenda.remove(element);
                i.remove();
            }
        }
        //System.out.println(aVenda);
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
