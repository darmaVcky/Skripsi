package routing;

import core.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import report.interestEngine;

/**
 * @author willy Sanata Dharma University
 */
public class PubSub implements RoutingDecisionEngine {

    public static final String CONTENT2 = "popularity";
    private static int waktu = 604800;// waktu perubahan interest 1 minggu

    public Map<String, Double> popularity = new HashMap<String, Double>();
    //menyimpan pesan request subscriber
    public Map<DTNHost, Message> subscriptionList = new HashMap<DTNHost, Message>();
    public DTNHost akuHost;

    public PubSub(Settings s) {
    }

    public PubSub(PubSub proto) {
        super();
        this.popularity = proto.popularity;

    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer, ArrayList interest) {
        akuHost = thisHost;
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer, ArrayList interest) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer, ArrayList interest) {

        DTNHost ahost = con.getOtherNode(peer);//get thisHost

        /* pertukaran interest */
 /* jika thisHost bukan publisher dan peer bukan publisher */
        if (!ahost.getRouter().status().equalsIgnoreCase("publiser") && !peer.getRouter().status().equalsIgnoreCase("publiser")) {
            for (Map.Entry<DTNHost, Message> entry : getOtherDecisionEngine(peer).subscriptionList.entrySet()) {
                /* looping subscribe list thisHost */
                DTNHost keyPeer = entry.getKey();
                Message valuePeer = entry.getValue();
                /* cek jika thisHost belum ada node peer di subscribe list */
                if (!subscriptionList.containsKey(keyPeer)) {
                    /* tambahkan interest node peer ke susbcribe list thisHost */
                    subscriptionList.put(keyPeer, valuePeer);
                } else {
                    /* jika sudah ada, cek value (interest) untuk perbaharui interest yang baru */
                    if (subscriptionList.get(keyPeer).getCreationTime() < valuePeer.getCreationTime()) {
                        subscriptionList.put(keyPeer, valuePeer);
                    }

                }
            }
        }
    }

    @Override
    public boolean newMessage(Message m, ArrayList interest, DTNHost h) {

        /* untuk pubisher */
 /* jika node adalah publisher */
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
            /* maka ubah interest publisher untuk membuat pesan sesuai interest */
            h.getRouter().resetInterest(m.getId());
        } /* untuk subscriber */ else {
            /* jika node adalah subscribe maka ubah interest */
            h.getRouter().resetInterest(m.getId());

            /* digunakan untuk queue popular */
 /* jika pesan adalah channel popular */
            if (popularity.containsKey(m.getId())) {
                /* dahulukan pengiriman pesan channel popular dengan menambah nilai popularity channel */
                popularity.put(m.getId(), popularity.get(m.getId()) + 1);
            } else {
                /* jika pesan adalah pesan baru maka set nilai popularity menjadi 1 */
                popularity.put(m.getId(), 1.0);
            }

        }

        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost, ArrayList interest) {
        /* jika peer adalah subscriber */
        if (aHost.getRouter().status().equalsIgnoreCase("subscriber")) {
            /* jika pesan bukan broadcast interest */
            if (!m.getContentType().equalsIgnoreCase("SUBS")) {
                /* jika pesan sesuai dengan interest peer maka terima pesan */
                if (aHost.getRouter().getInterest().contains(m.getContentType())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost,
            ArrayList interest) {
        /* jika node adalah publishe maka tolak pesan agar tidak disimpan */
        if (thisHost.getRouter().status().equalsIgnoreCase("publiser")) {
            return false;
        } else {
            /* pesan yg di terima request dari subscriber penyimpanan interest 
            dari subscriber */
 /* terima semua pesan untuk di-ralay oleh subscribe dan relay node */

 /*algoritma duplicate*/
//             if (!duplicateFilter(thisHost, m)) {
//                return false;
//            }

            /*algoritma hopCount Filtering*/
//            if (!hopCountFilter(thisHost, m)) {
//                return false;
//            }
            /*------------------------------------*/
            return true;
        }

    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost,
            ArrayList interest) {

        /* jika pesan adalah broadcast interest maka tolak pesan */
        if (m.getContentType().equalsIgnoreCase("SUBS")) {
            return false;
            /* jika node adalah publisher maka langsung kirim pesan */
        } else if (akuHost.getRouter().status.equalsIgnoreCase("publiser")) {
            return true;
        } else {
            /* jika pesan bukan broadcast interest maka */
            for (Map.Entry<DTNHost, Message> entry : subscriptionList.entrySet()) {
                /* jika thisHost terdaftar dalam subscribe list maka */
                DTNHost key = entry.getKey();
                Message value = entry.getValue();
                /* jika pesan sesuai dengan interest node maka terima pesan */
                if (value.getId().equals(m.getContentType())) {

//algoritma filtering
//                    if (duplicateFilter(otherHost, m) == false) {
//
//                        return false;
//                    }
//-------
//algoritma popularity
                    popularity(m);
                    return true;
                }
            }

            return false;
        }

    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost,
            ArrayList interest) {
        return false;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld,
            ArrayList interest) {
        return true;
    }

    private PubSub getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (PubSub) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new PubSub(this);
    }

    //sisi penerima
    public boolean hopCountFilter(DTNHost thisHost, Message m) {
        //mengecek ada pesan tidak di buffernya
        if (thisHost.getRouter().hasMessage(m.getId())) {
            //kalau ada ambil pesan tersebut
            int msgMeHost = thisHost.getRouter().getMessage(m.getId()).getHopCount();
            int msgPeer = m.getHopCount();

            if (msgMeHost > msgPeer) {//bandingkan hop count m1 & m2
//                System.out.println("hapus");
                thisHost.getRouter().removeFromMessages(m.getId());
            } else {
                //kalau m1 lebih kecil dari m2 tidak usah di simpan
                return false;
            }
        }

//        Collection<Message> peer = thisHost.getMessageCollection();
//        for (Message message : peer) {//loop sebanyak pesan di buffer meHost
//            int msgMeHost = message.getHopCount();//hitung hop count m1
//            int msgPeer = m.getHopCount();//hitung op count m2
//            //terima kalau hopcount thisHost lebih kecil dari peer dan hps di peer
//            //cek ada tidak pesan itu di buffer
//            if (message.getId().equals(m.getId())) {
//                if (msgMeHost > msgPeer) {//bandingkan hop count m1 & m2
////                System.out.println("hapus");
//                    thisHost.getRouter().removeFromMessages(m.getId());
//                } else {
//                    //kalau m1 lebih kecil dari m2 tidak usah di simpan
//                    return false;
//                }
//            }
//        }
        return true;
    }

    public boolean duplicateFilter(DTNHost otherHost, Message m) {
        //sisi pengirim
        if (otherHost.getRouter().hasMessage(m.getId())) {
            return false;
        }
        return true;
    }

    public void popularity(Message m) {
        /*jika pada subs list tidak ada catat interest subscriber*/
        if (!subscriptionList.isEmpty()) {
            double pop = 0.0;//digunakan ranking interest
            /*looping sebanyak subs list*/
            for (Map.Entry<DTNHost, Message> entry : subscriptionList.entrySet()) {
                DTNHost key = entry.getKey();
                Message value = entry.getValue();
                /*jika interest isi pesan sama dengan interset node maka ranking channel dinaikkan*/
                if (m.getContentType().equalsIgnoreCase(value.getId())) {
                    pop++;
                }
            }
            m.updateProperty(CONTENT2, pop);//update ranking channel
        }
    }

//    public List convertCollection(Collection<Message> coll) {
//        List list;
//        if (coll instanceof List) {
//            list = (List) coll;
//        } else {
//            list = new ArrayList(coll);
//        }
//        return list;
//    }
    // untuk popularity queue
    @Override
    public void update(DTNHost host) {
        if (SimClock.getIntTime() - waktu == 0) {
//            System.out.println(popularity);
            popularity.clear();
            waktu = waktu + 604800;
        }
    }
}
