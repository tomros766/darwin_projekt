package map.elements;

import map.RectangularMap;
import map.elements.animal.Animal;
import map.elements.grass.Grass;

import java.util.ArrayList;
import java.util.Collections;

public class MapElement {
    private ArrayList<Animal> animals;
    private Grass grass;
    RectangularMap map;

    public MapElement(Animal animal, RectangularMap map){
        this.animals = new ArrayList<>();
        this.animals.add(animal);
        this.map = map;
    }

    public MapElement(Grass grass, RectangularMap map){
        this.grass = grass;
        this.map = map;
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }

    public Grass getGrass() {
        return grass;
    }


    public void addAnimal(Animal animal){
        if(this.animals == null) this.animals = new ArrayList<>();
        this.animals.add(animal);
    }

    public void removeAnimal(Animal animal){
        if(animals!=null && animals.isEmpty()) throw new IllegalCallerException("This list is empty!");
        if(hasAnimals()) this.animals.remove(animal);
    }

    public Animal[] getCoupleAlpha(){
        this.animals.sort(Animal::compareTo);
        return new Animal[] {animals.get(0),animals.get(1)};
    }

    public ArrayList<Animal> getStrongestAnimal(){
        this.animals.sort(Animal::compareTo);
        ArrayList<Animal> strongest = new ArrayList<>();
        Collections.sort(animals);
        int i = 0;
        double maxEnergy = animals.get(0).getEnergy();
        while(i<animals.size() && animals.get(i).getEnergy() == maxEnergy) i++;
        for(int j = 0; j < i; j++) {
            strongest.add(animals.get(j));
        }
        return strongest;
    }

    public boolean hasAnimals(){
        return animals!=null && !animals.isEmpty() ;
    }

    public boolean hasGrass() {
        return grass!=null;
    }

    public void grassEaten(){
        grass = null;
    }

}
