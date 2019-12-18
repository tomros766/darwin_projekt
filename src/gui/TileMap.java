package gui;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import map.RectangularMap;
import map.Vector2d;
import map.elements.MapElement;
import map.elements.MapElementType;
import map.elements.grass.Grass;

import java.util.ArrayList;

public class TileMap  extends GridPane{
    int width;
    int height;
    RectangularMap map;
    ImageViewGenerator imgGenerator = new ImageViewGenerator();
    public TileMap(RectangularMap map){
        this.map = map;
        this.width = map.width;
        this.height = map.height;
        this.setMinSize(map.width*35, map.height*35);
    }

    public void refreshMap(){

//        this.getChildren().clear();
//        Vector2d position;
//        for(MapElement elem: map.getOccupied().values()) {
//            position = elem.getPosition();
//            this.add(imgGenerator.generateImageView(elem.getType()), position.x, position.y);
//        }
    }

}
