/*
 * P2PFileTransmitSystemView.java
 */
package P2PFileTransmitSystem.GUI;

import P2PFileTransmitSystem.Config.MyConfiguration;
import P2PFileTransmitSystem.Controller.Controller;
import P2PFileTransmitSystem.FileRelated.DownloadManager.ThreadPoolManager;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadedList;
import P2PFileTransmitSystem.FileRelated.DownloadManager.FileDownloadingList;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;
import javax.swing.JPopupMenu;
import P2PFileTransmitSystem.FileRelated.FileIO;
import P2PFileTransmitSystem.FileRelated.FileInfo;
import P2PFileTransmitSystem.GUI.ProgressRenderer;
import P2PFileTransmitSystem.GUI.SystemTree;
import P2PFileTransmitSystem.Network.Client.AllFileListClient;
import P2PFileTransmitSystem.Network.MyPlatform;
import P2PFileTransmitSystem.P2PFileTransmitSystemApp;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/**
 * The application's main frame.
 */
public class P2PFileTransmitSystemView extends FrameView {

    public P2PFileTransmitSystemView(SingleFrameApplication app) {
        super(app);

        initComponents();
        controller = new Controller(this);

        platform = new MyPlatform();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
        }
        platform.getInfo();

        fileListClient = new AllFileListClient(MyPlatform.TheNetworkManager, platform.CustomPeerGroup);

        threadManager = new ThreadPoolManager(MyConfiguration.getMax_Download_Count());
        threadNumMap = new HashMap<String, Integer>();
        
        FileIO.readList();
        FileDownloadedList.readList();
        FileDownloadingList.readList();
        FileDownloadingList.initState();

        controller.initDownloadList();

        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = P2PFileTransmitSystemApp.getApplication().getMainFrame();
            aboutBox = new P2PFileTransmitSystemAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        P2PFileTransmitSystemApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jtbWorkingSpace = new javax.swing.JTabbedPane();
        jptransmit = new javax.swing.JPanel();
        transmitBoard = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTTransmit = new javax.swing.JTable();
        jpNet = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        peerList = new javax.swing.JList();
        jLUser = new javax.swing.JLabel();
        jBtRefresh = new javax.swing.JButton();
        jBtGetList = new javax.swing.JButton();
        jLUser1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jBtClearList = new javax.swing.JButton();
        jBdownload = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTSelectedFileList = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jpSearch = new javax.swing.JPanel();
        result = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTSearchResult = new javax.swing.JTable();
        input = new javax.swing.JPanel();
        inputField = new javax.swing.JTextField();
        jBSearch = new javax.swing.JButton();
        netMode = new javax.swing.JComboBox();
        jBtDownload = new javax.swing.JButton();
        jBtClear = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jpShare = new javax.swing.JPanel();
        directoryPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTrContent = new SystemTree();
        detailPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTDetail = new javax.swing.JTable();
        jLState = new javax.swing.JLabel();
        control = new javax.swing.JPanel();
        jBShareSelected = new javax.swing.JButton();
        jBShareSelectedAll = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jpalreadyshared = new javax.swing.JPanel();
        transmitBoard1 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTAlreadyShared = new javax.swing.JTable();
        jBDisShareSelected = new javax.swing.JButton();
        jBDisShareAll = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jpalreadydownload = new javax.swing.JPanel();
        transmitBoard2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTAlreadyDownload = new javax.swing.JTable();
        jBDisDownload = new javax.swing.JButton();
        jBDisDownloadAll = new javax.swing.JButton();
        jButtonSelectRun = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jtbWorkingSpace.setName("jtbWorkingSpace"); // NOI18N

        jptransmit.setName("jptransmit"); // NOI18N

        transmitBoard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        transmitBoard.setName("transmitBoard"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTTransmit.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "文件名", "大小", "类型", "速度", "进度", "状态", "来源主机ID", "全路径"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTTransmit.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTTransmit.setCellSelectionEnabled(false);
        jTTransmit.setRowSelectionAllowed(true);
        jTTransmit.setDragEnabled(false);
        jTTransmit.setName("jTTransmit"); // NOI18N
        jTTransmit.setShowHorizontalLines(false);
        jTTransmit.setShowVerticalLines(false);
        jTTransmit.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(jTTransmit);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(P2PFileTransmitSystem.P2PFileTransmitSystemApp.class).getContext().getResourceMap(P2PFileTransmitSystemView.class);
        jTTransmit.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title0")); // NOI18N
        jTTransmit.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title1")); // NOI18N
        jTTransmit.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title2")); // NOI18N
        jTTransmit.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title3")); // NOI18N
        jTTransmit.getColumnModel().getColumn(4).setResizable(false);
        jTTransmit.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTTransmit.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title4")); // NOI18N
        jTTransmit.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title5")); // NOI18N
        jTTransmit.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title6")); // NOI18N
        jTTransmit.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("jTTransmit.columnModel.title7")); // NOI18N
        jTTransmit.getColumnModel().getColumn(4).setCellRenderer(new ProgressRenderer());
        jTTransmit.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu transJPMenu = new JPopupMenu();
        JMenuItem transMenuItem1 = new JMenuItem();
        JMenuItem transMenuItem2 = new JMenuItem();
        JMenuItem transMenuItem3 = new JMenuItem();
        JMenuItem transMenuItem4 = new JMenuItem();
        JMenuItem transMenuItem5 = new JMenuItem();
        transMenuItem1.setLabel("删除条目");
        transMenuItem2.setLabel("暂停下载");
        transMenuItem3.setLabel("恢复下载");
        transMenuItem4.setLabel("取消下载");
        transMenuItem5.setLabel("打开文件");
        transJPMenu.add(transMenuItem1);
        transJPMenu.add(transMenuItem2);
        transJPMenu.add(transMenuItem3);
        transJPMenu.add(transMenuItem4);
        transJPMenu.add(transMenuItem5);
        jTTransmit.setComponentPopupMenu(transJPMenu);

        transMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.deleteDownload(e);
            }
        });

        transMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.pauseDownload(e);
            }
        });

        transMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.recoverDownload(e);
            }
        });

        transMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.cancelDownload(e);
            }
        });

        transMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.openFile(e);
            }
        });

        javax.swing.GroupLayout transmitBoardLayout = new javax.swing.GroupLayout(transmitBoard);
        transmitBoard.setLayout(transmitBoardLayout);
        transmitBoardLayout.setHorizontalGroup(
            transmitBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        transmitBoardLayout.setVerticalGroup(
            transmitBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jptransmitLayout = new javax.swing.GroupLayout(jptransmit);
        jptransmit.setLayout(jptransmitLayout);
        jptransmitLayout.setHorizontalGroup(
            jptransmitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jptransmitLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jptransmitLayout.setVerticalGroup(
            jptransmitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jptransmitLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jptransmit.TabConstraints.tabTitle"), jptransmit); // NOI18N

        jpNet.setName("jpNet"); // NOI18N

        jScrollPane5.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane5.setName("jScrollPane5"); // NOI18N

        peerList.setModel(new DefaultListModel());
        peerList.setName("peerList"); // NOI18N
        peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(peerList);

        jLUser.setFont(resourceMap.getFont("jLUser.font")); // NOI18N
        jLUser.setText(resourceMap.getString("jLUser.text")); // NOI18N
        jLUser.setName("jLUser"); // NOI18N

        jBtRefresh.setText(resourceMap.getString("jBtRefresh.text")); // NOI18N
        jBtRefresh.setName("jBtRefresh"); // NOI18N
        jBtRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtRefreshActionPerformed(evt);
            }
        });

        jBtGetList.setText(resourceMap.getString("jBtGetList.text")); // NOI18N
        jBtGetList.setName("jBtGetList"); // NOI18N
        jBtGetList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtGetListActionPerformed(evt);
            }
        });

        jLUser1.setFont(resourceMap.getFont("jLUser1.font")); // NOI18N
        jLUser1.setText(resourceMap.getString("jLUser1.text")); // NOI18N
        jLUser1.setName("jLUser1"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N

        jBtClearList.setText(resourceMap.getString("jBtClearList.text")); // NOI18N
        jBtClearList.setName("jBtClearList"); // NOI18N
        jBtClearList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtClearListActionPerformed(evt);
            }
        });

        jBdownload.setText(resourceMap.getString("jBdownload.text")); // NOI18N
        jBdownload.setName("jBdownload"); // NOI18N
        jBdownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBdownloadActionPerformed(evt);
            }
        });

        jScrollPane6.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTSelectedFileList.setAutoCreateRowSorter(true);
        jTSelectedFileList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "选择", "文件名", "大小", "类型", "全路径"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTSelectedFileList.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTSelectedFileList.setName("jTSelectedFileList"); // NOI18N
        jTSelectedFileList.setShowHorizontalLines(false);
        jTSelectedFileList.setShowVerticalLines(false);
        jTSelectedFileList.setColumnSelectionAllowed(false);
        jTSelectedFileList.setRowSelectionAllowed(true);
        jTSelectedFileList.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(jTSelectedFileList);
        jTSelectedFileList.getColumnModel().getColumn(0).setResizable(false);
        jTSelectedFileList.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTSelectedFileList.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTSelectedFileList.columnModel.title0")); // NOI18N
        jTSelectedFileList.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTSelectedFileList.columnModel.title1")); // NOI18N
        jTSelectedFileList.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTSelectedFileList.columnModel.title2")); // NOI18N
        jTSelectedFileList.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTSelectedFileList.columnModel.title3")); // NOI18N
        jTSelectedFileList.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTSelectedFileList.columnModel.title4")); // NOI18N
        jTSelectedFileList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jBdownload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtClearList)
                .addContainerGap(249, Short.MAX_VALUE))
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jBdownload)
                    .addComponent(jBtClearList))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpNetLayout = new javax.swing.GroupLayout(jpNet);
        jpNet.setLayout(jpNetLayout);
        jpNetLayout.setHorizontalGroup(
            jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpNetLayout.createSequentialGroup()
                        .addComponent(jBtRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(jBtGetList))
                    .addComponent(jLUser, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpNetLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLUser1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(254, 254, 254))
                    .addGroup(jpNetLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jpNetLayout.setVerticalGroup(
            jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpNetLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLUser, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLUser1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpNetLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpNetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBtRefresh)
                            .addComponent(jBtGetList)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jpNet.TabConstraints.tabTitle"), jpNet); // NOI18N

        jpSearch.setName("jpSearch"); // NOI18N

        result.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        result.setName("result"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTSearchResult.setAutoCreateRowSorter(true);
        jTSearchResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "选择", "文件名", "大小", "类型", "来源主机ID", "全路径"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTSearchResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTSearchResult.setColumnSelectionAllowed(false);
        jTSearchResult.setRowSelectionAllowed(true);
        jTSearchResult.setGridColor(resourceMap.getColor("jTSearchResult.gridColor")); // NOI18N
        jTSearchResult.setName("jTSearchResult"); // NOI18N
        jTSearchResult.setShowHorizontalLines(false);
        jTSearchResult.setShowVerticalLines(false);
        jTSearchResult.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTSearchResult);
        jTSearchResult.getColumnModel().getColumn(0).setResizable(false);
        jTSearchResult.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTSearchResult.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title0")); // NOI18N
        jTSearchResult.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title1")); // NOI18N
        jTSearchResult.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title2")); // NOI18N
        jTSearchResult.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title3")); // NOI18N
        jTSearchResult.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title4")); // NOI18N
        jTSearchResult.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTSearchResult.columnModel.title5")); // NOI18N
        jTSearchResult.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout resultLayout = new javax.swing.GroupLayout(result);
        result.setLayout(resultLayout);
        resultLayout.setHorizontalGroup(
            resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        resultLayout.setVerticalGroup(
            resultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
        );

        input.setName("input"); // NOI18N

        inputField.setBackground(resourceMap.getColor("inputField.background")); // NOI18N
        inputField.setText(resourceMap.getString("inputField.text")); // NOI18N
        inputField.setName("inputField"); // NOI18N
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });

        jBSearch.setText(resourceMap.getString("jBSearch.text")); // NOI18N
        jBSearch.setName("jBSearch"); // NOI18N
        jBSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchActionPerformed(evt);
            }
        });

        netMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "局域网模式", "万维网模式" }));
        netMode.setName("netMode"); // NOI18N

        javax.swing.GroupLayout inputLayout = new javax.swing.GroupLayout(input);
        input.setLayout(inputLayout);
        inputLayout.setHorizontalGroup(
            inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(netMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSearch)
                .addContainerGap(366, Short.MAX_VALUE))
        );
        inputLayout.setVerticalGroup(
            inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSearch)
                    .addComponent(netMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jBtDownload.setText(resourceMap.getString("jBtDownload.text")); // NOI18N
        jBtDownload.setName("jBtDownload"); // NOI18N
        jBtDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtDownloadActionPerformed(evt);
            }
        });

        jBtClear.setText(resourceMap.getString("jBtClear.text")); // NOI18N
        jBtClear.setName("jBtClear"); // NOI18N
        jBtClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtClearActionPerformed(evt);
            }
        });

        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpSearchLayout = new javax.swing.GroupLayout(jpSearch);
        jpSearch.setLayout(jpSearchLayout);
        jpSearchLayout.setHorizontalGroup(
            jpSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(result, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(input, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpSearchLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBtDownload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtClear)))
                .addContainerGap())
        );
        jpSearchLayout.setVerticalGroup(
            jpSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(result, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtClear)
                    .addComponent(jBtDownload)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jpSearch.TabConstraints.tabTitle"), jpSearch); // NOI18N

        jpShare.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jpShare.setName("jpShare"); // NOI18N

        directoryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        directoryPanel.setName("directoryPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTrContent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTrContent.setAutoscrolls(true);
        jTrContent.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTrContent.setEditable(false);
        jTrContent.setLargeModel(true);
        jTrContent.setName("jTrContent"); // NOI18N
        jTrContent.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTrContentValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jTrContent);
        JPopupMenu treeJPMenu=new JPopupMenu();
        JMenuItem treeMenuItem1 = new JMenuItem();
        JMenuItem treeMenuItem2 = new JMenuItem();
        JMenuItem treeMenuItem3 = new JMenuItem();
        JMenuItem treeMenuItem4 = new JMenuItem();
        JMenuItem treeMenuItem5 = new JMenuItem();
        treeMenuItem1.setLabel("共享该目录");
        treeMenuItem2.setLabel("共享该目录及其子目录");
        treeMenuItem3.setLabel("取消共享该目录");
        treeMenuItem4.setLabel("取消共享该目录及其子目录");
        treeMenuItem5.setLabel("打开该目录");
        treeJPMenu.add(treeMenuItem1);
        treeJPMenu.add(treeMenuItem2);
        treeJPMenu.add(treeMenuItem3);
        treeJPMenu.add(treeMenuItem4);
        treeJPMenu.add(treeMenuItem5);
        jTrContent.setComponentPopupMenu(treeJPMenu);

        treeMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath treePath=jTrContent.getSelectionPath();
                FileIO.shareDir(FileIO.getFilePath(treePath),platform.getPeerID());
                controller.updateJTable();
                //System.out.println(e.getActionCommand());
            }
        });
        treeMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath treePath=jTrContent.getSelectionPath();
                FileIO.shareDirAll(FileIO.getFilePath(treePath),platform.getPeerID());
                //System.out.println(e.getActionCommand());
                controller.updateJTable();
            }
        });
        treeMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath treePath=jTrContent.getSelectionPath();
                FileIO.disShareDir(FileIO.getFilePath(treePath));
                //System.out.println(e.getActionCommand());
                controller.updateJTable();
            }
        });
        treeMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath treePath=jTrContent.getSelectionPath();
                FileIO.disShareDirAll(FileIO.getFilePath(treePath));
                //System.out.println(e.getActionCommand());
                controller.updateJTable();
            }
        });
        treeMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath treePath = jTrContent.getSelectionPath();
                //System.out.println(FileIO.getFilePath(treePath));
                try {
                    Runtime.getRuntime().exec("explorer " + FileIO.getFilePath(treePath));
                } catch (IOException ex) {
                    Logger.getLogger(P2PFileTransmitSystemView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        javax.swing.GroupLayout directoryPanelLayout = new javax.swing.GroupLayout(directoryPanel);
        directoryPanel.setLayout(directoryPanelLayout);
        directoryPanelLayout.setHorizontalGroup(
            directoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(directoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addContainerGap())
        );
        directoryPanelLayout.setVerticalGroup(
            directoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, directoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );

        detailPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        detailPanel.setName("detailPanel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTDetail.setAutoCreateRowSorter(true);
        jTDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "选择", "文件名", "大小", "类型", "位置"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTDetail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTDetail.setCellSelectionEnabled(false);
        jTDetail.setColumnSelectionAllowed(false);
        jTDetail.setRowSelectionAllowed(true);
        jTDetail.setName("jTDetail"); // NOI18N
        jTDetail.setShowHorizontalLines(false);
        jTDetail.setShowVerticalLines(false);
        jTDetail.getTableHeader().setReorderingAllowed(false);
        jTDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTDetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTDetail);
        jTDetail.getColumnModel().getColumn(0).setResizable(false);
        jTDetail.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTDetail.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTDetail.columnModel.title0")); // NOI18N
        jTDetail.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTDetail.columnModel.title1")); // NOI18N
        jTDetail.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTDetail.columnModel.title2")); // NOI18N
        jTDetail.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTDetail.columnModel.title3")); // NOI18N
        jTDetail.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTDetail.columnModel.title4")); // NOI18N
        jTDetail.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jLState.setFont(resourceMap.getFont("jLState.font")); // NOI18N
        jLState.setText(resourceMap.getString("jLState.text")); // NOI18N
        jLState.setName("jLState"); // NOI18N

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLState, javax.swing.GroupLayout.DEFAULT_SIZE, 526, Short.MAX_VALUE)
                .addGap(10, 10, 10))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLState, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
        );

        control.setName("control"); // NOI18N

        jBShareSelected.setText(resourceMap.getString("jBShareSelected.text")); // NOI18N
        jBShareSelected.setName("jBShareSelected"); // NOI18N
        jBShareSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBShareSelectedActionPerformed(evt);
            }
        });

        jBShareSelectedAll.setText(resourceMap.getString("jBShareSelectedAll.text")); // NOI18N
        jBShareSelectedAll.setName("jBShareSelectedAll"); // NOI18N
        jBShareSelectedAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBShareSelectedAllActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlLayout = new javax.swing.GroupLayout(control);
        control.setLayout(controlLayout);
        controlLayout.setHorizontalGroup(
            controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBShareSelectedAll)
                .addGap(4, 4, 4)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBShareSelected)
                .addContainerGap(329, Short.MAX_VALUE))
        );
        controlLayout.setVerticalGroup(
            controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(controlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBShareSelectedAll)
                    .addComponent(jBShareSelected)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpShareLayout = new javax.swing.GroupLayout(jpShare);
        jpShare.setLayout(jpShareLayout);
        jpShareLayout.setHorizontalGroup(
            jpShareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpShareLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(directoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpShareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(control, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpShareLayout.setVerticalGroup(
            jpShareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpShareLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpShareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(directoryPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpShareLayout.createSequentialGroup()
                        .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addComponent(control, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jpShare.TabConstraints.tabTitle"), jpShare); // NOI18N

        jpalreadyshared.setName("jpalreadyshared"); // NOI18N
        jpalreadyshared.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jpalreadysharedComponentShown(evt);
            }
        });

        transmitBoard1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        transmitBoard1.setName("transmitBoard1"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTAlreadyShared.setAutoCreateRowSorter(true);
        jTAlreadyShared.setForeground(resourceMap.getColor("jTAlreadyShared.foreground")); // NOI18N
        jTAlreadyShared.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "选择", "文件名", "大小", "类型", "位置"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTAlreadyShared.setCellSelectionEnabled(false);
        jTAlreadyShared.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTAlreadyShared.setRowSelectionAllowed(true);
        jTAlreadyShared.setColumnSelectionAllowed(false);
        jTAlreadyShared.setDragEnabled(true);
        jTAlreadyShared.setName("jTAlreadyShared"); // NOI18N
        jTAlreadyShared.setShowHorizontalLines(false);
        jTAlreadyShared.setShowVerticalLines(false);
        jTAlreadyShared.getTableHeader().setReorderingAllowed(false);
        jTAlreadyShared.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTAlreadySharedMouseClicked(evt);
            }
        });
        /*
        jTAlreadyShared.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTAlreadySharedMouseClicked(evt);
            }
        });
        jTAlreadyShared.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                jTAlreadySharedVetoableChange(evt);
            }
        });
        */
        jScrollPane7.setViewportView(jTAlreadyShared);
        jTAlreadyShared.getColumnModel().getColumn(0).setResizable(false);
        jTAlreadyShared.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTAlreadyShared.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTAlreadyShared.columnModel.title0")); // NOI18N
        jTAlreadyShared.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTAlreadyShared.columnModel.title1")); // NOI18N
        jTAlreadyShared.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTAlreadyShared.columnModel.title2")); // NOI18N
        jTAlreadyShared.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTAlreadyShared.columnModel.title3")); // NOI18N
        jTAlreadyShared.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTAlreadyShared.columnModel.title4")); // NOI18N
        jTAlreadyShared.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jBDisShareSelected.setText(resourceMap.getString("jBDisShareSelected.text")); // NOI18N
        jBDisShareSelected.setName("jBDisShareSelected"); // NOI18N
        jBDisShareSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDisShareSelectedActionPerformed(evt);
            }
        });

        jBDisShareAll.setText(resourceMap.getString("jBDisShareAll.text")); // NOI18N
        jBDisShareAll.setName("jBDisShareAll"); // NOI18N
        jBDisShareAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDisShareAllActionPerformed(evt);
            }
        });

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transmitBoard1Layout = new javax.swing.GroupLayout(transmitBoard1);
        transmitBoard1.setLayout(transmitBoard1Layout);
        transmitBoard1Layout.setHorizontalGroup(
            transmitBoard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transmitBoard1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBDisShareAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jBDisShareSelected)
                .addContainerGap(557, Short.MAX_VALUE))
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        transmitBoard1Layout.setVerticalGroup(
            transmitBoard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transmitBoard1Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transmitBoard1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jBDisShareAll)
                    .addComponent(jBDisShareSelected))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpalreadysharedLayout = new javax.swing.GroupLayout(jpalreadyshared);
        jpalreadyshared.setLayout(jpalreadysharedLayout);
        jpalreadysharedLayout.setHorizontalGroup(
            jpalreadysharedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpalreadysharedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpalreadysharedLayout.setVerticalGroup(
            jpalreadysharedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpalreadysharedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jpalreadyshared.TabConstraints.tabTitle"), jpalreadyshared); // NOI18N

        jpalreadydownload.setName("jpalreadydownload"); // NOI18N
        jpalreadydownload.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jpalreadydownloadComponentShown(evt);
            }
        });

        transmitBoard2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        transmitBoard2.setName("transmitBoard2"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        jTAlreadyDownload.setAutoCreateRowSorter(true);
        jTAlreadyDownload.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "选择", "文件名", "大小", "类型", "本机位置", "下载日期"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTAlreadyDownload.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTAlreadyDownload.setName("jTAlreadyDownload"); // NOI18N
        jTAlreadyDownload.setShowHorizontalLines(false);
        jTAlreadyDownload.setShowVerticalLines(false);
        jTAlreadyDownload.setCellSelectionEnabled(false);
        jTAlreadyDownload.setColumnSelectionAllowed(false);
        jTAlreadyDownload.setRowSelectionAllowed(true);
        jTAlreadyDownload.getTableHeader().setReorderingAllowed(false);
        jTAlreadyDownload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTAlreadyDownloadMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(jTAlreadyDownload);
        jTAlreadyDownload.getColumnModel().getColumn(0).setResizable(false);
        jTAlreadyDownload.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTAlreadyDownload.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title0")); // NOI18N
        jTAlreadyDownload.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title1")); // NOI18N
        jTAlreadyDownload.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title2")); // NOI18N
        jTAlreadyDownload.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title3")); // NOI18N
        jTAlreadyDownload.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title4")); // NOI18N
        jTAlreadyDownload.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("jTAlreadyDownload.columnModel.title5")); // NOI18N
        jTAlreadyDownload.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jBDisDownload.setText(resourceMap.getString("jBDisDownload.text")); // NOI18N
        jBDisDownload.setName("jBDisDownload"); // NOI18N
        jBDisDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDisDownloadActionPerformed(evt);
            }
        });

        jBDisDownloadAll.setText(resourceMap.getString("jBDisDownloadAll.text")); // NOI18N
        jBDisDownloadAll.setName("jBDisDownloadAll"); // NOI18N
        jBDisDownloadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDisDownloadAllActionPerformed(evt);
            }
        });

        jButtonSelectRun.setText(resourceMap.getString("jButtonSelectRun.text")); // NOI18N
        jButtonSelectRun.setName("jButtonSelectRun"); // NOI18N
        jButtonSelectRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectRunActionPerformed(evt);
            }
        });

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout transmitBoard2Layout = new javax.swing.GroupLayout(transmitBoard2);
        transmitBoard2.setLayout(transmitBoard2Layout);
        transmitBoard2Layout.setHorizontalGroup(
            transmitBoard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transmitBoard2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBDisDownloadAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBDisDownload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSelectRun)
                .addContainerGap(494, Short.MAX_VALUE))
            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );
        transmitBoard2Layout.setVerticalGroup(
            transmitBoard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, transmitBoard2Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(transmitBoard2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBDisDownloadAll)
                    .addComponent(jButton5)
                    .addComponent(jBDisDownload)
                    .addComponent(jButtonSelectRun))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpalreadydownloadLayout = new javax.swing.GroupLayout(jpalreadydownload);
        jpalreadydownload.setLayout(jpalreadydownloadLayout);
        jpalreadydownloadLayout.setHorizontalGroup(
            jpalreadydownloadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpalreadydownloadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpalreadydownloadLayout.setVerticalGroup(
            jpalreadydownloadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpalreadydownloadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transmitBoard2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbWorkingSpace.addTab(resourceMap.getString("jpalreadydownload.TabConstraints.tabTitle"), jpalreadydownload); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jtbWorkingSpace, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jtbWorkingSpace, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(P2PFileTransmitSystem.P2PFileTransmitSystemApp.class).getContext().getActionMap(P2PFileTransmitSystemView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 839, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 669, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 2, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jTrContentValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTrContentValueChanged
        // TODO add your handling code here:
        controller.updateJTable();
    }//GEN-LAST:event_jTrContentValueChanged

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        // TODO add your handling code here:
        FileIO.writeList();
        FileDownloadedList.writeList();
        FileDownloadingList.writeList();
        platform.advPublishServer.interrupt();
        MyPlatform.peerOffLine();
        MyConfiguration.SaveSetting();
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void jBShareSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShareSelectedActionPerformed
        // TODO add your handling code here:
        controller.jBShareSelectedActionPerformed(evt);
    }//GEN-LAST:event_jBShareSelectedActionPerformed

    private void jBDisShareAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDisShareAllActionPerformed
        controller.jBDisShareAllActionPerformed(evt);
    }//GEN-LAST:event_jBDisShareAllActionPerformed

    private void jBDisShareSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDisShareSelectedActionPerformed
        // TODO add your handling code here:
        controller.jBDisShareSelectedActionPerformed(evt);
    }//GEN-LAST:event_jBDisShareSelectedActionPerformed

    private void jBtRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtRefreshActionPerformed
        // TODO add your handling code here:
        controller.jBtRefreshActionPerformed(evt);

    }//GEN-LAST:event_jBtRefreshActionPerformed

    private void jBtGetListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtGetListActionPerformed
        // TODO add your handling code here:
        controller.jBtGetListActionPerformed(evt);
    }//GEN-LAST:event_jBtGetListActionPerformed

    private void jBtClearListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtClearListActionPerformed
        // TODO add your handling code here:
        controller.jBtClearListActionPerformed(evt);
    }//GEN-LAST:event_jBtClearListActionPerformed

    private void jBdownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBdownloadActionPerformed
        // TODO add your handling code here:
        controller.jBdownloadActionPerformed(evt);
    }//GEN-LAST:event_jBdownloadActionPerformed

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // TODO add your handling code here:
        controller.jBSearchActionPerformed(evt);

    }//GEN-LAST:event_jBSearchActionPerformed

    private void jBtClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtClearActionPerformed
        // TODO add your handling code here:
        controller.jBtClearActionPerformed(evt);

    }//GEN-LAST:event_jBtClearActionPerformed

    private void jBtDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtDownloadActionPerformed
        controller.jBtDownloadActionPerformed(evt);
    }//GEN-LAST:event_jBtDownloadActionPerformed

    private void jBDisDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDisDownloadActionPerformed
        controller.jBDisDownloadActionPerformed(evt);
    }//GEN-LAST:event_jBDisDownloadActionPerformed

    private void jBDisDownloadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDisDownloadAllActionPerformed
        controller.jBDisDownloadAllActionPerformed(evt);
    }//GEN-LAST:event_jBDisDownloadAllActionPerformed

    private void jBShareSelectedAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShareSelectedAllActionPerformed
        controller.jBShareSelectedAllActionPerformed(evt);
    }//GEN-LAST:event_jBShareSelectedAllActionPerformed

    private void jpalreadysharedComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jpalreadysharedComponentShown
        // TODO add your handling code here:
        controller.jpalreadysharedComponentShown(evt);

    }//GEN-LAST:event_jpalreadysharedComponentShown

    private void jpalreadydownloadComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jpalreadydownloadComponentShown
        // TODO add your handling code here:
        controller.jpalreadydownloadComponentShown(evt);

    }//GEN-LAST:event_jpalreadydownloadComponentShown

    private void jTAlreadyDownloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTAlreadyDownloadMouseClicked
        controller.jTAlreadyDownloadMouseClicked(evt);
    }//GEN-LAST:event_jTAlreadyDownloadMouseClicked

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        controller.inputFieldKeyPressed(evt);

    }//GEN-LAST:event_inputFieldKeyPressed

    private void jTAlreadySharedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTAlreadySharedMouseClicked
        controller.jTAlreadySharedMouseClicked(evt);
    }//GEN-LAST:event_jTAlreadySharedMouseClicked

    private void jButtonSelectRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectRunActionPerformed
        controller.jButtonSelectRunActionPerformed(evt);
    }//GEN-LAST:event_jButtonSelectRunActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            int num = 0;
            String numStr = JOptionPane.showInputDialog(null, "当前最大连接数" + MyConfiguration.getMax_Download_Count() + "\n请输入最大连接数（1-20）\n重启有效");
            if (numStr == null) {
                return;
            }
            num = Integer.parseInt(numStr);
            if (num <= 0 || num > 20) {
                throw new NumberFormatException();
            }
            MyConfiguration.setMax_Download_Count(num);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "输入格式错误，或不在指定范围内");
        } catch (Exception ex) {
            System.out.println("Cancel the operation");
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        String password = JOptionPane.showInputDialog("请输入好友共享模块密码");
        if (password == null) {
            return;
        }
        password = password.trim();
        MyConfiguration.setPassword(password);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        for (int i = 0; i < jTSelectedFileList.getRowCount(); i++) {
            jTSelectedFileList.setValueAt(true, i, 0);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        for (int i = 0; i < jTSelectedFileList.getRowCount(); i++) {
            jTSelectedFileList.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        for (int i = 0; i < jTDetail.getRowCount(); i++) {
            jTDetail.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        for (int i = 0; i < jTAlreadyShared.getRowCount(); i++) {
            jTAlreadyShared.setValueAt(true, i, 0);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        for (int i = 0; i < jTAlreadyDownload.getRowCount(); i++) {
            jTAlreadyDownload.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTDetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTDetailMouseClicked
        controller.jTDetailMouseClicked(evt);
    }//GEN-LAST:event_jTDetailMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        for (int i = 0; i < jTSearchResult.getRowCount(); i++) {
            jTSearchResult.setValueAt(true, i, 0);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        for (int i = 0; i < jTSearchResult.getRowCount(); i++) {
            jTSearchResult.setValueAt(false, i, 0);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        try {
            int num = 0;
            String numStr = JOptionPane.showInputDialog(null, "当前最大线程数" + MyConfiguration.getDownloadThread_Count() + "\n请输入单个文件线程下载数（1-5）");
            if (numStr == null) {
                return;
            }
            num = Integer.parseInt(numStr);
            if (num < 1 || num > 5) {
                throw new NumberFormatException();
            }
            MyConfiguration.setDownloadThread_Count(num);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "输入格式错误，或不在指定范围内");
        } catch (Exception ex) {
            System.out.println("Cancel the operation");
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    //public static void notice() {
    //controller.notice();
    //}
    private void initTableView() {
        DefaultTableModel rows = (DefaultTableModel) jTTransmit.getModel();
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel control;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JPanel directoryPanel;
    private javax.swing.JPanel input;
    public javax.swing.JTextField inputField;
    public javax.swing.JButton jBDisDownload;
    public javax.swing.JButton jBDisDownloadAll;
    public javax.swing.JButton jBDisShareAll;
    public javax.swing.JButton jBDisShareSelected;
    public javax.swing.JButton jBSearch;
    public javax.swing.JButton jBShareSelected;
    public javax.swing.JButton jBShareSelectedAll;
    public javax.swing.JButton jBdownload;
    private javax.swing.JButton jBtClear;
    public javax.swing.JButton jBtClearList;
    private javax.swing.JButton jBtDownload;
    public javax.swing.JButton jBtGetList;
    public javax.swing.JButton jBtRefresh;
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton2;
    public javax.swing.JButton jButton3;
    public javax.swing.JButton jButton4;
    public javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    public javax.swing.JButton jButtonSelectRun;
    public javax.swing.JLabel jLState;
    private javax.swing.JLabel jLUser;
    private javax.swing.JLabel jLUser1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    public javax.swing.JTable jTAlreadyDownload;
    public javax.swing.JTable jTAlreadyShared;
    public javax.swing.JTable jTDetail;
    public javax.swing.JTable jTSearchResult;
    public javax.swing.JTable jTSelectedFileList;
    public static javax.swing.JTable jTTransmit;
    public javax.swing.JTree jTrContent;
    private javax.swing.JPanel jpNet;
    private javax.swing.JPanel jpSearch;
    private javax.swing.JPanel jpShare;
    private javax.swing.JPanel jpalreadydownload;
    private javax.swing.JPanel jpalreadyshared;
    private javax.swing.JPanel jptransmit;
    public javax.swing.JTabbedPane jtbWorkingSpace;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    public javax.swing.JComboBox netMode;
    public javax.swing.JList peerList;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel result;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel transmitBoard;
    private javax.swing.JPanel transmitBoard1;
    private javax.swing.JPanel transmitBoard2;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    public MyPlatform platform;
    public AllFileListClient fileListClient;
    public HashMap<String, FileInfo> remoteFileList;
    public ThreadPoolManager threadManager;
    public HashMap<String, Integer> threadNumMap;
    public static Controller controller;
}
