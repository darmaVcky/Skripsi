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
import java.util.*;
import java.util.TreeMap;

/**
 *
 * @author asus
 */
public class latencyReport extends Report implements MessageListener, UpdateListener {

    private static Map<Message, Double> buatPesan;
    private static Map<Message, Double> selesai;
    private static Map<String, Integer> minta;
    private static Map<String, Double> rata;

    private double lastRecord = Double.MIN_VALUE;
    private int interval = 86400;

    public latencyReport() {
        super();
        buatPesan = new HashMap<Message, Double>();
        rata = new HashMap<String, Double>();
        selesai = new HashMap<Message, Double>();
        minta = new HashMap<String, Integer>();
    }

    @Override
    public void newMessage(Message m) {
        //mencatat semua pesan request dari subscriber
        if (m.getContentType().equalsIgnoreCase("SUBS")) {
            buatPesan.put(m, SimClock.getTime());
            rata.put(m.getId(), 0.0);

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

//        for (Map.Entry<Message, Double> entry : selesai.entrySet()) {
//            Message key = entry.getKey();
//            Double value = entry.getValue();
//            for (Map.Entry<String, Double> entry1 : rata.entrySet()) {
//                String key1 = entry1.getKey();
//                Double value1 = entry1.getValue();
//                if (key.getId().contains(key1)) {
//                    value1 = value1 + value;
//                    rata.put(key1, value1);
//                }
//            }
//        }
//        
//        for (Map.Entry<String, Double> entry : rata.entrySet()) {
//            String key = entry.getKey();
//            Double value = entry.getValue();
//            write(key+"\t"+value/minta.get(key));
//        }
        super.done();
    }

    @Override
    public void updated(List<DTNHost> hosts) {

        double simTime = SimClock.getTime();
        if (simTime - lastRecord >= interval) {
            lastRecord = SimClock.getTime();
            for (Map.Entry<Message, Double> entry : selesai.entrySet()) {
                Message key = entry.getKey();
                Double value = entry.getValue();
                for (Map.Entry<String, Double> entry1 : rata.entrySet()) {
                    String key1 = entry1.getKey();
                    Double value1 = entry1.getValue();
                    if (key.getId().contains(key1)) {
                        value1 = value1 + value;
                        rata.put(key1, value1);
                    }
                }
            }

            for (Map.Entry<String, Double> entry : rata.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                write(SimClock.getIntTime()+"\t"+key + "\t" + value / minta.get(key));
            }

            resetValueMap();

//            done();
            this.lastRecord = simTime - simTime % interval;
        }
    }

    public void resetValueMap() {
        for (Map.Entry<String, Double> entry : rata.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            rata.put(key, 0.0);
        }
    }

}
