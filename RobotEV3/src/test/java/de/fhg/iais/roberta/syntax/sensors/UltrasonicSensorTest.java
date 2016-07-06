package de.fhg.iais.roberta.syntax.sensors;

import org.junit.Test;

import de.fhg.iais.roberta.testutil.Helper;

public class UltrasonicSensorTest {
    @Test
    public void setUltrasonic() throws Exception {
        String a = "\nhal.getUltraSonicSensorDistance(SensorPort.S4)" + "hal.getUltraSonicSensorPresence(SensorPort.S2)";

        Helper.assertCodeIsOk(a, "/syntax/sensors/sensor_setUltrasonic.xml");
    }
}
