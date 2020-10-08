/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import util.Tuple;

/**
 *
 * @author Jarkom
 */
public class NewClass {
    public static LinkedList<Tuple<Integer, String>> temp = new LinkedList<>();
    public static void main(String[] args) {
//        String a= "aku";
//        if(a.equalsIgnoreCase("aku")){
//            System.out.println("bisa");
//    }
        Tuple<Integer, String>a = new Tuple<Integer,String>(1,"a");
        Tuple<Integer, String>b = new Tuple<Integer,String>(2,"b");
        Tuple<Integer, String>c = new Tuple<Integer,String>(3,"c");
        Tuple<Integer, String>d = new Tuple<Integer,String>(4,"d");
        Tuple<Integer, String>e = new Tuple<Integer,String>(5,"e");
     
       temp.add(a);
       temp.add(b);
       temp.add(c);
       temp.add(d);
       temp.add(e);
       
       ubah(temp);
     
        for (int i = 0; i < temp.size(); i++) {
            Tuple<Integer,String> tampung = temp.get(i);
            System.out.println(tampung);
        }
        
       
}
    
    public static void ubah(LinkedList<Tuple<Integer,String>>a){
        
         for (Iterator<Tuple<Integer, String>> i = a.iterator();
                    i.hasNext();) {
                Tuple<Integer, String> t = i.next();
                if (t.getKey() == 2) {
                    i.remove();
                }
            }
    }
}
