/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.FileRelated.DownloadManager;

import P2PFileTransmitSystem.Network.Client.SocketClient;
import P2PFileTransmitSystem.Network.MyPlatform;
import P2PFileTransmitSystem.GUI.P2PFileTransmitSystemView;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class ProgressPool extends Thread {

    public int threadNumber;    //表示此线程号
    boolean runningFlag;
    private int count = 0;
    private FileDownloadingList fdl;
    private int row;
    private int column;
    private SocketClient[] socketClient;
    private DownloadProcess downloadProcess;
    public int threadCount;

    public void setArgument(FileDownloadingList fdl, int row, int column) {
        this.fdl=fdl;
        this.row = row;
        this.column = column;
        this.threadCount=fdl.getSegCount();
        socketClient=new SocketClient[fdl.getSegCount()];
    }

    public ProgressPool(int threadNumber) {
        this.threadNumber = threadNumber;
        runningFlag = false;
    }

    public boolean isRunning() {
        return this.runningFlag;
    }

    public int getThreadNumber() {
        return this.threadNumber;
    }

    public synchronized void setRunning(boolean flag) {
        runningFlag = flag;
        if (flag) {
            this.notify();
        }
    }

    public synchronized void stopThread(){           
        for(int i=0;i<fdl.getSegCount();i++)
            socketClient[i].stopThread();
     //   this.notify();
    }

    @Override
    public synchronized void run() {
        try {
            while (true) {
                if (!runningFlag) {
                    System.out.println("Thread is sleeping...");
                    this.wait();
                    downloadProcess = new DownloadProcess(fdl, row, column);
                    downloadProcess.start();
                    for(int i=0;i<fdl.getSegCount();i++){
                        socketClient[i] = new SocketClient(MyPlatform.TheNetworkManager, MyPlatform.CustomPeerGroup);
                        socketClient[i].setParm(this,fdl,i+1);
                    }
                    for(int i=0;i<fdl.getSegCount();i++)
                        socketClient[i].start();
                } else {
                    this.wait();
                    System.out.println("Downloading Over");
                    setRunning(false); 
                }
            }
        } catch (InterruptedException ex) {                   
                System.out.println("ProgressPool exception"); 
        }
    }

    private void addToList(int row) {
        DefaultTableModel model = (DefaultTableModel) P2PFileTransmitSystemView.jTTransmit.getModel();
        String nameloc = model.getValueAt(row, 0).toString();
        String sizeloc = model.getValueAt(row, 1).toString();
        String type = model.getValueAt(row, 2).toString();
        String path = SpecialValue.INCOMING_PATH;
        File file = new File(path);
        path = file.getAbsolutePath();
        Date d = new Date();//获取当前时间
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义日期的格式
        String date = sm.format(d);//日期格式化
        FileDownloadedList.addFile(new FileDownloadedList(nameloc, sizeloc, type, path, date));
    }
}
