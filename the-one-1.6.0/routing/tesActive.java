/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author asus
 */
public class tesActive extends ActiveRouter {

    public static final String CONTENT = "popularity";
    private static int waktu = 86400;

    public Map<String, Integer> popularity = new HashMap<String, Integer>();
    //menyimpan pesan request subscriber
    public static Map<DTNHost, Message> subscriptionList = new HashMap<DTNHost, Message>();

    public tesActive(Settings s) {
        super(s);
    }

    public tesActive(tesActive r) {
        super(r);
    }

    @Override
    public boolean createNewMessage(Message m) {
        makeRoomForNewMessage(m.getSize());
        DTNHost h = getHost();
        if (!m.getContentType().equalsIgnoreCase("SUBS")) {
//            System.out.println(m.getId());
//            System.out.println(h.getRouter().getInterest());
            h.getRouter().resetInterest(m.getId());
            m.setTtl(5);
//            System.out.println(h.getRouter().getInterest());
//            System.out.println("");
//h.getRouter().interest=m.getProperty(waktu);
        } //untuk subscriber
        else {
//            System.out.println(h.getAddress());
//            System.out.println(h.getRouter().getInterest());

            h.getRouter().resetInterest(m.getId());
            m.setTtl(15);

            if (popularity.containsKey(m.getId())) {
                popularity.put(m.getId(), popularity.get(m.getId()) + 1);
            } else {

                popularity.put(m.getId(), 1);
            }
//            System.out.println(m.getId());
//            System.out.println(m.getContentType());
//            System.out.println("");

        }
        addToMessages(m, true);

        return true;

    }

    @Override
    public void changedConnection(Connection con) {
        super.changedConnection(con); //To change body of generated methods, choose Tools | Templates.
        if (con.isUp()) {

            DTNHost thisHost = getHost();
            DTNHost peer = con.getOtherNode(thisHost);

            MessageRouter otherRouter = peer.getRouter();
            assert otherRouter instanceof ProphetRouter : "only works "
                    + " with other routers of same type";

            for (Map.Entry<DTNHost, Message> entry : ((tesActive) otherRouter).subscriptionList.entrySet()) {
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

            // pengiriman pesan 
            Collection<Message> msgs = getMessageCollection();
            for (Message m : msgs) {
                if (m.getContentType().equalsIgnoreCase("SUBS")) {
                    //queue dengan popularity
                    m.updateProperty(CONTENT, 20);
                    receiveMessage(m, peer);
                } else {
                    for (Map.Entry<DTNHost, Message> entry : subscriptionList.entrySet()) {
                        DTNHost key = entry.getKey();
                        Message value = entry.getValue();
                        if (value.getId().equals(m.getContentType())) {
                            receiveMessage(m, key);
                        }

                    }
                }
            }
        }
    }

    @Override
    public Message messageTransferred(String id, DTNHost from) {
        Message m = super.messageTransferred(id, from);
        this.getHost().getRouter().putToIncomingBuffer(m, from);
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
        }

        if (from.getRouter().status().equalsIgnoreCase("subscriber")) {
//            System.out.println(aHost.getRouter().getInterest()+"\t"+m.getId());
            if (!m.getContentType().equalsIgnoreCase("SUBS")) {
                if (from.getRouter().getInterest().contains(m.getContentType())) {
//                    System.out.println(aHost.getAddress() + " " + m.getId());
//                    System.out.println(m.getHops());
//                    System.out.println("");
                    return super.messageTransferred(id, from); //To change body of generated methods, choose Tools | Templates.

                }
            }
        }
        return null;

    }

    @Override
    public void update() {
        super.update(); //To change body of generated methods, choose Tools | Templates.
        super.update();
        if (!canStartTransfer() || isTransferring()) {
            return; // nothing to transfer or is currently transferring 
        }

        /* try messages that could be delivered to final recipient */
        if (exchangeDeliverableMessages() != null) {
            return;
        }

        tryAllMessagesToAllConnections();
    }

    @Override
    public MessageRouter replicate() {
        return new tesActive(this);
    }

}
