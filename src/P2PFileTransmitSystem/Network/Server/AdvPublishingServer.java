/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Server;


import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Document;
import net.jxta.document.MimeMediaType;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 *
 * @author Maxwell
 */
public class AdvPublishingServer extends Thread {

    private String Local_Peer_Name;
    private PeerGroup CustomPeerGroup;
    private PeerGroupID CustomPeerGroupId;
    private PeerAdvertisement peerAdvertisement;
    private PipeAdvertisement fileTransAdvertisement;
    private PipeAdvertisement fileListAdvertisement;
    private PipeAdvertisement indexSearchAdvertisement;

    public AdvPublishingServer(String Local_Peer_Name, PeerGroup CustomPeerGroup, PeerGroupID CustomPeerGroupId) {
        this.Local_Peer_Name = Local_Peer_Name;
        this.CustomPeerGroup = CustomPeerGroup;
        this.CustomPeerGroupId = CustomPeerGroupId;
        peerAdvertisement=getPeerAdv();
        fileTransAdvertisement = this.createAdvertisement(SpecialValue.File_Trans_ADV_FILE, SpecialValue.FILE_TRANSMIT_ADV_NAME);
        fileListAdvertisement = this.createAdvertisement(SpecialValue.File_List_ADV_FILE, SpecialValue.FILE_LIST_ADV_NAME);
        indexSearchAdvertisement = this.createAdvertisement(SpecialValue.Index_Search_ADV_FILE, SpecialValue.INDEX_SEARCH_ADV_NAME);
        
    }

    @Override
    public void run() {
        int i=50;
        while (true) {
            while(i>=0){
                try {
                    CustomPeerGroup.getDiscoveryService().publish(peerAdvertisement, SpecialValue.PEER_LOCAL_TIME, SpecialValue.PEER_LOCAL_TIME);
                } catch (IOException ex) {
                    Logger.getLogger(AdvPublishingServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                i--;
            }
         //   CustomPeerGroup.getDiscoveryService().remotePublish(null,peerAdvertisement, SpecialValue.PEER_LOCAL_TIME);
            try {
                CustomPeerGroup.getDiscoveryService().publish(peerAdvertisement, SpecialValue.PEER_LOCAL_TIME, SpecialValue.PEER_LOCAL_TIME);
            //    CustomPeerGroup.getDiscoveryService().remotePublish(peerAdvertisement, SpecialValue.PEER_REMOTE_TIME);         
                publishAdvertisement();
                
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AdvPublishingServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(AdvPublishingServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void publishAdvertisement() {
        try {
            CustomPeerGroup.getDiscoveryService().publish(fileTransAdvertisement, SpecialValue.PIPE_LOCAL_TIME, SpecialValue.PIPE_LOCAL_TIME);
            CustomPeerGroup.getDiscoveryService().publish(fileListAdvertisement, SpecialValue.PIPE_LOCAL_TIME, SpecialValue.PIPE_LOCAL_TIME);
            CustomPeerGroup.getDiscoveryService().publish(indexSearchAdvertisement, SpecialValue.PIPE_LOCAL_TIME, SpecialValue.PIPE_LOCAL_TIME);
            /*
            CustomPeerGroup.getDiscoveryService().remotePublish(null, fileTransAdvertisement, SpecialValue.PIPE_REMOTE_TIME);
            CustomPeerGroup.getDiscoveryService().remotePublish(null, fileListAdvertisement, SpecialValue.PIPE_REMOTE_TIME);
            CustomPeerGroup.getDiscoveryService().remotePublish(null, indexSearchAdvertisement, SpecialValue.PIPE_REMOTE_TIME);
             */
        } catch (IOException ex) {
            Logger.getLogger(AdvPublishingServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PipeAdvertisement createAdvertisement(String fileName, String AdvName) {
        PipeID socketID = null;
        socketID = IDFactory.newPipeID(CustomPeerGroupId);
        PipeAdvertisement advertisement;
        try {
            FileInputStream is = new FileInputStream(SpecialValue.ADV_PATH + fileName);
            advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(new MimeMediaType("text/xml"), is);
            is.close();
        } catch (IOException e) {
            advertisement = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
                    PipeAdvertisement.getAdvertisementType());
            advertisement.setPipeID(socketID);
            advertisement.setType(PipeService.UnicastType);
            advertisement.setName(AdvName);
            advertisement.setDescription(CustomPeerGroup.getPeerID().toString());

            //save pipeAd in file
            Document pipeAdDoc = advertisement.getDocument(new MimeMediaType("text/xml"));
            try {
                File file = new File(SpecialValue.ADV_PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File f = new File(file, fileName);
                FileOutputStream os = new FileOutputStream(f);
                pipeAdDoc.sendToStream(os);
                os.flush();
                os.close();              
             } catch (IOException ex) {
                System.out.println("Can't save pipe advertisement to file " + fileName);
                System.exit(-1);
            }
        }
        return advertisement;
    }

    private PeerAdvertisement getPeerAdv() {
        PeerAdvertisement peerAdv = (PeerAdvertisement) AdvertisementFactory.newAdvertisement(PeerAdvertisement.getAdvertisementType());
        peerAdv.setName(Local_Peer_Name);  
        PeerID peerID = CustomPeerGroup.getPeerID();
        peerAdv.setPeerID(peerID);
        peerAdv.setPeerGroupID(CustomPeerGroup.getPeerGroupID());
        peerAdv.setDescription("true");
        return peerAdv;
    }

    public PipeAdvertisement getFileListAdvertisement() {
        return fileListAdvertisement;
    }

    public PipeAdvertisement getFileTransAdvertisement() {
        return fileTransAdvertisement;
    }

    public PipeAdvertisement getIndexSearchAdvertisement() {
        return indexSearchAdvertisement;
    }

    public PeerAdvertisement getPeerAdvertisement() {
        return peerAdvertisement;
    }  
}
