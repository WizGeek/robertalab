package de.fhg.iais.roberta.syntax.expr;

import org.junit.Test;

import de.fhg.iais.roberta.testutil.Helper;

public class ListsCreateWithItemTest {
    @Test
    public void Test() throws Exception {
        String a = "BlocklyMethods.createListWithItem(1, 5)";

        Helper.assertCodeIsOk(a, "/syntax/lists/lists_create_with_item.xml");
    }

}
