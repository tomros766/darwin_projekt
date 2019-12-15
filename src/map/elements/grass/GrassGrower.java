package map.elements.grass;

import map.MapBorders;
import map.RectangularMap;
import map.Vector2d;

import java.util.Random;

public class GrassGrower {


    public Grass growGrass(MapBorders borders, RectangularMap map){
        int i = 0;
        int newX;
        int newY;
        Vector2d newPosition = new Vector2d(-1, -1);
        do{
            newX =  new Random().nextInt(borders.width) + borders.lowerLeft.x;
            newY = new Random().nextInt(borders.height) + borders.lowerLeft.y;
            newPosition.x = newX;
            newPosition.y = newY;
            i++;
        }
        while((map.getGrasses().containsKey(newPosition) || map.getOccupied().containsKey(newPosition)) && i < borders.getArea()*1.5);
        if(!map.getOccupied().containsKey(newPosition) && !map.getGrasses().containsKey(newPosition)){
            return new Grass(newPosition, map.plantEnergy);
        }
        return null;
    }
}

