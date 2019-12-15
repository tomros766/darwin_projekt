package map;

import map.elements.animal.Animal;
import map.elements.grass.Grass;
import map.elements.grass.GrassGrower;

import java.util.*;

public class RectangularMap implements IPositionChangeObserver {

    final public int width, height;
    final public double moveEnergy;
    final public double startEnergy;
    final public double plantEnergy;
    final public double jungleRatio;
    final public MapBorders jungleBorders;
    protected List<Animal> animals = new ArrayList<>();
    protected HashMap<Vector2d, Grass> grasses = new HashMap<>();
    protected HashMap<Vector2d, ArrayList<Animal>> occupied = new HashMap<>();



    public RectangularMap(int width, int height,double  moveEnergy,double startEnergy, double plantEnergy, double jungleRatio){
        this.width = width;
        this.height = height;
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        jungleBorders = new MapBorders(this,jungleRatio);
    }

    public HashMap<Vector2d, Grass> getGrasses() {
        return new HashMap<>(grasses);
    }

    public HashMap<Vector2d, ArrayList<Animal>> getOccupied() {
        return new HashMap<>(occupied);
    }

    public boolean place(Animal animal) throws IllegalArgumentException{

        if(this.isOccupied(animal.position)) throw new IllegalArgumentException(animal.position.toString() + ": place is occupied");
        animal.addObserver(this);

        if(!this.occupied.containsKey(animal.position)) this.occupied.put(animal.position,new ArrayList<Animal>());

        this.occupied.get(animal.position).add(animal);
        this.animals.add(animal);
        return true;
    }

    private void placeGrass(Grass grass){
        if(grass.position.follows(new Vector2d(0,0)) && grass.position.precedes(new Vector2d(width-1,height-1))) {
            this.grasses.put(grass.position,grass);
        }
        else throw new IllegalArgumentException("Grass with such position cannot be grown on this map!");
    }

    public void growGrass(){
        Grass jungleCandidate = new GrassGrower().growGrass(jungleBorders, this);
        if(jungleCandidate != null) placeGrass(jungleCandidate);

        Grass steppeCandidate;
        int i = 0;
        do{
            steppeCandidate = new GrassGrower().growGrass(new MapBorders(this,1),this);
            i++;
        }
        while(steppeCandidate!= null && steppeCandidate.position.follows(jungleBorders.lowerLeft) && steppeCandidate.position.precedes(jungleBorders.upperRight) && i < width*height);
        if(i < width*height){
            placeGrass(steppeCandidate);
        }

    }

    public void circleOfLife() {


        ArrayList<Animal> dead = mementoMori();
        cleanUp(dead);


        for(Animal animal : animals){
            animal.move(this);
        }

        for(ArrayList<Animal> luckilyAlive : occupied.values()){
            winnerEatsItAll(luckilyAlive);
        }

        for(ArrayList<Animal> fullAndEager : new ArrayList<>(occupied.values())){
            procreate(fullAndEager);
        }

        growGrass();

    }

    private ArrayList<Animal> mementoMori() {
        ArrayList<Animal> dead = new ArrayList<>();
        for(Animal animal : animals){
            if(animal.getEnergy() <= 0) dead.add(animal);
        }
        return dead;
    }

    private void cleanUp(ArrayList<Animal> dead){
        for(Animal animal: dead){
            animals.remove(animal);
            animal.perish(this);
            occupied.get(animal.position).remove(animal);
            if(occupied.get(animal.position).isEmpty()) occupied.remove(animal.position);
        }
    }

    private void winnerEatsItAll(ArrayList<Animal> luckilyAlive) {
        if(luckilyAlive.size()>0){
            Grass unluckilyEaten = grasses.getOrDefault(luckilyAlive.get(0).position,null);
            if(unluckilyEaten != null) {
                Collections.sort(luckilyAlive);
                int i = 0;
                double maxEnergy = luckilyAlive.get(0).getEnergy();
                while(i<luckilyAlive.size() && luckilyAlive.get(i).getEnergy() == maxEnergy) i++;
                for(int j = 0; j < i; j++) {
                    luckilyAlive.get(j).consume(unluckilyEaten.nutritionalValue/i);
                }
                grasses.remove(unluckilyEaten.position);
            }
        }
    }

    private void procreate(ArrayList<Animal> fullAndEager){
        if(fullAndEager.size() >= 2){
            Collections.sort(fullAndEager);
            int i = 1;
            while(i < fullAndEager.size() && fullAndEager.get(i).getEnergy() == fullAndEager.get(i-1).getEnergy()) i++;
            Animal dominant;
            Animal subservient;
            if(i > 1) {
                dominant = fullAndEager.get(new Random().nextInt(i));
                do{
                    subservient = fullAndEager.get(new Random().nextInt(i));
                }
                while(subservient != dominant);
            }
            else {
                dominant = fullAndEager.get(0);
                subservient = fullAndEager.get(1);
            }

            if(canProcreate(dominant,subservient)){
                ArrayList<Vector2d> potentialNursery = getPotentialNursery(dominant, subservient);
                if(potentialNursery.size() > 0){
                    dominant.procreate(this, subservient, potentialNursery.get(new Random().nextInt(potentialNursery.size())));
                }
            }
        }


    }

    private boolean canProcreate(Animal animal, Animal konkubent) {
        return animal.position.equals(konkubent.position) && animal.strongEnough(this) && konkubent.strongEnough(this);
    }

    private ArrayList<Vector2d> getPotentialNursery(Animal parent, Animal konkubent) {
        ArrayList<Vector2d> potentialSpots = new ArrayList<>();
        potentialSpots.clear();
        Vector2d potential;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                potential = new Vector2d(parent.position.x + i, parent.position.y + j);
                if(!isOccupied(potential)) potentialSpots.add(potential);
            }
        }
        System.out.println(potentialSpots + " tu by sie dao");
        return potentialSpots;
    }

    public boolean isOccupied(Vector2d position) {
        if(objectAt(position)!=null) return true;
        return false;
    }

    public Object objectAt(Vector2d position) {
        if (this.occupied.containsKey(position) && this.occupied.get(position)!=null) return this.occupied.get(position);
        if (this.grasses.containsKey(position) && this.grasses.get(position)!=null) return this.grasses.get(position);
        return null;
    }

    public void positionChanged(Vector2d oldPosition,Animal animal){
        this.occupied.get(oldPosition).remove(animal);
        if(this.occupied.get(oldPosition).isEmpty()) this.occupied.remove(oldPosition);

        if(!this.occupied.containsKey(animal.position)) this.occupied.put(animal.position, new ArrayList<Animal>());
        this.occupied.get(animal.position).add(animal);
    }

    public String toString(){
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(new Vector2d(0,0), new Vector2d(width-1, height-1));
    }
}
