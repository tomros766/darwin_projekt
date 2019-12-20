package map;

import map.elements.animal.Animal;

public interface IPositionChangeObserver {
    public void positionChanged(Animal animal, Vector2d newPosition);
}