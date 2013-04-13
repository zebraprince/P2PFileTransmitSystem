/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.FileRelated.DownloadManager;

import P2PFileTransmitSystem.GUI.P2PFileTransmitSystemView;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author maxwell
 */
public class DownloadProcess extends Thread {

    private String name;
    private long size;
    private int row;
    private int column;
    private String peerID;
    private String fullPath;
    private DefaultTableModel model;
    private FileDownloadingList fdl;

    public DownloadProcess(FileDownloadingList fdl, int row, int column) {
        this.peerID = fdl.getSource();
        this.fullPath = fdl.getPath();
        this.name = fdl.getName();
        this.size = fdl.getSize();
        this.row = row;
        this.column = column;
        this.fdl=fdl;
        model = (DefaultTableModel) P2PFileTransmitSystemView.jTTransmit.getModel();
    }

    public void run() {
        File file = new File(SpecialValue.INCOMING_PATH + name+".part1");
        DecimalFormat df2 = new DecimalFormat("#0.00");
        double progress;

        fdl.setStatus(FileDownloadingList.PROCESS);
        FileDownloadingList.fileDownloadingList.put(fdl.getKey(), fdl);
        model.setValueAt("下载中", row, column);
        int standardCount = model.getRowCount();
        int currentCount = model.getRowCount();
        
        try {
            while (!file.exists()) {
                this.sleep(100);
            }
            while (true) {
                long currentSize = getCurrentSize(fdl);
                 this.sleep(500);
                 long currentSize2 = getCurrentSize(fdl);
                 System.out.println("SIZE IS: "+this.size+"Current IS:"+currentSize2);
                //判断是否有新增删的行
                currentCount = model.getRowCount();
                //表格最后增加了行
                if (currentCount - standardCount > 0) {
                    standardCount = currentCount;
                } //表格删除了行
                else if (currentCount - standardCount < 0) {
                    //测试自己是否为最后一行
                    if(currentCount==row){
                        row=row-1;
                    }else{
                        row=FindRow(fdl.getKey(),row);
                    }
                  standardCount = currentCount;
                } //表格没变
                else {
                    progress = (double) currentSize / (double) size;
                    if (currentSize != size) {
                        final int i = (int) (progress * 100);
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                model.setValueAt(i, row, column - 1);
                            }
                        });
                        // 传送变更事件给指定行列
                       
                        double speed = (currentSize2 - currentSize) * 2;
                        if (speed < 1024) {
                            model.setValueAt(df2.format((double) speed) + "B/s", this.row, this.column - 2);
                        } else if (speed < 1048576) {
                            model.setValueAt(df2.format((double) speed / 1024) + "KB/s", this.row, this.column - 2);
                        } else {
                            model.setValueAt(df2.format((double) speed / 1048576) + "MB/s", this.row, this.column - 2);
                        }
                        if(((String)model.getValueAt(row, column)).equals("暂停")){
                            throw new InterruptedException();
                        }
                    } else {
                        model.setValueAt("下载完毕", this.row, this.column);
                        model.setValueAt("", this.row, this.column - 2);
                        model.setValueAt(100, row, column - 1);
                        model.fireTableCellUpdated(row, column - 1);
                        addToList(this.row);
                        fdl.setStatus(FileDownloadingList.FINISH);
                        FileDownloadingList.fileDownloadingList.put(fdl.getKey(), fdl);
                        P2PFileTransmitSystemView.controller.notice();
                        CombinFile();
                        break;
                    }
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("DownloadProcess exception");
            model.setValueAt("暂停", this.row, this.column);
            model.setValueAt("", row, column-2);
            fdl.setStatus(FileDownloadingList.PAUSE);
            FileDownloadingList.fileDownloadingList.put(fdl.getKey(), fdl);
            P2PFileTransmitSystemView.controller.notice();
        } catch (Exception ex) {
            System.out.println("DownloadProcess exception");
        }
    }

    private void addToList(int row) {
      //  DefaultTableModel model = (DefaultTableModel) P2PFileTransmitSystemView.jTTransmit.getModel();
        String namelocal = model.getValueAt(row, 0).toString();
        String sizelocal = model.getValueAt(row, 1).toString();
        String type = model.getValueAt(row, 2).toString();
        String path = SpecialValue.INCOMING_PATH;
        File file = new File(path);
        path = file.getAbsolutePath();
        Date d = new Date();//获取当前时间
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义日期的格式
        String date = sm.format(d);//日期格式化
        FileDownloadedList.addFile(new FileDownloadedList(namelocal, sizelocal, type, path, date));
    }

    private int FindRow(String key,int row){
        //测试自己原来是否在最后一行
        //测试是否比自己大的行删除
        String pid=(String)model.getValueAt(row, 6);
        String pth=(String)model.getValueAt(row, 7);
        if(key.equals(pid+pth)){
            return row;
        }
        //删除了行号比自己小的，可能删除多行，依次比较
        else{
            for(int i=row-1;i>=0;i--){
                 pid=(String)model.getValueAt(i, 6);
                 pth=(String)model.getValueAt(i, 7);
                 if(key.equals(pid+pth)){
                     return i;
                 }
            }
            return 0;
        }
    }

    private long getCurrentSize(FileDownloadingList files) {
        long fileLength = 0;
        File file = new File(files.getLocalpath());
        if (file.exists()) {
            return file.length();
        } else {
            for (int i = 0; i < files.getSegCount(); i++) {               
                file = new File(files.getLocalpath() + ".part" + (i + 1));
                if (!file.exists()) {
                    fileLength += 0;
                } else {
                    fileLength += file.length();
                }
            }
            return fileLength;
        }
    }
    
    private void CombinFile(){
        int count = fdl.getSegCount();
        String path=fdl.getLocalpath();
        try {            
            RandomAccessFile resultfile = new RandomAccessFile(path, "rw");
            for(int i=1;i<=count;i++){
                File file = new File(path+".part"+i);
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                byte[] buf = new byte[1024 * 1024];
                int num = raf.read(buf);
                while (num != -1) {           //是否读完所有数据
                    resultfile.write(buf, 0, num);//将数据写往文件
                    resultfile.skipBytes(num);//顺序写文件字节
                    num = raf.read(buf); //继续从文件中读取数据
                }
                raf.close();
                file.delete();
            }
           resultfile.close();
        } catch (IOException ex) {
            Logger.getLogger(DownloadProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
