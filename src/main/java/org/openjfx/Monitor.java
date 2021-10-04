package org.openjfx;

import javafx.beans.property.SimpleDoubleProperty;
import org.openjfx.exceptions.InvalidScreenSizeException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Monitor extends PC_Component {
    private PC_Component component;
    private transient SimpleDoubleProperty screenSize;

    public Monitor(PC_Component component, double screenSize) {
        super("","", 0.0);
        this.component = component;
        this.screenSize = new SimpleDoubleProperty(screenSize);
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
    public double getScreenSize() {
        return screenSize.get();
    }

    public void setScreenSize(double screenSize) {
        if(!ComponentValidator.checkScreenSize(screenSize)) {
            throw new InvalidScreenSizeException("Use screen size between 16 - 65 inches!");
        }
        this.screenSize.set(screenSize);
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeUTF(getComponentType());
        os.writeUTF(getName());
        os.writeDouble(getScreenSize());
        os.writeDouble(getPrice());
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        this.component.setComponentType(is.readUTF());
        this.component.setName(is.readUTF());
        this.screenSize = new SimpleDoubleProperty(is.readDouble());
        this.component.setPrice(is.readDouble());

    }

    @Override
    public String toString() {
        return getComponentType() + ";" + getName() + ";" + getScreenSize() + ";" + getPrice();
    }

}
