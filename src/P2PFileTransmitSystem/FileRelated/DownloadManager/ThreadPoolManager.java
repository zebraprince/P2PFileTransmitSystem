/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.FileRelated.DownloadManager;

import java.util.Vector;

/**
 *
 * @author maxwell
 */
public class ThreadPoolManager {

    private int maxThread;
    public Vector<ProgressPool> vector;

    public void setMaxThread(int threadCount) {
        maxThread = threadCount;
    }

    public ThreadPoolManager(int threadCount) {
        setMaxThread(threadCount);
        System.out.println("Starting thread pool...");
        vector = new Vector();
        for (int i = 1; i <= maxThread; i++) {
            ProgressPool thread = new ProgressPool(i);
            vector.addElement(thread);
            thread.start();
        }
        System.out.println("Thread pool ready");
    }

    public ProgressPool getThread(int i){
        return vector.get(i);
    }
    
    public void setWait(int i){
        ProgressPool p=vector.get(i);
        p.stopThread();      
    }


    

    public int process(FileDownloadingList fdl,int row, int column) {
        int i;
        for (i = 0; i < vector.size(); i++) {
            ProgressPool currentThread = (ProgressPool) vector.elementAt(i);
            if (!currentThread.isRunning()) {
                currentThread.setArgument(fdl, row, column);
                currentThread.setRunning(true);
                return i;
            }
            if (i == vector.size()) {
                System.out.println("pool is full, try in another time.");
                return -1;
            }
        }
        return -1;
    }
}

