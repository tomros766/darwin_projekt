package map.MapTools;

public class Vector2d {
    public int x, y;

    public Vector2d (int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals (Object other){
        if (this == other)
            return true;
        if(!(other instanceof Vector2d))
            return false;

        Vector2d that = (Vector2d) other;
        return (this.x == that.x && this.y == that.y);
    }
    @Override
    public int hashCode(){
        int hash = 13;
        hash += this.x * 31;
        hash += this.y * 17;
        return hash;
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    public boolean precedes (Vector2d other){
        return (this.x <= other.x && this.y <= other.y);
    }

    public boolean follows (Vector2d other){
        return (this.x >= other.x && this.y >= other.y);
    }


    public Vector2d add (Vector2d other) {
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    public Vector2d subtract (Vector2d other) {
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    public Vector2d copy(){
        return new Vector2d(this.x, this.y);
    }
}
