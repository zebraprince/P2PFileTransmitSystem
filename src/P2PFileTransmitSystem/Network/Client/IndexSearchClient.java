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
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

/**
 *
 * @author Maxwell
 */
public class IndexSearchClient {

    private transient NetworkManager manager;
    private transient PeerGroup peerGroup;
    long start;

    public IndexSearchClient(NetworkManager manager, PeerGroup peerGroup) {
        this.manager = manager;
        this.peerGroup = peerGroup;
    }

    private PipeAdvertisement getPipeAdv(String peerid) {
        Set<Advertisement> advSet = AdvFindingServer.getAdvSet(SpecialValue.INDEX_SEARCH_ADV_NAME);
        //通过custom peer group提供的发现服务发现"Name"属性为""的管道通告
        PipeAdvertisement adv = null;
        Iterator enu = advSet.iterator();
        while ((enu != null) && enu.hasNext()) {
            adv = (PipeAdvertisement) enu.next();//得到对等机广告
            /*
            if (adv.getDescription().equals(peerid.toString())) {
                break;
            }
            adv = null;
             * 
             */
        }
        System.out.println("连接上目标端点");
        if (adv == null) {
            System.out.println("目标端点不可达...");
        }
        return adv;
    }

    public HashSet<FileInfo> getSearchResultBoard(HashSet<String> keySet) {
        HashSet<FileInfo> resultSet = new HashSet<FileInfo>();
        HashSet<PeerAdvertisement> peerSet = AdvFindingServer.getPeerSet();
        Iterator it = peerSet.iterator();
        while (it != null && it.hasNext()) {
            String id = ((PeerAdvertisement) it.next()).getPeerID().toString();
           /*
            if (!id.equals(peerGroup.getPeerID().toString())) {
                HashSet<FileInfo> tempSet = getSearchResult(id, keySet);
                if(tempSet!=null)
                    resultSet.addAll(tempSet);
            }
            *
            */
             HashSet<FileInfo> tempSet = getSearchResult(id, keySet);
                if(tempSet!=null)
                    resultSet.addAll(tempSet);
        }
        return resultSet;
    }

    /**
     * Interact with the server.
     */
    public HashSet<FileInfo> getSearchResult(String peerid, HashSet<String> keySet) {
        HashSet<FileInfo> searchResult = new HashSet<FileInfo>();
        try {
            PipeAdvertisement pipeAdv = getPipeAdv(peerid);
            if (pipeAdv == null) {
                System.out.println("目标主机不可达");
            }
            start = System.currentTimeMillis();
            System.out.println("Connecting to the server: All File List Service");
            JxtaSocket socket;
            socket = new JxtaSocket(peerGroup, net.jxta.peer.PeerID.create(new URI(peerid)), pipeAdv, 3000, true);
          //  socket.setSoTimeout(3000);


            // get the socket output stream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(keySet);
            oos.close();


            // get the socket input stream
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            searchResult = (HashSet<FileInfo>) ois.readObject();
            ois.close();

            socket.close();
            System.out.println("Socket connection closed");
        } catch (SocketTimeoutException ste) {
            long end = System.currentTimeMillis();
            long time=end-start;
            System.out.println("SocketTimeoutException"+time);
            return null;
        } catch (Exception ex) {
            System.out.println("IndexSerachClient Exception");
        }
        return searchResult;
    }
}
