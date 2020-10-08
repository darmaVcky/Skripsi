/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author asus
 */
public class buatFileSubs {

    public static int awalSimulasi = 0;
    public static int akhirSimulasi = 274883;
    public static int kelipatan = 10;
    public static String nodeSubs = "6,7,8,9,10,11,12,13,14,15,16,17,18,19";
    public static String nodeInterest = "luna,via,nella,maya,ana";
    public static Map<Integer, String> temp = new HashMap<Integer, String>();
    public static int luna, via, nella, maya, ana;

    public static void main(String[] args) {
        luna = via = nella = maya = ana = 1;

        //baca node publisher nya dulu
        List<Integer> pub = bacaSubs(nodeSubs);
        List<String> interest = bacaInterest(nodeInterest);

        //pas awal simulasi membuat tiap node ada interest nya dulu  
        temp(pub, interest);

        //Inisialisasi Objek dan Mendefinisikan Path Lokasi File
        File file = new File("G:\\ContohFileSubs.txt");

        //Membuat Statement Try-Resource-Statement
        int waktu = 86400;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            for (int i = awalSimulasi; i < akhirSimulasi; i = i + 86400) {

                //looping untuk membuat berapa banyak pesan pada detik tertentu
                for (int j = 0; j < pub.size(); j++) {
                    int from = pub.get(j);
                    int to = getRandomNumberInRange(0, 40);

                    //mengecek node Dest yang di random bukan ke dirinya sendiri
                    while (pub.contains(to)) {
                        to = getRandomNumberInRange(0, 40);
                    }

                    int loop;
                    if (temp.get(from).equalsIgnoreCase("luna")) {
                        loop = luna;
                        //Menulis data String
                        bw.write(i + "\tC\t" + temp.get(from)  + "\t\t" + from + "\t" + to + "\t" + "1" + "\t" + "SUBS");

                        //Membuat Baris Baru
                        bw.newLine();
                        luna++;
                    } else if (temp.get(from).equalsIgnoreCase("via")) {
                        loop = via;
                        //Menulis data String
                        bw.write(i + "\tC\t" + temp.get(from) + "\t\t" + from + "\t" + to + "\t" + "1" + "\t" + "SUBS");

                        //Membuat Baris Baru
                        bw.newLine();
                        via++;

                    } else if (temp.get(from).equalsIgnoreCase("nella")) {
                        loop = nella;
                        //Menulis data String
                        bw.write(i + "\tC\t" + temp.get(from)  + "\t\t" + from + "\t" + to + "\t" + "1" + "\t" + "SUBS");

                        //Membuat Baris Baru
                        bw.newLine();
                        nella++;

                    } else if (temp.get(from).equalsIgnoreCase("maya")) {
                        loop = maya;
                        //Menulis data String
                        bw.write(i + "\tC\t" + temp.get(from)  + "\t\t" + from + "\t" + to + "\t" + "1" + "\t" + "SUBS");

                        //Membuat Baris Baru
                        bw.newLine();
                        maya++;

                    } else if (temp.get(from).equalsIgnoreCase("ana")) {
                        loop = ana;
                        //Menulis data String
                        bw.write(i + "\tC\t" + temp.get(from) + "\t\t" + from + "\t" + to + "\t" + "1" + "\t" + "SUBS");

                        //Membuat Baris Baru
                        bw.newLine();
                        ana++;
                    }

                }
//mengubah interest nya pada saat ganti hari
                if (i%86400==0) {
                    temp.clear();
                    Collections.shuffle(interest);
                    temp(pub, interest);
                }
            }

        } catch (FileNotFoundException ex) {
            //Menampilkan pesan jika file tidak ditemukan
            System.out.println("File " + file.getName() + " Tidak Ditemukan");
        } catch (IOException ex) {
            //Menampilkan pesan jika terjadi error atau file tidak dapat dibaca
            System.out.println("File " + file.getName() + " Tidak Dapat DIbaca");
        }
    }

    //membuat bilangan random antara min - max
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    //membaca node publish untuk array
    private static ArrayList<Integer> bacaSubs(String text) {
        String[] pecah = text.split(",");
        ArrayList jadi = new ArrayList();
        for (String string : pecah) {
            jadi.add(Integer.parseInt(string));
        }
//        System.out.println(jadi);
        return jadi;
    }

    private static ArrayList<String> bacaInterest(String text) {
        String[] pecah = text.split(",");
        ArrayList jadi = new ArrayList();
        for (String string : pecah) {
            jadi.add(string);
        }
//        System.out.println(jadi);
        return jadi;
    }

    //mengambil random untuk node dalam array
    private static int getRandomElement(List<Integer> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    //mengambil random untuk Interest dalam array
    public static String getRandomElementInterest(List<String> list) {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    //membuat random lagi node yang publish
    private static String nodeSubs() {
        String node = "";
        for (int i = 0; i < 10; i++) {
            node = node + getRandomNumberInRange(0, 40) + ",";
        }
        node.substring(0, node.length() - 1);
        return node;
    }

    //mengabungkan antaran suatu node dengan interest nya
    private static void temp(List<Integer> pub, List<String> Interest) {
        for (Integer node : pub) {
            String inter = getRandomElementInterest(Interest);
            //untuk mengecek interest nya sudah ada belum
            while (temp.containsValue(inter) && temp.size() < 5) {
                inter = getRandomElementInterest(Interest);
            }
            temp.put(node, inter);

        }
        System.out.println(temp);
    }
}
