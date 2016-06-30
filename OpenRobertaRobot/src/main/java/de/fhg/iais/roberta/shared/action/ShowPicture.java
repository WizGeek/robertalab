package de.fhg.iais.roberta.shared.action;

import java.util.Locale;

import de.fhg.iais.roberta.util.dbc.DbcException;

public enum ShowPicture {
    OLDGLASSES( "BRILLE", "Brille" ), EYESOPEN( "AUGENOFFEN", "Augen Offen" ), EYESCLOSED( "AUGENZU", "AUGEN ZU" ), FLOWERS( "BLUMEN", "Blumen" ), TACHO(
        "Tacho" );

    private final String[] values;

    private ShowPicture(String... values) {
        this.values = values;
    }

    /**
     * get picture from {@link ShowPicture} from string parameter. It is possible
     * for one mode to have multiple string mappings. Throws exception if
     * the mode does not exists.
     *
     * @param name of the picture
     * @return picture from the enum {@link ShowPicture}
     */
    public static ShowPicture get(String s) {
        if ( s == null || s.isEmpty() ) {
            throw new DbcException("Invalid picture: " + s);
        }
        String sUpper = s.trim().toUpperCase(Locale.GERMAN);
        for ( ShowPicture mo : ShowPicture.values() ) {
            if ( mo.toString().equals(sUpper) ) {
                return mo;
            }
            for ( String value : mo.values ) {
                if ( sUpper.equals(value) ) {
                    return mo;
                }
            }
        }
        throw new DbcException("Invalid picture: " + s);
    }

}
