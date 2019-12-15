package map.elements.animal;

import map.MapDirection;
import map.RectangularMap;
import map.Vector2d;

import java.util.Random;

public class AnimalMotorics {
    public RectangularMap map;
    public Animal animal;

    public AnimalMotorics(RectangularMap map, Animal animal){
        this.map = map;
        this.animal = animal;
    }

    public void spin() {
        int spin = animal.genoType.genes.get(new Random().nextInt(32));
        animal.orientation.add(MapDirection.valueOf(spin));
    }

    public Vector2d getNewPosition(){
        Vector2d newPosition = animal.position.copy().add(animal.getOrientation().toUnitVector());
        if(newPosition.x < 0) newPosition.x = (map.width - newPosition.x) % map.width;
        if(newPosition.y < 0) newPosition.y = (map.height - newPosition.y) % map.height;
        if(newPosition.x >= map.width) newPosition.x = newPosition.x % map.width;
        if(newPosition.y >= map.height) newPosition.y = newPosition.y % map.height;
        return newPosition;

    }
}
