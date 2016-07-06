package de.fhg.iais.roberta.syntax.expr;

import org.junit.Test;

import de.fhg.iais.roberta.testutil.Helper;

public class MathConstrainTest {
    @Test
    public void Test() throws Exception {
        String a = "BlocklyMethods.clamp(hal.getUltraSonicSensorDistance(SensorPort.S4),1,100)";

        Helper.assertCodeIsOk(a, "/syntax/math/math_constrain.xml");
    }
}
