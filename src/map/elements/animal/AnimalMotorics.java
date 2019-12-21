package map.elements.animal;

import map.MapTools.MapDirection;
import map.MapTools.Vector2d;

import java.util.Random;

public class AnimalMotorics {
    public Animal animal;

    public AnimalMotorics(Animal animal){
        this.animal = animal;
    }

    public void spin() {
        int spin = animal.genoType.genes.get(new Random().nextInt(animal.genoType.genes.size()));
        for(int i = 0; i < spin; i++)
            animal.orientation = animal.orientation.next();
    }

    public Vector2d getNewPosition(){
        Vector2d newPosition = animal.position.copy().add(animal.getOrientation().toUnitVector());
        return newPosition;
    }


}
