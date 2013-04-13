/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Client;

import P2PFileTransmitSystem.FileRelated.DownloadManager.ProgressPool;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadingList;
import P2PFileTransmitSystem.Network.Server.AdvFindingServer;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.document.Advertisement;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

/**
 *
 * @author maxwell
 */
public class SocketClient extends Thread {

    private transient NetworkManager manager;
    private transient PeerGroup peerGroup;
    private boolean isPaused = false;
    private RandomAccessFile raf;
    private InputStream ins;
    private JxtaSocket socket;
    private String peerid;
    private String filepathString;
    private String filename;
    private ProgressPool fatherThread;
    private int currentThreadNum;
    private FileDownloadingList fdl;

    public SocketClient(NetworkManager manager, PeerGroup peerGroup) {
        this.manager = manager;
        this.peerGroup = peerGroup;

    }

    private PipeAdvertisement getPipeAdv(PeerID peerid) {
        Set<Advertisement> advSet = AdvFindingServer.getAdvSet(SpecialValue.FILE_TRANSMIT_ADV_NAME);
        //通过custom peer group提供的发现服务发现"Name"属性为""的管道通告
        PipeAdvertisement adv = null;
        Iterator enu = advSet.iterator();
        while ((enu != null) && enu.hasNext()) {
            adv = (PipeAdvertisement) enu.next();//得到对等机广告
            if (adv.getDescription().equals(peerid.toString())) {
                break;
            }
            adv = null;
        }
        System.out.println("连接上目标端点");
        if (adv == null) {
            System.out.println("目标端点不可达...");
        }
        return adv;
    }

    /**
     *
     * @param peerid    目标主机ID
     * @param pipeAdvertisement 管道ID
     * @param filepathString    目标文件路径
     * @param filename  本地储存文件名
     */
    public void setParm(ProgressPool fatherThread,FileDownloadingList fdl,int currentThreadNum) {
        this.peerid = fdl.getSource();
        this.filename = fdl.getName();
        this.filepathString = fdl.getPath();
        this.fatherThread = fatherThread;
        this.currentThreadNum=currentThreadNum;
        this.fdl=fdl;
    }

    @Override
    public synchronized void run() {
        try {
            PeerID peerID = net.jxta.peer.PeerID.create(new URI(peerid));
            PipeAdvertisement pipeAdv = getPipeAdv(peerID);
            if (pipeAdv == null) {
                System.out.println("目标主机不可达");
            }


            long start = System.currentTimeMillis();
            System.out.println("已连接上对方主机");
            socket = new JxtaSocket(peerGroup, peerID, pipeAdv, 500, true);
            socket.setReceiveBufferSize(60 * 1024);
            
            // get the socket output stream
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            //判断本地文件是否同目标文件一样大
            File directory = new File(SpecialValue.INCOMING_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            long currentSize = 0;
            File file = new File(directory, filename+".part"+currentThreadNum);
            if (file.exists()) {
                currentSize = (long) file.length();
            } else {
                file.createNewFile();
            }
            
            System.out.println("FilePath is:" + filepathString + "File length is: " + currentSize);
            long startPos=0;
            long transmitLen=0;
            long[] pos=(long[])fdl.getPosition();
            if(currentThreadNum==1){
                startPos=0+currentSize;
                transmitLen=pos[0]-startPos;
            }else{
                startPos=currentSize+pos[currentThreadNum-2];
                transmitLen=pos[currentThreadNum-1]-startPos;
            }
            String startPosStr=startPos+"";
            String transmitLenStr=transmitLen+"";
         //   byte[] startByte=startPosStr.getBytes("utf8");
          //  byte[] transmitLenByte=transmitLenStr.getBytes("utf8");

            dos.writeBytes(startPosStr);
            dos.flush();

            dos.writeBytes(transmitLenStr);
            dos.flush();

            byte[] filepath = filepathString.getBytes("utf8");
            dos.write(filepath);

            dos.close();

            // get the socket input stream
            InputStream in = socket.getInputStream();
            ins = new DataInputStream(new BufferedInputStream(in, 1024 * 60));


            raf = new RandomAccessFile(file, "rw");

            byte[] buf = new byte[1024 * 60];
            int count=0;
            int num = ins.read(buf);
            raf.seek(currentSize);
            while (num != (-1)) {//是否读完所有数据
                raf.write(buf, 0, num);//将数据写往文件
                raf.skipBytes(num);//顺序写文件字节
                count+=num;
                System.out.println("Receiving for "+count);
                if (isPaused) {
                    System.out.println("STOP TRANSMIT");
                    ins.close();
                    raf.close();
                    socket.close();
                    throw new InterruptedIOException();
                } else {
                    num = in.read(buf);//继续从网络中读取文件
                }
            }
            ins.close();
            raf.close();

            long total = file.length();

            long finish = System.currentTimeMillis();
            long elapsed = finish - start;

            System.out.println(MessageFormat.format("EOT. Processed {0} bytes in {1} ms. Throughput = {2} KB/sec.", total, elapsed,
                    (total / elapsed) * 1000 / 1024));
            socket.close();
            System.out.println("Socket connection closed");
            synchronized (fatherThread) {
                if (fatherThread.threadCount - 1 > 0) {
                    fatherThread.threadCount--;
                } else {
                    (fatherThread).notify();
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
              synchronized(fatherThread){
                if (fatherThread.threadCount - 1 > 0) {
                    fatherThread.threadCount--;
                } else {
                    (fatherThread).notify();
                }
            }
        } catch (InterruptedIOException iex) {
            System.out.println("MY InterruptedIOException");
             synchronized(fatherThread){
                if (fatherThread.threadCount - 1 > 0) {
                    fatherThread.threadCount--;
                } else {
                    (fatherThread).notify();
                }
            }
        } catch (SocketException sex) {
            System.out.println("对方主机不可达！！");
             synchronized(fatherThread){
                if (fatherThread.threadCount - 1 > 0) {
                    fatherThread.threadCount--;
                } else {
                    (fatherThread).notify();
                }
            }
        }
        catch (IOException sex) {
            System.out.println("socket client IOException！！");
             synchronized(fatherThread){
               if (fatherThread.threadCount - 1 > 0) {
                    fatherThread.threadCount--;
                } else {
                    (fatherThread).notify();
                }
            }
        } 
    }

    public void stopThread() {
        System.out.println("SET TO TRUE");
        isPaused = true;
    }
}
