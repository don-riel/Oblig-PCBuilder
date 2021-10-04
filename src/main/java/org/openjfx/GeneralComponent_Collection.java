package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;


public class GeneralComponent_Collection {
    private transient ObservableList<GeneralComponent> components_Collection = FXCollections.observableArrayList();

    public void attach_Comp_to_TableView(TableView<GeneralComponent> tableView) {
        tableView.setItems(components_Collection);
    }

    public void add_Component(GeneralComponent component) {
        components_Collection.add(component);
    }

    public void delete_Component(int index) {
        components_Collection.remove(index);
    }



}