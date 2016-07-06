package de.fhg.iais.roberta.syntax.expr;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.AstVisitor;

public class ShadowExpr<V> extends Expr<V> {
    private final Expr<V> shadow;
    private final Expr<V> block;

    private ShadowExpr(Expr<V> shadow, Expr<V> block) {
        super(BlockType.SHADOW_EXPR, BlocklyBlockProperties.make("1", "1", false, false, false, false, false, true, false), null);
        Assert.isTrue(shadow != null);
        this.shadow = shadow;
        this.block = block;
        setReadOnly();
    }

    /**
     * create read only instance from {@link EmptyExpr}.
     *
     * @param defVal type of the value that the missing expression should have.
     * @return read only object of class {@link EmptyExpr}.
     */
    public static <V> ShadowExpr<V> make(Expr<V> shadow, Expr<V> block) {
        return new ShadowExpr<V>(shadow, block);
    }

    /**
     * create read only instance from {@link EmptyExpr}.
     *
     * @param defVal type of the value that the missing expression should have.
     * @return read only object of class {@link EmptyExpr}.
     */
    public static <V> ShadowExpr<V> make(Expr<V> shadow) {
        return new ShadowExpr<V>(shadow, null);
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
    protected V accept(AstVisitor<V> visitor) {
        return visitor.visitShadowExpr(this);
    }

    @Override
    public Block astToBlock() {
        return null;
    }

    public Expr<V> getBlock() {
        return this.block;
    }

    public Expr<V> getShadow() {
        return this.shadow;
    }

    @Override
    public String toString() {
        return "ShadowExpr [" + this.shadow + ", " + this.block + "]";
    }

}
