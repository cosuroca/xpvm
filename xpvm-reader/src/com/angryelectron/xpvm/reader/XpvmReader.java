package com.angryelectron.xpvm.reader;

import com.angryelectron.xpvm.XPVM;
import com.angryelectron.xpvm.XPVMSample;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

public class XpvmReader {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.lang.NoSuchFieldException
     */
    public static void main(String[] args) throws FileNotFoundException, NoSuchFieldException {

        XPVM xpvm = new XPVM();
        File logfile = new File("/tmp/xpvm.xml");

        /**
         * Load the latest sample data from a file created by the logger daemon.
         */
        try {
            xpvm.loadSampleData(logfile);
        } catch (FileNotFoundException ex) {
            System.out.println("Logfile " + logfile + " not found.");
            System.exit(-1);
        }

        /**
         * Get data from a Node called "THERMOMETER".
         */
        XPVMSample sample = xpvm.getSample("THERMOMETER");

        /**
         * Display some data.
         */
        Date expires = new Date(System.currentTimeMillis() - 60000);
        if (expires.after(sample.getTimeStamp())) {
            /**
             * Alert - no data received for 1 minute or more.
             */
            System.out.println("No samples received in last 60 seconds.  Is logger daemon running?");
        } else {
            /**
             * Print some node data.
             */
            System.out.println("Address: " + sample.getAddress());
            System.out.println("Timestamp: " + sample.getTimeStamp());
            System.out.println("Current Temperature: " + sample.getTemperatureC());
        }
    }

}
