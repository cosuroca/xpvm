/**
 * XBeeSampleListener.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */

package com.angryelectron.xbeelogger;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Receives, maintains, and queries a list of IO samples received from a 
 * network of XBee Series 2 radios.
 */
class XBeeSampleListener extends Observable {
        
    private final XBee xbee = new XBee();    
    
    /**
     * This map holds the latest sample data from all XBees on the network.
     * One entry per node, indexed by node address.
     */
    private HashMap<XBeeAddress, XBeeSample> sampleList = new HashMap<>();
   
    /**
     * Process incoming IO Samples.  The sampleList is updated with new data
     * as it arrives.
     */
    private final PacketListener ioPacketListener = new PacketListener() {
        @Override
        public void processResponse(XBeeResponse response) {
            if (response.getApiId() == ApiId.ZNET_IO_SAMPLE_RESPONSE) {
                ZNetRxIoSampleResponse xbeeIOSample = (ZNetRxIoSampleResponse) response;                  
                XBeeSample sample = new XBeeSample(xbeeIOSample);
                if (sampleList.containsKey(sample.getAddress())) {
                    /* re-use the Node Identifier */
                    String ni = sampleList.get(sample.getAddress()).getNodeIdentifier();
                    sample.setNodeIdentifier(ni);
                    sampleList.put(sample.getAddress(), sample);                     
                } else {
                    /* This XBee is being seen for the first time, so we must
                     * request the node identifer.
                     */
                    sampleList.put(sample.getAddress(), sample);                     
                    sendNIRequest(sample.getAddress().toXBeeAddress64());
                }      
                setChanged();
                notifyObservers(sample);
            }
        }        
    };   
    
    /**
     * Process incoming responses to Node-identifier requests.  Updates
     * the Node Identifier field of an existing XBeeSample and ignore everything
     * else.
     */
    private final PacketListener niPacketListener;

    public XBeeSampleListener() {
        this.niPacketListener = new PacketListener() {
            @Override
            public void processResponse(XBeeResponse response) {
                if (response.getApiId() == ApiId.REMOTE_AT_RESPONSE) {
                    RemoteAtResponse atResponse = (RemoteAtResponse) response;
                    if (atResponse.getCommand().equals("NI")) {
                        XBeeAddress address = new XBeeAddress(atResponse.getRemoteAddress64());
                        if (sampleList.containsKey(address)) {
                            XBeeSample sample = sampleList.get(address);
                            sample.setNodeIdentifier(intArrayToString(atResponse.getValue()));
                            sampleList.put(sample.getAddress(), sample);
                        }
                    }
                }
            }
        };
    }
    
    /**
     * Convert int[] into String.  The node identifier in a REMOTE_AT_RESPONSE
     * is returned as int[], but String is preferable.
     * @param value Integer array containing ASCII values.
     * @return Node Identifier string.
     */
    private String intArrayToString(int[] value) {        
        if ((value == null) || (value.length == 0)) {
            return "";
        }
        char[] result = new char[value.length];
        for (int i=0; i< value.length; i++) {
            result[i] = (char) value[i];
        }
        return new String(result);
    }
    
    
    /**
     * Request the Node-Identifier field from a remote XBee.  The results are
     * returned asynchronously and processed by the niPacketListener.
     * @param address The remote XBee's 64-bit address.
     */
    private void sendNIRequest(XBeeAddress64 address) {
        RemoteAtRequest request = new RemoteAtRequest(address, "NI");
        try {
            xbee.sendAsynchronous(request);
        } catch (XBeeException ex) {            
            Logger.getLogger(XBeeSampleListener.class.getName()).log(Level.ERROR, null, ex);
        }
    }
                 
    /**
     * Start listening for incoming samples using an XBee attached to the specified
     * port.
     * @param port XBee serial port (ie. /dev/ttyUSB0, COM6, etc.).
     * @param baudRate Typically 9600, but this depends on how the local XBee
     * is programmed.
     * @throws XBeeException if the XBee cannot be opened.  May be caused by
     * permissions, wiring, or an incorrect port.
     */
    void start(String port, int baudRate) throws XBeeException {
        xbee.open(port, baudRate);
        xbee.addPacketListener(ioPacketListener);
        xbee.addPacketListener(niPacketListener);               
    }
    
    /**
     * Stop listening for incoming samples.  The XBee API automatically handles
     * clean-up using a shutdown hook, so it is not necessary to call this function
     * when shutting down.
     */
    void stop() {        
        /* the api automatically removes any packet listeners when closing */
        xbee.close();
    }
                    
    /**
     * Register an observer.  By implementing Observer, a custom class can be 
     * notified whenever sample data is updated.  The object passed as the 
     * Observer.update() argument represents the new sample data, and should be
     * cast as an XBeeSample.
     * @param o An object which implements Observer and wished to be notified
     * whenever new sample data is recorded.
     */
    void register(Observer o) {
        this.addObserver(o);
    }
    
    /**
     * Unregister an observer.  Stops a custom class from receiving notifications
     * about updated sample data.
     * @param o An object which implements Observer that no longer wishes to be
     * notified whenever new sample data is recorded.
     */
    void unregister(Observer o) {
        this.deleteObserver(o);
    }
    
    /**
     * Get a HashMap of sample data, indexed by 64-bit address.  
     * @return A read-only Hashmap.
     */
    HashMap<XBeeAddress, XBeeSample> getSamples() {
        HashMap<XBeeAddress, XBeeSample> tempMap = new HashMap<>(sampleList);
        return tempMap;
    }
}
