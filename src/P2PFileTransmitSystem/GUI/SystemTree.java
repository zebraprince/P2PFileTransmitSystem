/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Administrator
 */
public class SystemTree extends JTree {

    public SystemTree() {

        super((TreeModel) null);
        File[] roots = File.listRoots();
        // Use horizontal and vertical lines
        putClientProperty("JTree.lineStyle", "Angled");
        // Create the first node
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("所有目录");
        for (int i = roots.length; i > 0; i--) {
            if (roots[roots.length - i].canRead()) {
                try {
                    SystemTreeNode driver = new SystemTreeNode(null, roots[roots.length - i].getPath());
                    boolean addedNodes = driver.populateDirectories(true);
                    rootNode.add(driver);
                } catch (SecurityException ex) {
                    Logger.getLogger(SystemTree.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SystemTree.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        setModel(new DefaultTreeModel(rootNode));
        addTreeExpansionListener(new TreeExpansionHandler());

    }

    protected class TreeExpansionHandler implements TreeExpansionListener {

        public void treeExpanded(TreeExpansionEvent evt) {
            TreePath path = evt.getPath(); // The expanded path
            JTree tree = (JTree) evt.getSource(); // The tree

            // Get the last component of the path and
            // arrange to have it fully populated.
            DefaultMutableTreeNode node=(DefaultMutableTreeNode)path.getLastPathComponent();
            if(node.isRoot())
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
            //SystemTreeNode node = (SystemTreeNode) path.getLastPathComponent();
            //if (node.populateDirectories(true)) {
            else{
                SystemTreeNode nodes=(SystemTreeNode) path.getLastPathComponent();
                if(nodes.populateDirectories(true))
                    ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(node);
            }
        }

        public void treeCollapsed(TreeExpansionEvent evt) {
            // Nothing to do
        }
    }
}
