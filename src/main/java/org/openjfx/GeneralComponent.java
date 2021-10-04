package org.openjfx;

public class GeneralComponent {
    private PC_Component comp;
    private Processor processor;
    private Harddisk harddisk;
    private Memory memory;
    private Monitor monitor;

    public void addComp(PC_Component comp) {
        this.comp = comp;
    }

    public void addProcessor(PC_Component processor) {
        this.processor = (Processor) processor;
    }

    public void addHarddisk(PC_Component harddisk) {
        this.harddisk = (Harddisk) harddisk;
    }

    public void addMemory(PC_Component memory) {
        this.memory = (Memory) memory;
    }

    public void addMonitor(PC_Component monitor) {
        this.monitor = (Monitor) monitor;
    }


    public double getCoreClock() {
        return processor.getCoreClock();
    }

    public int getCapacity() {
        try {
            return harddisk.getCapacity();
        } catch (Exception e) {
            return memory.getCapacity();
        }
    }
    public double getScreenSize() {
        return monitor.getScreenSize();
    }

    public String getComponentType() {
        String comptype = null;
        if(this.comp.getComponentType() != null) {
            comptype =  this.comp.getComponentType();
        }
        if(this.processor.getComponentType() != null) {
            comptype =  this.processor.getComponentType();
        }
        if(this.harddisk.getComponentType() != null) {
            comptype =  this.harddisk.getComponentType();
        }
        if(this.memory.getComponentType() != null) {
            comptype =  this.memory.getComponentType();
        }
        if (this.monitor.getComponentType() != null) {
            comptype =  this.monitor.getComponentType();
        }

        return comptype;
    }

    public String getName() {
        String name = null;
        if(this.comp.getName() != null) {
            name =  this.comp.getName();
        }
        if(this.comp.getName() != null) {
            name =  this.processor.getName();
        }
        if(this.comp.getName() != null) {
            name =  this.harddisk.getName();
        }
        if(this.comp.getName() != null) {
            name =  this.memory.getName();
        }
        if (this.comp.getName() != null) {
            name =  this.monitor.getName();
        }

        return name;
    }

    public double getPrice() {
        double price = 0.0;
        if(this.comp.getPrice() != 0.0) {
            price =  this.comp.getPrice();
        }
        if(this.comp.getPrice() != 0.0) {
            price =  this.processor.getPrice();
        }
        if(this.comp.getPrice() != 0.0) {
            price =  this.harddisk.getPrice();
        }
        if(this.comp.getPrice() != 0.0) {
            price =  this.memory.getPrice();
        }
        if (this.comp.getPrice() != 0.0) {
            price =  this.monitor.getPrice();
        }

        return price;
    }

}
