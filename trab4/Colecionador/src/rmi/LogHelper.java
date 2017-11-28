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
import sun.text.normalizer.UTF16;

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
                    out.print(c.toString());
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
        clearTheFile(nome);
        PrintWriter out = null;
        try {
            out = new PrintWriter(nome);
            for (String c : cards) {
                out.print(c.toString());
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

        } catch (IOException ex) {
            Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                pwOb.close();
                fwOb.close();
            } catch (IOException ex) {
                Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
}
