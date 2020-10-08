/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.ConnectionListener;
import core.DTNHost;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jarkom
 */
public class totalKontak extends Report implements ConnectionListener {

    public Map<DTNHost, Integer> contak = new HashMap<DTNHost, Integer>();

    @Override
    public void hostsConnected(DTNHost host1, DTNHost host2) {
      if (contak.containsKey(host1)) {
            contak.put(host1, contak.get(host1) + 1);
        } else {
            contak.put(host1, 1);
        }
    }

    @Override
    public void hostsDisconnected(DTNHost host1, DTNHost host2) {
  
    }

    @Override
    public void done() {
        for (Map.Entry<DTNHost, Integer> entry : contak.entrySet()) {
            DTNHost key = entry.getKey();
            Integer value = entry.getValue();
            write(key.getAddress() + "\t" + value);
        }
        super.done();
    }
}
