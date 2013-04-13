/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.Network;

import java.util.Enumeration;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.Advertisement;
import net.jxta.protocol.DiscoveryResponseMsg;

/**
 *
 * @author maxwell
 */
public class GroupDiscoveryListener implements DiscoveryListener {

    public void discoveryEvent(DiscoveryEvent ev) {
        DiscoveryResponseMsg res = ev.getResponse();

        // let's get the responding peer's advertisement
        System.out.println("===== [  Got a Discovery Response [" + res.getResponseCount() + " elements]  from peer : " + ev.getSource() + "  ]");

        Advertisement adv;
        Enumeration en = res.getAdvertisements();

        if (en != null) {
            while (en.hasMoreElements()) {
                adv = (Advertisement) en.nextElement();
                System.out.println(adv);
            }
        }
    }

}
