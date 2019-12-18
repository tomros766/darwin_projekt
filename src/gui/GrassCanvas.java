package gui;

import javafx.scene.paint.Color;
import map.RectangularMap;


public class GrassCanvas extends AbstractCanvasMap {

    public GrassCanvas(RectangularMap map){
        super(map);
        this.color = Color.GREEN;
    }

    public void refreshMap(){
//            super.refreshMap(map.getGrasses());
        }
}


