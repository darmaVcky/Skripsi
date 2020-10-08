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
import java.util.*;
import java.util.TreeMap;

/**
 *
 * @author asus
 */
public class convergenTimeReport extends Report implements MessageListener {

    private static Map<Message, Double> buatPesan;
    private static Map<Message, Double> selesai;
    private static Map<Double, Map<String, Double>> rata;
    private static Map<Double, Map<String, Integer>> request;
//        private static Map<String, Double> minta;

    public convergenTimeReport() {
        super();
        buatPesan = new HashMap<Message, Double>();
        selesai = new HashMap<Message, Double>();
        rata = new HashMap<Double, Map<String, Double>>();
        request = new HashMap<Double, Map<String, Integer>>();
    }

    @Override
    public void newMessage(Message m) {
        //mencatat semua pesan request dari subscriber
        if (m.getContentType().equalsIgnoreCase("SUBS")) {
            buatPesan.put(m, SimClock.getTime());
            
            //mencatat berapa banya sub pada setiap chennel per waktu tertentu untuk rata2
            Map<String, Integer> temp = new HashMap<String, Integer>();
            //cek ad tidak pada detik tertentu
            if (request.containsKey(m.getCreationTime())) {
                temp = request.get(m.getCreationTime());
                //cek apaka udh ad channel di detik itu
                if (temp.containsKey(m.getId())) {
                    temp.put(m.getId(), temp.get(m.getId()) + 1);
                } else {
                    temp.put(m.getId(), 1);
                }
                request.put(m.getCreationTime(), temp);
            } else {
                temp.put(m.getId(), 1);
                request.put(m.getCreationTime(), temp);
            }
        }
    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        //menyimpan semua pesan yang terkirim ke variable selesai
        for (Map.Entry<Message, Double> entry : buatPesan.entrySet()) {
            Message key = entry.getKey();

            //mengecek sama tidak yang di simpan di request subscriber
            if (key.getId().equals(m.getContentType()) && key.getFrom().equals(to)) {
                double hit = entry.getValue();
                double value = SimClock.getTime() - hit;
//                    write(key.getFrom()+" \t"+value);

                //penyimpanan pesan latency yang di terima                
                if (!selesai.containsKey(key)) {
                    selesai.put(key, value);
                }
            }
        }

    }

    @Override
    public void done() {

//        Map<Message, Double> mapTree= new TreeMap<Message,Double>(selesai);
//pindah kn ke list dulu
        List<Map.Entry<Message, Double>> list
                = new LinkedList<Map.Entry<Message, Double>>(selesai.entrySet());

        //urutkan list nya dengan pembanding creation timenya
        Collections.sort(list, new Comparator<Map.Entry<Message, Double>>() {
            public int compare(Map.Entry<Message, Double> o1,
                    Map.Entry<Message, Double> o2) {
                String a = String.valueOf(o1.getKey().getCreationTime());
                String b = String.valueOf(o2.getKey().getCreationTime());
                if (a.compareTo(b) == 0) {
                    return o1.getKey().getId().compareTo(o2.getKey().getId());
                }
                return (a.compareTo(b));
            }
        });

        //pindahkan ke map lagi untuk di write
        Map<Message, Double> sortedMap = new LinkedHashMap<Message, Double>();
        for (Map.Entry<Message, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        write("waktu\tSubscriber\tchannel\tlatency");
        for (Map.Entry<Message, Double> entry : sortedMap.entrySet()) {
            Message key = entry.getKey();
            Double value = entry.getValue();

            Map<String, Double> temp = new HashMap<String, Double>();
            if (rata.containsKey(key.getCreationTime())) {
                temp = rata.get(key.getCreationTime());
                if (temp.containsKey(key.getId())) {
                    double total = temp.get(key.getId());
                    total = total + value;
                    temp.put(key.getId(), total);
                } else {
                    temp.put(key.getId(), value);
                }
                rata.put(key.getCreationTime(), temp);
            } else {
                temp.put(key.getId(), value);
                rata.put(key.getCreationTime(), temp);
            }
            write(key.getCreationTime() + "\t" + key.getFrom() + "\t\t" + key.getId() + "\t" + value);
        }

        write("");
        write("rata - rata latency per channel pada detik tertentu");
        for (Map.Entry<Double, Map<String, Double>> entry : rata.entrySet()) {
            Double key = entry.getKey();
            Map<String, Double> value = entry.getValue();
            
            // mengambil jumlah request setiap channel
            Map<String, Integer>temp=request.get(key);

            for (Map.Entry<String, Double >entry1 : value.entrySet()) {
                String channel = entry1.getKey();
                double total = entry1.getValue();
                
                int jumlah = temp.get(channel);
                write(key + "\t" + channel + "\t" + total/jumlah);
            }
        }
        super.done();
    }

}
