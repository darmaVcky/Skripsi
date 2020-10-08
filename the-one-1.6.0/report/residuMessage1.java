/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.SimClock;
import core.UpdateListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author asus
 */
public class residuMessage1 extends Report implements MessageListener,UpdateListener {

    private static int nella, via, ana, luna, maya = 0;
    private static Map<Message, Integer> buatPesan;
    private static Map<String, Double> selesai;
    private static Map<String, Integer> minta;
    private static Map<Message, Double> deliveryRate;
//        private static Map<String, Double> minta;

    private static int waktu = 86400;
    public residuMessage1() {
        super();
        buatPesan = new HashMap<Message, Integer>();
        selesai = new HashMap<String, Double>();
        deliveryRate = new HashMap<Message, Double>();
        minta = new HashMap<String, Integer>();
    }

    @Override
    public void newMessage(Message m) {
        
        //pesan publiser
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
            buatPesan.put(m, 0);
            hitJumlahPesan(m);

            // pesan subscriber
        } else {
            selesai.put(m.getId(), 0.0);

            if (minta.containsKey(m.getId())) {

                minta.put(m.getId(), minta.get(m.getId()) + 1);
            } else {
                minta.put(m.getId(), 1);
            }

        }

    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
      //menghitung nilai delivery prob per pesan ketika ttl habis
        if (dropped && minta.get(m.getContentType()) != null && buatPesan.get(m) != null) {
            double subs = minta.get(m.getContentType());
            double delivery = buatPesan.get(m);
            deliveryRate.put(m, delivery / subs);
        }
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        //menyimpan pesan yang sudah di kirim agar dapat di hitung ketika ttl habis
        if (firstDelivery) {
            for (Map.Entry<Message, Integer> entry : buatPesan.entrySet()) {
                Message key = entry.getKey();
                int value = entry.getValue();
                if (key.getId().equals(m.getId())) {
                    value++;
                    buatPesan.put(key, value);
                }
            }
        }
    }

    @Override
    public void done() {
//        for (Map.Entry<Message, Integer> entry : buatPesan.entrySet()) {
//            Message key = entry.getKey();
//            Integer value = entry.getValue();
//            write(key+"\t"+value);
//        }
//        
//        for (Map.Entry<Message, Integer> entry : buatPesan.entrySet()) {
//            Message key = entry.getKey();
//            Integer value = entry.getValue();
//            for (Map.Entry<String, Double> entry1 : selesai.entrySet()) {
//                String key1 = entry1.getKey();
//                Double value1 = entry1.getValue();
//                if (key.getId().contains(key1)) {
//                    value1 = value1 + value;
//                    selesai.put(key1, value1);
//                }
//            }
//            write(key + "\t" + value);
//
//        }

        for (Map.Entry<Message, Double> entry : deliveryRate.entrySet()) {
            Message key = entry.getKey();
            Double value = entry.getValue();

            //mentimpan total delivery nya per channel untuk di rata-rata
            for (Map.Entry<String, Double> entry1 : selesai.entrySet()) {
                String key1 = entry1.getKey();
                Double value1 = entry1.getValue();
                if (key.getId().contains(key1)) {
                    value1 = value1 + value;
                    selesai.put(key1, value1);
                }

            }
            write(key + "\t" + value);

        }
//        for (Map.Entry<String, Double> entry : deliveryRate.entrySet()) {
//            String key = entry.getKey();
//            Double value = entry.getValue();
//            write(key+"\t"+value);
//        }

//total delivery per channel
        double total = 0;
        write("");
        write("Total pesan per chanel");
        for (Map.Entry<String, Double> entry : selesai.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            
            //tentukan jumlah pesan yang di buat per channel
            int a = tentJumlah(key);
            total = total + value/a;
            write(key + "\t" + value / a);
        }
        write("total delivery rate dari semua channel :"+total/5);
        super.done();
    }

    //menghitung jumlah pesan yang telah dibuat di setiap chennel
    public static void hitJumlahPesan(Message m) {
        if (m.getContentType().equalsIgnoreCase("ana")) {
            ana++;
        } else if (m.getContentType().equalsIgnoreCase("luna")) {
            luna++;
        } else if (m.getContentType().equalsIgnoreCase("maya")) {
            maya++;
        } else if (m.getContentType().equalsIgnoreCase("nella")) {
            nella++;
        } else if (m.getContentType().equalsIgnoreCase("via")) {
            via++;
        }
    }

    //menentukan jumlah pesan yang dibagi
    public int tentJumlah(String a) {
        if (a.equals("ana")) {
            return ana;
        } else if (a.equals("via")) {
            return via;
        } else if (a.equals("nella")) {
            return nella;
        } else if (a.equals("maya")) {
            return maya;
        } else {
            return luna;
        }
    }

    @Override
    public void updated(List<DTNHost> hosts) {
        if(SimClock.getIntTime()-waktu>=0){
            minta.clear();
          waktu = waktu+86400;
        }
    }

}
