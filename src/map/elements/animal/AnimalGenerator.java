package map.elements.animal;

import map.RectangularMap;
import map.Vector2d;

import java.util.Random;

public class AnimalGenerator {
    public Animal generateAnimal (RectangularMap map){
        Vector2d position = new Vector2d(new Random().nextInt(map.width), new Random().nextInt(map.height));
        GenoType genoType = new GenoType();
        return new Animal(position,genoType,map,map.startEnergy);
    }
}
