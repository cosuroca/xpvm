package com.angryelectron.sensors;

/**
 * AttoPilot 45/90/180 Voltage & Current Sensor.  Converts AttoPilot outputs to actual current and voltage.
 * {@link <a href="http://dlnmh9ip6v2uc.cloudfront.net/datasheets/Sensors/Current/DC%20Voltage%20and%20Current%20Sense%20PCB%20with%20Analog%20Output.pdf">
 * (Datasheet)</a>}
 */
public class AttoPilot {

    private final Model model;

    /**
     * AttoPilot Model. Different models have different values for scaling the
     * voltage and current outputs. The scaling values for each model are taken
     * from the datasheet as Model(mv/V, mv/A).
     */
    public enum Model {

        /**
         * AttoPilot 13.6V, 45A Model.
         */
        A45(242.3, 73.2),
        
        /**
         * AttoPilot 50V, 90A Model.
         */
        A90(63.69, 36.60),
        
        /**
         * AttoPilot 50V, 180A Model.
         */
        A180(63.69, 18.30);

        private final double vScale;
        private final double iScale;

        private Model(double vScale, double iScale) {
            this.vScale = vScale;
            this.iScale = iScale;
        }

        /**
         * Get the current scaling value.
         * @return Scaling value.
         */
        double iScale() {
            return iScale;
        }

        /**
         * Get the voltage scaling value.
         * @return Scaling value.
         */
        double vScale() {
            return vScale;
        }
    }

    /**
     * Constructor.
     * @param model The AttoPilot model.
     */
    public AttoPilot(Model model) {
        this.model = model;
    }
            
    /**
     * Calculate the true voltage from the scaled output of the AttoPilot.
     * @param V Voltage (in volts) measured at the AttoPilot's "V" pin.     
     * @return Actual voltage, in volts.
     */
    public Double getVoltage(double V) {
        return V * model.vScale();
    }

    /**
     * Calculate the true current from the scaled output of the AttoPilot.
     * @param I Voltage (in volts) measured at the AttoPilot's "I" pin.     
     * @return Actual current, in amps.
     */
    public Double getCurrent(double I) {
        return I * model.iScale();
    }
}
