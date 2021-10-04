package org.openjfx;

import javafx.beans.property.SimpleIntegerProperty;
import org.openjfx.exceptions.InvalidHDCapacityException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Harddisk extends PC_Component {
    private PC_Component component;
    private transient SimpleIntegerProperty capacity;

    public Harddisk(PC_Component component, int capacity) {
        super("","",0.0);
        this.component = component;
        this.capacity = new SimpleIntegerProperty(capacity);
    }

    @Override
    public String getComponentType() {
        return component.getComponentType();
    }

    @Override
    public void setComponentType(String component) {
        this.component.setComponentType(component);
    }

    @Override
    public String getName() {
        return this.component.getName();
    }

    @Override
    public void setName(String name) {
        this.component.setName(name);
    }

    @Override
    public double getPrice() {
        return this.component.getPrice();
    }

    @Override
    public void setPrice(Double price) {
        this.component.setPrice(price);
    }



    @Override
    public int getCapacity() {
        return capacity.get();
    }



    public void setCapacity(int capacity) {
        if(!ComponentValidator.checkHdCapacity(capacity)) {
            throw new InvalidHDCapacityException("Use capacity value between 8 - 16000 Gb!");
        }
        this.capacity.set(capacity);
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeUTF(getComponentType());
        os.writeUTF(getName());
        os.writeInt(getCapacity());
        os.writeDouble(getPrice());
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        this.component.setComponentType(is.readUTF());
        this.component.setName(is.readUTF());
        this.capacity = new SimpleIntegerProperty(is.readInt());
        this.component.setPrice(is.readDouble());

    }

    @Override
    public String toString() {
        return getComponentType() + ";" + getName() + ";" + getCapacity() + ";" + getPrice();
    }


}
