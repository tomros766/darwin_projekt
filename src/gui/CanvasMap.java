package gui;

import javafx.scene.Scene;
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
    int round = 0;
    int width;
    int height;
    RectangularMap map;
    double ratio;
    GraphicsContext gc;
    boolean trackAlphas = false;

    public CanvasMap(RectangularMap map){
        super(((double) map.width/ (double) map.height) * 600,600);
        this.map = map;
        this.width = map.width;
        this.height = map.height;
        ratio = this.getHeight()/height;
        gc = this.getGraphicsContext2D();
    }

    public RectangularMap getMap() {
        return map;
    }

    public void refreshMap(){
        round++;
        gc.clearRect(0,0,this.getWidth(),this.getHeight());
        Color color = Color.WHITE;
        for(Animal animal: map.getAnimals()) {
            if(map.statistics.animalFollowed == null || animal != map.statistics.animalFollowed.animal){
                color = Color.color(Math.min(1,Math.max(animal.getEnergy()/map.startEnergy,0)),0,0);
            }
            if(trackAlphas && animal.getGenoType().equals(map.statistics.getDominantGenoType())){
                color = Color.YELLOW;
            }
            if(map.statistics.animalFollowed != null && animal == map.statistics.animalFollowed.animal ){
                color = Color.color(0,0,Math.min(1,Math.max(animal.getEnergy()/map.startEnergy,0)));
            }
            gc.setFill(color);
            gc.fillRect(animal.position.x*ratio,(animal.position.y)*ratio,ratio,ratio);


        }
        for(MapElement grass: map.getGrasses()){
            gc.setFill(Color.GREEN);
            gc.fillRect(grass.getGrass().position.x*ratio,grass.getGrass().position.y*ratio,ratio,ratio);
        }

    }
}


