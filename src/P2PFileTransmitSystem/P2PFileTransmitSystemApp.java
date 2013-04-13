/*
 * P2PFileTransmitSystemApp.java
 */

package P2PFileTransmitSystem;

import P2PFileTransmitSystem.GUI.P2PFileTransmitSystemView;
import P2PFileTransmitSystem.Config.MyConfiguration;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadedList;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadingList;
import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.Network.MyPlatform;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class P2PFileTransmitSystemApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new P2PFileTransmitSystemView(this));
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        FileIO.writeList();
        FileDownloadedList.writeList();
        FileDownloadingList.writeList();
        MyConfiguration.SaveSetting();
        MyPlatform.peerOffLine();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of P2PFileTransmitSystemApp
     */
    public static P2PFileTransmitSystemApp getApplication() {
        return Application.getInstance(P2PFileTransmitSystemApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(P2PFileTransmitSystemApp.class, args);
        
    }
}
