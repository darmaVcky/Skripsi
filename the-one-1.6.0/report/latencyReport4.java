/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.ConnectionListener;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.SimClock;
import core.UpdateListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jarkom
 */
public class latencyReport4 extends Report implements MessageListener, UpdateListener{
 private List<Double> latencies = new ArrayList<Double>();
  private int totalContact=0;
    private int lastRecord=0;
    private int interval=7200;
    private Map<Integer, String> nrofLatency = new HashMap<Integer, String>();
    


    @Override
    public void newMessage(Message m) {
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
    if (firstDelivery) {
            double latenciesValue = SimClock.getIntTime()-m.getCreationTime();
            this.latencies.add(latenciesValue);
        }
    }
    
    @Override
    public void done() {
        String statsText = "Contact\tLatencies\n";
        for (Map.Entry<Integer, String> entry : nrofLatency.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            statsText += key + "\t" + value + "\n";
        }
        write(statsText);
        super.done();
    }

    @Override
    public void updated(List<DTNHost> hosts) {

        if (SimClock.getIntTime() - lastRecord >= interval) {
            String latenciesValue = getAverage(latencies);
            nrofLatency.put(lastRecord, latenciesValue);
            lastRecord = SimClock.getIntTime();
        }
    }
}
