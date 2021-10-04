package org.openjfx;

import org.openjfx.dialogs.Dialogs;
import org.openjfx.exceptions.InvalidItemFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReaderTxt  {

    public List<PC_Component_User> readFile(Path path) throws IOException {
        ArrayList<PC_Component_User> list = new ArrayList<>();
        BufferedReader reader = Files.newBufferedReader(path);
        String text;
        while ((text = reader.readLine()) != null) {
            PC_Component_User component_user = CompParser.parseUserComp(text);
            list.add(component_user);
       }

        return list;
    }

    public List<PC_Component_User> readUserFile(Path path) throws IOException {
        ArrayList<PC_Component_User> list = new ArrayList<>();
        BufferedReader reader = Files.newBufferedReader(path);
        String text;
        while ((text = reader.readLine()) != null) {
            try {
                PC_Component_User component_user = CompParser.parseCompFromFile(text);
                list.add(component_user);
            } catch (InvalidItemFormatException e) {
                Dialogs.showFileErrorDialog(e.getMessage());
                break;
            }

        }

        return list;
    }


    public String read(List<PC_Component_User> list) {
        StringBuilder out = new StringBuilder(" ");
        for(PC_Component_User p : list) {
            out.append(p);
        }
        return out.toString();
    }
}
