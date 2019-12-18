package gui;

import javafx.scene.paint.Color;
import map.RectangularMap;


public class AnimalCanvas extends AbstractCanvasMap {
    public AnimalCanvas(RectangularMap map) {
        super(map);
        this.color = Color.ORANGE;
    }


    public void refreshMap() {
//        super.refreshMap(map.getElementswAnimals());
    }
}
