/**
 * TMP36.java Copyright 2013 Andrew Bythell <abythell@ieee.org>
 */
package com.angryelectron.sensors;

/**
 * Analog Devices TMP35 Low Voltage Temperature Sensor. Not compatible with
 * TMP35 or TMP37.
 *
 * {@link <a href="http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Sensors/Temp/TMP35_36_37.pdf"
 * >(Datasheet)</a>}
 */
public class TMP36 {

    /**
     * Calculate sensor temperature.
     * @param volts Sensor output voltage, in volts.
     * @return Temperature, in degrees Celsius.
     */
    public static double getTemperatureC(double volts) {
        return 100 * volts - 50;
    }

    /**
     * Calculate sensor temperature.
     * @param volts Sensor output voltage, in volts.
     * @return Temperature, in degrees Fahrenheit.
     */
    public static double getTemperatureF(double volts) {
        return (getTemperatureC(volts) - 32) * 5 / 9;
    }

}
