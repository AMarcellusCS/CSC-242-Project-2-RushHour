package puzzles.jam.model;

public class JamCar {
    /** Coordinate for the back of the car*/
    private Coordinate backOfCar;
    /** Coordinate for the front of the car*/
    private Coordinate frontOfCar;
    /** boolean for if the car is vertical or horizontal*/
    private boolean vertical;
    /** Unique ID for the car*/
    private char id;

    /**
     * Constructor for a JamCar
     * @param backOfCar coordinate for the back of a car
     * @param frontOfCar coordinate for the front of a car
     * @param id the car's ID character
     */
    public JamCar(Coordinate backOfCar, Coordinate frontOfCar, char id){
        this.backOfCar = backOfCar;
        this.frontOfCar = frontOfCar;
        this.id = id;
        this.vertical = backOfCar.getX() != frontOfCar.getX();
    }

    /**
     * returns the cars id character
     * @return id
     */
    public char getId() {
        return this.id;
    }

    /**
     * returns the coordinate of the back of the car
     * @return backOfCar
     */
    public Coordinate getBackOfCar() {
        return this.backOfCar;
    }

    /**
     * returns the front of a car coordinate
     * @return frontOfCar
     */
    public Coordinate getFrontOfCar() {
        return this.frontOfCar;
    }

    /**
     * returns if the car is vertical or horizontal
     * @return true if vertical
     */
    public boolean isVertical() {
        return this.vertical;
    }

    /**
     * returns the color of the car from the static util using the ID
     * @return the cars color as a css string
     */
    public String getColor(){return JamUtil.getColor(this.id);}

    /**
     * checks if two cars are equal
     * @param obj obj to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JamCar)
            return this.id == ((JamCar) obj).getId() && this.backOfCar.equals(((JamCar) obj).getBackOfCar())
                && this.frontOfCar.equals(((JamCar) obj).getFrontOfCar());
        return false;
    }

    /**
     * string for a JamCar object
     * @return string representation of a JamCar
     */
    public String toString(){
        return this.id + ": " + this.backOfCar.toString() + "-" + this.frontOfCar.toString();
    }

    /**
     * checks if a car can move to a location based on location and verticality
     * @param x destination x cord
     * @param y destination y cord
     * @return true if car can move to destination
     */
    public boolean canMoveTo(int x, int y) {
        if(this.vertical)
            return this.backOfCar.getY() == y;
        return this.backOfCar.getX() == x;
    }

    /**
     * moves a car forward
     */
    public void moveForward(){
        if(this.vertical) {
            this.getBackOfCar().decrementX();
            this.getFrontOfCar().decrementX();
        }
        else {
            this.getBackOfCar().incrementY();
            this.getFrontOfCar().incrementY();
        }
    }

    /**
     * moves a car backward
     */
    public void moveBackward(){
        if(this.vertical) {
            this.getBackOfCar().incrementX();
            this.getFrontOfCar().incrementX();
        }
        else {
            this.getBackOfCar().decrementY();
            this.getFrontOfCar().decrementY();
        }
    }

    /**
     * moves a car forward or backward until it reaches destination.
     * @param dx destination x cord
     * @param dy destination y cord
     */
    public void moveto(int dx, int dy) {
        if(!this.canMoveTo(dx, dy))
            return;
        if(this.vertical){
            while(this.frontOfCar.getX() < dx)
                this.moveBackward();
            while(this.backOfCar.getX() > dx)
                this.moveForward();
        }
        else{
            while(this.frontOfCar.getY() < dy)
                this.moveForward();
            while(this.backOfCar.getY() > dy)
                this.moveBackward();
        }
    }

    /**
     * returns a cars image path
     * @return string path to file
     */
    public String getImage() {return JamUtil.getImage(this.id, this.vertical);}
}
