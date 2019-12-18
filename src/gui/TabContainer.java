package gui;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import map.RectangularMap;

public class TabContainer extends TabPane {

    public TabContainer(){
        super();
        Tab sim1 = new Tab("symulacja 1", new StackPane());
        this.getTabs().add(sim1);
    }

    public void addSimulation(){
        Tab newSim = new Tab("symulacja " + (this.getTabs().size()+1),new StackPane());
        this.getTabs().add(newSim);
    }
}
