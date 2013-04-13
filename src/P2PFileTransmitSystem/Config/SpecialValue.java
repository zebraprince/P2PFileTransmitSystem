/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package P2PFileTransmitSystem.Config;

/**
 *
 * @author Maxwell
 */
public interface SpecialValue {

    public static final String Local_Network_Manager_Name = "My Local Network Manager";
    public static final String Group_Name = "FileTransSystemGroup";
    public static final String File_Trans_ADV_FILE = "FileTrans.adv";
    public static final String File_List_ADV_FILE = "FileList.adv";
    public static final String Index_Search_ADV_FILE="indexSearch.adv";
    public static final String Config_FILE="Configuration.dat";
    public static final String FILE_TRANSMIT_ADV_NAME = "FileTransSystemSocketAdvertisement";
    public static final String FILE_LIST_ADV_NAME = "AllFileListAdvertisement";
    public static final String INDEX_SEARCH_ADV_NAME="IndexSearchAdvertisement";
    public static final String INCOMING_PATH="incoming\\";
    public static final String SHARING_PATH=".jxta\\userdata\\";
    public static final String DOWNLOADED_PATH=".jxta\\userdata\\";
    public static final String CONFIG_PATH=".jxta\\userdata\\";
    public static final String ADV_PATH=".jxta\\adv\\";
    public static final String CM_PATH=".jxta\\cm\\";
    public static final long PEER_LOCAL_TIME=1*60*1000;
    public static final long PEER_REMOTE_TIME=1*60*1000;
    public static final long PIPE_LOCAL_TIME=30*60*1000;
    public static final long PIPE_REMOTE_TIME=30*60*1000;
    
}
