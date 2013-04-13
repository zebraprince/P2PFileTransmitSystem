/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Network.Handler;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwell
 */
public class ConnectionHandler implements Runnable {

    Socket socket = null;
    RandomAccessFile raf;
    OutputStream ous;
    InputStream in;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        sendAndReceiveData(socket);
    }

    /**
     * Sends data over socket
     *
     * @param socket the socket
     */
    private void sendAndReceiveData(Socket socket) {
        try {
            long start = System.currentTimeMillis();

            // get the socket input stream
            in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);

            byte[] fileStartByte = new byte[40];
            dis.read(fileStartByte);
            String fileStart = new String(fileStartByte);
            fileStart = fileStart.trim();
            System.out.println(fileStart + "====Start========ConnectionHandler============");

            byte[] filelenByte = new byte[40];
            dis.read(filelenByte);
            String filelen = new String(filelenByte);
            filelen = filelen.trim();
            System.out.println(filelen + "=======length=====ConnectionHandler============");


            byte[] filepathbyte = new byte[1024];
            dis.read(filepathbyte);
            String filepath = new String(filepathbyte, "utf8");
            filepath = filepath.trim();
            System.out.println("===========ConnectionHandler=============" + filepath);


            // get the socket output stream
            OutputStream out = socket.getOutputStream();

            File file = new File(filepath);
            long total = file.length();
            long startPos=Long.parseLong(fileStart);
            int len = Integer.parseInt(filelen);

            //FileInputStream fos = new FileInputStream(file);
            ous = new DataOutputStream(new BufferedOutputStream(out, 1024 * 60));

            raf = new RandomAccessFile(file, "r");

            byte[] buf = new byte[1024 * 60];
            raf.seek(startPos);
            int count=0;
            int num = raf.read(buf);
              while (num != -1) {      //是否读完所有数据
                    count+=num;
                    if(count<len){
                        ous.write(buf, 0, num);       //将数据写往管道
                        num = raf.read(buf);         //继续从文件中读取数据
                    }else{
                        int moreNum=(count-len);
                        ous.write(buf,0,num-moreNum);
                        raf.close();
                        ous.close();
                        break;
                    }
                }
            System.out.println(MessageFormat.format("Sending {0} bytes to client.", filelen));

            in.close();

            long finish = System.currentTimeMillis();
            long elapsed = finish - start;
            System.out.println(MessageFormat.format("EOT. Received {0} bytes in {1} ms. Throughput = {2} KB/sec.", total, elapsed,
                    (total / elapsed) * 1000 / 1024));
            socket.close();
            System.out.println("Connection closed");
        } catch (IOException iex) {
            try {
                raf.close();
                ous.close();
                in.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }
}
