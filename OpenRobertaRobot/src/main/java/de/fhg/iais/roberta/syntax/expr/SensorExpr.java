package de.fhg.iais.roberta.syntax.expr;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.AstVisitor;

/**
 * Wraps subclasses of the class {@link Sensor} so they can be used as {@link Expr} in expressions.
 */
public class SensorExpr<V> extends Expr<V> {
    private final Sensor<V> sensor;

    private SensorExpr(Sensor<V> sens) {
        super(BlockType.SENSOR_EXPR, sens.getProperty(), sens.getComment());
        Assert.isTrue(sens.isReadOnly());
        this.sensor = sens;
        setReadOnly();
    }

    /**
     * Create object of the class {@link SensorExpr}.
     *
     * @param sensor that we want to wrap,
     * @return expression with wrapped sensor inside
     */
    public static <V> SensorExpr<V> make(Sensor<V> sens) {
        return new SensorExpr<V>(sens);
    }

    /**
     * @return sensor that is wrapped in the expression
     */
    public Sensor<V> getSens() {
        return this.sensor;
    }

    @Override
    public int getPrecedence() {
        return 999;
    }

    @Override
    public Assoc getAssoc() {
        return Assoc.NONE;
    }

    @Override
    public BlocklyType getVarType() {
        return BlocklyType.NOTHING;
    }

    @Override
    public String toString() {
        return "SensorExpr [" + this.sensor + "]";
    }

    @Override
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitSensorExpr(this);
    }

    @Override
    public Block astToBlock() {
        Phrase<V> p = getSens();
        return p.astToBlock();
    }
}
