/**
 * XBeeAddress.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xbeelogger;

import com.rapplogic.xbee.api.XBeeAddress64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A human-readable representation of a 64-bit XBee Address.  With methods for
 * converting to and from com.rapplogic.xbee.api.XBeeAddress64.
 * @author Andrew Bythell <abythell@ieee.org>
 */
public class XBeeAddress {
    
    private final String address;   
           
    /**
     * Constructor.
     * @param addr 64-bit XBee Address, formatted as a string of 16 hexadecimal digits.
     */
    public XBeeAddress(String addr) {
        Pattern pattern = Pattern.compile("[0-9a-fA-F]{8}");
        Matcher matcher = pattern.matcher(addr);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid address.");
        }
        address = addr;
    }
    
    /**
     * Constructor.
     * @param addr 64-bit XBee Address using the com.rapplogic.xbee.api format.
     */
    public XBeeAddress(XBeeAddress64 addr) {
        address = XBeeAddress.toXBeeAddress(addr);
    }
    
    
    /**
     * Convert an XBeeAddress64 to a formatted string.
     * @param address64 XBeeAddress64 format.
     * @return String of 16 hexadecimal digits.
     */
    public static final String toXBeeAddress(XBeeAddress64 address64) {        
        String addr = address64.toString().replaceAll("0x|[,|\\s+]","");
        return addr.toUpperCase();
    }
    
    /**
     * Convert a formatted string to an XBeeAddress64
     * @param address String of 16 hexadecimal digits.
     * @return XBeeAddress64 object.
     */
    public static final XBeeAddress64 toXBeeAddress64(String address) {        
        String address64 = address.replaceAll("(?<=\\G.{2})", " ");
        return new XBeeAddress64(address64.trim());        
    }
    
    /**
     * Convert this object into an XBeeAddress64 object.
     * @return XBeeAddress64.
     */
    public XBeeAddress64 toXBeeAddress64() {
        return toXBeeAddress64(address);
    }
    
    /**
     * Internal.  Convert this object into a string.
     * @return String of 16 hexadecimal digits.
     */
    @Override
    public String toString() {
        return address;
    }

    /**
     * Internal. Required to use XBeeAddress as a key in HashMaps.
     * @param obj The XBeeAddress to be compared.
     * @return True if address strings are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XBeeAddress)) {
            return false;
        }
        XBeeAddress a = (XBeeAddress)obj;
        return address.equals(a.toString());
    }

    /**
     * Internal.  Required to use XBeeAddress as a key in HashMaps.
     * @return The hashcode for this object.
     */
    @Override
    public int hashCode() {
        return address.hashCode();
    }
    
    
    
}
