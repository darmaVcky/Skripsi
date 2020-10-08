package routing;

import core.*;
import java.util.*;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;

/**
 * @author Junandus Sijabat
 * Sanata Dharma University
 */
public class EpidemicWithoutInterest implements routing.RoutingDecisionEngine {

    public static final String CONTENT_PROPERTY = "interest";

    public EpidemicWithoutInterest(Settings s) {}

    public EpidemicWithoutInterest(EpidemicWithoutInterest proto) {}

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer, String interest) {}

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer, String interest) {}

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer, String interest) {}

    @Override
    public boolean newMessage(Message m, String interest) {
        m.addProperty(CONTENT_PROPERTY, interest);
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost, String interest) {
        return m.getTo() == aHost;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost, String interest) {
        return true;
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost, String interest) {
        return true;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost, String interest) {
        return false;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld, String interest) {
        return true;
    }
    
    @Override
    public RoutingDecisionEngine replicate() {
        return new EpidemicWithoutInterest(this);
    }
    private EpidemicWithoutInterest getOtherDecisionEngine(DTNHost h){
        MessageRouter otherRouter = h.getRouter();
		assert otherRouter instanceof DecisionEngineRouter : "This router only works " + 
		" with other routers of same type";
		
	return (EpidemicWithoutInterest) ((DecisionEngineRouter)otherRouter).getDecisionEngine();
    }
}