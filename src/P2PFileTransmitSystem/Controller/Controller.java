/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Controller;

import P2PFileTransmitSystem.FileRelated.DownloadManager.ProgressPool;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadedList;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadingList;
import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.FileRelated.FileInfo;
import P2PFileTransmitSystem.FileRelated.WordHandler.MyIKAnalysis;
import P2PFileTransmitSystem.Config.MyConfiguration;
import P2PFileTransmitSystem.Network.Server.AdvFindingServer;
import P2PFileTransmitSystem.GUI.P2PFileTransmitSystemView;
import P2PFileTransmitSystem.Config.SpecialValue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import net.jxta.protocol.PeerAdvertisement;

/**
 *
 * @author Administrator
 */
public class Controller {

    private P2PFileTransmitSystemView view;
    
    public Controller(P2PFileTransmitSystemView view) {
        this.view = view;
    }

    public void updateJTable() {

        DefaultTableModel rows = (DefaultTableModel) view.jTDetail.getModel();
        for (int i = rows.getRowCount() - 1; i >= 0; i--) {
            rows.removeRow(i);
        }
        view.jTDetail.repaint();
        TreePath treePath = view.jTrContent.getSelectionPath();
        String path = FileIO.getFilePath(treePath);
        view.jLState.setText(path);
        view.jBShareSelected.setEnabled(true);
        view.jBDisShareSelected.setEnabled(false);
        view.jBDisShareAll.setEnabled(false);
        //   jBShowShare.setEnabled(true);
        if (!path.equals("")) {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null) {
                File current;
                String name;
                long size;
                String type;
                for (int i = 0; i < files.length; i++) {
                    current = files[i];
                    if (!files[i].isDirectory() && files[i].canRead()) {
                        name = current.getName();
                        size = current.length();
                        type = FileIO.getFileType(name);
                        rows.addRow(new Object[]{false, name, FileIO.formatFileSize(size) + "(" + size + ")", type, path});
                    }
                }
            }
        }

    }

    public void jTrContentValueChanged(javax.swing.event.TreeSelectionEvent evt) {
        // TODO add your handling code here:
        updateJTable();

    }

    public void jBShareSelectedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        int[] selected = this.getSelectedRows(view.jTDetail);
        if (selected != null) {
            DefaultTableModel rows = (DefaultTableModel) view.jTDetail.getModel();
            for (int i = 0; i < selected.length; i++) {
                String fullPath = (String) rows.getValueAt(view.jTDetail.convertRowIndexToModel(selected[i]), 4) + (String) rows.getValueAt(view.jTDetail.convertRowIndexToModel(selected[i]), 1);
                FileIO.shareFile(new File(fullPath), view.platform.getPeerID());
            }
            for (int i = 0; i < view.jTDetail.getRowCount(); i++) {
                view.jTDetail.setValueAt(false, i, 0);
            }
            view.jTDetail.clearSelection();
            view.jTDetail.repaint();
        }
    }

    public void jBDisShareAllActionPerformed(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < view.jTAlreadyShared.getRowCount(); i++) {
            view.jTAlreadyShared.setValueAt(false, i, 0);
        }
    }

    public void jBDisShareSelectedActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        int[] selected = this.getSelectedRows(view.jTAlreadyShared);
        if (selected != null) {
            String key;
            for (int i = selected.length - 1; i >= 0; i--) {
                DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyShared.getModel();
                key = (String) rows.getValueAt(view.jTAlreadyShared.convertRowIndexToModel(selected[i]), 4) + (String) rows.getValueAt(view.jTAlreadyShared.convertRowIndexToModel(selected[i]), 1);
                FileIO.list.remove(key);
                rows.removeRow(view.jTAlreadyShared.convertRowIndexToModel(selected[i]));
            }
            view.jTAlreadyShared.clearSelection();
            view.jTAlreadyShared.repaint();
        }
    }

    public void jBtRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        HashSet userList = AdvFindingServer.getPeerSet();
        DefaultListModel model = (DefaultListModel) view.peerList.getModel();
        model.removeAllElements();
        for (Iterator it = userList.iterator(); it.hasNext();) {
            PeerAdvertisement adv = (PeerAdvertisement) it.next();

            //  if (!adv.getPeerID().toString().equals(platform.getPeerID())) {
            model.addElement((String) adv.getName() + "(" + adv.getPeerID() + ")");
            // }
        }

    }

    public void jBtGetListActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        jBtClearListActionPerformed(evt);
        String peerID;
        String temp = (String) view.peerList.getSelectedValue();
        int begin = temp.indexOf("(");
        int end = temp.indexOf(")");
        peerID = temp.substring(begin + 1, end);
        String password = JOptionPane.showInputDialog("请输入连接密码");
        if (password == null) {
            password = "";
        }
        view.remoteFileList = view.fileListClient.getFileList(peerID, password.trim());
        if (view.remoteFileList == null) {
            JOptionPane.showMessageDialog(null, "Password Error");
        } else {
            if (!view.remoteFileList.isEmpty()) {
                DefaultTableModel rows = (DefaultTableModel) view.jTSelectedFileList.getModel();
                //String identifier;
                String name;
                long size;
                String type;
                String fullPath;
                FileInfo info;
                for (Iterator<String> key = view.remoteFileList.keySet().iterator(); key.hasNext();) {
                    fullPath = key.next();
                    info = view.remoteFileList.get(fullPath);
                    name = info.getName();
                    size = info.getSize();
                    type = info.getType();
                    rows.addRow(new Object[]{false, name, FileIO.formatFileSize(size) + "(" + size + ")", type, fullPath});
                }
            }

        }
    }

    public void jBtClearListActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if (view.jTSelectedFileList.getRowCount() > 0) {
            DefaultTableModel rows = (DefaultTableModel) view.jTSelectedFileList.getModel();
            for (int i = rows.getRowCount() - 1; i >= 0; i--) {
                rows.removeRow(i);
            }
            view.remoteFileList.clear();
            view.jTSelectedFileList.repaint();
        }

    }

    public void jBdownloadActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        int[] selected = this.getSelectedRows(view.jTSelectedFileList);
        if (selected != null) {
            DefaultTableModel rows = (DefaultTableModel) view.jTSelectedFileList.getModel();
            DefaultTableModel trans = (DefaultTableModel) view.jTTransmit.getModel();
            FileInfo info;
            String fullPath;
            String name;
            String peerID;
            long size;
            for (int i = 0; i < selected.length; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
                fullPath = (String) rows.getValueAt(view.jTSelectedFileList.convertRowIndexToModel(selected[i]), 4);
                info = view.remoteFileList.get(fullPath);
                name = info.getName();
                size = info.getSize();
                peerID = info.getPeerID();
                int flag = FileDownloadingList.findFile(peerID + fullPath);
                //如果文件过去已经下载完成
                if (flag == FileDownloadingList.FINISH) {
                    int choice = JOptionPane.showConfirmDialog(null, "已经存在，是否打开？", "Warning", JOptionPane.YES_NO_OPTION);
                    //选择现在就打开文件
                    if (choice == JOptionPane.YES_OPTION) {
                        String localpath = FileDownloadingList.getFilePath(peerID + fullPath);
                        File file = new File(localpath);
                        //如果文件存在，直接打开
                        if (file.exists()) {
                            try {
                                Runtime.getRuntime().exec("explorer " + " " + localpath);
                            } catch (IOException ex) {
                                Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } //endif 文件存在
                        //if 文件已经删除了
                        else {
                            int choice2 = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                            if (choice2 == JOptionPane.YES_OPTION) {
                                FileDownloadingList.removeFile(peerID + fullPath);
                            }
                        }//endif 文件已经删除
                    }//endif 选择不打开文件
                }//endif 文件过去已经下载完成
                //如果文件正在下载中
                else if (flag == FileDownloadingList.PROCESS || flag == FileDownloadingList.WAIT) {
                    JOptionPane.showMessageDialog(null, "文件正在下载中，请稍等片刻....");
                } //如果文件以前没有下载过
                else {
                    //   FileDownloadingList fileDownloadList = new FileDownloadingList(name, size + "", info.getType(), peerID, fullPath, MyConfiguration.getDownloadThread_Count());

                    //判断是否需要重命名文件
                    String type = info.getType();
                    File file = new File(SpecialValue.INCOMING_PATH + name);
                    //多线程下载临时文件名
                    File file2 = new File(SpecialValue.INCOMING_PATH + name + ".part1");
                    // if 已经有同名的文件，则改名
                    if (file.exists() || file2.exists()) {
                        String pureName = FileIO.getFileName(name);
                        int count = 1;
                        String postFix;
                        if (type.equals("文件")) {
                            postFix = "";
                        } else {
                            postFix = "." + type;
                        }
                        String post = ("(" + count + ")");
                        file = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix);
                        file2 = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix + ".part1");
                        while (file.exists() || file2.exists()) {
                            count++;
                            post = ("(" + count + ")");
                            file = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix);
                            file2 = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix + ".part1");
                        }
                        name = pureName + post + postFix;
                    }//endif 同名文件改名
                    FileDownloadingList fileDownloadList = new FileDownloadingList(name, size + "", info.getType(), peerID, fullPath, MyConfiguration.getDownloadThread_Count());
                    //否则直接下载文件
                    //加入下载列表
                    fileDownloadList.setLocalpath(SpecialValue.INCOMING_PATH + name);
                    FileDownloadingList.addFile(fileDownloadList);
                    //下载文件

                    int rowCount = trans.getRowCount();
                    int threadNO = view.threadManager.process(fileDownloadList, rowCount, 5);
                    //是否到达线程上线
                    if (threadNO == -1) {
                        System.out.println("已达下载上限");
                        trans.addRow(new Object[]{name, FileIO.formatFileSize(size), info.getType(), "", 0,
                                    "等待", peerID, fullPath});
                    } else {
                        trans.addRow(new Object[]{name, FileIO.formatFileSize(size), info.getType(), "", 0,
                                    "下载中", peerID, fullPath});
                        view.threadNumMap.put(fileDownloadList.getKey(), threadNO);
                    }
                }//endif 如果文件以前没有下载过
            }
            for (int i = 0; i < view.jTSelectedFileList.getRowCount(); i++) {
                view.jTSelectedFileList.setValueAt(false, i, 0);
            }
            view.jTSelectedFileList.clearSelection();
            view.jTSelectedFileList.repaint();
        }
    }

    public void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        jBtClearActionPerformed(evt);
        if (view.netMode.getSelectedItem().equals("局域网模式")) {
            String request = view.inputField.getText();
            if (!request.equals("") && request != null) {
                HashSet<String> id = MyIKAnalysis.Analysis(request);
                if (!id.isEmpty()) {
                    HashSet<FileInfo> results = view.platform.getIndexSearchClient().getSearchResultBoard(id);
                    System.out.println("RESULT is" + results.size());
                    if (!results.isEmpty()) {
                        DefaultTableModel rows = (DefaultTableModel) view.jTSearchResult.getModel();
                        FileInfo info;
                        for (Iterator it = results.iterator(); it.hasNext();) {
                            info = (FileInfo) it.next();
                            rows.addRow(new Object[]{false, info.getName(), FileIO.formatFileSize(info.getSize()) + "(" + info.getSize() + ")",
                                        info.getType(), info.getPeerID(), info.getPath() + info.getName()});
                        }
                    }
                }
            }
        } else {
            ;//--------------------------->万维网处理暂空。。。。。。。。。
        }

    }

    public void jBtClearActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        if (view.jTSearchResult.getRowCount() > 0) {
            DefaultTableModel rows = (DefaultTableModel) view.jTSearchResult.getModel();
            for (int i = rows.getRowCount() - 1; i >= 0; i--) {
                rows.removeRow(i);
            }
            view.jTSearchResult.repaint();
        }

    }

    public void jBtDownloadActionPerformed(java.awt.event.ActionEvent evt) {
        int[] selected = this.getSelectedRows(view.jTSearchResult);
        if (selected != null) {
            DefaultTableModel rows = (DefaultTableModel) view.jTSearchResult.getModel();
            DefaultTableModel trans = (DefaultTableModel) view.jTTransmit.getModel();
            String type;
            String fullPath;
            String name;
            String peerID;
            String size;
            for (int i = 0; i < selected.length; i++) {
                int sele = view.jTSearchResult.convertRowIndexToModel(selected[i]);
                name = (String) rows.getValueAt(sele, 1);
                size = (String) rows.getValueAt(sele, 2);
                type = (String) rows.getValueAt(sele, 3);
                peerID = (String) rows.getValueAt(sele, 4);
                fullPath = (String) rows.getValueAt(sele, 5);
                long sizeLong = FileIO.getSizeLong(size);
                String sizeStr = FileIO.getSizeStr(size);

                int flag = FileDownloadingList.findFile(peerID + fullPath);
                //如果文件过去已经下载完成
                if (flag == FileDownloadingList.FINISH) {
                    int choice = JOptionPane.showConfirmDialog(null, "已经存在，是否打开？", "Warning", JOptionPane.YES_NO_OPTION);
                    //选择现在就打开文件
                    if (choice == JOptionPane.YES_OPTION) {
                        String localpath = FileDownloadingList.getFilePath(peerID + fullPath);
                        File file = new File(localpath);
                        //如果文件存在，直接打开
                        if (file.exists()) {
                            try {
                                Runtime.getRuntime().exec("explorer " + " " + localpath);
                            } catch (IOException ex) {
                                Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } //endif 文件存在
                        //if 文件已经删除了
                        else {
                            int choice2 = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                            if (choice2 == JOptionPane.YES_OPTION) {
                                FileDownloadingList.removeFile(peerID + fullPath);
                            }
                        }//endif 文件已经删除
                    }//endif 选择不打开文件
                }//endif 文件过去已经下载完成
                //如果文件正在下载中
                else if (flag == FileDownloadingList.PROCESS || flag == FileDownloadingList.WAIT) {
                    JOptionPane.showMessageDialog(null, "文件正在下载中，请稍等片刻....");
                } //如果文件以前没有下载过
                else {
                    //判断是否需要重命名文件
                    File file = new File(SpecialValue.INCOMING_PATH + name);
                    //多线程下载临时文件名
                    File file2 = new File(SpecialValue.INCOMING_PATH + name + ".part1");
                    // if 已经有同名的文件，则改名
                    if (file.exists() || file2.exists()) {
                        String pureName = FileIO.getFileName(name);
                        int count = 1;
                        String postFix;
                        if (type.equals("文件")) {
                            postFix = "";
                        } else {
                            postFix = "." + type;
                        }
                        String post = ("(" + count + ")");
                        file = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix);
                        file2 = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix + ".part1");
                        while (file.exists() || file2.exists()) {
                            count++;
                            post = ("(" + count + ")");
                            file = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix);
                            file2 = new File(SpecialValue.INCOMING_PATH + pureName + post + postFix + ".part1");
                        }
                        name = pureName + post + postFix;
                    }//endif 同名文件改名
                    FileDownloadingList fileDownloadList = new FileDownloadingList(name, sizeLong + "", type, peerID, fullPath, MyConfiguration.getDownloadThread_Count());
                    //否则直接下载文件
                    //加入下载列表
                    fileDownloadList.setLocalpath(SpecialValue.INCOMING_PATH + name);
                    FileDownloadingList.addFile(fileDownloadList);
                    //下载文件

                    int rowCount = trans.getRowCount();
                    int threadNO = view.threadManager.process(fileDownloadList, rowCount, 5);

                    if (threadNO == -1) {
                        System.out.println("已达下载上限");
                        //  JOptionPane.showMessageDialog(null, "已达达到下载上限!\n文件" + name + "无法下载");
                        trans.addRow(new Object[]{name, sizeStr, type, "", 0,
                                    "等待", peerID, fullPath});
                    } else {
                        trans.addRow(new Object[]{name, sizeStr, type, "", 0,
                                    "下载中", peerID, fullPath});
                        view.threadNumMap.put(fileDownloadList.getKey(), threadNO);
                    }
                }//endif 如果文件以前没有下载过
            }
            for (int i = 0; i < view.jTSearchResult.getRowCount(); i++) {
                view.jTSearchResult.setValueAt(false, i, 0);
            }
            view.jTSearchResult.clearSelection();
            view.jTSearchResult.repaint();
        }

    }
    /**/

    public void jBDisDownloadActionPerformed(java.awt.event.ActionEvent evt) {
        //  int[] selected = jTAlreadyDownload.getSelectedRows();
        int[] selected = this.getSelectedRows(view.jTAlreadyDownload);
        if (selected != null) {
            String key;
            for (int i = selected.length - 1; i >= 0; i--) {
                DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyDownload.getModel();
                key = (String) rows.getValueAt(view.jTAlreadyDownload.convertRowIndexToModel(selected[i]), 1);
                FileDownloadedList.removeFile(key);
                rows.removeRow(view.jTAlreadyDownload.convertRowIndexToModel(selected[i]));
            }
            view.jTAlreadyDownload.clearSelection();
            view.jTAlreadyDownload.repaint();
        }
    }

    public void jBDisDownloadAllActionPerformed(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < view.jTAlreadyDownload.getRowCount(); i++) {
            view.jTAlreadyDownload.setValueAt(true, i, 0);
        }
    }

    public void jBShareSelectedAllActionPerformed(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < view.jTDetail.getRowCount(); i++) {
            view.jTDetail.setValueAt(true, i, 0);
        }
    }

    public void jpalreadysharedComponentShown(java.awt.event.ComponentEvent evt) {
        // TODO add your handling code here:
        DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyShared.getModel();
        for (int i = rows.getRowCount() - 1; i >= 0; i--) {
            rows.removeRow(i);
        }
        String fullPath = "";
        String name = "";
        long size = 0;
        String type = "";
        String path = "";
        FileInfo info = new FileInfo();
        if (!FileIO.list.isEmpty()) {
            for (Iterator<String> key = FileIO.list.keySet().iterator(); key.hasNext();) {
                fullPath = key.next();
                info = FileIO.list.get(fullPath);
                name = info.getName();
                size = info.getSize();
                type = info.getType();
                path = info.getPath();
                rows.addRow(new Object[]{false, name, FileIO.formatFileSize(size) + "(" + size + ")", type, path});
            }
            view.jBDisShareSelected.setEnabled(true);
            view.jBDisShareAll.setEnabled(true);
        } else {
            view.jBDisShareSelected.setEnabled(false);
            view.jBDisShareAll.setEnabled(false);
        }
        view.jTAlreadyShared.repaint();

    }

    public void jpalreadydownloadComponentShown(java.awt.event.ComponentEvent evt) {
        // TODO add your handling code here:
        DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyDownload.getModel();
        for (int i = rows.getRowCount() - 1; i >= 0; i--) {
            rows.removeRow(i);
        }
        if ((FileDownloadedList.fileDownloadList).size() != 0) {
            Collection<FileDownloadedList> col = FileDownloadedList.fileDownloadList.values();
            Iterator it = col.iterator();
            while (it != null && it.hasNext()) {
                FileDownloadedList filelist = (FileDownloadedList) it.next();
                rows.addRow(new Object[]{false, filelist.getName(), filelist.getSize(), filelist.getType(), filelist.getPath(), filelist.getDate()});
            }
            view.jBDisDownload.setEnabled(true);
            view.jBDisDownloadAll.setEnabled(true);
            view.jButtonSelectRun.setEnabled(true);
        } else {
            view.jBDisDownload.setEnabled(false);
            view.jBDisDownloadAll.setEnabled(false);
            view.jButtonSelectRun.setEnabled(false);
        }
        view.jTAlreadyDownload.repaint();

    }

    public void jTAlreadyDownloadMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int[] selected = view.jTAlreadyDownload.getSelectedRows();
            //  int[] selected = this.getSelectedRows(jTAlreadyDownload);
            if (selected != null) {
                DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyDownload.getModel();
                String name = (String) rows.getValueAt(view.jTAlreadyDownload.convertRowIndexToModel(selected[0]), 1);
                String path = (String) rows.getValueAt(view.jTAlreadyDownload.convertRowIndexToModel(selected[0]), 4);
                String fullpath = path + "\\" + name;
                try {
                    File file = new File(fullpath);
                    if (file.exists()) {
                        Runtime.getRuntime().exec("explorer " + " " + fullpath);
                    } else {
                        int choice = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            rows.removeRow(view.jTAlreadyDownload.convertRowIndexToModel(selected[0]));
                            FileDownloadedList.removeFile(name);
                            view.jTAlreadyDownload.clearSelection();
                            view.jTAlreadyDownload.repaint();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
        }
    }

    public void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyChar() == (58 - '0')) {
            if (view.jTSearchResult.getRowCount() > 0) {
                DefaultTableModel rows = (DefaultTableModel) view.jTSearchResult.getModel();
                for (int i = rows.getRowCount() - 1; i >= 0; i--) {
                    rows.removeRow(i);
                }
                view.jTSearchResult.repaint();
            }
            if (view.netMode.getSelectedItem().equals("局域网模式")) {
                String request = view.inputField.getText();
                if (!request.equals("") && request != null) {
                    HashSet<String> id = MyIKAnalysis.Analysis(request);
                    if (!id.isEmpty()) {
                        HashSet<FileInfo> results = view.platform.getIndexSearchClient().getSearchResultBoard(id);
                        if (!results.isEmpty()) {
                            DefaultTableModel rows = (DefaultTableModel) view.jTSearchResult.getModel();
                            FileInfo info;
                            for (Iterator it = results.iterator(); it.hasNext();) {
                                info = (FileInfo) it.next();
                                rows.addRow(new Object[]{false, info.getName(), FileIO.formatFileSize(info.getSize()) + "(" + info.getSize() + ")",
                                            info.getType(), info.getPeerID(), info.getPath() + info.getName()});
                            }
                        }
                    }
                }
            } else {
                ;//--------------------------->万维网处理暂空。。。。。。。。。
            }
        }

    }

    public void jTAlreadySharedMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int[] selected = this.view.jTAlreadyShared.getSelectedRows();
            //    int[] selected = this.getSelectedRows(jTAlreadyShared);
            if (selected != null) {
                DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyShared.getModel();
                String name = (String) rows.getValueAt(view.jTAlreadyShared.convertRowIndexToModel(selected[0]), 1);
                String path = (String) rows.getValueAt(view.jTAlreadyShared.convertRowIndexToModel(selected[0]), 4);
                String fullpath = path + name;
                try {
                    File file = new File(fullpath);
                    if (file.exists()) {
                        Runtime.getRuntime().exec("explorer " + " " + fullpath);
                    } else {
                        int choice = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            FileIO.list.remove(fullpath);
                            rows.removeRow(view.jTAlreadyShared.convertRowIndexToModel(selected[0]));
                            view.jTAlreadyShared.clearSelection();
                            view.jTAlreadyShared.repaint();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void jButtonSelectRunActionPerformed(java.awt.event.ActionEvent evt) {
        //   int[] selected = this.jTAlreadyDownload.getSelectedRows();
        int[] selected = this.getSelectedRows(view.jTAlreadyDownload);
        int removeCount = 0;
        if (selected != null) {
            for (int i = 0; i < selected.length; i++) {
                DefaultTableModel rows = (DefaultTableModel) view.jTAlreadyDownload.getModel();
                String name = (String) rows.getValueAt(view.jTAlreadyDownload.convertRowIndexToModel(selected[i]) - removeCount, 1);
                String path = (String) rows.getValueAt(view.jTAlreadyDownload.convertRowIndexToModel(selected[i]) - removeCount, 4);
                String fullpath = path + "\\" + name;
                try {
                    File file = new File(fullpath);
                    if (file.exists()) {
                        Runtime.getRuntime().exec("explorer " + " " + fullpath);
                    } else {
                        int choice = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            FileDownloadedList.removeFile(name);
                            rows.removeRow(view.jTAlreadyDownload.convertRowIndexToModel(selected[i]) - removeCount);
                            removeCount++;
                            view.jTAlreadyDownload.clearSelection();
                            view.jTAlreadyDownload.repaint();

                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void deleteDownload(ActionEvent e) {
        int[] selected = view.jTTransmit.getSelectedRows();
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        for (int i = 0; i < selected.length; i++) {
            if (((String) (rows.getValueAt(selected[i], 5))).equals("下载中")) {
                JOptionPane.showMessageDialog(null, "任务下载中，请先暂停，再删除");
            } else {
                String peerID = (String) rows.getValueAt(selected[i], 6);
                String fullPath = (String) rows.getValueAt(selected[i], 7);
                FileDownloadingList.fileDownloadingList.remove(peerID + fullPath);
                rows.removeRow(selected[i]);
            }
        }
    }

    public void pauseDownload(ActionEvent e) {
        int[] selected = view.jTTransmit.getSelectedRows();
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        String status = (String) rows.getValueAt(selected[0], 5);
        if (status.equals("下载完毕")) {
            JOptionPane.showMessageDialog(null, "下载已完成，无法暂停");
        } else if (status.equals("暂停")) {
            JOptionPane.showMessageDialog(null, "您已经暂停次下载任务");
        }//状态为等待时：
        else {
            String peerID = (String) rows.getValueAt(selected[0], 6);
            String fullPath = (String) rows.getValueAt(selected[0], 7);
            FileDownloadingList fdl = (FileDownloadingList) FileDownloadingList.fileDownloadingList.get(peerID + fullPath);
            fdl.setStatus(FileDownloadingList.PAUSE);
            FileDownloadingList.fileDownloadingList.put(peerID + fullPath, fdl);
            rows.setValueAt("暂停", selected[0], 5);

            int threadNO = view.threadNumMap.get(peerID + fullPath);
            ProgressPool progressPool = view.threadManager.getThread(threadNO);
            try {
                view.threadManager.setWait(threadNO);
                System.out.println("Stop Button Clicked" + threadNO + " and set Wait");
            } catch (Exception ex) {
                Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void recoverDownload(ActionEvent e) {
        int[] selected = view.jTTransmit.getSelectedRows();
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        String status = (String) rows.getValueAt(selected[0], 5);
        if (status.equals("下载完毕")) {
            JOptionPane.showMessageDialog(null, "下载已完成");
        } else {
            String peerID = (String) rows.getValueAt(selected[0], 6);
            String fullPath = (String) rows.getValueAt(selected[0], 7);
            FileDownloadingList file = FileDownloadingList.fileDownloadingList.get(peerID + fullPath);
            int threadNO = view.threadManager.process(file, selected[0], 5);
            if (threadNO == -1) {
                System.out.println("已达下载上限");
                rows.setValueAt("等待", selected[0], 5);
                file.setStatus(FileDownloadingList.WAIT);
            } else {
                System.out.println("Starting Downloading");
                rows.setValueAt("下载中", selected[0], 5);
                file.setStatus(FileDownloadingList.PROCESS);
                view.threadNumMap.put(file.getKey(), threadNO);
            }
            FileDownloadingList.fileDownloadingList.put(file.getKey(), file);
        }
    }

    public void cancelDownload(ActionEvent e) {
        int[] selected = view.jTTransmit.getSelectedRows();
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        String status = (String) rows.getValueAt(selected[0], 5);
        if (status.equals("下载完毕")) {
            JOptionPane.showMessageDialog(null, "下载已完成，无法取消");
        }//状态为下载中时：
        else {
            String peerID = (String) rows.getValueAt(selected[0], 6);
            String fullPath = (String) rows.getValueAt(selected[0], 7);
            
            rows.setValueAt("暂停", selected[0], 5);
            if (status.equals("下载中")) {
                int threadNO = view.threadNumMap.get(peerID + fullPath);
                ProgressPool progressPool = view.threadManager.getThread(threadNO);
                try {
                    view.threadManager.setWait(threadNO);
                    System.out.println("Stop Button Clicked" + threadNO + " and set Wait");
                } catch (Exception ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //都已经处于等待或暂停状态
            FileDownloadingList fdl = (FileDownloadingList) FileDownloadingList.fileDownloadingList.get(peerID + fullPath);
            String path = fdl.getLocalpath();
            int n = fdl.getSegCount();
            FileDownloadingList.fileDownloadingList.remove(fdl.getKey());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
            }
            FileIO.deleteFile(path, n);
            rows.removeRow(selected[0]);
        }
    }

    public void openFile(ActionEvent e) {
        int[] selected = view.jTTransmit.getSelectedRows();
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        String status = (String) rows.getValueAt(selected[0], 5);
        if (status.equals("下载完毕")) {
            String peerID = (String) rows.getValueAt(selected[0], 6);
            String fullPath = (String) rows.getValueAt(selected[0], 7);
            FileDownloadingList fdl = (FileDownloadingList) FileDownloadingList.fileDownloadingList.get(peerID + fullPath);
            String path = fdl.getLocalpath();
            File f = new File(path);
            if (f.exists()) {
                try {
                    Runtime.getRuntime().exec("explorer " + " " + path);
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "本地文件不存在");
            }
        }//状态为下载中时：
        else {
            JOptionPane.showMessageDialog(null, "下载未完成，请稍候打开");
        }
    }

    public void jTDetailMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            int[] selected = view.jTDetail.getSelectedRows();
            if (selected != null) {
                DefaultTableModel rows = (DefaultTableModel) view.jTDetail.getModel();
                String name = (String) rows.getValueAt(view.jTDetail.convertRowIndexToModel(selected[0]), 1);
                String path = (String) rows.getValueAt(view.jTDetail.convertRowIndexToModel(selected[0]), 4);
                String fullpath = path + name;
                try {
                    File file = new File(fullpath);
                    if (file.exists()) {
                        Runtime.getRuntime().exec("explorer " + " " + fullpath);
                    } else {
                        int choice = JOptionPane.showConfirmDialog(null, "文件: " + name + " 不存在，是否删除条目？", "Warning", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            rows.removeRow(view.jTDetail.convertRowIndexToModel(selected[0]));
                            view.jTDetail.clearSelection();
                            view.jTDetail.repaint();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private int[] getSelectedRows(javax.swing.JTable table) {
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((Boolean) table.getValueAt(i, 0))) {
                count++;
            }
        }
        int[] returnValue = new int[count];
        int j = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((Boolean) table.getValueAt(i, 0))) {
                returnValue[j] = i;
                j++;
            }
        }
        return returnValue;
    }

    public void notice() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
        }
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        for (int i = 0; i < rows.getRowCount(); i++) {
            if ((((String) rows.getValueAt(i, 5)).trim()).equals("等待")) {
                String pid = (String) rows.getValueAt(i, 6);

                String pth = (String) rows.getValueAt(i, 7);
                FileDownloadingList fdl = (FileDownloadingList) FileDownloadingList.fileDownloadingList.get(pid + pth);
                int threadNO = view.threadManager.process(fdl, i, 5);
                if (threadNO == -1) {
                    System.out.println(pid + "已达下载上限");
                } else {
                    System.out.println(pid + "正在下载中");
                    fdl.setStatus(FileDownloadingList.PROCESS);
                    FileDownloadingList.fileDownloadingList.put(fdl.getKey(), fdl);
                    view.threadNumMap.put(fdl.getKey(), threadNO);
                }
                break;
            }
        }
    }

    public void initDownloadList() {
        DefaultTableModel rows = (DefaultTableModel) view.jTTransmit.getModel();
        for (int i = rows.getRowCount() - 1; i >= 0; i--) {
            rows.removeRow(i);
        }
        if ((FileDownloadingList.fileDownloadingList).size() != 0) {
            Collection<FileDownloadingList> col = FileDownloadingList.fileDownloadingList.values();
            Iterator it = col.iterator();
            while (it != null && it.hasNext()) {
                FileDownloadingList filelist = (FileDownloadingList) it.next();
                int stat = filelist.getStatus();
                String status = "等待";
                if (stat == FileDownloadingList.FINISH) {
                    status = "下载完毕";
                } else if (stat == FileDownloadingList.PROCESS) {
                    status = "下载中";
                } else if (stat == FileDownloadingList.PAUSE) {
                    status = "暂停";
                }
                rows.addRow(new Object[]{filelist.getName(), filelist.getSizeStr(), filelist.getType(), "", filelist.getPercent(), status, filelist.getSource(), filelist.getPath()});
            }
        }
    }
}
