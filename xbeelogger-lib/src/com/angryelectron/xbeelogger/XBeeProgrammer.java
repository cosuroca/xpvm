/**
 * XBeeProgrammer.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */ 

package com.angryelectron.xbeelogger;

import com.rapplogic.xbee.api.AtCommand;
import com.rapplogic.xbee.api.AtCommandResponse;
import com.rapplogic.xbee.api.AtCommandResponse.Status;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeException;

public class XBeeProgrammer {

    private final XBee xbee = new XBee();
    private Integer TIMEOUT = 5000;
    
    public void open(String port, Integer baud) throws XBeeException {
       xbee.open(port, baud); 
    }
    
    public void close() {
        xbee.close();
    }
    
    public void setTimeout(Integer timeout) {
        this.TIMEOUT = timeout;
    }
    
    public int[] command(String command) throws XBeeException {
        AtCommand atcmd = new AtCommand(command);
        AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(atcmd, TIMEOUT);
        if (response.isError()) {
            throw new XBeeException("Could not set " + command + ".");
        }
        return response.getValue();        
    }
                
    public int[] command(String command, int value) throws XBeeException {
        AtCommand atcmd = new AtCommand(command, value);
        AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(atcmd, TIMEOUT);
        if (response.isError()) {
            throw new XBeeException("Could not set " + command + ".");
        }
        return response.getValue();
    }
    
    public int[] command(String command, int[] values) throws XBeeException {
        AtCommand atcmd = new AtCommand(command, values);
        AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(atcmd, TIMEOUT);
        if (response.isError()) {
            throw new XBeeException("Could not set " + command + ".");
            
        }
        return response.getValue();
    }
    
    public int[] command(String command, String value) throws XBeeException {        
        AtCommand atcmd;
        if (command.equals("NI")) {
            atcmd = new AtCommand(command, stringToIntArrayASCII(value));
        } else {
            atcmd = new AtCommand(command, stringtoIntArray(value));
        }
        AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(atcmd, TIMEOUT);
        if (response.isError() || response.getStatus() != Status.OK) {
            throw new XBeeException("Could not set " + command + "(" + response.getStatus() + ".");          
        }
        return response.getValue();    
    }
            
    private int[] stringToIntArrayASCII(String s) {
        int[] array = new int[s.length()];
        for (int i=0; i< s.length(); i++) {
            array[i] = (int)s.charAt(i);
        }
        return array;
    }             
    
    private int[] stringtoIntArray(String value) {        
        if (value.length() % 2 == 1) {
            value = "0" + value;
        }
        String[] strings = value.toUpperCase().split("(?<=\\G.{2})");
        int[] array = new int[strings.length];
        for (int i=0; i<strings.length; i++ ) {
            array[i] = Integer.parseInt(strings[i], 16);            
        }
        return array;
    }
        
    
}
