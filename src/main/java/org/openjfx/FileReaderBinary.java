package org.openjfx;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileReaderBinary  {


    public List<PC_Component> readFile(Path path) throws IOException, ClassNotFoundException {
        List<PC_Component> componentList;
        InputStream in = Files.newInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(in);

        Object comps = ois.readObject();
        componentList = new ArrayList<>((Collection<? extends PC_Component>) comps);
        return componentList;
    }


    public String read(List<PC_Component_User> list) {
        return null;
    }


}
