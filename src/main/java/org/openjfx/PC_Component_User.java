package org.openjfx;

public class PC_Component_User {
    private String componentType;
    private String name;
    private double price;


    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PC_Component_User(String componentType, String name, double price) {
        this.componentType = componentType;
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return getComponentType() + ";" + getName() + ";" + getPrice();
    }
}
