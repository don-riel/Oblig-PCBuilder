package org.openjfx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PC_Component implements Serializable {

    private static final long serialVersionUID = 1;

    private transient SimpleStringProperty componentType;
    private transient SimpleStringProperty name;
    private transient SimpleDoubleProperty price;

    public PC_Component (String component, String name, Double price) {
        this.componentType = new SimpleStringProperty(component);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
    }


    public String getComponentType() {
        return componentType.getValue();
    }

    public void setComponentType(String component) {
        this.componentType.set(component);
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getPrice() {
        return price.getValue();
    }

    public void setPrice(Double price) {
        this.price.set(price);
    }

    public double getCoreClock() {
        return 0;
    };

    public int getCapacity() {
        return 0;
    }

    public double getScreenSize() {
        return 0.0;
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeUTF(getComponentType());
        os.writeUTF(getName());
        os.writeDouble(getPrice());


    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        this.componentType = new SimpleStringProperty(is.readUTF());
        this.name = new SimpleStringProperty(is.readUTF());
        this.price = new SimpleDoubleProperty(is.readDouble());
    }




    @Override
    public String toString() {
        return getComponentType() + ";" + getName() + ";" + getPrice();
    }

}

