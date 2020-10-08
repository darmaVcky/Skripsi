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
public class totalReplicate extends Report implements MessageListener, UpdateListener {

    private static int nella, via, ana, luna, maya = 0;
    private static Map<String, Integer> pesanPub;
    private static Map<String, Integer> pesanSub;
    private static Map<String, Integer> minta;
    private double lastRecord = Double.MIN_VALUE;
    private int interval = 7200;
//        private static Map<String, Double> minta;

    public totalReplicate() {
        super();
        pesanPub = new HashMap<String, Integer>();
        pesanSub = new HashMap<String, Integer>();
        minta = new HashMap<String, Integer>();
    }

    @Override
    public void newMessage(Message m) {

        //pesan publiser
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
            pesanPub.put(m.getId(), 0);
            hitJumlahPesan(m);

            if (minta.containsKey(m.getContentType())) {

                minta.put(m.getContentType(), minta.get(m.getContentType()) + 1);
            } else {
                minta.put(m.getContentType(), 1);
            }

            // pesan subscriber
        } else {
            if (!pesanSub.containsKey(m.getId())) {
                pesanSub.put(m.getId(), 0);
            }

        }

    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
//        //mencatat jumlah pesan yang di forward/copy per pesan
//        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
//            pesanPub.put(m.getId(), pesanPub.get(m.getId()) + 1);
//        }
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {

    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        //mencatat jumlah pesan yang di forward/copy per pesan
        if (!firstDelivery) {
            if (!m.getContentType().equalsIgnoreCase("SUBS")) {
                pesanPub.put(m.getId(), pesanPub.get(m.getId()) + 1);
            }
        }
    }

    @Override
    public void done() {
//        for (Map.Entry<String, Integer> entry1 : pesanPub.entrySet()) {
//            String keyPub = entry1.getKey();
//            Integer valuePub = entry1.getValue();
//
//            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
//                String keySub = entry.getKey();
//                Integer valueSub = entry.getValue();
//                //untuk menghitung rata2 copy per channel
//                if (keyPub.contains(keySub)) {
//                    int value = valueSub + valuePub;
//                    temp.put(keySub, value);
//                }
//                //menampilkan jumlah copy per pesan
//            }
////            write(keyPub + "\t" + valuePub);
//        }
//        write("");
//        write("Avarage copy per channel");
//
//        for (Map.Entry<String, Integer> entry : temp.entrySet()) {
//            String key = entry.getKey();
//            Integer value = entry.getValue();
//            write(key + "\t" + value / tentJumlah(key));
//        }

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
        double simTime = SimClock.getTime();
        if (simTime - lastRecord >= interval) {
            lastRecord = SimClock.getTime();

            //proses hitung total copynya
            for (Map.Entry<String, Integer> entry1 : pesanPub.entrySet()) {
                String keyPub = entry1.getKey();
                Integer valuePub = entry1.getValue();

                for (Map.Entry<String, Integer> entry : pesanSub.entrySet()) {
                    String keySub = entry.getKey();
                    Integer valueSub = entry.getValue();
                    //untuk menghitung rata2 copy per channel
                    if (keyPub.contains(keySub)) {
                        int value = valueSub + valuePub;
                        pesanSub.put(keySub, value);
                    }
                    //menampilkan jumlah copy per pesan
                }
//                write(keyPub+ "\t" + "\t" + valuePub);
            }

            //proses menampilin per channel
            for (Map.Entry<String, Integer> entry : pesanSub.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                double a, b;
                b = tentJumlah(key);
                if (b == 0) {
                    a = 0;
                } else {
                    a = value / tentJumlah(key);
                }
                write(SimClock.getIntTime() + "\t" + key + "\t" + a);
            }
            resetValueMap();

//            done();
            this.lastRecord = simTime - simTime % interval;
        }
    }

    public void resetValueMap() {
        for (Map.Entry<String, Integer> entry : pesanSub.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            pesanSub.put(key, 0);

        }
    }
}
