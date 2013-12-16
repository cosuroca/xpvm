package com.angryelectron.xpvm;

import com.angryelectron.xbeelogger.XBeeAddress;
import com.angryelectron.xbeelogger.XBeeSample;
import com.angryelectron.xbeelogger.XBeeLogReader;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Fetch XPVM node data.
 */
public class XPVM {
        
    private final XBeeLogReader sampleData = new XBeeLogReader();
            
    /**
     * Load sample data from an XBeeLogWriter file.
     * @param sampleFile The XML file containing the XPVM node data.
     * @throws FileNotFoundException If the sampleFile cannot be found.
     */
    public void loadSampleData(File sampleFile) throws FileNotFoundException {
        sampleData.loadSampleData(sampleFile);        
    }
    
    /**
     * Get sample data from an XPVM node.
     * @param address The node's address.
     * @return Node sample data.
     * @throws NoSuchFieldException if no data has been received from the node or 
     * if the data file has not been loaded.
     */
    public XPVMSample getSample(XBeeAddress address) throws NoSuchFieldException {                
        XBeeSample sample = sampleData.getSampleByAddress(address);
        return new XPVMSample(sample);        
    }
    
    /**
     * Get sample data from an XPMV node.
     * @param nodeIdentifier The XBee's NI string.
     * @return Node sample data.
     * @throws NoSuchFieldException  if no data has been received from the node or
     * if the data file has not been loaded.
     */
    public XPVMSample getSample(String nodeIdentifier) throws NoSuchFieldException {
        XBeeSample sample = sampleData.getSampleByName(nodeIdentifier);
        return new XPVMSample(sample);
    }
}
