/**
 * XPVMWriter.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */

package com.angryelectron.xpvm.writer;

import com.angryelectron.xbeelogger.XBeeLogWriter;
import com.rapplogic.xbee.api.XBeeException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Listen for and log data sent from XPVM nodes.  This demonstrates the simple
 * use of the com.angryelectron.xbeelogger.XBeeLogWriter class, combined with some
 * basic command line handling.
 */
public class XPVMWriter {

    static final String version = "1.0";
    static final String progname = "xpvm-writer";

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Logger.getLogger(XPVMWriter.class.getName()).log(Level.INFO, "Stopping.");                
            }
        });
        
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        
        /**
         * The default baud rate to use if none specified on the command line.
         */
        Integer baud = 9600;

        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException ex) {
            showHelp();
            System.exit(-1);
        }

        if (cmd.hasOption("h")) {
            showHelp();
            System.exit(0);
        }

        /**
         * Override the default baud rate.
         */
        if (cmd.hasOption("b")) {
            baud = Integer.parseInt(cmd.getOptionValue("b"));
        }

        /**
         * Start the logger using the given port and filename.
         */
        if (cmd.hasOption("p") && cmd.hasOption("f")) {
            XBeeLogWriter writer = new XBeeLogWriter(
                    new File(cmd.getOptionValue("f")),
                    cmd.getOptionValue("p"),
                    baud);
            Logger.getLogger(XPVMWriter.class.getName()).log(Level.INFO, 
                    "Starting " + progname + " verion " + version + ".");
            try {
                writer.start();
            } catch (XBeeException | FileNotFoundException ex) {
                Logger.getLogger(XPVMWriter.class.getName()).log(Level.ERROR, ex.getMessage());
                System.exit(-1);
            }
        } else {
            showHelp();
            System.exit(-1);
        }

    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("p", "port", true, "XBee serial port.");
        options.addOption("b", "baud", true, "Baud rate.");
        options.addOption("f", "file", true, "Log file name.");
        options.addOption("h", "help", false, "Show help.");
        return options;
    }

    private static void showHelp() {
        System.out.println(progname + " version " + version);
        System.out.println("(C)2013 Andrew Bythell <abythell@ieee.org>");
        System.out.println("Listen and log data from XPVM nodes.\n");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(progname, getOptions());
    }
}
