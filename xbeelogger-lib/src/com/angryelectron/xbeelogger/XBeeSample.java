/**
 * XBeeSample.java
 * Copyright 2013, Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xbeelogger;

import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import java.util.Date;

/**
 * An object representing XBee IO sample data.  This is an enhanced version of
 * com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse, containing an easier
 * to use address format, timestamp, and node identifier in addition to all the
 * I/O sample data.
 */
public class XBeeSample {
    
    private final ZNetRxIoSampleResponse ioSample;
    private final Date timestamp;
    private String nodeIdentifier = "Unknown"; 
    private final XBeeAddress address;

    /**
     * Constructor.
     * @param xbeeIOSample Initial IOSample data.
     */
    protected XBeeSample(ZNetRxIoSampleResponse xbeeIOSample) {
        this.ioSample = xbeeIOSample;
        timestamp = new Date();
        address = new XBeeAddress(xbeeIOSample.getRemoteAddress64());
    }

    /**
     * Get IO Sample Data.  
     * @return  Analog and digital sample data, which can be further examined
     * using com.rapplogic.xbee.api.
     */
    public ZNetRxIoSampleResponse getIoSample() {
        return ioSample;
    }
    
    /**
     * Get the date and time at which this sample was recorded.
     * @return Timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get the Node Identifier of the XBee that produced this sample.  
     * Node Identifiers are human-readable strings that may be easier to work with
     * than XBee 64-bit addresses.  
     * @return Node Identifier, as programmed in the XBee's NI field.
     */
    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    protected void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }
    
    /**
     * Get the 64-bit address of the XBee that produced this sample.     
     * @return Address, in XBeeAddress format.
     */
    public XBeeAddress getAddress() {
        return address;
    }
              
}
