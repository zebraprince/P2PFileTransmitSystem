/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.FileRelated;

import P2PFileTransmitSystem.FileRelated.WordHandler.Hash;
import P2PFileTransmitSystem.FileRelated.WordHandler.MyIKAnalysis;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.TreePath;

/**
 *
 * @author Administrator
 */

public class FileIO {

    public static String formatFileSize(long size){

        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;

    }

    public static String getFilePath(TreePath treePath){

        String path="";
        if (treePath != null) {
            Object[] nodes = treePath.getPath();
            if (nodes != null) {
                if (nodes.length > 1) {
                    path=nodes[1].toString();
                    if(nodes.length>2){
                        for (int i = 2; i < nodes.length; i++) {
                            path = path + nodes[i].toString() + java.io.File.separator;
                        }
                    }
                }
            }
        }
        return path;
    }

    public static String getFileType(String name){

        String type="";
        if(!name.equals("")){
            if(name.contains(".")){
                int position=name.lastIndexOf(".");
                type=name.substring(position+1);
            }
            else{
                type="文件";
            }
        }
        return type;

    }

    public static String getFileName(String name){
        String fileName="";
        if(!name.equals("")){
            if(name.contains(".")){
                int position=name.lastIndexOf(".");
                fileName=name.substring(0,position);
            }
            else{
                fileName=name;
            }
        }
        return fileName;
    }

     public static long getSizeLong(String size) {
        int start=size.lastIndexOf("(");
        int end=size.lastIndexOf(")");
        String sizelong=size.substring(start+1, end);
        return Long.parseLong(sizelong);
    }

    public static String getSizeStr(String size) {
        int end=size.lastIndexOf("(");
        String sizeStr=size.substring(0, end);
        return sizeStr.trim();
    }

    public static boolean shareDirAll(String path,String peerID){

        if(!path.equals("")){
            File file=new File(path);
            if(file.exists()){
                File[] files=file.listFiles();
                if(files!=null){
                    File current;
                    for(int i=0;i<files.length;i++){
                        current=files[i];
                        if(current.isDirectory()){
                            shareDirAll(current.getPath(),peerID);
                        }
                        else if(current.canRead()){
                            shareFile(current,peerID);
                        }
                        else
                            ;
                    }
                }
            }
        }
        return true;

    }

    public static boolean shareDir(String path,String peerID){

        if(!path.equals("")){
            File file=new File(path);
            if(file.exists()){
                File[] files=file.listFiles();
                if(files!=null){
                    File current;
                    for(int i=0;i<files.length;i++){
                        current=files[i];
                        if(!current.isDirectory()&&current.canRead())
                            shareFile(current,peerID);
                    }
                }
            }
        }
        return true;

    }

    public static boolean shareFile(File file,String peerID){

        if(file.exists()){
            if(!file.isDirectory()&&file.canRead()){
                String fullPath=file.getPath();
                String name=file.getName();
                String path=file.getPath();
                int position=path.lastIndexOf(File.separator);
                path=path.substring(0, position+1);
                String type=getFileType(fullPath);
                long size=file.length();
                String identifier=Hash.hash(peerID+fullPath);
                HashSet<String> id=MyIKAnalysis.Analysis(getFileName(name));
                FileInfo info=new FileInfo(name,size,type,path,id,identifier,peerID);
                //
                list.put(fullPath, info);
            }
        }
        return true;

    }

    public static boolean disShareDirAll(String path){

        if(!path.equals("")){
            File file=new File(path);
            String fullPath;
            for ( Iterator<String> key = FileIO.list.keySet().iterator();key.hasNext();) {
                fullPath = key.next();
                if(fullPath.startsWith(path))
                    //list.remove(fullPath);
                    key.remove();
            }
        }
        return true;

    }

    public static boolean disShareDir(String path){

        if(!path.equals("")){
            File file=new File(path);
            String fullPath="";
            FileInfo info;
            for (Iterator<String> key = FileIO.list.keySet().iterator(); key.hasNext();) {
                fullPath = key.next();
                if(fullPath.startsWith(path)){
                    info=list.get(fullPath);
                    if(info.getPath().equals(path))
                        //list.remove(fullPath);
                        key.remove();
                }
            }
        }
        return true;

    }

    public static boolean readList(){
        ObjectInputStream input = null;
        File file=new File(SpecialValue.SHARING_PATH);
        if(!file.exists())
            file.mkdirs();
        else {
            File SharingList=new File(file,"SharingList.dat");
            if (SharingList.exists()) {
                try {
                    input = new ObjectInputStream(new FileInputStream(SharingList));
                    list = (HashMap) input.readObject();
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
        File file = new File(SpecialValue.SHARING_PATH);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            File SharingList = new File(file, "SharingList.dat");
            try {
                output = new ObjectOutputStream(new FileOutputStream(SharingList));
                output.writeObject(list);
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

    /*WAN-索引相关操作！！！
    public void putElement(FileInfo element) {

        String id = element.getCurrentID();
        HashSet<FileInfo> result = RemoteIndex.get(id);
        result.add(element);
        RemoteIndex.put(id, result);

    }

    public HashSet<FileInfo> searchElement(String id){
        return RemoteIndex.get(id);
    }

    public void removeElement(String id,String identifier){
        if(RemoteIndex.containsKey(id)){
            HashSet<FileInfo> result=RemoteIndex.get(id);
            if(!result.isEmpty()){
                FileInfo info;
                for(Iterator it=result.iterator();it.hasNext();){
                    info=(FileInfo) it.next();
                    if(info.getIdentifier().equals(identifier)){
                        it.remove();
                        break;
                    }
                }
            }
        }
    }

    public void createRemoteIndex(){

    }*/

    public static HashSet<FileInfo> searchLocalIndex(HashSet<String> key){

        HashSet<FileInfo> result=new HashSet<FileInfo>();
        if(!list.isEmpty()){
            FileInfo info;
            HashSet<String> id;
            for(Iterator it1=list.keySet().iterator();it1.hasNext();){
                info=list.get((String)it1.next());
                id=info.getID();
                if(id.containsAll(key))
                    result.add(info);
            }
        }
        return result;

    }

    public static void deleteDir(String path){
        File file=new File(path);
        if(file.isDirectory()){
            File[] files=file.listFiles();
            if(files.length>0){
                for(int i=0;i<files.length;i++){
                    if(files[i].isDirectory())
                        deleteDir(files[i].getAbsolutePath());
                    else
                        files[i].delete();
                }
            }
        }
    }
     public static void deleteFile(String path, int i){
         for (int j = 1; j <= i; j++) {
             File file = new File(path+".part"+j);
             System.out.println("REMOVING "+file.getPath());
             if (file.exists()) {
                 file.delete();
             }
         }
    }

    public static HashMap<String,FileInfo> list=new HashMap<String,FileInfo>();
    private static HashMap<String,HashSet<FileInfo>> localIndex=new HashMap<String,HashSet<FileInfo>>();
    private static HashMap<String,HashSet<FileInfo>> RemoteIndex=new HashMap<String,HashSet<FileInfo>>();

   

}
