package map;

import java.util.HashMap;
import java.util.Map;

public enum MapDirection {
    NORTH(0),
    NORTHWEST(1),
    NORTHEAST(2),
    SOUTH(3),
    SOUTHWEST(4),
    SOUTHEAST(5),
    WEST(6),
    EAST(7);

    private int value;
    private static Map map = new HashMap<>();

    MapDirection(int value) {
        this.value = value;
    }

    static {
        for(MapDirection mapDirection : MapDirection.values()){
            map.put(mapDirection.value,mapDirection);
        }
    }

    public static MapDirection valueOf(int mapDirection){
        return (MapDirection) map.get(mapDirection);
    }

    public int getValue(){
        return value;
    }

    public void add(MapDirection mapDirection){
        this.value = (this.value + mapDirection.getValue())%map.size();
    }



    public String toString() {
        switch (this) {
            case NORTH:
                return "Północ";
            case NORTHWEST:
                return "Północny zachód";
            case NORTHEAST:
                return "Północny wschód";
            case SOUTH:
                return "Południe";
            case SOUTHWEST:
                return "Południowy zachód";
            case SOUTHEAST:
                return "Południowy wschód";
            case EAST:
                return "Wschód";
            case WEST:
                return "Zachód";
        }
        return null;
    }

    public MapDirection next() {
        switch (this) {
            case NORTH:
                return NORTHWEST;
            case NORTHWEST:
                return WEST;
            case WEST:
                return SOUTHWEST;
            case SOUTHWEST:
                return SOUTH;
            case SOUTH:
                return SOUTHEAST;
            case SOUTHEAST:
                return EAST;
            case EAST:
                return NORTHEAST;
            case NORTHEAST:
                return NORTH;
        }
        return null;
    }

    public MapDirection previous() {
        switch (this) {
            case NORTH:
                return NORTHEAST;
            case NORTHEAST:
                return EAST;
            case EAST:
                return SOUTHEAST;
            case SOUTHEAST:
                return SOUTH;
            case SOUTH:
                return SOUTHWEST;
            case SOUTHWEST:
                return WEST;
            case WEST:
                return NORTHWEST;
            case NORTHWEST:
                return NORTH;
        }
        return null;
    }

    public Vector2d toUnitVector(){
        switch (this) {
            case NORTH:
                return new Vector2d(0, 1);
            case NORTHEAST:
                return new Vector2d(1,1);
            case NORTHWEST:
                return new Vector2d(1, -1);
            case SOUTH:
                return new Vector2d(0, -1);
            case SOUTHEAST:
                return new Vector2d(-1,1);
            case SOUTHWEST:
                return new Vector2d(-1,-1);
            case EAST:
                return new Vector2d(1, 0);
            case WEST:
                return new Vector2d(-1, 0);
        }
        return null;
    }
}
