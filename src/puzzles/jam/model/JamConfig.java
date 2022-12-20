package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Configuration for Rush Hour game.
 * @author Adrian Marcellus
 */
public class JamConfig implements Configuration {
    /** Array representing the game board */
    private char[][] board;
    /** HashMap of car objects */
    private HashMap<Character, JamCar> cars;
    /** x dimension of board */
    private int xDim;
    /** y dimension of board */
    private int yDim;
    /** main car ID */
    private final char MAIN_CAR = 'X';
    /** blank space ID */
    public static final char BLANK = '.';

    /**
     * Initial Jam Config constructor using file
     * @param filePath file path of game to load
     */
    public JamConfig(String filePath) {
        this.cars = new HashMap<Character, JamCar>();
        try(Scanner in = new Scanner(new File(filePath))){
            String[] fields = in.nextLine().split(" ");
            this.xDim = Integer.parseInt(fields[0]);
            this.yDim = Integer.parseInt(fields[1]);
            in.nextLine();
            while(in.hasNextLine()){
                fields = in.nextLine().split(" ");
                this.cars.put(fields[0].charAt(0), new JamCar(
                        new Coordinate(Integer.parseInt(fields[1]), Integer.parseInt(fields[2])),
                        new Coordinate(Integer.parseInt(fields[3]), Integer.parseInt(fields[4])),
                        fields[0].charAt(0)
                        ));
            }
        } catch (FileNotFoundException | IndexOutOfBoundsException e) {
            System.out.println("File: " + filePath +  " not found.");
        }
        this.board = new char[this.xDim][this.yDim];
        this.updateBoard();
    }

    /**
     * Jam config constructor used in getting neighbors.
     * @param board game board
     * @param xDim x dimension of game board
     * @param yDim y dimension of game board
     */
    private JamConfig(char[][] board, int xDim, int yDim){
        this.xDim = xDim;
        this.yDim = yDim;
        this.board = board;
    }

    /**
     * public class to update config instance 2d character array board from car objects
     */
    public void updateBoard() {this.board = getBoardFromCars(this.cars);}

    /**
     * returns a game board pulled from collection of car objects
     * @param cars cars to pull board from
     * @return 2d character array board
     */
    private char[][] getBoardFromCars(HashMap<Character, JamCar> cars) {
        char[][] board = new char[this.xDim][this.yDim];
        for(char[] row : board)
            Arrays.fill(row, '.');
        for(JamCar car : cars.values()){
            for(int x = car.getBackOfCar().getX(); x <= car.getFrontOfCar().getX(); x++){
                for(int y = car.getBackOfCar().getY(); y <= car.getFrontOfCar().getY(); y++){
                    board[x][y] = car.getId();
                }
            }
        }
        return board;
    }

    /**
     * public class to update config instance of cars from 2d array character board
     */
    public void updateCars(){this.cars = getCarsFromBoard();}

    /**
     * returns a car map pulled from instance 2d character array board
     * @return character, car object hashmap
     */
    private HashMap<Character, JamCar> getCarsFromBoard(){
        HashMap<Character, JamCar> cars = new HashMap<Character, JamCar>();
        for(int x = 0; x < this.xDim; x++){
            for(int y = 0; y < this.yDim; y++) {
                if(this.board[x][y] != BLANK && !cars.containsKey(this.board[x][y])) {
                    char id = this.board[x][y];
                    Coordinate backOfVehicle = new Coordinate(x, y);
                    int iterator = 1;
                    while(x+iterator < this.xDim && this.board[x+iterator][y] == id)
                        iterator++;
                    if (iterator != 1)
                        cars.put(id, new JamCar(backOfVehicle, new Coordinate(x + iterator-1, y), id));
                    iterator = 1;
                    while(y+iterator < this.yDim && this.board[x][y+iterator] == id)
                        iterator++;
                    if(iterator != 1)
                        cars.put(id, new JamCar(backOfVehicle, new Coordinate(x, y + iterator-1), id));
                }
            }
        }
        return cars;
    }

    /**
     * returns if configuration instance is solution to puzzle
     * @return true if solution
     */
    @Override
    public boolean isSolution() {
        for(int x = 0; x < this.xDim; x++)
            if(this.board[x][yDim-1] == MAIN_CAR) {
                return true;
            }
        return false;
    }

    /**
     * gets the neighbors of the current configuration.
     * @return collection of configuration objects
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> list = new ArrayList<Configuration>();
        HashMap<Character, JamCar> newMap = this.getCarsFromBoard();
        for(JamCar car : newMap.values()){
            if(this.canMoveForward(car)){
                newMap = this.getCarsFromBoard();
                newMap.get(car.getId()).moveForward();
                list.add(new JamConfig(this.getBoardFromCars(newMap), this.xDim, this.yDim));
            }
            if(this.canMoveBackward(car)){
                newMap = this.getCarsFromBoard();
                newMap.get(car.getId()).moveBackward();
                list.add(new JamConfig(this.getBoardFromCars(newMap), this.xDim, this.yDim));
            }
        }
        return list;
    }

    /**
     * checks if JamCar object can move backward on the board
     * @param car car object to check
     * @return true if car can move backward
     */
    private boolean canMoveBackward(JamCar car) {
        if(car.isVertical())
            return car.getFrontOfCar().getX() < this.xDim-1 &&
                    this.board[car.getFrontOfCar().getX()+1][car.getFrontOfCar().getY()] == BLANK;
        else
            return car.getBackOfCar().getY() > 0 &&
                    this.board[car.getBackOfCar().getX()][car.getBackOfCar().getY()-1] == BLANK;
    }
    /**
     * checks if JamCar object can move forward on the board
     * @param car car object to check
     * @return true if car can move forward
     */
    private boolean canMoveForward(JamCar car) {
        if(car.isVertical())
            return car.getBackOfCar().getX() > 0 &&
                    this.board[car.getBackOfCar().getX()-1][car.getBackOfCar().getY()] == BLANK;
        else
            return car.getFrontOfCar().getY() < this.yDim-1 &&
                    this.board[car.getFrontOfCar().getX()][car.getFrontOfCar().getY()+1] == BLANK;
    }

    /**
     * returns character at coordinate on board
     * @param x x cord to check
     * @param y y cord to check
     * @return character at x and y coordinates
     */
    public char charAtCord(int x, int y){return this.board[x][y];}

    /**
     * returns a JamCar object from the car hashmap using a character key
     * @param car character key to pull form map
     * @return JamCar object in map
     */
    public JamCar getCar(char car) { return this.cars.get(car);}

    /**
     * returns x dimension of board
     * @return xDim
     */
    public int getXDim(){return this.xDim;}
    /**
     * returns y dimension of board
     * @return yDim
     */
    public int getYDim(){return this.yDim;}

    /**
     * checks if obj is equal to this object
     * @param obj obj to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JamConfig){
            return Arrays.deepEquals(this.board, ((JamConfig) obj).board);
        }
        return false;
    }

    /**
     * hashcode of this object
     * @return hashcode value
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }

    /**
     * toString for this object
     * @return string of object
     */
    public String toString(){
        String str = "\n";
        for(char[] arr : this.board){
            for(char chara : arr){
                str += " " + chara;
            }
            str += "\n";
        }
        return str;
    }

    /**
     * returns if a selected car can move to a destination
     * @param sx selected x cord
     * @param sy selected y cord
     * @param dx destination x cord
     * @param dy destination y cord
     * @return true if car object can move to destination.
     */
    public boolean canMoveSelectToDestination(int sx, int sy, int dx, int dy) {
        return this.board[sx][sy] != BLANK && this.cars.get(this.board[sx][sy]).canMoveTo(dx,dy)
                && blankPath(sx, sy, dx, dy);
    }

    /**
     * Checks if a car has a blank path to destination on board.
     * @param sx selected x cord
     * @param sy selected y cord
     * @param dx destination x cord
     * @param dy destination y cord
     * @return return true if board path empty
     */
    private boolean blankPath(int sx, int sy, int dx, int dy) {
        JamCar car = this.cars.get(this.board[sx][sy]);
        if(car.isVertical()){
            if(sx < dx){
                for(int x = sx+1; x <= dx; x++){
                    if(this.board[x][dy] != BLANK && this.board[x][dy] != car.getId())
                        return false;
                }
            }
            else if(sx > dx){
                for(int x = sx-1; x >= dx; x--){
                    if(this.board[x][dy] != BLANK && this.board[x][dy] != car.getId())
                        return false;
                }
            }
        }
        else{
            if(sy < dy){
                for(int y = sy+1; y <= dy; y++){
                    if(this.board[dx][y] != BLANK && this.board[dx][y] != car.getId())
                        return false;
                }
            }
            else if(sy > dy){
                for(int y = sy-1; y >= dy; y--){
                    if(this.board[dx][y] != BLANK && this.board[dx][y] != car.getId())
                        return false;
                }
            }
        }
        return true;
    }
}
