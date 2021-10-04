package org.openjfx;


import org.openjfx.exceptions.InvalidItemFormatException;

public class CompParser {
    public static PC_Component parseComp(String txt) {
        PC_Component component = null;
        String[] compString = txt.split(";");
        String comptype = compString[0];
        String name = compString[1];

        if(compString.length == 3) {
            double price = Double.parseDouble(compString[2]);
            component  = new PC_Component(comptype, name, price);
        }
        if(compString.length == 4 && comptype.equals("Processor")) {
            double price = Double.parseDouble(compString[3]);
            PC_Component newComp = new PC_Component(comptype, name, price);
            double coreClock = Double.parseDouble(compString[2]);
            component = new Processor(newComp, coreClock);
        }
        if(compString.length == 4 && comptype.equals("Harddisk")) {
            double price = Double.parseDouble(compString[3]);
            PC_Component newComp = new PC_Component(comptype, name, price);
            int capacity = Integer.parseInt(compString[2]);
            component = new Harddisk(newComp, capacity);
        }
        if(compString.length == 4 && comptype.equals("Memory")) {
            double price = Double.parseDouble(compString[3]);
            PC_Component newComp = new PC_Component(comptype, name, price);
            int capacity = Integer.parseInt(compString[2]);
            component = new Memory(newComp, capacity);
        }
        if(compString.length == 4 && comptype.equals("Monitor")) {
            double price = Double.parseDouble(compString[3]);
            PC_Component newComp = new PC_Component(comptype, name, price);
            double screenSize = Double.parseDouble(compString[2]);
            component = new Monitor(newComp, screenSize);
        }
        
        return component;

    }

    public static PC_Component_User parseUserComp(String txt) {

        PC_Component_User component = null;
        String[] compString = txt.split(";");
        String comptype = compString[0];
        String name = compString[1];

        if(compString.length == 3) {
            double price = Double.parseDouble(compString[2]);
            component  = new PC_Component_User(comptype, name, price);
        }
        if(compString.length == 4 && comptype.equals("Processor")) {
            double price = Double.parseDouble(compString[3]);
            double coreClock = Double.parseDouble(compString[2]);
            component = new PC_Component_User(comptype, (name + " " + coreClock + "Ghz"), price);

        }
        if(compString.length == 4 && comptype.equals("Harddisk")) {
            double price = Double.parseDouble(compString[3]);
            int capacity = Integer.parseInt(compString[2]);
            component = new PC_Component_User(comptype, (name + " " + capacity + "Gb"), price);
        }
        if(compString.length == 4 && comptype.equals("Memory")) {
            double price = Double.parseDouble(compString[3]);
            int capacity = Integer.parseInt(compString[2]);
            component = new PC_Component_User(comptype, (name + " " + capacity + "Gb"), price);
        }
        if(compString.length == 4 && comptype.equals("Monitor")) {
            double price = Double.parseDouble(compString[3]);
            double screenSize = Double.parseDouble(compString[2]);
            component = new PC_Component_User(comptype, (name + " " + screenSize + " inches"), price);
        }

        return component;

    }

    public static PC_Component_User parseCompFromFile(String txt) throws InvalidItemFormatException {
        PC_Component_User component = null;
        String[] compString;
        if(txt.split(";").length > 1) {
            compString = txt.split(";");
            if(checkCompNameValid(compString[0])) {
                String comptype = compString[0];
                String name = compString[1];
                try {
                    double price = Double.parseDouble(compString[2]);
                    component = new PC_Component_User(comptype, name, price);
                } catch (NumberFormatException e) {
                    throw new InvalidItemFormatException("Some items from the file are written in invalid format!");
                }
            } else {
                throw new InvalidItemFormatException("Some items from the file are written in invalid format");
            }
        }
        return component;
    }

    public static boolean checkCompNameValid(String txt) {
        boolean isValid = false;
        String[] validComponentNames = {"Processor", "Graphics card", "Harddisk", "Memory", "Monitor", "Keyboard", "Mouse", "Motherboard"};
        for (String s : validComponentNames) {
            if (txt.equals(s)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }


}

