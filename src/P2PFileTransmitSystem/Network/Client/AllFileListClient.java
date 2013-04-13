/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Client;

import P2PFileTransmitSystem.FileRelated.FileInfo;
import P2PFileTransmitSystem.Network.Server.AdvFindingServer;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

/**
 *
 * @author Maxwell
 */
public class AllFileListClient {

    private transient NetworkManager manager;
    private transient PeerGroup peerGroup;

    public AllFileListClient(NetworkManager manager, PeerGroup peerGroup) {
        this.manager = manager;
        this.peerGroup = peerGroup;
    }

    private PipeAdvertisement getPipeAdv(String peerid) {
        Set<Advertisement> advSet = AdvFindingServer.getAdvSet(SpecialValue.FILE_LIST_ADV_NAME);
        //通过custom peer group提供的发现服务发现"Name"属性为""的管道通告
        PipeAdvertisement adv = null;
        Iterator enu = advSet.iterator();
        while ((enu != null) && enu.hasNext()) {
            adv = (PipeAdvertisement) enu.next();//得到对等机广告
            if (adv.getDescription().equals(peerid.toString())) {
                break;
            }
            adv = null;
        }
      //  System.out.println("ADV IS:==========" + adv);
        System.out.println("连接上目标端点");
        if (adv == null) {
            System.out.println("目标端点不可达...");
        }
        return adv;
    }

    /**
     * Interact with the server.
     */
    public HashMap<String, FileInfo> getFileList(String peerid,String password) {
        HashMap<String, FileInfo> filelist = null;
        try {
            PipeAdvertisement pipeAdv = getPipeAdv(peerid);
            if (pipeAdv == null) {
                System.out.println("目标主机不可达");
            }
            long start = System.currentTimeMillis();
            System.out.println("Connecting to the server: All File List Service");
            JxtaSocket socket;
            socket = new JxtaSocket(peerGroup, net.jxta.peer.PeerID.create(new URI(peerid)), pipeAdv, 3000, true);
         //   socket.setSoTimeout(3000);
              // get the socket output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(password);
            oos.close();


            // get the socket input stream
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object o=ois.readObject();
            if(o instanceof HashMap){
                filelist=(HashMap<String, FileInfo>)o;
            }
            else{
                filelist=null;
            }
            ois.close();
            socket.close();
            System.out.println("Socket connection closed");
        }
        catch (Exception ex) {
            Logger.getLogger(AllFileListClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filelist;
    }
}
