package routing;

import core.*;
import java.util.*;
import routing.*;
import report.interestEngine;

/**
 * @author willy Sanata Dharma University
 */
public class EpidemicWithInterest implements RoutingDecisionEngine {

    public static final String CONTENT2 = "popularity";
//    public String waktu;
//    public boolean cek = true;
//    public static int lastCek = 0;

    private static int waktu = 604800;//86400//waktu untuk mengubah interest node
    //digunakan untuk priority
    public Map<String, Double> popularity = new HashMap<String, Double>();
    //menyimpan pesan request subscriber
    public Map<DTNHost, Message> subscriptionList = new HashMap<DTNHost, Message>();

    public EpidemicWithInterest(Settings s) {
    }

    public EpidemicWithInterest(EpidemicWithInterest proto) {
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

//        DTNHost ahost = con.getOtherNode(peer);
//        DecisionEngineRouter other = (DecisionEngineRouter) (peer.getRouter());
//        DecisionEngineRouter thisHost = (DecisionEngineRouter) (ahost.getRouter());
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
            if (!subscriptionList.containsKey(keyPeer)) {//cek sub list 
                subscriptionList.put(keyPeer, valuePeer);//masukkan ID peer dan interestnya
            } else {
                // jika sudah ada cek velue nya  untuk perbaharui pesan yang baru
                if (subscriptionList.get(keyPeer).getCreationTime() < valuePeer.getCreationTime()) {
                    /* cek peer minta pesan maka cek waktu minta pesan, jika minta pesan setelah pesan dibuat maka
                    simpan interest pada subs list*/
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
    public boolean newMessage(Message m, ArrayList interest, DTNHost h) {

        //untuk pubisher
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
            h.getRouter().resetInterest(m.getId());//untuk mengubah interest pub
            //System.out.println("pesan baru: "+m.getId()+"\t ints: "+m.getContentType());
            m.setTtl(10000);
        } //untuk subscriber
        else {

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

            h.getRouter().resetInterest(m.getId());//untuk mengubah interest subs
            //System.out.println("pesan baru: "+m.getId()+"\t ints: "+m.getContentType());
            m.setTtl(5000);

            if (popularity.containsKey(m.getId())) {//cek banyak sub yg berminat pada pesan tersebut
                popularity.put(m.getId(), popularity.get(m.getId()) + 1);//simpan populariti pesan jika sudah ada sebelumnya
            } else {

                popularity.put(m.getId(), 1.0);//masukkan populariti pesan untuk pesan baru
            }
        }

        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost, ArrayList interest) {

        if (aHost.getRouter().status().equalsIgnoreCase("subscriber")) {//jika node adalah subs
            if (!m.getContentType().equalsIgnoreCase("SUBS")) {//jika pesan adalah bukan pesan broadcast interest
                if (aHost.getRouter().getInterest().contains(m.getContentType())) {//jika pesan sesuai dengan interestnya
                    return true;//pesan telah sampai
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost, ArrayList interest) {

        if (thisHost.getRouter().status().equalsIgnoreCase("publiser")) {//jika node adalah pub
            return false;//tolak pesan
        } else {
            /* pesan yg diterima request dari subscriber penyimpanan interest dari subscriber */

//            if (m.getContentType().equalsIgnoreCase("SUBS")) { //jika pesan adalah subs
//                if (subscriptionList.containsKey(m.getFrom())) {//jika pada subs list thisHost ada pesan yg diminta
//                    Message mLama = subscriptionList.get(m.getFrom());
//                    if (m.getCreationTime() > mLama.getCreationTime()) {//jika pesan peer lebih baru
//                        subscriptionList.put(m.getFrom(), m);//tambahkan pesan broadcast thisHost pada subs list
//                    }
//                } else {//jika pesan peer lebih baru dari thisHost
//                    subscriptionList.put(m.getFrom(), m);//maka simpan pesan broadcast peer
//                }
//                //pesan subs perlu disimpan untuk di sebar...
//                return true;
//            }

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
            ArrayList interest) {

        if (m.getContentType().equalsIgnoreCase("SUBS")) {//jika pesan adalah subs
            //queue dengan popularity
//            m.updateProperty(CONTENT2, 20.0);//known
//                        m.updateProperty(CONTENT2, 3.0);//duplicate

            return false;
        } else {
            for (Map.Entry<DTNHost, Message> entry : subscriptionList.entrySet()) {//loop sebanyak subs list
                DTNHost key = entry.getKey();
                Message value = entry.getValue();
                if (value.getId().equals(m.getContentType())) {//cek interest pesan 

                    //algoritma hop count
//                    if (!hopCountFilter(otherHost, m)) {
//                        return false;
//                    }
                    //algoritma filtering
//                    if (duplicateFilter(otherHost, m)==false) {
//                        return false;
//                    }
//-------
                    //algoritma populari
                    popularity(m);
                    return true;//kirim pesan
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

    private EpidemicWithInterest getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (EpidemicWithInterest) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new EpidemicWithInterest(this);
    }

    /*sisi penerima*/
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
        return true;
    }

    public boolean duplicateFilter(DTNHost otherHost, Message m) {
        if (otherHost.getRouter().hasMessage(m.getId())) {
            return false;
        }
        return true;
    }

    public void popularity(Message m) {
        if (m.getProperty(CONTENT2) != "") {
            m.updateProperty(CONTENT2, popularity.get(m.getContentType()));

        } else {
            System.out.println("tidak ada");
            m.addProperty(CONTENT2, popularity.get(m.getContentType()));
        }
    }

    // untuk popularity queue
    @Override
    public void update(DTNHost host) {
        if (SimClock.getIntTime() - waktu == 0) {
//            System.out.println(popularity);
            popularity.clear();
            waktu = waktu + 604800;//perubahan interest reality
            //86400 perubahan interest haggle
        }
    }
}
