package de.fhg.iais.roberta.syntax.expr;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.functions.Function;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.AstVisitor;

/**
 * Wraps subclasses of the class {@link Function} so they can be used as {@link Expr} in expressions.
 */
public class FunctionExpr<V> extends Expr<V> {
    private final Function<V> function;

    private FunctionExpr(Function<V> function) {
        super(BlockType.FUNCTION_EXPR, function.getProperty(), function.getComment());
        Assert.isTrue(function.isReadOnly());
        this.function = function;
        setReadOnly();
    }

    /**
     * Create object of the class {@link FunctionExpr}.
     *
     * @param function that we want to wrap,
     * @return expression with wrapped function inside
     */
    public static <V> FunctionExpr<V> make(Function<V> function) {
        return new FunctionExpr<V>(function);
    }

    /**
     * @return function that is wrapped in the expression
     */
    public Function<V> getFunction() {
        return this.function;
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
        return visitor.visitFunctionExpr(this);
    }

    @Override
    public String toString() {
        return "FunctionExpr [" + this.function + "]";
    }

    @Override
    public Block astToBlock() {
        Phrase<V> p = this.getFunction();
        return p.astToBlock();
    }
}
