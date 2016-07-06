package de.fhg.iais.roberta.ast.expr;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.syntax.expr.Assoc;
import de.fhg.iais.roberta.syntax.expr.Var;
import de.fhg.iais.roberta.testutil.Helper;
import de.fhg.iais.roberta.transformer.Jaxb2BlocklyProgramTransformer;

public class VariableTest {

    @Test
    public void variableSet() throws Exception {
        String a = "BlockAST [project=[[Location [x=-23, y=-797], Var [item]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/variables/variable_set1.xml"));
    }

    @Test
    public void getValue() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/variables/variable_set1.xml");
        Var<Void> var = (Var<Void>) transformer.getTree().get(0).get(1);
        Assert.assertEquals("item", var.getValue());
    }

    @Test
    public void getPresedance() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/variables/variable_set1.xml");
        Var<Void> var = (Var<Void>) transformer.getTree().get(0).get(1);
        Assert.assertEquals(999, var.getPrecedence());
    }

    @Test
    public void getAssoc() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/variables/variable_set1.xml");
        Var<Void> var = (Var<Void>) transformer.getTree().get(0).get(1);
        Assert.assertEquals(Assoc.NONE, var.getAssoc());
    }

    @Test
    public void variableSet4() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=1, y=7], MainTask [\n"
                + "exprStmt VarDeclaration [NUMBER, Element2, NumConst [0], true, true]\n"
                + "exprStmt VarDeclaration [STRING, Element3, StringConst [], true, true]\n"
                + "exprStmt VarDeclaration [BOOLEAN, Element4, BoolConst [true], true, true]\n"
                + "exprStmt VarDeclaration [ARRAY_NUMBER, Element5, ListCreate [NUMBER, NumConst [0], NumConst [0], NumConst [0]], true, true]\n"
                + "exprStmt VarDeclaration [ARRAY_STRING, Element6, ListCreate [STRING, StringConst [], StringConst [], StringConst []], true, true]\n"
                + "exprStmt VarDeclaration [ARRAY_BOOLEAN, Element, ListCreate [BOOLEAN, BoolConst [true], BoolConst [true], BoolConst [true]], false, true]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/variables/variable_set4.xml"));
    }

    @Test
    public void reverseTransformatin() throws Exception {
        Helper.assertTransformationIsOk("/ast/variables/variable_set.xml");
    }

    @Test
    public void reverseTransformatin1() throws Exception {
        Helper.assertTransformationIsOk("/ast/variables/variable_set1.xml");
    }

    @Test
    public void reverseTransformatin2() throws Exception {
        Helper.assertTransformationIsOk("/ast/variables/variable_set2.xml");
    }

    @Test
    public void reverseTransformatin3() throws Exception {
        Helper.assertTransformationIsOk("/ast/variables/variable_set3.xml");
    }

}
