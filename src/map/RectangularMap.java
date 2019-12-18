package map;

import map.elements.MapElement;
import map.elements.animal.Animal;
import map.elements.grass.Grass;
import map.elements.grass.GrassGrower;

import java.awt.geom.Path2D;
import java.util.*;
import java.util.stream.Collectors;

public class RectangularMap implements IPositionChangeObserver {

    final public int width, height;
    final public double moveEnergy;
    final public double startEnergy;
    final public double plantEnergy;
    final public double jungleRatio;
    final public MapBorders jungleBorders;
    protected List<Animal> animals = new ArrayList<>();
    protected HashMap<Vector2d, MapElement> occupied = new HashMap<>();

    public RectangularMap(int width, int height, double moveEnergy, double startEnergy, double plantEnergy, double jungleRatio) {
        this.width = width;
        this.height = height;
        this.moveEnergy = moveEnergy;
        this.startEnergy = startEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        jungleBorders = new MapBorders(this, jungleRatio);
    }

    public ArrayList<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }

    public Collection<MapElement> getElementswAnimals() {
        return occupied.values().stream().filter(e -> e.hasAnimals()).distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<MapElement> getGrasses() {
        return occupied.values().stream().filter(e -> e.hasGrass() && !e.hasAnimals()).collect(Collectors.toCollection(ArrayList::new));
    }

    public HashMap<Vector2d, MapElement> getOccupied() {
        return new HashMap<>(occupied);
    }

    public boolean place(Animal animal) {

        if (!this.isOccupied(animal.position))
            animal.addObserver(this);

        if (!this.occupied.containsKey(animal.position))
            this.occupied.put(animal.position, new MapElement(animal, this));

        this.occupied.get(animal.position).addAnimal(animal);
        this.animals.add(animal);
        return true;
    }

    private void placeGrass(Grass grass) {
        if (grass.position.follows(new Vector2d(0, 0)) && grass.position.precedes(new Vector2d(width - 1, height - 1))) {
            this.occupied.put(grass.position, new MapElement(grass, this));
        } else throw new IllegalArgumentException("Grass with such position cannot be grown on this map!");
    }

    public void growGrass() {
        Grass jungleCandidate = new GrassGrower().growGrass(jungleBorders, this);
        if (jungleCandidate != null) placeGrass(jungleCandidate);

        Grass steppeCandidate;
        int i = 0;
        do {
            steppeCandidate = new GrassGrower().growGrass(new MapBorders(this, 1), this);
            i++;
        }
        while (steppeCandidate != null && (steppeCandidate.position.follows(jungleBorders.lowerLeft) && steppeCandidate.position.precedes(jungleBorders.upperRight) && i < width * height));
        if (steppeCandidate != null && i < width * height) {
            placeGrass(steppeCandidate);
        }

    }

    public void circleOfLife() {


        ArrayList<Animal> dead = mementoMori();
        cleanUp(dead);


        for (Animal animal : animals) {
            animal.move(this);
        }

        for (MapElement luckilyAlive : occupied.values()) {
            if (luckilyAlive.hasAnimals() && luckilyAlive.hasGrass()) winnerEatsItAll(luckilyAlive);
        }

        for (MapElement fullAndEager : new ArrayList<>(occupied.values())) {
            if (fullAndEager.hasAnimals()) procreate(fullAndEager);
        }

        growGrass();

    }

    private ArrayList<Animal> mementoMori() {
        ArrayList<Animal> dead = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getEnergy() <= 0) dead.add(animal);
        }
        return dead;
    }

    private void cleanUp(ArrayList<Animal> dead) {
        for (Animal animal : dead) {
            animals.remove(animal);
            animal.perish(this);

            if (occupied.containsKey(animal.position))
                occupied.get(animal.position).removeAnimal(animal);

            if (occupied.containsKey(animal.position) && occupied.get(animal.position).getAnimals().isEmpty())
                occupied.remove(animal.position);
        }
    }

    private void winnerEatsItAll(MapElement luckilyAlive) {
        ArrayList<Animal> strongest = luckilyAlive.getStrongestAnimal();
        for (Animal animal : strongest) {
            animal.consume(luckilyAlive.getGrass().nutritionalValue / strongest.size());
        }
        luckilyAlive.grassEaten();
    }

    private void procreate(MapElement fullAndEager) {
        if (fullAndEager.getAnimals().size() > 1) {
            Animal[] parents = fullAndEager.getCoupleAlpha();

            if (canProcreate(parents[0], parents[1])) {
                ArrayList<Vector2d> potentialNursery = getPotentialNursery(parents[0], parents[1]);
                if (potentialNursery.size() > 0) {
                    parents[0].procreate(this, parents[1], potentialNursery.get(new Random().nextInt(potentialNursery.size())));
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
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                potential = new Vector2d(parent.position.x + i, parent.position.y + j);
                if (!isOccupied(potential)) potentialSpots.add(potential);
            }
        }
        return potentialSpots;
    }

    public boolean isOccupied(Vector2d position) {
        if (objectAt(position) != null) return true;
        return false;
    }

    public Object objectAt(Vector2d position) {
        if (this.occupied.containsKey(position) && this.occupied.get(position) != null)
            return this.occupied.get(position);
        return null;
    }

    public void positionChanged(Vector2d oldPosition, Animal animal) {
        if (this.occupied.containsKey(oldPosition)) {
            this.occupied.get(oldPosition).removeAnimal(animal);
            parsePosition(animal);
            if (this.occupied.get(oldPosition).getAnimals().isEmpty()) this.occupied.remove(oldPosition);
        }

        if (!this.occupied.containsKey(animal.position))
            this.occupied.put(animal.position, new MapElement(animal, this));
        else this.occupied.get(animal.position).addAnimal(animal);
    }

    public String toString() {
        MapVisualizer visualizer = new MapVisualizer(this);
        return visualizer.draw(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }

    private void parsePosition(Animal animal) {
        if(animal.position.x < 0) animal.position.x = (width - animal.position.x) % width;
        if(animal.position.y < 0) animal.position.y = (height - animal.position.y) % height;
        if(animal.position.x >= width) animal.position.x = animal.position.x % width;
        if(animal.position.y >= height) animal.position.y = animal.position.y % height;
    }

    public int getAvgEnergy(){
        ArrayList<Double> energies = this.animals.stream().map(animal -> animal.getEnergy()).collect(Collectors.toCollection(ArrayList::new));
        return (int) (energies.stream().reduce(0.0, (a,b) -> a+b)/energies.size());
    }

}
