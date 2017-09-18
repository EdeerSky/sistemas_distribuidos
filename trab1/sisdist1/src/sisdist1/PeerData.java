/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sisdist1;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 *
 * @author samot
 */
public class PeerData implements Comparable<PeerData> {

    public int port;
//    public int isIndex;
    public long timeOfLastPing;
    public PublicKey publicKey;
    public ArrayList<String> produtos;

    public PeerData(int porta) {
        port = porta;
        timeOfLastPing = System.currentTimeMillis();
    }
    
    public PeerData(int porta, PublicKey pk) {
        publicKey = pk;
        port = porta;
        timeOfLastPing = System.currentTimeMillis();
    }
    
    public PeerData(int porta, PublicKey pk, ArrayList<String> prd) {
        publicKey = pk;
        port = porta;
        produtos = prd;
        timeOfLastPing = System.currentTimeMillis();
    }
        
    public boolean isAlive() {
        long now = System.currentTimeMillis();
        if (now - timeOfLastPing > 5010) {
            // nao está respondendo
            System.out.println("Its dead jim");
            return false;
        }
//        System.out.println("not dead yet");
        return true;
    }

    public void updateTime() {
        timeOfLastPing = System.currentTimeMillis();
    }

    @Override
    public int compareTo(PeerData o) {
        return Integer.compare(this.port, o.port);
    }

    @Override
    public String toString() {
        return "{" + port + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.port;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeerData other = (PeerData) obj;
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

}
