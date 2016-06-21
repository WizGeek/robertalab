package de.fhg.iais.roberta.ast.action;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.shared.action.ev3.ActorPort;
import de.fhg.iais.roberta.syntax.action.generic.MotorOnAction;
import de.fhg.iais.roberta.testutil.Helper;
import de.fhg.iais.roberta.transformer.Jaxb2BlocklyProgramTransformer;

public class MotorOnActionTest {

    @Test
    public void make() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=1, y=1], MotorOnAction [B, MotionParam [speed=NumConst [30], duration=null]], MotorOnAction [C, MotionParam [speed=NumConst [50], duration=null]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOn.xml"));
    }

    @Test
    public void getParam() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/actions/action_MotorOnFor.xml");
        MotorOnAction<Void> mo = (MotorOnAction<Void>) transformer.getTree().get(0).get(1);
        Assert.assertEquals("MotionParam [speed=NumConst [30], duration=MotorDuration [type=ROTATIONS, value=NumConst [1]]]", mo.getParam().toString());
    }

    @Test
    public void getPort() throws Exception {
        Jaxb2BlocklyProgramTransformer<Void> transformer = Helper.generateTransformer("/ast/actions/action_MotorOnFor.xml");
        MotorOnAction<Void> mo = (MotorOnAction<Void>) transformer.getTree().get(0).get(1);
        Assert.assertEquals(ActorPort.B, mo.getPort());
    }

    @Test
    public void motorOnFor() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=1, y=55], MotorOnAction [B, MotionParam [speed=NumConst [30], duration=MotorDuration [type=ROTATIONS, value=NumConst [1]]]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOnFor.xml"));
    }

    @Test
    public void motorOnForSim() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=2, y=46], MainTask [], MotorOnAction [C, MotionParam [speed=NumConst [30], duration=MotorDuration [type=ROTATIONS, value=NumConst [1]]]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOnForSim.xml"));
    }

    @Test
    public void motorOnMissing() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=55, y=64], MotorOnAction [B, MotionParam [speed=EmptyExpr [defVal=class java.lang.Integer], duration=null]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOnMissing.xml"));
    }

    @Test
    public void motorOnSim() throws Exception {
        String a = "BlockAST [project=[[Location [x=2, y=46], MainTask [], MotorOnAction [B, MotionParam [speed=NumConst [30], duration=null]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOnSim.xml"));
    }

    @Test
    public void motorOnForMissing() throws Exception {
        String a =
            "BlockAST [project=[[Location [x=62, y=103], MotorOnAction [B, MotionParam [speed=EmptyExpr [defVal=class java.lang.Integer], duration=MotorDuration [type=DEGREE, value=EmptyExpr [defVal=class java.lang.Integer]]]], MotorOnAction [B, MotionParam [speed=NumConst [30], duration=MotorDuration [type=ROTATIONS, value=EmptyExpr [defVal=class java.lang.Integer]]]], MotorOnAction [B, MotionParam [speed=EmptyExpr [defVal=class java.lang.Integer], duration=MotorDuration [type=ROTATIONS, value=NumConst [30]]]]]]]";
        Assert.assertEquals(a, Helper.generateTransformerString("/ast/actions/action_MotorOnForMissing.xml"));
    }

    @Test
    public void reverseTransformatin() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOn.xml");
    }

    @Test
    public void reverseTransformatinSim() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOnSim.xml");
    }

    @Test
    public void reverseTransformatinOnFor() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOnFor.xml");
    }

    @Test
    public void reverseTransformatinOnForSim() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOnForSim.xml");
    }

    @Test
    public void reverseTransformatinOnMissing() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOnFor.xml");
    }

    @Test
    public void reverseTransformatinOnForMissing() throws Exception {
        Helper.assertTransformationIsOk("/ast/actions/action_MotorOnForMissing.xml");
    }

}
