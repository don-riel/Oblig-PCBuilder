package org.openjfx;

import org.openjfx.dialogs.Dialogs;

public class DoubleStringConverter extends javafx.util.converter.DoubleStringConverter {

    private boolean conversionSuccessful;


    @Override
    public Double fromString(String input) {

        try {
            Double result = super.fromString(input);
            conversionSuccessful = true;
            return result;

        } catch(NumberFormatException e) {
            Dialogs.showErrorDialog("Enter a valid number");
            conversionSuccessful = false;
            return 0.0;
        }
    }



    public boolean isSuccessful() {
        return conversionSuccessful;
    }
}
