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
public class EpidemicWithInterest1 implements RoutingDecisionEngine {

    public static final String CONTENT = "popularity";
//    public String waktu;
//    public boolean cek = true;
//    public static int lastCek = 0;

    private static int waktu = 86400;

    public Map<String, Double> popularity = new HashMap<String, Double>();
    //menyimpan pesan request subscriber
    public Map<DTNHost, Message> subscriptionList = new HashMap<DTNHost, Message>();

    public EpidemicWithInterest1(Settings s) {
    }

    public EpidemicWithInterest1(EpidemicWithInterest1 proto) {
        super();
        this.popularity = proto.popularity;

    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer, ArrayList interest) {
//        DecisionEngineRouter other = (DecisionEngineRouter) (peer.getRouter());
//        DecisionEngineRouter thisHost1 = (DecisionEngineRouter) (thisHost.getRouter());
//
//        if (interest.size() == 4 && cek == true) {
//            String a = Double.toString(SimClock.getTime());
//            waktu = a;
//            cek = false;
//
//        }
//            if (!thisHost.getRouter().status().equalsIgnoreCase("publiser")) {
//                for (int i = 0; i < other.getInterest().size(); i++) {
//                    if (!interest.contains(other.getInterest().get(i))) {
//                        interest.add(other.getInterest().get(i));
//                    }
//                }
//            }
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer, ArrayList interest) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer, ArrayList interest) {

        DTNHost ahost = con.getOtherNode(peer);
        DecisionEngineRouter other = (DecisionEngineRouter) (peer.getRouter());
        DecisionEngineRouter thisHost = (DecisionEngineRouter) (ahost.getRouter());

//        if (interest.size() == 4 && cek == true) {
//            String a = Double.toString(SimClock.getTime());
//            waktu = a;
//            cek = false;
//
//        }
        //pertukaran interest
        for (Map.Entry<DTNHost, Message> entry : getOtherDecisionEngine(peer).subscriptionList.entrySet()) {
            DTNHost keyPeer = entry.getKey();
            Message valuePeer = entry.getValue();
            //cek sudah ada host itu belum
            if (!subscriptionList.containsKey(keyPeer)) {
                subscriptionList.put(keyPeer, valuePeer);
            } else {
                // jika sudah ada cek velue nya  untuk perbaharui pesan yang baru
                if (subscriptionList.get(keyPeer).getCreationTime() < valuePeer.getCreationTime()) {
                    subscriptionList.put(keyPeer, valuePeer);
                }

            }
        }

//        if (!ahost.getRouter().status().equalsIgnoreCase("publiser")) {
//            if(subscriptionList.containsKey(peer)){
//               ArrayList temp= subscriptionList.get(peer);
//                for (Object object : temp) {
//                    
//                }
//            }else{
//                
//            }
//            for (int i = 0; i < other.getInterest().size(); i++) {
//                if (!interest.contains(other.getInterest().get(i))) {
//                    interest.add(other.getInterest().get(i));
//                }
//            }
    }

    @Override
    public boolean newMessage(Message m, ArrayList interest,
            DTNHost h
    ) {

        //untuk pubisher
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
//            System.out.println(m.getId());
//            System.out.println(h.getRouter().getInterest());
            h.getRouter().resetInterest(m.getId());
//            m.setTtl(5);
//            System.out.println(h.getRouter().getInterest());
//            System.out.println("");
//h.getRouter().interest=m.getProperty(waktu);
        } //untuk subscriber
        else {
//            System.out.println(h.getAddress());
//            System.out.println(h.getRouter().getInterest());

//===============================
// update subscription list
            if (!subscriptionList.containsKey(h)) {
                subscriptionList.put(h, m);
            } else {
                // jika sudah ada cek velue nya  untuk perbaharui pesan yang baru
                if (subscriptionList.get(h).getCreationTime() < m.getCreationTime()) {
                    subscriptionList.put(h, m);
                }

            }
//================================

            h.getRouter().resetInterest(m.getId());
//            m.setTtl(10);

            if (popularity.containsKey(m.getId())) {
                popularity.put(m.getId(), popularity.get(m.getId()) + 1);
            } else {

                popularity.put(m.getId(), 1.0);
            }
//            System.out.println(m.getId());
//            System.out.println(m.getContentType());
//            System.out.println("");

        }

        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost,
            ArrayList interest
    ) {
        if (aHost.getRouter().status().equalsIgnoreCase("subscriber")) {
//            System.out.println(aHost.getRouter().getInterest()+"\t"+m.getId());
            if (!m.getContentType().equalsIgnoreCase("SUBS")) {
                if (aHost.getRouter().getInterest().contains(m.getContentType())) {
//                    System.out.println(aHost.getAddress() + " " + m.getId());
//                    System.out.println("");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost,
            ArrayList interest
    ) {
        if (thisHost.getRouter().status().equalsIgnoreCase("publiser")) {
            return false;
        } else {
            //pesan yg di terima request dari subscriber
            //penyimpanan interest dari subscriber

            if (m.getContentType().equalsIgnoreCase("SUBS")) {
                if (subscriptionList.containsKey(m.getFrom())) {
                    Message mLama = subscriptionList.get(m.getFrom());
                    if (m.getCreationTime() > mLama.getCreationTime()) {
                        subscriptionList.put(m.getFrom(), m);
                    }
                } else {
                    subscriptionList.put(m.getFrom(), m);
                }
                // pesan subs perlu disimpan untuk di sebar...
                return true;
            }

            //algoritma hopCount Filtering
            if (!hopCountFilter(thisHost, m)) {
                return false;
            }
//---------
//algoritma duplicate filtering
//            if (!duplicateFilter(thisHost, m)) {
//                return false;
//            }
            return true;
        }

    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost,
            ArrayList interest
    ) {
        if (m.getContentType().equalsIgnoreCase("SUBS")) {
            //queue dengan popularity
//            m.updateProperty(CONTENT, 20);
            return false;
        } else {
            for (Map.Entry<DTNHost, Message> entry : subscriptionList.entrySet()) {
                DTNHost key = entry.getKey();
                Message value = entry.getValue();
                if (value.getId().equals(m.getContentType())) {
//                System.out.println(m.getId());

//algoritma filtering
//                    if (!duplicateFilter(otherHost, m) == false) {
////                        System.out.println("kirim");
//                        return false;
//                    }
//-------

//algoritma popularity
                    popularity(m);
                    return true;
                }
            }
//            System.out.println("tidak kirim");
            return false;
        }

    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost,
            ArrayList interest
    ) {
        return false;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld,
            ArrayList interest
    ) {
        return true;
    }

    private EpidemicWithInterest1 getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (EpidemicWithInterest1) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new EpidemicWithInterest1(this);
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
        Collection<Message> peer = otherHost.getMessageCollection();
        for (Message message : peer) {//loop sebanyak pesan di buffer meHost
            if (m.getId().equals(message.getId())) {//bandingkan hop count m1 & m2
//                    System.out.println("simpan \t" + msgThisHost + " " + msgPeer);
                return false;
            }
        }
        return true;
    }

    public void popularity(Message m) {
        m.updateProperty(CONTENT, popularity.get(m.getContentType()));

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
    @Override
    public void update(DTNHost host) {
        if (SimClock.getIntTime() - waktu == 0) {
//            System.out.println(popularity);
            popularity.clear();
            waktu = waktu + 86400;
        }
    }
}
