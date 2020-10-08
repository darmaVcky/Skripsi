/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import java.util.ArrayList;

/**
 *
 * @author Jarkom
 */
public interface interestEngine {
    public ArrayList<String> getInterest(DTNHost h);
    public String getWaktu(DTNHost h);
    public String getStatus(DTNHost h);
}
