package org.openjfx;

import java.util.ArrayList;
import java.util.List;

public class BuildChecker {




    public boolean isComponentAdded(List<PC_Component_User> list, PC_Component_User comp) {
        boolean componentAdded = false;
        for (PC_Component_User pc : list) {
            if (pc.getComponentType().equals(comp.getComponentType())) {
                componentAdded = true;
            }
        }
        return componentAdded;
    }

    public boolean isBuildComplete(List<PC_Component_User> list) {
        ArrayList<String> types = new ArrayList<>();
        addTypes(types);
        boolean isComplete = true;
        if(list.isEmpty()) {
            isComplete = false;
        } {
            ArrayList<String> typesFromList = new ArrayList<String>();
            for(PC_Component_User pc : list) {
                typesFromList.add(pc.getComponentType());
            }
            for(String s : types) {
                if(!typesFromList.contains(s)) {
                    isComplete = false;
                    break;
                }
            }
        }
        return isComplete;
    }

    public String missing(List<PC_Component_User> list) {
        ArrayList<String> types = new ArrayList<>();
        addTypes(types);
        StringBuilder out = new StringBuilder();


        ArrayList<String> typesFromList = new ArrayList<>();

        for(PC_Component_User pc : list) {
            typesFromList.add(pc.getComponentType());
        }

        for(String s : types) {
            if(!typesFromList.contains(s)) {
                out.append(s).append("\n");
            }
        }
        return out.toString();
    }

    private ArrayList<String> addTypes(ArrayList<String> list) {
        list.add("Processor");
        list.add("Motherboard");
        list.add("Graphics card");
        list.add("Harddisk");
        list.add("Memory");
        list.add("Monitor");
        list.add("Keyboard");
        list.add("Mouse");
        return list;
    }

}

