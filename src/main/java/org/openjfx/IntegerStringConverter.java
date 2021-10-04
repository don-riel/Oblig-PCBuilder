package org.openjfx;

import org.openjfx.dialogs.Dialogs;

public class IntegerStringConverter extends  javafx.util.converter.IntegerStringConverter {

    private boolean conversionSuccessful;


    @Override
    public Integer fromString(String input) {

        try {
            Integer result = super.fromString(input);
            conversionSuccessful = true;
            return result;

        } catch(NumberFormatException e) {
            Dialogs.showErrorDialog("Enter a valid whole number");
            conversionSuccessful = false;
            return 0;
        }
    }



    public boolean isSuccessful() {
        return conversionSuccessful;
    }
}