/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.Config;

import P2PFileTransmitSystem.Config.SpecialValue;
import P2PFileTransmitSystem.FileRelated.FileIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwell
 */
public class MyConfiguration {
    private static String password="";
    private static int Max_Download_Count=10;
    private static int DownloadThread_Count=2;

    public MyConfiguration(){
        Max_Download_Count=10;
        DownloadThread_Count=2;
    }

    public static int getDownloadThread_Count() {
        return DownloadThread_Count;
    }

    public static void setDownloadThread_Count(int DownloadThread_Count) {
        MyConfiguration.DownloadThread_Count = DownloadThread_Count;
    }

    public static int getMax_Download_Count() {
        return Max_Download_Count;
    }

    public static void setMax_Download_Count(int Max_Download_Count) {
        MyConfiguration.Max_Download_Count = Max_Download_Count;
    }

    public static String getPassword(){
        return password;
    }
    public static void setPassword(String str){
        password=str;
    }
   
    public static void LoadSetting(){
        ObjectInputStream input = null;
        File file = new File(SpecialValue.CONFIG_PATH);
        if(!file.exists())
            file.mkdirs();
        else {
            File settingFile=new File(file,SpecialValue.Config_FILE);
            if (settingFile.exists()) {
                try {
                    input = new ObjectInputStream(new FileInputStream(settingFile));
                    Max_Download_Count=(int)input.readInt();
                    DownloadThread_Count=(int)input.readInt();
                    byte[] b=new byte[1024];
                    input.read(b);
                    password=new String(b,"utf8");
                    password=password.trim();
                }  catch (IOException ex) {
                    Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("password is"+password+"\t MaxThread is"+Max_Download_Count+"\t DownloadThread_Count is: "+DownloadThread_Count);
    }
    public static void SaveSetting(){
        ObjectOutputStream output = null;
        File file = new File(SpecialValue.CONFIG_PATH);
        if (!file.exists()) {
            file.mkdirs();
        } else {
            File settingFile = new File(file, SpecialValue.Config_FILE);
            try {
                output = new ObjectOutputStream(new FileOutputStream(settingFile));                
                output.writeInt(Max_Download_Count);
                output.writeInt(DownloadThread_Count);
                output.writeBytes(password);
            } catch (IOException ex) {
                Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    output.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
