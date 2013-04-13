/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.Network.Server;


import P2PFileTransmitSystem.Network.Handler.ConnectionHandler;
import java.io.IOException;
import java.net.Socket;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;

/**
 *
 * @author maxwell
 */
public class SocketServer implements Runnable{
    private JxtaServerSocket serverSocket;

    public SocketServer( PeerGroup CustomPeerGroup,PipeAdvertisement pad){
        try {
            serverSocket = new JxtaServerSocket(CustomPeerGroup, pad, 50,0);
            serverSocket.setSoTimeout(0);
            serverSocket.setReceiveBufferSize(60*1024);
        } catch (IOException e) {
            System.out.println("failed to create a server socket");
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
    public void run() {
        System.out.println("Starting ServerSocket");
        while (true) {
            try {
                System.out.println("Waiting for connections");
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    System.out.println("New socket connection accepted");
                    Thread thread = new Thread(new ConnectionHandler(socket), "Connection Handler Thread");
                    thread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }  
}
