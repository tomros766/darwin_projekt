package map;

import map.elements.animal.Animal;
import map.elements.animal.GenoType;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapStatistics {
    RectangularMap map;
    public Animal animalFollowed;
    private int countDeadAnimals = 0;
    private double avgLifeTime = 0.0;

    public MapStatistics(RectangularMap map){
        this.map = map;
    }

    public double getAvgLifeTime() {
        return avgLifeTime;
    }

    public void updateAvgLifeTime(Animal animal){
        this.avgLifeTime = (avgLifeTime*countDeadAnimals + animal.getAge())/(countDeadAnimals+1);
        countDeadAnimals++;
    }

    public GenoType getDominantGenoType(){
        if(map.animals !=null){
            ArrayList<GenoType> genoTypes = map.animals.stream().map(a -> a.getGenoType()).collect(Collectors.toCollection(ArrayList::new));
            return genoTypes.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey).orElse(null);
        }
        else return null;
    }

    public int countDominantAnimals(){
        GenoType dominantGenotype = getDominantGenoType();
        return map.animals.stream().filter(a -> a.getGenoType().equals(dominantGenotype)).collect(Collectors.toCollection(ArrayList::new)).size();
    }

    public int getAvgChildrenCount(){
        ArrayList<Integer> childrenCount = map.animals.stream().map(animal -> animal.getChildren().size()).collect(Collectors.toCollection(ArrayList::new));
        return childrenCount.stream().reduce(0, (a,b) -> a+b)/childrenCount.size();
    }

    public int getAvgEnergy(){
        ArrayList<Double> energies = map.animals.stream().map(animal -> animal.getEnergy()).collect(Collectors.toCollection(ArrayList::new));
        return (int) (energies.stream().reduce(0.0, (a,b) -> a+b)/energies.size());
    }
}
