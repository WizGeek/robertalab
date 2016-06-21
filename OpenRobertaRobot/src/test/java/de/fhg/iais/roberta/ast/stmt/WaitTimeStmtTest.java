package de.fhg.iais.roberta.ast.stmt;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.testutil.Helper;

public class WaitTimeStmtTest {

    @Test
    public void test() throws Exception {
        String a = "BlockAST [project=[[Location [x=75, y=116], WaitTimeStmt [time=NumConst [500]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/wait_time_stmt.xml"));
    }

    @Test
    public void reverseTransformation() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/wait_time_stmt.xml");
    }

}
