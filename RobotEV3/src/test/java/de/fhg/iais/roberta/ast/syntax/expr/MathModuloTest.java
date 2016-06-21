package de.fhg.iais.roberta.ast.syntax.expr;

import org.junit.Test;

import de.fhg.iais.roberta.testutil.ev3.Helper;

public class MathModuloTest {
    @Test
    public void Test() throws Exception {
        String a = "floatvariablenName=1%0;publicvoidrun()throwsException{}";

        Helper.assertCodeIsOk(a, "/syntax/math/math_modulo.xml");
    }

}
