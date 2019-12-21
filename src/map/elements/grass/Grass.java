package map.elements.grass;

import map.MapTools.Vector2d;

public class Grass {
    public final Vector2d position;
    public final double nutritionalValue;

    public Grass(Vector2d position, double plantEnergy){
        this.position = position;
        this.nutritionalValue = plantEnergy;
    }

    public String toString(){
        return "g";
    }
}
