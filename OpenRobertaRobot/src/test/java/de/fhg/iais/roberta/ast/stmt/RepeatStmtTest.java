package de.fhg.iais.roberta.ast.stmt;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.syntax.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.stmt.RepeatStmt.Mode;
import de.fhg.iais.roberta.testutil.Helper;
import de.fhg.iais.roberta.transformer.Jaxb2BlocklyProgramTransformer;
import de.fhg.iais.roberta.util.dbc.DbcException;

public class RepeatStmtTest {

    @Test
    public void repeatStmt() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=33, y=-573], \n"
                + "(repeat [TIMES, Var [k0], NumConst [0], NumConst [10], NumConst [1]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item3], StringConst [Proba]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item3], StringConst [Proba1]]\n"
                + "(repeat [TIMES, Var [k1], NumConst [0], NumConst [10], NumConst [1]]\n"
                + ")\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt.xml"));
    }

    @Test
    public void getMode() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/control/repeat_stmt.xml");

        RepeatStmt<Void> repeatStmt = (RepeatStmt<Void>) transformer.getTree().get(0).get(1);

        Assert.assertEquals(Mode.TIMES, repeatStmt.getMode());
    }

    @Test
    public void getExpr() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/control/repeat_stmt.xml");

        RepeatStmt<Void> repeatStmt = (RepeatStmt<Void>) transformer.getTree().get(0).get(1);

        Assert.assertEquals("Var [k0], NumConst [0], NumConst [10], NumConst [1]", repeatStmt.getExpr().toString());
    }

    @Test
    public void getList() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/control/repeat_stmt.xml");

        RepeatStmt<Void> repeatStmt = (RepeatStmt<Void>) transformer.getTree().get(0).get(1);

        String a =
            "\nexprStmt Binary [TEXT_APPEND, Var [item3], StringConst [Proba]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item3], StringConst [Proba1]]\n"
                + "(repeat [TIMES, Var [k1], NumConst [0], NumConst [10], NumConst [1]]\n"
                + ")";

        Assert.assertEquals(a, repeatStmt.getList().toString());
    }

    @Test
    public void repeatStmt1() throws Exception {
        String a = "BlockAST [project=[[Location [x=-93, y=1], \n" + "(repeat [TIMES, Var [k0], NumConst [0], NumConst [10], NumConst [1]]\n)]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt1.xml"));
    }

    @Test
    public void repeatStmt2() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=42, y=46], MainTask [], \n"
                + "(repeat [TIMES, Var [k0], NumConst [0], NumConst [10], NumConst [1]]\n"
                + "AktionStmt [DriveAction [FOREWARD, MotionParam [speed=NumConst [50], duration=null]]]\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt2.xml"));
    }

    @Test
    public void repeatStmtWhileUntil() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=-372, y=47], \n"
                + "(repeat [WHILE, BoolConst [true]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item], StringConst [sd]]\n"
                + "exprStmt Binary [MATH_CHANGE, Var [variablenName], NumConst [1]]\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_whileUntil.xml"));
    }

    @Test
    public void repeatStmtWhileUntil1() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=-372, y=47], \n"
                + "(repeat [WHILE, BoolConst [true]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item], StringConst [sd]]\n"
                + "exprStmt Binary [MATH_CHANGE, Var [variablenName], NumConst [1]]\n"
                + "StmtFlowCon [BREAK]\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_whileUntil1.xml"));
    }

    @Test
    public void repeatStmtWhileUntil2() throws Exception {
        String a = "BlockAST [project=[[Location [x=-93, y=101], \n" + "(repeat [WHILE, EmptyExpr [defVal=class java.lang.Boolean]]\n)]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_whileUntil2.xml"));
    }

    @Test
    public void repeatStmtFor() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=-517, y=190], \n"
                + "(repeat [FOR, Var [i], NumConst [1], NumConst [10], NumConst [1]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item], StringConst [kllk]]\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_for.xml"));
    }

    @Test
    public void repeatStmtFor1() throws Exception {
        String a = "BlockAST [project=[[Location [x=-93, y=190], \n" + "(repeat [FOR, Var [i], NumConst [1], NumConst [10], NumConst [1]]\n)]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_for1.xml"));
    }

    @Test
    public void repeatStmtForEach() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=-436, y=284], \n"
                + "(repeat [FOR_EACH, Binary [IN, VarDeclaration [STRING, j, EmptyExpr [defVal=class java.lang.Integer], false, false], EmptyList [STRING]]]\n"
                + "exprStmt Binary [TEXT_APPEND, Var [item], StringConst [gg]]\n"
                + ")]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_for_each.xml"));
    }

    @Test
    public void repeatStmtForEach1() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=-93, y=290], \n"
                + "(repeat [FOR_EACH, Binary [IN, VarDeclaration [NUMBER, i, EmptyExpr [defVal=class java.lang.Integer], false, false], EmptyExpr [defVal=class java.util.ArrayList]]]\n)]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_for_each1.xml"));
    }

    @Test
    public void repeatStmtForEach2() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=39, y=60], \n"
                + "(repeat [FOR_EACH, Binary [IN, VarDeclaration [NUMBER, j, EmptyExpr [defVal=class java.lang.Integer], false, false], ListCreate [NUMBER, EmptyExpr [defVal=class java.util.ArrayList], EmptyExpr [defVal=class java.util.ArrayList], EmptyExpr [defVal=class java.util.ArrayList]]]]\n"
                + "AktionStmt [LightAction [GREEN, ON]]\n)]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/control/repeat_stmt_for_each2.xml"));
    }

    @Test
    public void loopForever() throws Exception {
        String a =
            "BlockAST[project=[[Location[x=1,y=379],(repeat[FOREVER,BoolConst[true]]FunctionStmt[TextPrintFunct[[ColorConst[GREEN]]]]),(repeat[FOREVER,BoolConst[true]]FunctionStmt[TextPrintFunct[[EmptyExpr[defVal=classjava.lang.String]]]])]]]";

        Assert.assertEquals(a.replaceAll("\\s+", ""), Helper.generateTransformerString("/ast/control/repeat_stmt_loopForever.xml").replaceAll("\\s+", ""));
    }

    @Test(expected = DbcException.class)
    public void invalid() {
        RepeatStmt.Mode.get("");
    }

    @Test(expected = DbcException.class)
    public void invalid1() {
        RepeatStmt.Mode.get(null);
    }

    @Test(expected = DbcException.class)
    public void invalid2() {
        RepeatStmt.Mode.get("asdf");
    }

    @Test
    public void reverseTransformation() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt.xml");
    }

    @Test
    public void reverseTransformation1() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt1.xml");
    }

    @Test
    public void reverseTransformation2() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt2.xml");
    }

    @Test
    public void reverseTransformationWhileUntil() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_whileUntil.xml");
    }

    @Test
    public void reverseTransformationWhileUntil1() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_whileUntil1.xml");
    }

    @Test
    public void reverseTransformationWhileUntil2() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_whileUntil2.xml");
    }

    @Test
    public void reverseTransformationWhileUntil3() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_whileUntil3.xml");
    }

    @Test
    public void reverseTransformationWhileUntil4() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_whileUntil4.xml");
    }

    @Test
    public void reverseTransformationFor() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_for.xml");
    }

    @Test
    public void reverseTransformationFor1() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_for1.xml");
    }

    @Test
    public void reverseTransformationForEach() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_for_each.xml");
    }

    @Test
    public void reverseTransformationForEach1() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_for_each1.xml");
    }

    @Test
    public void reverseTransformationForEach2() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_for_each2.xml");
    }

    @Test
    public void reverseTransformationForEach3() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/forEach_stmt.xml");
    }

    @Test
    public void reverseTransformationForLoopForever() throws Exception {
        Helper.assertTransformationIsOk("/ast/control/repeat_stmt_loopForever.xml");
    }

}
