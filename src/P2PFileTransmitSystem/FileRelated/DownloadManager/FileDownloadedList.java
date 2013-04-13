/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.FileRelated.DownloadManager;

import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwell
 */
public class FileDownloadedList implements Serializable{
    public static HashMap<String, FileDownloadedList> fileDownloadList=new HashMap<String,FileDownloadedList>();


    private String name;
    private String size;
    private String type;
    private String path;
    private String date;

    public FileDownloadedList(String name,String size,String type,String path,String date){
        this.name=name;
        this.size=size;
        this.type=type;
        this.path=path;
        this.date=date;
    }

    public String getName(){
        return name;
    }
       public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public static void addFile(FileDownloadedList file){
        fileDownloadList.put(file.getName(), file);
    }

    public static void removeFile(String name){
        fileDownloadList.remove(name);
    }

    public static boolean readList(){
        ObjectInputStream input = null;
        File file=new File(SpecialValue.DOWNLOADED_PATH);
        if(!file.exists())
            file.mkdirs();
        else {
            File DownloadFileList=new File(file,"downloadedFileList.dat");
            if (DownloadFileList.exists()) {
                try {
                    input = new ObjectInputStream(new FileInputStream(DownloadFileList));
                    fileDownloadList = (HashMap) input.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                } catch (IOException ex) {
                    Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                } finally {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean writeList() {
        ObjectOutputStream output = null;
        File file = new File(SpecialValue.DOWNLOADED_PATH);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            File DownloadFileList = new File(file, "downloadedFileList.dat");
            try {
                output = new ObjectOutputStream(new FileOutputStream(DownloadFileList));
                output.writeObject(fileDownloadList);
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } finally {
                try {
                    output.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return true;

    }
}
