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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwell
 */
public class FileDownloadingList implements Serializable{
    public final static int NOTEXIST=0;
    public final static int WAIT=1;
    public final static int PROCESS=2;
    public final static int FINISH=3;
    public final static int PAUSE=4;
    public static HashMap<String, FileDownloadingList> fileDownloadingList=new HashMap<String,FileDownloadingList>();

    private String name;
    private long size;
    private String type;
    private String path;
    private String source;
    private int status;
    private String key;
    private int segCount;
    private long[] position;
    private String localpath;
    private int percent;
    

    public FileDownloadingList(String name,String size,String type,String source,String path,int segCount){
        this.name=name;
        this.size=Long.parseLong(size);
        this.type=type;
        this.path=path;
        this.source=source;
        status=FileDownloadingList.WAIT;
        key=source+path;
        percent=0;
        this.segCount=segCount;
        if (segCount == 1) {
            position = new long[1];
            position[0] = this.size;
        } else {
            position = new long[segCount];
            calDivide();
            position[segCount-1]=this.size;
        }        
    }

    private void calDivide(){
        int segment=(int)((double)size/segCount);
        for(int i=0;i<segCount-1;i++){
            position[i]=(long)(segment*(i+1));
        }
    }

    public int getPercent() {
        if(status==FileDownloadingList.FINISH)
            return 100;
        else{
         //   System.out.println(localpath+"======localpath");
            long fileLength=0;
            for(int i=0;i<this.segCount;i++){
                File file=new File(localpath+".part"+(i+1));
                if(!file.exists())
                    fileLength+=0;
                else{
                    fileLength+=file.length();
                }
            }
            percent=(int)(((double)fileLength/(double)size)*100);
            return percent;
        }
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long[] getPosition() {
        return position;
    }

    public void setPosition(long[] position) {
        this.position = position;
    }


    public int getSegCount() {
        return segCount;
    }

    public void setSegCount(int segCount) {
        this.segCount = segCount;
    }

    public String getSizeStr() {
         if (size < 1024) {
             return size+"B";
         }else if(size<1048576)
             return size/1024+"KB";
         else{
            return size/1048576+"MB";
         }
    }

    public long getSize(){
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
   

    public static void addFile(FileDownloadingList file){
        fileDownloadingList.put(file.getKey(), file);
    }

    public static void removeFile(String name){
        fileDownloadingList.remove(name);
    }

    public static int findFile(String key){
        if(!fileDownloadingList.containsKey(key)){
            return FileDownloadingList.NOTEXIST;
        }else{
          return ((FileDownloadingList)fileDownloadingList.get(key)).getStatus();
        }
    }

    public static String getFilePath(String key){
        return ((FileDownloadingList)fileDownloadingList.get(key)).getLocalpath();
    }

    public static boolean readList(){
        ObjectInputStream input = null;
        File file=new File(SpecialValue.DOWNLOADED_PATH);
        if(!file.exists())
            file.mkdirs();
        else {
            File DownloadFileList=new File(file,"downloadingList.dat");
            if (DownloadFileList.exists()) {
                try {
                    input = new ObjectInputStream(new FileInputStream(DownloadFileList));
                    fileDownloadingList = (HashMap<String, FileDownloadingList>) input.readObject();
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
            File DownloadFileList = new File(file, "downloadingList.dat");
            try {
                output = new ObjectOutputStream(new FileOutputStream(DownloadFileList));
                output.writeObject(fileDownloadingList);
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
    public static void initState(){
      Collection<FileDownloadingList> coll=(Collection<FileDownloadingList>) fileDownloadingList.values();
      Iterator it=coll.iterator();
          while(it!=null && it.hasNext()){
              FileDownloadingList fdl=(FileDownloadingList)it.next();
              if(fdl.status==FileDownloadingList.PROCESS){
                  fdl.status=FileDownloadingList.WAIT;
                  fileDownloadingList.put(fdl.getKey(), fdl);
              }
          }
      }
    
}
