/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Server;

import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peer.PeerID;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;


/**
 *
 * @author maxwell
 */
public class AdvFindingServer implements Runnable {

    private static HashSet<PeerAdvertisement> peerSet;
    private static HashMap<String, HashSet<Advertisement>> advMap;
    private DiscoveryService discoveryService;

    public AdvFindingServer(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
        peerSet = new HashSet<PeerAdvertisement>();
        advMap = new HashMap<String, HashSet<Advertisement>>();
    }

    public AdvFindingServer() {
        peerSet = new HashSet<PeerAdvertisement>();
        advMap = new HashMap<String, HashSet<Advertisement>>();
    }

    public void run() {
        while (true) {
            getAllPeers();
            getAllAdvs(SpecialValue.FILE_TRANSMIT_ADV_NAME);
            getAllAdvs(SpecialValue.FILE_LIST_ADV_NAME);
            getAllAdvs(SpecialValue.INDEX_SEARCH_ADV_NAME);
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException ex) {;}
        }
    }

    private void clearAdv(String key, PeerAdvertisement peerAdv) {
        HashSet<Advertisement> advSet = advMap.get(key);
        if (advSet != null) {
            Iterator<Advertisement> it = advSet.iterator();
            while (it != null && it.hasNext()) {
                PeerAdvertisement adv = (PeerAdvertisement) it.next();
                if (adv.getDescription().toString().equals(peerAdv.getPeerID())) {
                    try {
                        this.discoveryService.flushAdvertisement((Advertisement) adv);
                    } catch (IOException ex) {
                        Logger.getLogger(AdvFindingServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private void clearBuf(PeerAdvertisement peerAdv){
        peerSet.remove(peerAdv);
        clearAdv(SpecialValue.FILE_TRANSMIT_ADV_NAME, peerAdv);
        clearAdv(SpecialValue.FILE_LIST_ADV_NAME, peerAdv);
        clearAdv(SpecialValue.INDEX_SEARCH_ADV_NAME, peerAdv);
          //  this.discoveryService.flushAdvertisements(null, DiscoveryService.PEER);
           // this.discoveryService.flushAdvertisements(null,DiscoveryService.ADV);
    }

//返回所有节点
    public static HashSet<PeerAdvertisement> getPeerSet() {
        return peerSet;
    }
//返回特定名称的广告

    public static HashSet<Advertisement> getAdvSet(String name) {
        return (HashSet<Advertisement>) advMap.get(name);
    }

    private void getAllPeers() {
        Enumeration enu = null;//发现Peer广告      
        discoveryService.getRemoteAdvertisements(null, DiscoveryService.PEER, "Name", "", 1000);
        try {
            //通过custom peer group提供的发现服务发现"Name"属性为""的对等机
            enu = discoveryService.getLocalAdvertisements(DiscoveryService.PEER, "Name", "");//搜索对等组下面所有Peer信息
            while ((enu != null) && enu.hasMoreElements()) {
                PeerAdvertisement adv = (PeerAdvertisement) enu.nextElement();//得到对等机广告
                System.out.println("Find Peer: " + adv.getName() + "\t" + adv.getDesc().getValue() + "\t" + discoveryService.getAdvExpirationTime(adv) + "\t" + discoveryService.getAdvLifeTime(adv));
                if (adv != null && adv.getDescription() != null) {
                    if (adv.getDescription().equals("true")) {
                        if (addPeer(adv)) {
                            System.out.println("Add Peer:" + adv.getName());
                        }
                    }else{
                        clearBuf(adv);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private boolean addPeer(PeerAdvertisement adv){
        boolean flag=true;
        Iterator it=peerSet.iterator();
        while(it!=null && it.hasNext()){
            if(((PeerAdvertisement)it.next()).getPeerID().equals(adv.getPeerID())){
                flag=false;
                return flag;
            }
        }
        peerSet.add(adv);
        return flag;
    }

    private void getAllAdvs(String key) {
        HashSet<Advertisement> advSet =(HashSet<Advertisement>)advMap.get(key);
        if(advSet==null)
            advSet=new HashSet<Advertisement>();
        discoveryService.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", key, 1000);
        Enumeration enu = null;
        try {
            //通过custom peer group提供的发现服务发现"Name"属性为""的对等机
            enu = discoveryService.getLocalAdvertisements(DiscoveryService.ADV, "Name", key);//搜索对等组下面所有Peer信息
            while ((enu != null) && enu.hasMoreElements()) {
                PipeAdvertisement adv = (PipeAdvertisement) enu.nextElement();//得到对等机广告
                System.out.println("Pipe: " + adv.getID()+" Type: "+adv.getName()+" Source: "+adv.getDescription());
                boolean flag=true;
                Iterator it = advSet.iterator();
                //与现有的所有广告比较
                while (it != null && it.hasNext()) {
                    String id=((PipeAdvertisement)it.next()).getPipeID().toString();
                    if (id.equals(adv.getPipeID().toString())) {
                        //发现存在，不加入
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    advSet.add(adv);
                  }
            }
            if (advMap.containsKey(key)) {
                advMap.remove(key);
                advMap.put(key, advSet);
            }
            else{
                advMap.put(key, advSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
