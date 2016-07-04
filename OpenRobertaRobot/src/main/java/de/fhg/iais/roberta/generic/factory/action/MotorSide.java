package de.fhg.iais.roberta.generic.factory.action;

import de.fhg.iais.roberta.factory.IMotorSide;

public enum MotorSide implements IMotorSide {
    RIGHT( "right" ), LEFT( "left" ), NONE( "" );

    private final String[] values;

    private MotorSide(String... values) {
        this.values = values;
    }

    @Override
    public String[] getValues() {
        return this.values;
    }

    @Override
    public String getText() {
        return this.values[0];
    }

}