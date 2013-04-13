package P2PFileTransmitSystem.Network;

import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.Config.MyConfiguration;
import P2PFileTransmitSystem.Config.SpecialValue;
import P2PFileTransmitSystem.Network.Server.AdvFindingServer;
import P2PFileTransmitSystem.Network.Client.AllFileListClient;
import P2PFileTransmitSystem.Network.Client.IndexSearchClient;
import P2PFileTransmitSystem.Network.Server.AdvPublishingServer;
import P2PFileTransmitSystem.Network.Server.AllFileListServer;
import P2PFileTransmitSystem.Network.Server.IndexSearchServer;
import P2PFileTransmitSystem.Network.Server.SocketServer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import javax.security.cert.CertificateException;
import javax.swing.JOptionPane;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

public class MyPlatform {

    private static String Local_Peer_Name;
    public static NetworkManager TheNetworkManager;
    public NetworkConfigurator TheConfig;
    public PeerGroup TheNetPeerGroup;
    public static PeerGroup CustomPeerGroup;
    private PeerGroupID CustomPeerGroupId;
    private AllFileListClient fileListClient = null;
    private IndexSearchClient indexSearchClient = null;
    public AdvPublishingServer advPublishServer;
    /*
    public PipeAdvertisement fileTransAdv;
    public PipeAdvertisement fileListAdv;
    public PipeAdvertisement indexSearchAdv;
     */

    public MyPlatform() {
        this.run();
    }

    public String getLocalPeerName() {
        return Local_Peer_Name;
    }

    private void run() {
        initManager();
        initConfig();
        try {
            TheNetPeerGroup = TheNetworkManager.startNetwork();
        } catch (PeerGroupException ex) {
            Logger.getLogger(MyPlatform.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }
        initGroup();
        //初始化所有服务的管道
        removeAdvInfo();
        initLocalService();
        initLocalClient();

    }

    private void initManager() {
        // Creating the Network Manager
        try {
            System.out.println("Creating the Network Manager");
            TheNetworkManager = new NetworkManager(
                    NetworkManager.ConfigMode.EDGE, SpecialValue.Local_Network_Manager_Name);
            System.out.println("Network Manager created");
        } catch (IOException ex) {
            System.out.println("========wrong===========");
            ex.printStackTrace();
            System.exit(-1);
        }

        // Persisting it to make sure the Peer ID is not re-created each
        // time the Network Manager is instantiated
        TheNetworkManager.setConfigPersistent(true);

        System.out.println("PeerID: " + TheNetworkManager.getPeerID().toString());

        // Since we won't be setting our own relay or rendezvous seed peers we
        // will use the default (public network) relay and rendezvous seeding.
        TheNetworkManager.setUseDefaultSeeds(true);
    }

    private void initConfig() {
        // Retrieving the Network Configurator
        System.out.println("Retrieving the Network Configurator");
        try {
            TheConfig = TheNetworkManager.getConfigurator();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Network Configurator retrieved");

        // Does a local peer configuration exist?
        if (TheConfig.exists()) {

            System.out.println("Local configuration found");
            // We load it
            File LocalConfig = new File(TheConfig.getHome(), "PlatformConfig");
            try {
                System.out.println("Loading found configuration");
                TheConfig.load(LocalConfig.toURI());
                Local_Peer_Name = TheConfig.getName();
                MyConfiguration.LoadSetting();
                System.out.println("Configuration loaded");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            } catch (CertificateException ex) {
                // An issue with the existing peer certificate has been encountered
                ex.printStackTrace();
                System.exit(-1);
            }
        } else {
            System.out.println("No local configuration found");
            while (Local_Peer_Name == null || Local_Peer_Name.equals("")) {
                Local_Peer_Name = JOptionPane.showInputDialog("请输入你的帐户名");
            }
            TheConfig.setName(Local_Peer_Name);
            String password = JOptionPane.showInputDialog("请输入好友共享模块密码");
            if (password == null) {
                password = "";
            }
            password = password.trim();
            MyConfiguration.setPassword(password);
            try {
                System.out.println("Saving new configuration");
                TheConfig.save();
                System.out.println("New configuration saved successfully");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void initGroupID() {
        CustomPeerGroupId = IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, hash(SpecialValue.Group_Name.toLowerCase()));
    }

    private static byte[] hash(final String expression) {
        byte[] result;
        MessageDigest digest;

        if (expression == null) {
            throw new IllegalArgumentException("Invalid null expression");
        }

        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException failed) {
            failed.printStackTrace(System.err);
            RuntimeException failure = new IllegalStateException("Could not get SHA-1 Message");
            failure.initCause(failed);
            throw failure;
        }

        try {
            byte[] expressionBytes = expression.getBytes("UTF-8");
            result = digest.digest(expressionBytes);
        } catch (UnsupportedEncodingException impossible) {
            RuntimeException failure = new IllegalStateException("Could not encode expression as UTF8");
            failure.initCause(impossible);
            throw failure;
        }
        return result;
    }

    private void initGroup() {
        initGroupID();

        int count = 5; // Max times to find the group
        System.out.println(" 试图发现组名为 " + SpecialValue.Group_Name + " 对等组");

        // get the discovery service from NetPeerGroup
        DiscoveryService disco = TheNetPeerGroup.getDiscoveryService();

        Enumeration en = null; // recording the advertisements founded

        // loop until we find the peer group
        while (count-- > 0) {
            try {
                // find in local
                // find Peer Group whose name is Group_Name
                en = disco.getLocalAdvertisements(DiscoveryService.GROUP, "Name", SpecialValue.Group_Name);

                // if founded
                if ((en != null) && en.hasMoreElements()) {
                    System.out.println("Find it");
                    break;
                }
                // start the remote find service

                // sleep the thread to wait for the response
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*-------------------------------------------
         *     Then join the group
         * ----------------------------------------*/

        PeerGroupAdvertisement customNetAdv = null;

        // check whether have we found the peer group advertisement
        //if not, we should create the peer group
        if (en == null || !en.hasMoreElements()) {
            System.out.println("Could not find the Group and createing one");
            try {
                // 通过NetPeerGroup获得一个一般对等组的通告。
                createGroup();
                // 获得一个对等组通告
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            // find it and we can join the group
            customNetAdv = (PeerGroupAdvertisement) en.nextElement();
            try {
                // join the group。
                CustomPeerGroup = TheNetPeerGroup.newGroup(customNetAdv);
                System.out.println("找到对等组，并加入存在的该组");
            } catch (PeerGroupException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private PeerGroupAdvertisement createGroupAdv() {
        try {
            //create the group Advertisement
            ModuleImplAdvertisement customGroupImplAdv = TheNetPeerGroup.getAllPurposePeerGroupImplAdvertisement();
            PeerGroupAdvertisement customGroupAdv = (PeerGroupAdvertisement) AdvertisementFactory.newAdvertisement(PeerGroupAdvertisement.getAdvertisementType());
            customGroupAdv.setPeerGroupID(CustomPeerGroupId);
            customGroupAdv.setModuleSpecID(customGroupImplAdv.getModuleSpecID());
            customGroupAdv.setName(SpecialValue.Group_Name);
            //publish the advertisement
            TheNetPeerGroup.getDiscoveryService().publish(customGroupAdv);
            TheNetPeerGroup.getDiscoveryService().remotePublish(customGroupAdv);
            return customGroupAdv;
        } catch (Exception ex) {
            Logger.getLogger(MyPlatform.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void createGroup() {
        try {
            PeerGroupAdvertisement pga = createGroupAdv();
            CustomPeerGroup = TheNetPeerGroup.newGroup(pga);
            CustomPeerGroup.startApp(new String[0]);
        } catch (PeerGroupException ex) {
            Logger.getLogger(MyPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void getInfo() {
        System.out.println("Peer Name: " + CustomPeerGroup.getPeerName());
        System.out.println("Peer ID:" + CustomPeerGroup.getPeerID().toString());
        System.out.println("Peer Group Name:" + CustomPeerGroup.getPeerGroupName());
        System.out.println("Peer Group ID:" + CustomPeerGroup.getPeerGroupID());
    }

    private void initLocalService() {
        advPublishServer = new AdvPublishingServer(Local_Peer_Name, CustomPeerGroup, CustomPeerGroupId);
        advPublishServer.start();

        Runnable PeerAndPipeFound = new AdvFindingServer(CustomPeerGroup.getDiscoveryService());
        Thread findThread = new Thread(PeerAndPipeFound);
        findThread.start();

        Runnable serverSocket = new SocketServer(CustomPeerGroup, advPublishServer.getFileTransAdvertisement());
        Thread socketServerThread = new Thread(serverSocket);
        socketServerThread.start();

        Runnable fileListService = new AllFileListServer(CustomPeerGroup, advPublishServer.getFileListAdvertisement());
        Thread fileListThread = new Thread(fileListService);
        fileListThread.start();

        Runnable indexSearchService = new IndexSearchServer(CustomPeerGroup, advPublishServer.getIndexSearchAdvertisement());
        Thread indexSearchThread = new Thread(indexSearchService);
        indexSearchThread.start();
    }

    private void initLocalClient() {
     //   socketClient = new SocketClient(TheNetworkManager, CustomPeerGroup);
        fileListClient = new AllFileListClient(TheNetworkManager, CustomPeerGroup);
        indexSearchClient = new IndexSearchClient(TheNetworkManager, CustomPeerGroup);
    }
/*
    public SocketClient getSocketClient() {
        return socketClient;
    }
 *
 */

    public AllFileListClient getAllFileListClient() {
        return fileListClient;
    }

    public String getPeerID() {
        return CustomPeerGroup.getPeerID().toString();
    }

    public IndexSearchClient getIndexSearchClient() {
        return indexSearchClient;
    }

    private void removeAdvInfo() {
        String str = CustomPeerGroupId + "";
        int begin = str.lastIndexOf(":");
        str = str.substring(begin + 1);
        FileIO.deleteDir(SpecialValue.CM_PATH + str);
    }

    public static void peerOffLine() {
        PeerAdvertisement peerAdv = (PeerAdvertisement) AdvertisementFactory.newAdvertisement(PeerAdvertisement.getAdvertisementType());
        PeerID peerID = CustomPeerGroup.getPeerID();
        peerAdv.setName(Local_Peer_Name);
        peerAdv.setPeerID(peerID);
        peerAdv.setPeerGroupID(CustomPeerGroup.getPeerGroupID());
        peerAdv.setDescription("false");
        System.out.println("Set false and publish" + peerAdv);
        try {
            int i = 50;
            while (i >= 0) {
                CustomPeerGroup.getDiscoveryService().publish(peerAdv, SpecialValue.PEER_LOCAL_TIME, SpecialValue.PEER_LOCAL_TIME);
                i--;
            }
            System.out.println("Exit success");
        } catch (IOException ex) {
            Logger.getLogger(MyPlatform.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
