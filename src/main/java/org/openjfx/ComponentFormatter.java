package org.openjfx;


import org.openjfx.PC_Component;

import java.util.List;

public class ComponentFormatter {
    public static String DELIMITER = " ; ";

    public static String formatPart(PC_Component obj) {
        return obj.getComponentType() + DELIMITER + obj.getName() + DELIMITER + obj.getPrice();
    }




    public static String formatComponents(List<PC_Component> list) {
        StringBuffer str = new StringBuffer();
        for(PC_Component value : list) {
            str.append(formatPart(value));
            str.append("\n");
        }
        return str.toString();
    }
}
