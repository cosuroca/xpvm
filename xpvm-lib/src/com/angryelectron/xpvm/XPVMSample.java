/**
 * XPVMSample.java
 * Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.xpvm;

import com.angryelectron.sensors.AttoPilot;
import com.angryelectron.sensors.TMP36;
import com.angryelectron.xbeelogger.XBeeAddress;
import com.angryelectron.xbeelogger.XBeeSample;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import java.util.Date;

/**
 * I/O sample data received from an XPVM Node.  Includes
 * hardware definitions and methods for reading values from various sensors.
 */
public class XPVMSample {

    private final XBeeSample xbeeSample;

    /**
     * Select the AttoPilot Model.
     */    
    private final AttoPilot attoPilot = new AttoPilot(AttoPilot.Model.A180);
    
    /**
     * Select which sensors are attached to which XBee IO pins.  Also determines
     * the ADC's reference voltage and resolution.
     */
    private enum XBeeIO {

        TMP36(0), /* temperature sensor A0 */
        PVV(1), /* AttoPilot1, pin V, A1 */
        PVA(2), /* AttoPilot1, pin I, A2 */
        BV(3), /* AttoPilot2, pin V, A3 */
        BA(4); /* AttoPilot2, pin I, A4 */

        private final Integer ioPin;
        private final Double vRef = 3.3;
        private final Integer resolution = 1023;

        private XBeeIO(Integer ioPin) {
            this.ioPin = ioPin;
        }

        double voltage(XBeeSample sample) {
            ZNetRxIoSampleResponse ioSample = sample.getIoSample();
            if (ioSample.isAnalogEnabled(ioPin)) {
                return (ioSample.getAnalog(ioPin) * vRef) / resolution;            
            }
            throw new UnsupportedOperationException("AD" + ioPin + 
                    " on XBee " + sample.getAddress() + " is not configured as an analog output.");
        }
    }
    
    /**
     * Constructor.
     * @param xbeeSample initial sample data.
     */
    public XPVMSample(XBeeSample xbeeSample) {
        this.xbeeSample = xbeeSample;
    }

    /**
     * Get timestamp.
     * @return The date and time at which this sample was received at the hub.
     */
    public Date getTimeStamp() {
        return xbeeSample.getTimestamp();
    }
   
    /**
     * Get Node Address.
     * @return The address of the node that generated this sample.
     */
    public XBeeAddress getAddress() {
        return xbeeSample.getAddress();
    }

    /**
     * Get Node Name.
     * @return The Node Identifier (NI) of the node that generated this sample.
     */
    public String getName() {
        return xbeeSample.getNodeIdentifier();
    }

    /**
     * Get Temperature (C).
     * @return Node ambient temperature, in degrees Celsius.
     */
    public Double getTemperatureC() {        
        return TMP36.getTemperatureC(XBeeIO.TMP36.voltage(xbeeSample));
    }
    
    /**
     * Get Temperature (F).
     * @return Node ambient temperature, in degrees Fahrenheit.
     */
    public Double getTemperatureF() {
        return TMP36.getTemperatureF(XBeeIO.TMP36.voltage(xbeeSample));
    }
    
    /**
     * Get PV array voltage.
     * @return Voltage (V).
     */
    public Double getArrayVoltage() {        
        Double voltage = XBeeIO.PVV.voltage(xbeeSample);
        return attoPilot.getVoltage(voltage);
    }

    /**
     * Get PV array current.
     * @return Current (A).
     */
    public Double getArrayCurrent() {
        Double voltage = XBeeIO.PVA.voltage(xbeeSample);
        return attoPilot.getCurrent(voltage);
    }

    /**
     * Get PV battery bank voltage.
     * @return Voltage (V).
     */
    public Double getBatteryVoltage() {
        Double voltage = XBeeIO.BV.voltage(xbeeSample);
        return attoPilot.getVoltage(voltage);

    }

    /**
     * Get PV battery bank current.
     * @return Current (A).
     */
    public Double getBatteryCurrent() {
        Double voltage = XBeeIO.BA.voltage(xbeeSample);
        return attoPilot.getCurrent(voltage);
    }
}
