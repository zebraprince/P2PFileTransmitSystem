/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Handler;

import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.FileRelated.FileInfo;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;

/**
 *
 * @author Maxwell
 */
public class IndexSearchConnectionHandler implements Runnable {

    Socket socket = null;

    public IndexSearchConnectionHandler(Socket socket) {
        this.socket = socket;

    }

    public void run() {
        sendAndReceiveData(socket);
    }

    private void sendAndReceiveData(Socket socket) {
        try {
            // get the socket input stream
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            HashSet<String> keySet=(HashSet<String>)ois.readObject();
            ois.close();

            //process the local searching service
            HashSet<FileInfo> indexSearchResult = new HashSet<FileInfo>();
            indexSearchResult=FileIO.searchLocalIndex(keySet);
            System.out.println("SEARCH SIZE is:"+indexSearchResult.size());
            if (indexSearchResult != null && indexSearchResult.size() != 0) {
                // get the socket output stream
                OutputStream out=socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(indexSearchResult);
                oos.flush();
                oos.close();
                out.close();
            }
            socket.close();
            System.out.println("Connection closed");
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }
}
