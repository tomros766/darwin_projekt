package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import map.RectangularMap;
import map.elements.MapElement;
import map.elements.MapElementType;
import map.elements.animal.Animal;
import map.elements.grass.Grass;

import java.util.Collection;

public class CanvasMap extends Canvas {
    int width;
    int height;
    RectangularMap map;
    double ratio;
    GraphicsContext gc;
    Color color;
    public CanvasMap(RectangularMap map){
        super(((double) map.width/ (double) map.height) * 600,600);
        this.map = map;
        this.width = map.width;
        this.height = map.height;
        ratio = this.getHeight()/height;
        gc = this.getGraphicsContext2D();
    }

    public void refreshMap(){
        gc.clearRect(0,0,this.getWidth(),this.getHeight());
        for(Animal animal: map.getAnimals()) {
            Color color = Color.color(Math.min(1,Math.max(animal.getEnergy()/map.startEnergy,0)),0,0);
            gc.setFill(color);
            gc.fillRect(animal.position.x*ratio,(animal.position.y+1)*ratio,ratio,ratio);

        }
        for(MapElement grass: map.getGrasses()){
            gc.setFill(Color.GREEN);
            gc.fillRect(grass.getPosition().x*ratio,grass.getPosition().y*ratio,ratio,ratio);
        }

    }
}


