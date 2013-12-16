/**
 * XBeeSampleStream.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xbeelogger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Serialize XBee I/O data to/from XML file.
 */
class XBeeSampleStream {
    
    private final XStream xstream;    
    private final File dataFile;
    
    /**
     * Create a new XML serializer.
     * @param file Full path of an XML file for reading/writing.  If file does
     * not exist it will be created when written.     
     */
    XBeeSampleStream(File file) {
        xstream = new XStream(new StaxDriver());          
        this.dataFile = file;
    }
            
    /**
     * Read XML data from disk.
     * @return XBee sample data HashMap, indexed by XBee address.
     */
    HashMap<XBeeAddress, XBeeSample> read() throws FileNotFoundException {
        HashMap<XBeeAddress, XBeeSample> map = new HashMap<>();
        try {
            Object fromXML = xstream.fromXML(dataFile, map);
            return (HashMap<XBeeAddress, XBeeSample>) fromXML;
        } catch (StreamException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }
    
    /**
     * Write XBee sample data to disk.
     * @param data The XBee I/O Hashmap to be written.
     * @throws IOException If the XML file cannot be written.
     */
    void write(HashMap<XBeeAddress, XBeeSample> data) throws IOException {                        
            xstream.marshal(data, new PrettyPrintWriter(new FileWriter(dataFile, false)));                    
    }        
}
