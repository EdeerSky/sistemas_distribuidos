/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a1013343
 */
public class LogHelper {

    //todo: fazer por ultimo
    String nome;

    public static File generateCardCollection(String nome) {
        //pesquisa se ja existe uma coleção com esse nome
        File db = new File(nome + ".txt");
        PrintWriter out = null;
        if (db.exists()) {
            return db;
        } //senão, criar
        else {
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(new Card("Curitiba", 2));
            cards.add(new Card("Maringa", 1));
            cards.add(new Card("Pokemon", 1));
            cards.add(new Card("Cerulean", 1));
            cards.add(new Card("JimmyCity", 1));

            try {
                out = new PrintWriter(db);
                for (Card c : cards) {
                    out.println(c.toString());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
            }
            return db;
        }
    }
    
    public static File createF(String nome) {
        //pesquisa se ja existe uma coleção com esse nome
        File db = new File(nome + ".txt");
        PrintWriter out = null;
        if (db.exists()) {
            return db;
        } //senão, criar
        else {
            ArrayList<Card> cards = new ArrayList<>();
            cards.add(new Card("temporario", 1));
            try {
                out = new PrintWriter(db);
                for (Card c : cards) {
                    out.println(c.toString());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                out.close();
            }
            return db;
        }
    }
            
    public static synchronized void recreateFile(File nome, List<String> cards) {
        if (nome != null) {
            clearTheFile(nome);
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(nome);
            for (String c : cards) {
                out.println(c.toString());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

    public static void clearTheFile(File file) {
        FileWriter fwOb = null;
        PrintWriter pwOb = null;
        try {
            fwOb = new FileWriter(file, false);
            pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();

            pwOb.close();
            fwOb.close();

        } catch (IOException ex) {
            System.out.println("deu erro:" + ex.getLocalizedMessage());
        }
//        finally {
//            try {
//                pwOb.close();
//                fwOb.close();
//            } catch (IOException ex) {
//                Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    public static synchronized int lerQtde(File nome, String cartao) {
        Path path = Paths.get(nome.getAbsolutePath());
        try {
            List<String> dados = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String l : dados) {
                if (l.trim().split(":")[0].equals(cartao)) {
                    return Integer.parseInt(l.trim().split(":")[1]);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static synchronized List<String> readdb(File nome) {
        Path path = Paths.get(nome.getAbsolutePath());
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static synchronized void retirarCartao(File nome, String cartao) {
        List<String> cards = LogHelper.readdb(nome);
        for (String c : cards) {
            String cidade = c.split(":")[0];
            String quantidade = c.split(":")[1];
            if (cidade.equalsIgnoreCase(cartao)) {
                int qtde = Integer.parseInt(quantidade);
                qtde--;
                if (qtde < 1) {
                    cards.remove(c);
                } else {
                    c = cidade + ":" + String.valueOf(qtde);
                }
                break;
            }
        }
        LogHelper.recreateFile(nome, cards);
    }

    public static synchronized void colocarCartao(File nome, String cartao) {
        List<String> cards = LogHelper.readdb(nome);
        for (String c : cards) {
            String cidade = c.split(":")[0];
            String quantidade = c.split(":")[1];
            if (cidade.equalsIgnoreCase(cartao)) {
                int qtde = Integer.parseInt(quantidade);
                qtde++;
                c = cidade + ":" + String.valueOf(qtde);
                break;
            }
        }
        LogHelper.recreateFile(nome, cards);
    }

    public static synchronized void retirarCartaoTmp(File nome, String cartao, int idT) {
        //saopaulo:qte:idT:a
        //rio:qte:idT:r
        //rio:qte
        List<String> cards = LogHelper.readdb(nome);
        for (String c : cards) {
            String cidade = c.split(":")[0];
            String quantidade = c.split(":")[1];
            if (cidade.equalsIgnoreCase(cartao)) {
                //falta checar se ja nao tem outra transacao alterando essa carta
                c += ":" + String.valueOf(idT) + ":" + "r";
                break;
            }
        }
        LogHelper.recreateFile(nome, cards);
    }

    public static synchronized void colocarCartaoTmp(File nome, String cartao, int idT) {
        //saopaulo:qte:idT:a
        //rio:qte:idT:r
        //rio:qte
        List<String> cards = LogHelper.readdb(nome);
        for (String c : cards) {
            String cidade = c.split(":")[0];
            String quantidade = c.split(":")[1];
            if (cidade.equalsIgnoreCase(cartao)) {
                //falta checar se ja nao tem outra transacao alterando essa carta
                c += ":" + String.valueOf(idT) + ":" + "a";
                break;
            }
        }
        LogHelper.recreateFile(nome, cards);
    }

    public static synchronized void deleteFile(File nome) {
        //todo: implementar
    }

    public static synchronized void copyFile(File orig, File dest) {
        List<String> origData = readdb(orig);
        recreateFile(dest, origData);
    }
}
