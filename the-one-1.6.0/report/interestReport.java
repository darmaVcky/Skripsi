/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.SimScenario;
import java.util.ArrayList;
import java.util.List;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;

/**
 *
 * @author Jarkom
 */
public class interestReport extends Report {
    @Override
    public void done() {
        List<DTNHost> nodes = SimScenario.getInstance().getHosts();
        
        for (DTNHost h : nodes) {
            MessageRouter r = h.getRouter();
            if (!(r instanceof DecisionEngineRouter)) {
                continue;
            }
            RoutingDecisionEngine de = ((DecisionEngineRouter) r).getDecisionEngine();
            if (!(de instanceof interestEngine)) {
                continue;
            }
            interestEngine cd = (interestEngine) de;
            double total=0, avg=0;
            ArrayList<String> a = cd.getInterest(h);
            String waktu = cd.getWaktu(h);
            String status = cd.getStatus(h);
            write(h+"\t"+a+"\t"+waktu+"\t"+status);
        }
        
        super.done();
    }
}
