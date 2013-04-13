package P2PFileTransmitSystem.GUI;

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.tree.DefaultMutableTreeNode;

public class SystemTreeNode extends DefaultMutableTreeNode {

    /**
     *
     */
    protected File file; // File object for this node
    protected String name; // Name of this node
    protected boolean populated;// true if we have been populated
    protected boolean interim; // true if we are in interim state
    protected boolean isDir; // true if this is a directory

    public SystemTreeNode(File parent, String name) throws SecurityException,
            FileNotFoundException {
        this.name = name;
        // See if this node exists and whether it is a directory
        file = new File(parent, name);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + name
                    + " does not exist");
        }

        isDir = file.isDirectory();

        // Hold the File as the user object.
        setUserObject(file);
    }

    // Override isLeaf to check whether this is a directory
    public boolean isLeaf() {
        return !isDir;
    }

    // Override getAllowsChildren to check whether this is a directory
    public boolean getAllowsChildren() {
        return isDir;
    }

    // For display purposes, we return our own name
    public String toString() {
        return name;
    }

    // If we are a directory, scan our contents and populate
    // with children. In addition, populate those children
    // if the "descend" flag is true. We only descend once,
    // to avoid recursing the whole subtree.
    // Returns true if some nodes were added
    boolean populateDirectories(boolean descend) {
        boolean addedNodes = false;

        // Do this only once
        if (populated == false) {
            if (interim == true) {
                // We have had a quick look here before:
                // remove the dummy node that we added last time
                removeAllChildren();
                interim = false;
            }

            String[] names = file.list(); // Get list of contents

            // Process the directories
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                File d = new File(file, name);
                try {
                    if (d.isDirectory()) {
                        SystemTreeNode node = new SystemTreeNode(file, name);
                        this.add(node);
                        if (descend) {
                            node.populateDirectories(false);
                        }
                        addedNodes = true;
                        if (descend == false) {
                            // Only add one node if not descending
                            break;
                        }
                    } /*else {
                    SystemTreeNode node = new SystemTreeNode(file, name);
                    this.add(node);
                    }*/
                } catch (Throwable t) {
                    // Ignore phantoms or access problems
                }
            }

            // If we were scanning to get all subdirectories,
            // or if we found no subdirectories, there is no
            // reason to look at this directory again, so
            // set populated to true. Otherwise, we set interim
            // so that we look again in the future if we need to
            if (descend == true || addedNodes == false) {
                populated = true;
            } else {
                // Just set interim state
                interim = true;
            }
        }
        return addedNodes;
    }
}
