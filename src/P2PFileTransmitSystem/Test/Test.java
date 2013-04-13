package P2PFileTransmitSystem.Test;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import P2PFileTransmitSystem.FileRelated.FileInfo;
import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.FileRelated.MD5;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class Test {

    public static void main(String args[]) {

        //System.out.println(Hash.hash("xiaogg"));
        //System.out.println(Hash.hash("XIAOGG"));
        //System.out.println(FileIO.getFileName("呵呵呵"));
        //        System.out.println(MyIKAnalysis.Analysis("p2p java程序设计"));
            /*FileIO.readList();
        for (Iterator<String> key = FileIO.list.keySet().iterator(); key.hasNext();) {
        String fullPath = key.next();
        FileInfo info = FileIO.list.get(fullPath);
        System.out.println(info.getName()+info.getID());
        }*/
        try {
            long begin = System.currentTimeMillis();
            File big = new File("E:\\咏乐汇-2010.04.29.rmvb");
            String md5 = MD5.getFileMD5String(big);
            //   String md5 = getFileMD5String_deprecated(big);
            long end = System.currentTimeMillis();
            System.out.println("md5:" + md5 + " time:" + ((end - begin) / 1000) + "s");
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
