package map.elements.animal;

import map.IPositionChangeObserver;
import map.MapDirection;
import map.RectangularMap;
import map.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal implements Comparable{

    public MapDirection orientation;
    public Vector2d position;
    double energy;
    private AnimalMotorics motorics;
    protected GenoType genoType;
    protected ArrayList<Animal> children = new ArrayList<>();
    private int age = 0;
    public boolean followed = false;


    List<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(Vector2d setPosition, GenoType genoType, RectangularMap map, double energy){
        Integer orient = new Random().nextInt(8);
        orientation = MapDirection.valueOf(orient);
        this.genoType = genoType;
        position = setPosition.copy();
        this.energy = energy;
        motorics = new AnimalMotorics(map,this);
        map.place(this);
    }

    public Double getEnergy(){
        return this.energy;
    }

    public ArrayList<Animal> getChildren() {
        return children;
    }

    public int getAge() {
        return age;
    }

    public GenoType getGenoType() {
        return genoType;
    }

    public Animal procreate (RectangularMap map, Animal subservient, Vector2d newBeginning){

        double childEnergy = (this.energy + subservient.energy)/4;
        this.energy *= 0.75;
        subservient.energy *= 0.75;
        System.out.println("miracle of life");
        Animal baby = new Animal(newBeginning, new GenoType(this.genoType,subservient.genoType), map, childEnergy);
        this.children.add(baby);
        subservient.children.add(baby);
        return baby;
    }

    public void move(RectangularMap map){
        age++;
        motorics.spin();
        Vector2d newPosition = motorics.getNewPosition();
        this.positionChanged(newPosition);
        this.energy -= map.moveEnergy;
    }

    public void perish(RectangularMap map){
        removeObserver(map);
    }

    public boolean strongEnough(RectangularMap map){
        return this.energy >= 0.5*map.startEnergy;
    }

    public void consume(Double nutritionalValue){
        this.energy += nutritionalValue;
    }

    public String toString(){
        return "c";
//        return Integer.toString(this.orientation.getValue());
    }

    public MapDirection getOrientation(){
        return this.orientation;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    private void positionChanged(Vector2d newPosition){
        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(this,newPosition);
        }
        this.position = newPosition;
    }

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Animal) ) throw new IllegalArgumentException("You cannot compare " + o.toString() + " to Animal!");

        if(((Animal) o).energy == this.energy) return 0;
        if(((Animal) o).energy > this.energy) return -1;
        return 1;
    }

    public int countChildren(){
        return children.size();
    }


//    public int countDescendants(){
//
//    }
}
