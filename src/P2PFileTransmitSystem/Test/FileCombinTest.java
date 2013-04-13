/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author maxwell
 */
public class FileCombinTest {
    public static void main(String[] args){
       DevideFile();
       CombinFile();
    }

      private static void CombinFile(){
        int count = 3;
        String path="海贼王450.rmvb";
        try {
            RandomAccessFile resultfile = new RandomAccessFile("ll.rmvb", "rw");
            for(int i=1;i<=count;i++){
                File file = new File(path+".part"+i);
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                byte[] buf = new byte[1024 * 60];
                int num = raf.read(buf);
                while (num != -1) {          //是否读完所有数据
                    resultfile.write(buf, 0, num);//将数据写往文件
                    resultfile.skipBytes(num);//顺序写文件字节
                    num = raf.read(buf); //继续从文件中读取数据
                }
                raf.close();
                file.delete();
            }
           resultfile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("fileNotFound");
        }
        catch (IOException ex) {
           System.out.println("IO Exception");
        }  
      }

    private static void DevideFile(){
        String path="海贼王450.rmvb";
        try {
            RandomAccessFile resultfile = new RandomAccessFile(path, "r");
            long fileLen=resultfile.length();
            int[] part=new int[4];
            part[0]=0;
            part[1]=(int)(fileLen/3);
            part[2]=(int)part[1]*2;
            part[3]=(int)(fileLen)-1;
            int count=0;
            for(int i=1;i<=3;i++){
                File file = new File(path+".part"+i);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                byte[] buf = new byte[1024 * 60];
                resultfile.seek(part[i-1]);
                int num = resultfile.read(buf);
                while (num != -1) {      //是否读完所有数据
                    count+=num;
                //    System.out.println("count: "+count+" part: "+part[i]+" Num: "+num);
                    if(count<=part[i]){
                        raf.write(buf, 0, num);//将数据写往文件
                        raf.skipBytes(num);//顺序写文件字节
                        num = resultfile.read(buf);
                    }else{
                        int moreNum=count-part[i];
                        raf.write(buf,0,num-moreNum);
                        raf.skipBytes(num-moreNum);
                        raf.close();
                        count=part[i];
                        break;
                    }
                }
            }
           resultfile.close();
        } catch (IOException ex) {
           ;
        }
    }
}
