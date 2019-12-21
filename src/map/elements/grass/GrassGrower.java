package map.elements.grass;

import map.MapTools.MapBorders;
import map.RectangularMap;
import map.MapTools.Vector2d;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class GrassGrower {


    public Grass growGrass(MapBorders borders, RectangularMap map){
        int spotsOccupied = map.getOccupied().keySet().stream().filter(e -> e.precedes(borders.upperRight) && e.follows(borders.lowerLeft)).collect(Collectors.toCollection(ArrayList::new)).size();
        if(borders.getArea() > spotsOccupied)
        {
            int i = 0;
            int newX;
            int newY;
            Vector2d newPosition;
            do{
                newX =  new Random().nextInt(borders.width) + borders.lowerLeft.x;
                newY = new Random().nextInt(borders.height) + borders.lowerLeft.y;
                newPosition = new Vector2d(newX, newY);
                i++;
            }
            while(map.getOccupied().containsKey(newPosition) && i < borders.getArea()*1.5);
            if(!map.getOccupied().containsKey(newPosition)){
                return new Grass(newPosition, map.plantEnergy);
            }
        }

        return null;
    }
}

