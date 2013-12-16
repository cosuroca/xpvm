/**
 * XPVMProgrammer.java Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xpvm.programmer;

import com.angryelectron.xbeelogger.XBeeProgrammer;
import com.rapplogic.xbee.api.XBeeException;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Program an XBee radio for use in an XPVM node.
 */
public class XPVMProgrammer {

    static final String version = "1.0";
    static final String progname = "xpvm-programmer";
    static Integer baud = 9600;
    static CommandLine cmd = null;

    public static void main(String[] args) {

        CommandLineParser parser = new PosixParser();
               
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException ex) {
            showHelp();
            System.exit(-1);
        }

        /**
         * Show help if required arguments are not specified.
         */
        if (cmd.hasOption("h") || !cmd.hasOption("p") || !cmd.hasOption("i") || !cmd.hasOption("n")) {
            showHelp();
            System.exit(0);
        }

        /**
         * Override the default baud rate if specified.
         */
        if (cmd.hasOption("b")) {
            baud = Integer.parseInt(cmd.getOptionValue("b"));
        }
        
        /** 
         * Validate other arguments.
         */
        if (!verifyPan()) {
            System.out.println("Invalid Network/Pan ID (0-7FFF).");
            System.exit(-1);
        }      
        
        if (!verifySampleRate()) {
            System.out.println("Invalid Sample Rate (0-FFFF)");
            System.exit(-1);
        }
        try {            
            programXBees();       
        } catch (XBeeException ex) {
            System.out.println(ex.getMessage());
            System.exit(-1);
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("p", "port", true, "XBee serial port.");
        options.addOption("b", "baud", true, "Baud rate (optional).");
        options.addOption("h", "help", false, "Show help.");
        options.addOption("w", "write", false, "Write (finalize) settings in firmware.");
        options.addOption("i", "id", true, "Network/Pan ID (0-7FFF).");
        options.addOption("n", "name", true, "Node name.");
        options.addOption("r", "rate", true, "Sample rate in ms (0-FFFF)");
        return options;
    }

    private static void showHelp() {
        System.out.println(progname + " version " + version);
        System.out.println("(C)2013 Andrew Bythell <abythell@ieee.org>");
        System.out.println("Program XBee radios for use in XPVM nodes.\n");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(progname, getOptions());
    }

    private static void programXBees() throws XBeeException {
        XBeeProgrammer programmer = new XBeeProgrammer();
        
            programmer.open(cmd.getOptionValue("p"), baud);
            programmer.command("AP", 2);

            int[] hwVersion = programmer.command("HV");
            int[] hwFirmware = programmer.command("VR");

            System.out.println("hardware: " + Integer.toHexString(hwVersion[0]) + Integer.toHexString(hwVersion[1]));
            System.out.println("firmware: " + Integer.toHexString(hwFirmware[0]) + Integer.toHexString(hwFirmware[1]));

            programmer.command("ID", cmd.getOptionValue("i"));
            programmer.command("NI", cmd.getOptionValue("n"));
            programmer.command("D0", 2);
            programmer.command("D1", 2);
            programmer.command("D2", 2);
            programmer.command("D3", 2);
            programmer.command("D4", 2);
            programmer.command("PR", 0);
            programmer.command("IR", cmd.getOptionValue("r"));

            if (cmd.hasOption("w")) {
                programmer.command("WR");
            }
            programmer.close();        
    }
    
    private static boolean verifyPan() {        
        try {
            Integer pan = Integer.parseInt(cmd.getOptionValue("i"), 16);
            if (pan < 0 || pan > 0x7FFF) {
                return false;
            }
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
    
    private static boolean verifySampleRate() {                
        try {
            Integer sampleRate = Integer.parseInt(cmd.getOptionValue("r"), 16);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
