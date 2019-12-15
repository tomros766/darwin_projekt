package map;

public class MapBorders {
    final public Vector2d lowerLeft;
    final public Vector2d upperRight;
    final public int width;
    final public int height;

    public MapBorders(RectangularMap map, double ratio){
        this.lowerLeft = new Vector2d((int) (map.width*(1-Math.sqrt(ratio))/2),(int) (map.height*(1-Math.sqrt(ratio))/2));
        this.upperRight = new Vector2d((int) (map.width*(1+Math.sqrt(ratio))/2),(int) (map.height*(1+Math.sqrt(ratio))/2));
        width = upperRight.x - lowerLeft.x;
        height = upperRight.y - lowerLeft.y;
    }
    public int getArea(){
        return width*height;
    }
}
