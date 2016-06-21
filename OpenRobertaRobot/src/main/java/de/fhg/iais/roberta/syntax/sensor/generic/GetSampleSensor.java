package de.fhg.iais.roberta.syntax.sensor.generic;

import java.util.List;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.blockly.generated.Field;
import de.fhg.iais.roberta.blockly.generated.Mutation;
import de.fhg.iais.roberta.shared.action.ev3.ActorPort;
import de.fhg.iais.roberta.shared.sensor.ev3.BrickKey;
import de.fhg.iais.roberta.shared.sensor.ev3.ColorSensorMode;
import de.fhg.iais.roberta.shared.sensor.ev3.GyroSensorMode;
import de.fhg.iais.roberta.shared.sensor.ev3.InfraredSensorMode;
import de.fhg.iais.roberta.shared.sensor.ev3.MotorTachoMode;
import de.fhg.iais.roberta.shared.sensor.ev3.SensorPort;
import de.fhg.iais.roberta.shared.sensor.ev3.SensorType;
import de.fhg.iais.roberta.shared.sensor.ev3.TimerSensorMode;
import de.fhg.iais.roberta.shared.sensor.ev3.UltrasonicSensorMode;
import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.syntax.sensor.generic.BrickSensor.Mode;
import de.fhg.iais.roberta.transformer.Jaxb2AstTransformer;
import de.fhg.iais.roberta.transformer.JaxbTransformerHelper;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.visitor.AstVisitor;

/**
 * This class represents the <b>robSensors_getSample</b> block from Blockly
 * into
 * the AST (abstract syntax
 * tree).
 * Object from this class will generate code for setting the mode of the sensor or getting a sample from the sensor.<br/>
 * <br>
 * The client must provide the {@link SensorType} and port. See enum {@link SensorType} for all possible type of sensors.<br>
 * <br>
 * To create an instance from this class use the method {@link #make}.<br>
 */
public class GetSampleSensor<V> extends Sensor<V> {
    private final Sensor<V> sensor;
    private final String sensorPort;
    private final SensorType sensorType;

    private GetSampleSensor(SensorType sensorType, String port, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(BlockType.SENSOR_GET_SAMPLE, properties, comment);
        Assert.isTrue(sensorType != null && port != "");
        this.sensorPort = port;
        this.sensorType = sensorType;
        switch ( sensorType.getSensorType() ) {
            case BlocklyConstants.TOUCH:
                this.sensor = TouchSensor.make(SensorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.ULTRASONIC:
                this.sensor = UltrasonicSensor.make(UltrasonicSensorMode.get(sensorType.getSensorMode()), SensorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.COLOUR:
                this.sensor = ColorSensor.make(ColorSensorMode.get(sensorType.getSensorMode()), SensorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.INFRARED:
                this.sensor = InfraredSensor.make(InfraredSensorMode.get(sensorType.getSensorMode()), SensorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.ENCODER:
                this.sensor = EncoderSensor.make(MotorTachoMode.get(sensorType.getSensorMode()), ActorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.KEY_PRESSED:
                this.sensor = BrickSensor.make(Mode.IS_PRESSED, BrickKey.get(port), properties, comment);
                break;
            case BlocklyConstants.GYRO:
                this.sensor = GyroSensor.make(GyroSensorMode.get(sensorType.getSensorMode()), SensorPort.get(port), properties, comment);
                break;
            case BlocklyConstants.TIME:
                this.sensor = TimerSensor.make(TimerSensorMode.GET_SAMPLE, Integer.valueOf(port), properties, comment);
                break;
            default:
                throw new DbcException("Invalid sensor " + sensorType.getSensorType() + "!");
        }
        setReadOnly();
    }

    /**
     * Create object of the class {@link GetSampleSensor}.
     *
     * @param sensorType; must be <b>not</b> null,
     * @param port on which the sensor is connected; must be <b>non-empty</b> string,
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of class {@link GetSampleSensor}
     */
    public static <V> GetSampleSensor<V> make(SensorType sensorType, String port, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new GetSampleSensor<V>(sensorType, port, properties, comment);
    }

    /**
     * @return the sensor
     */
    public Sensor<V> getSensor() {
        return this.sensor;
    }

    /**
     * @return name of the port
     */
    public String getSensorPort() {
        return this.sensorPort;
    }

    /**
     * @return type of the sensor who will get the sample
     */
    public SensorType getSensorType() {
        return this.sensorType;
    }

    @Override
    public String toString() {
        return "GetSampleSensor [sensor=" + this.sensor + "]";
    }

    @Override
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitGetSampleSensor(this);
    }

    /**
     * Transformation from JAXB object to corresponding AST object.
     *
     * @param block for transformation
     * @param helper class for making the transformation
     * @return corresponding AST object
     */
    public static <V> Phrase<V> jaxbToAst(Block block, Jaxb2AstTransformer<V> helper) {
        List<Field> fields = helper.extractFields(block, (short) 2);
        String modeName = helper.extractField(fields, BlocklyConstants.SENSORTYPE);
        String portName = helper.extractField(fields, SensorType.get(modeName).getPortTypeName());
        return GetSampleSensor.make(SensorType.get(modeName), portName, helper.extractBlockProperties(block), helper.extractComment(block));
    }

    @Override
    public Block astToBlock() {
        Block jaxbDestination = new Block();
        JaxbTransformerHelper.setBasicProperties(this.sensor, jaxbDestination);

        Mutation mutation = new Mutation();
        mutation.setInput(getSensorType().name());
        jaxbDestination.setMutation(mutation);

        JaxbTransformerHelper.addField(jaxbDestination, BlocklyConstants.SENSORTYPE, getSensorType().name());
        JaxbTransformerHelper.addField(jaxbDestination, getSensorType().getPortTypeName(), getSensorPort());

        return jaxbDestination;
    }

}
