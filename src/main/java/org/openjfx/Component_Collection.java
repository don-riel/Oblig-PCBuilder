package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;


public class Component_Collection {
    private transient ObservableList<PC_Component> components_Collection = FXCollections.observableArrayList();

    public void attach_Comp_to_TableView(TableView<PC_Component> tableView) {
        tableView.setItems(components_Collection);
    }

    public void add_Component(PC_Component component) {
        components_Collection.add(component);
    }

    public void delete_Component(int index) {
        components_Collection.remove(index);
    }



}