/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a1013343
 */
public class NameAnnouncer {

    int intervalo = 5000;
    String msg;
    String ipMulti = "224.0.0.251";
    int portaMulti = 6789;
    PublicKey pbk;

    MulticastSocket s = null;
    InetAddress group;
    DatagramPacket messageOut;

    public NameAnnouncer(int mensagem, String ip, int porta, PublicKey pk) {
        String encodedKey = null;
        if (pk != null) {
            encodedKey = Base64.getEncoder().encodeToString(pk.getEncoded());
        }
        if (encodedKey == null) {
            encodedKey = "aaaa";
        }
        msg = "oi meu id e=:=" + Integer.toString(mensagem) + "=:=" + encodedKey;
        ipMulti = ip;
        portaMulti = porta;
        pbk = pk;
        try {
            group = InetAddress.getByName(ipMulti);
            s = new MulticastSocket(portaMulti);
            s.joinGroup(group);
            byte[] m = msg.getBytes();
            messageOut = new DatagramPacket(m, m.length, group, portaMulti);
            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {
                    try {
                        s.send(messageOut);
                    } catch (IOException ex) {
                        Logger.getLogger(NameAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            Timer timer = new Timer("MyTimer");//create a new Timer
            timer.scheduleAtFixedRate(timerTask, 30, intervalo);//this line starts the timer at the same time its executed

        } catch (UnknownHostException ex) {
            Logger.getLogger(NameAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NameAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
