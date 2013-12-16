/**
 * XBeeLogger.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xbeelogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Read log data from an XBeeLogWriter log file. 
 */
public class XBeeLogReader {

    private HashMap<XBeeAddress, XBeeSample> data;
    
    /**
     * Load data from file.  Data is static once loaded, so call this each time
     * new data is required.
     * @param dataFile The file, created by {@link XBeeLogWriter}, to read.
     * @throws FileNotFoundException if the file cannot be opened.
     */
    public void loadSampleData(File dataFile) throws FileNotFoundException {
        XBeeSampleStream xml = new XBeeSampleStream(dataFile);
        data = xml.read();
    }
    
    /**
     * Get XBee IO sample data.
     * @param address XBee 64-bit address.
     * @return The data from the XBee at the given address.
     * @throws NoSuchFieldException If no data has been received from an XBee with 
     * this address, or if the data has not been loaded.
     */
    public XBeeSample getSampleByAddress(XBeeAddress address) throws NoSuchFieldException {
        if (!data.containsKey(address)) {
            throw new NoSuchFieldException("No samples were found for that address.");
        }
        return data.get(address);
    }
    
    /**
     * Get XBee IO sample data.
     * @param nodeIdentifier XBee 'NI' string.
     * @return The data from the XBee with the specified node identifier.
     * @throws NoSuchFieldException If no data has been received from an XBee with
     * this node identifier, or if the data has not been loaded.
     */
    public XBeeSample getSampleByName(String nodeIdentifier) throws NoSuchFieldException {
        for (Map.Entry<XBeeAddress, XBeeSample> entry: data.entrySet()) {
            XBeeSample sample = entry.getValue();
            if (sample.getNodeIdentifier().equals(nodeIdentifier)) {
                return sample;
            }
        }
        throw new NoSuchFieldException("No samples were found for that address.");
    }
}
