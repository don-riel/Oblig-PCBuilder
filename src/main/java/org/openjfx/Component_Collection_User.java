package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class Component_Collection_User {
    private transient ObservableList<PC_Component_User> components_Collection = FXCollections.observableArrayList();

    public void attach_Comp_to_TableView(TableView<PC_Component_User> tableView) {
        tableView.setItems(components_Collection);
    }

    public void add_Component(PC_Component_User component) {
        components_Collection.add(component);
    }

    public void delete_Component(int index) {
        components_Collection.remove(index);
    }

}
