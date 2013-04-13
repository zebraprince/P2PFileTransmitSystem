/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Server;

import P2PFileTransmitSystem.Network.Handler.AllFileListConnectionHandler;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;

/**
 *
 * @author Maxwell
 */
public class AllFileListServer implements Runnable {

    private PeerGroup CustomPeerGroup;
    private JxtaServerSocket serverSocket;
    private PipeAdvertisement pad;

    public AllFileListServer(PeerGroup CustomPeerGroup, PipeAdvertisement pad) {
        this.CustomPeerGroup = CustomPeerGroup;
        this.pad = pad;
    }

    public void run() {
        System.out.println("Starting AllFileListService");
        try {
            serverSocket = new JxtaServerSocket(CustomPeerGroup, pad, 50, 0);
            serverSocket.setSoTimeout(0);
            serverSocket.setReceiveBufferSize(60 * 1024);
        } catch (IOException e) {
            System.out.println("failed to create a server socket");
            e.printStackTrace();
            System.exit(-1);
        }
        while (true) {
            try {
                System.out.println("Waiting for AllFileList connections");
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    System.out.println("New socket connection accepted:All File List");
                    Thread thread = new Thread(new AllFileListConnectionHandler(socket), "All File List Connection Handler Thread");
                    thread.start();
                }
            } catch (SocketTimeoutException ex) {
                System.out.println("Connection Time out Error");
         //      javax.swing.JOptionPane.showMessageDialog(null, "Error");
            } catch (IOException ex) {
                ;
            }
        }
    }
}


