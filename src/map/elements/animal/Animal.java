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


    public Animal procreate (RectangularMap map, Animal subservient, Vector2d newBeginning){

        double childEnergy = (this.energy + subservient.energy)/4;
        this.energy *= 0.75;
        subservient.energy *= 0.75;

        return new Animal(newBeginning, new GenoType(this.genoType,subservient.genoType), map, childEnergy);
    }

    public void move(RectangularMap map){
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
        Vector2d oldPosition = this.position.copy();
        this.position = newPosition;
        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(oldPosition,this);
        }
    }

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Animal) ) throw new IllegalArgumentException("You cannot compare " + o.toString() + " to Animal!");

        if(((Animal) o).energy == this.energy) return 0;
        if(((Animal) o).energy > this.energy) return -1;
        return 1;
    }
}
