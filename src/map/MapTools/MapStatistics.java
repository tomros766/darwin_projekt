package map.MapTools;

import map.RectangularMap;
import map.elements.animal.Animal;
import map.elements.animal.FollowedAnimal;
import map.elements.animal.GenoType;
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapStatistics {
    RectangularMap map;
    public FollowedAnimal animalFollowed;
    private int round = 0;
    private int countDeadAnimals = 0;
    private double avgLifeTime = 0.0;
    private double avgAnimalsCount = 0;
    private double avgGrassCount = 0;
    private HashMap <GenoType, Integer> dominantGenoTypes = new HashMap<>();
    private double avgAvgEnergy = 0;
    private double avgAvgChildrenCount = 0;


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
        if(map.getAnimals() !=null){
            ArrayList<GenoType> genoTypes = map.getAnimals().stream().map(a -> a.getGenoType()).collect(Collectors.toCollection(ArrayList::new));
            return genoTypes.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet().stream().max(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey).orElse(null);
        }
        else return null;
    }

    public int countDominantAnimals(){
        GenoType dominantGenotype = getDominantGenoType();
        return map.getAnimals().stream().filter(a -> a.getGenoType().equals(dominantGenotype)).collect(Collectors.toCollection(ArrayList::new)).size();
    }

    public int getAvgChildrenCount(){
        ArrayList<Integer> childrenCount = map.getAnimals().stream().map(animal -> animal.getChildren().size()).collect(Collectors.toCollection(ArrayList::new));
        return childrenCount.stream().reduce(0, (a,b) -> a+b)/childrenCount.size();
    }

    public int getAvgEnergy(){
        ArrayList<Double> energies = map.getAnimals().stream().map(animal -> animal.getEnergy()).collect(Collectors.toCollection(ArrayList::new));
        return (int) (energies.stream().reduce(0.0, (a,b) -> a+b)/energies.size());
    }

    public int countAnimals(){
        return map.getAnimals().size();
    }

    public int countGrasses(){
        return map.getGrasses().size();
    }

    public void updateAvgs() {
        avgAnimalsCount = (avgAnimalsCount * round + countAnimals()) / (round + 1);
        avgAvgChildrenCount = (avgAvgChildrenCount * round + getAvgChildrenCount()) / (round + 1);
        avgGrassCount = (avgGrassCount * round + countGrasses()) / (round + 1);
        if (!dominantGenoTypes.containsKey(getDominantGenoType())) dominantGenoTypes.put(getDominantGenoType(), 1);
        else {
            int count = dominantGenoTypes.get(getDominantGenoType());
            dominantGenoTypes.replace(getDominantGenoType(), count + 1);
        }
        avgAvgEnergy = (avgAvgEnergy * round + getAvgEnergy()) / (round + 1);
        round++;
    }

    public double getAvgAnimalsCount() {
        return avgAnimalsCount;
    }

    public double getAvgAvgChildrenCount() {
        return avgAvgChildrenCount;
    }

    public double getAvgAvgEnergy() {
        return avgAvgEnergy;
    }

    public double getAvgGrassCount() {
        return avgGrassCount;
    }

    public GenoType getMostDominantGenoType(){
        GenoType winner = null;
        int record = 0;
        for(GenoType genoType : dominantGenoTypes.keySet()){
            if(dominantGenoTypes.get(genoType) > record) {
                record = dominantGenoTypes.get(genoType);
                winner = genoType;
            }
        }

        return winner;
    }

    public int getRound(){
        return round;
    }
}
