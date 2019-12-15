package map;

import map.elements.animal.Animal;

public interface IPositionChangeObserver {
    public void positionChanged(Vector2d oldPosition, Animal animal);
}