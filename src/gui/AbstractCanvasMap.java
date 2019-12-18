package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import map.RectangularMap;
import map.elements.MapElement;

import java.util.Collection;

public class AbstractCanvasMap extends Canvas {
        int width;
        int height;
        RectangularMap map;
        double ratio;
        GraphicsContext gc;
        Color color;
        public AbstractCanvasMap(RectangularMap map){
            super(800,600);
            this.map = map;
            this.width = map.width;
            this.height = map.height;
            ratio = this.getHeight()/height;
            gc = this.getGraphicsContext2D();
        }

        public void refreshMap(Collection<MapElement> elements){
            gc.clearRect(0,0,this.getWidth(),this.getHeight());
            gc.setFill(color);
            for(MapElement elem: elements) {
                gc.fillRect(elem.getPosition().x * ratio, elem.getPosition().y * ratio, ratio, ratio);
            }
        }

}

