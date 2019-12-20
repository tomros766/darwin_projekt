package map.elements.animal;

import gui.CanvasMap;
import map.RectangularMap;
import map.Vector2d;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FollowedAnimal {
    CanvasMap canvasMap;
    int roundCount = 0;
    public Animal animal;
    int timeOfDeath = 0;



    public FollowedAnimal(Animal animal, CanvasMap canvasMap){
        this.animal = animal;
        this.canvasMap = canvasMap;
    }

    public int getTimeOfDeath() {
        return timeOfDeath;
    }

    public int countRounds() {
        return roundCount;
    }

    public int countChildren(){
        return animal.children.stream().filter(a -> a.getAge() < roundCount).collect(Collectors.toCollection(ArrayList::new)).size();
    }

    public long countDescendands() {
        return animal.getDescendands().filter(a -> a.getAge() < roundCount).collect(Collectors.toCollection(ArrayList::new)).size();
    }


    public void timeGoesBy(){
        roundCount++;
        if(isDead() && timeOfDeath==0) timeOfDeath = roundCount;
    }

    public boolean isDead(){
        return animal.getEnergy() <=0;
    }


}
