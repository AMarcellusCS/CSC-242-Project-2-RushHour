package puzzles.jam.model;

public class Coordinate {
    /** represents x coordinate on a 2D plane */
    private int xCord;
    /** represents y coordinate on a 2D plane */
    private int yCord;

    /**
     * constructor for coordinate
     * @param x x cord
     * @param y y cord
     */
    public Coordinate(int x, int y){
        this.xCord = x;
        this.yCord = y;
    }

    /**
     * returns x cord
     * @return xCord
     */
    public int getX(){ return this.xCord;}
    /**
     * returns y cord
     * @return yCord
     */
    public int getY(){return this.yCord;}

    /**
     * increments x cord by 1
     */
    public void incrementX(){this.xCord++;}
    /**
     * increments y cord by 1
     */
    public void incrementY(){this.yCord++;}
    /**
     * decrements x cord by 1
     */
    public void decrementX(){this.xCord--;}
    /**
     * decrements y cord by 1
     */
    public void decrementY(){this.yCord--;}

    /**
     * checks if an object is equal to this coordinate
     * @param obj object to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Coordinate)
            return this.xCord == ((Coordinate) obj).getX() && this.yCord == ((Coordinate) obj).getY();
        return false;
    }

    /**
     * string representation of coordinate
     * @return string of x and y cords
     */
    @Override
    public String toString() {
        return "(" + this.xCord + ", " + this.yCord + ")";
    }
}
