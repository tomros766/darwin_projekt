package map;

import map.elements.animal.Animal;
import map.elements.animal.GenoType;

public class World {
    public static void main(String[] args) {
        int width = 20;
        int height = 10;
        double startEnergy = 80;
        double moveEnergy = 3;
        double plantEnergy = 4;
        double jungleRatio = 0.3;

        RectangularMap map = new RectangularMap(width, height, moveEnergy, startEnergy, plantEnergy, jungleRatio);

        GenoType genoType = new GenoType();
        GenoType genoType2 = new GenoType();
        GenoType g3 = new GenoType();
        GenoType g4 = new GenoType();

        Vector2d position1 = new Vector2d(2, 3);
        Vector2d position2 = new Vector2d(2, 2);
        Vector2d p3 = new Vector2d(2, 1);
        Vector2d p4 = new Vector2d(2, 4);

        Animal adam = new Animal(position1, genoType, map, 80);
        Animal eve = new Animal(position2, genoType2, map, 80);
        Animal a1 = new Animal(p3, g3, map, 80);
        Animal a2 = new Animal(p4, g4, map, 80);


        for (int i = 0; i < 30; i++) {
            System.out.println(i + ":______________________________");
            map.circleOfLife();
            System.out.println(map);
            System.out.println("adam: " + adam.position + ", " + adam.getEnergy());
            System.out.println("eve: " + eve.position + ", " + eve.getEnergy());
            System.out.println("a1: " + a1.position + ", " + a1.getEnergy());
            System.out.println("a2: " + a2.position + ", " + a2.getEnergy());

        }
    }
}