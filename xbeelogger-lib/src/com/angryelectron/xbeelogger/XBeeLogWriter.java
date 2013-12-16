/**
 * XBeeLogWriter.java 
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */ 

package com.angryelectron.xbeelogger;

import com.rapplogic.xbee.api.XBeeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Continuously write I/O Sample data received from an XBee Series 2 mesh network to an 
 * XML file.
 */
public class XBeeLogWriter implements Observer {
            
        private final File logFile;
        private final String port;
        private final Integer baud;
        
        private final XBeeSampleListener listener = new XBeeSampleListener();  
        private XBeeSampleStream xml;
        
        /**
         * Constructor.
         * @param logfile Full path of XML file to write.
         * @param port XBee Serial Port.  eg. /dev/ttyUSB0, COM6.
         * @param baud Typically 9600.  Depends on XBee configuration.
         */
        public XBeeLogWriter(File logfile, String port, Integer baud) {
            this.logFile = logfile;
            this.port = port;
            this.baud = baud;
        }
                                
        /**
         * Start logging I/O samples.  All other XBee packet types are ignored.
         * @throws XBeeException if XBee cannot be opened.
         * @throws FileNotFoundException if log file cannot be written.
         */
        public void start() throws XBeeException, FileNotFoundException {
            xml = new XBeeSampleStream(logFile);
            listener.register(this);
            listener.start(port, baud);            
        }
        
        /**
         * Stop logging I/O samples.
         */
        public void stop() {
            listener.stop();
            listener.unregister(this);            
        }
                                
        /**
         * Internal.  Write data to file when new I/O samples are received from
         * the network.
         * @param o 
         * @param arg 
         */
        @Override
        public void update(Observable o, Object arg) {            
            try {

                xml.write(listener.getSamples());                
            } catch (IOException ex) {
                Logger.getLogger(XBeeLogWriter.class.getName()).log(Level.ERROR, null, ex);
            }
        }
}
