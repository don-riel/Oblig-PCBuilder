package org.openjfx;

import javafx.beans.property.SimpleDoubleProperty;
import org.openjfx.exceptions.InvalidCoreSpeedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Processor extends PC_Component {
    private PC_Component component;
    private transient SimpleDoubleProperty coreClock;

    public Processor(PC_Component component, Double coreClock) {
        super("","", 0.0);
        this.component = component;
        this.coreClock = new SimpleDoubleProperty(coreClock);
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
    public double getCoreClock() {
        return coreClock.get();
    }


    public void setCoreClock(double coreClock) {
        if(!ComponentValidator.checkCoreClock(coreClock)) {
            throw new InvalidCoreSpeedException("Use core speed between 1.1 - 4-7 Ghz!");
        }
        this.coreClock.set(coreClock);
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
        os.writeUTF(getComponentType());
        os.writeUTF(getName());
        os.writeDouble(getCoreClock());
        os.writeDouble(getPrice());
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        this.component.setComponentType(is.readUTF());
        this.component.setName(is.readUTF());
        this.coreClock = new SimpleDoubleProperty(is.readDouble());
        this.component.setPrice(is.readDouble());

    }

    @Override
    public String toString() {
        return getComponentType() + ";" + getName() + ";" + getCoreClock() + ";" + getPrice();
    }




}
