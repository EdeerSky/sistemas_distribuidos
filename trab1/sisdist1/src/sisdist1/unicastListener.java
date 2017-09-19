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
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


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
                    Connection c = new Connection(clientSocket, cmds);
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
    public Connection(Socket aClientSocket, ArrayList<String> c) {
        try {
            clientSocket = aClientSocket;
            comandos = c;
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
            //System.out.println("\nrecebi por unicast:" + data);
            //TODO cod pra criptografar aqui
            //comando de venda-> venda=:=produto=:=preco
            //comando de compra-> compra=:=produto
            if(!data.isEmpty()){
                String[] splitado = data.split("=:=", 0);
                //envia pro vendedor os dados criptografados pra confirmar a compra
                //o peer que quer comprar que recebe esse comando
                if(splitado[0].equals("encrypted")) {
                    comandos.add(clientSocket.getLocalPort()+"=:=decrypt=:="+splitado[1]);
                }
                //imprime o item que comprou, e de quem comprou
                //o peer que comprou recebe esse comando
                else if (splitado[0].equals("end")) {
                    System.out.println(splitado[1]);
                }
                //se recebeu um comando do requisitions (começando com venda, compra ou remove)
                //->adiciona a lista de comandos
                //o index recebe esses comandos                
                else if (splitado[1].equals("venda") || splitado[1].equals("compra") || splitado[1].equals("remove")) {
                    //System.out.println("unicast listener receebeu> " + data);
                    comandos.add(data);
                }
                //o peer recebe do index e avalia de qual vendedor quer comprar
                else if (splitado[1].equals("vendedores")) { 
                    vend = Integer.parseInt(splitado[0].trim());
                    if (vend>0){
                        List<Integer> prices = new ArrayList<>();
                        for(int i=3; i<=(2*vend+1);i+=2) {
                            prices.add(Integer.parseInt(splitado[i].trim()));
                        }
                        //prices.add(Integer.parseInt(splitado[splitado.length-1]));
                        String theChosen = splitado[(prices.indexOf(Collections.min(prices))*2)+2];
                        //System.out.println(clientSocket.getLocalPort());
                        comandos.add(clientSocket.getLocalPort()+"=:=escolhido=:="+theChosen+"=:="+splitado[splitado.length-1]);
                    }else{
                        System.out.println("Não existem vendedores para esse item!");
                    }
                }
                //o index recebe a resposta do peer sobre qual vendedor escolheu
                else if (splitado[1].equals("escolhido")) {
                    comandos.add(splitado[0]+"=:=sendkey=:="+splitado[2]+"=:="+splitado[3]);
                }
                else if (splitado[1].equals("startp2p")) {
                    //[0] - id index, [2] - id escolhido, [3] - nome item, [4] - chave pub
                    comandos.add(clientSocket.getLocalPort()+"=:=startp2p=:="+splitado[2]+"=:="+splitado[3]+"=:="+splitado[4]);
                }
            }
            //
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
