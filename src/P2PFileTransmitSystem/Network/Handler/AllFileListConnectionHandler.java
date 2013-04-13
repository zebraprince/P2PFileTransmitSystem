  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Handler;

import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.FileRelated.FileInfo;
import P2PFileTransmitSystem.Config.MyConfiguration;
import P2PFileTransmitSystem.Network.MyPlatform;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxwell
 */
public class AllFileListConnectionHandler implements Runnable {

    Socket socket = null;

    public AllFileListConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        sendAndReceiveData(socket);
    }

    private void sendAndReceiveData(Socket socket) {
        {

            OutputStream out = null;
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String passwordIn = (String) ois.readObject();
                ois.close();
                String password=MyConfiguration.getPassword();
                if(password==null)
                    password="";
               System.out.println("password is:"+password+" input value is:"+passwordIn);
                if (passwordIn.equals(password)) {
                    HashMap<String, FileInfo> filelist = new HashMap<String, FileInfo>();
                    filelist.putAll(FileIO.list);
                    out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject(filelist);
                    oos.flush();
                    oos.close();
                    out.close();
                    socket.close();
                    System.out.println("Connection closed");
                }else{
                    out = socket.getOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject("Password Error");
                    oos.flush();
                    oos.close();
                    out.close();
                    socket.close();
                    System.out.println("Password Error");
                } 
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AllFileListConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AllFileListConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(AllFileListConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
