package map;

import map.MapTools.Vector2d;
import map.elements.animal.Animal;

public interface IPositionChangeObserver {
    public void positionChanged(Animal animal, Vector2d newPosition);
}