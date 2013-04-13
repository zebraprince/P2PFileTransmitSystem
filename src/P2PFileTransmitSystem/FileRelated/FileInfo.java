/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package P2PFileTransmitSystem.FileRelated;

import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author Administrator
 */
public class FileInfo implements Serializable {

    public FileInfo() {
        this.name = "";
        this.size = 0;
        this.path = "";
        this.type = "";
        this.ID = new HashSet<String>();
    }

    public FileInfo(String name, long size, String type, String path, HashSet ID,String identifier,String peerID) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.path = path;
        this.ID = ID;
        this.identifier=identifier;
        this.peerID=peerID;
    }

    public HashSet getID() {
        return ID;
    }

    public void setID(HashSet ID) {
        this.ID = ID;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String name;
    private long size;
    private String path;
    private String type;
    private HashSet<String> ID;
    private String peerID;
    private String identifier;
    private String currentID;

    public String getCurrentID() {
        return currentID;
    }

    public void setCurrentID(String currentID) {
        this.currentID = currentID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPeerID() {
        return peerID;
    }

    public void setPeerID(String peerID) {
        this.peerID = peerID;
    }

}
