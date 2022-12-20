package puzzles.jam.model;

import puzzles.common.Observer;
import puzzles.common.solver.BetterSolver;
import puzzles.common.solver.Configuration;
import puzzles.jam.gui.JamGUI;

import java.io.File;
import java.util.*;

public class JamModel {

    /** the collection of observers of this model */
    private final List<Observer<JamModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private JamConfig currentConfig;
    /** current configurations file*/
    private File currentFile;
    /** if the game is won */
    private boolean win = false;

    /**
     * JamModel constructor
     * @param file file to load game from
     */
    public JamModel(File file) {
        this.loadGame(file);
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /**
     * Model toString which calls current config toString
     * @return string of current config
     */
    public String toString(){
        return this.currentConfig.toString();
    }

    /**
     * sets current config to solved path then alerts observers
     */
    public void hint() {
        if(!win) {
            ArrayList<Configuration> path = BetterSolver.solvePath(this.currentConfig);
            if(path == null) {
                this.alertObservers("No Solution!");
                return;
            }
            this.currentConfig = (JamConfig) path.get(path.size() - 2);
            this.currentConfig.updateCars();
            if (this.currentConfig.isSolution()) {
                this.win = true;
                this.alertObservers("You WIN! Good for you... Maybe try without a hint...");
            }
            else
                this.alertObservers("Hint Processed.");
        }
        else
            this.alertObservers("You already won!");
    }

    /**
     * loads a game file
     * @param file file to create new config
     */
    public void loadGame(File file) {
        if(file == null) {
            this.alertObservers("No file chosen!");
            return;
        }
        this.currentConfig = new JamConfig(file.getPath());
        this.win = this.currentConfig.isSolution();
        this.currentFile = file;
        this.alertObservers("Loading: " + file.getName());
    }

    /**
     * resets current config to stored file
     */
    public void reset() {
        this.currentConfig = new JamConfig(currentFile.getPath());
        this.win = this.currentConfig.isSolution();
        this.alertObservers("Resetting for: " + this.currentFile.getName());
    }

    /**
     * process move then updates observers
     * @param sx selected x cord
     * @param sy selected y cord
     * @param dx destination x cord
     * @param dy destination y cord
     */
    public void move(int sx, int sy, int dx, int dy) {
        if(!this.moveWithinBounds(sx, sy, dx, dy)) {
            this.alertObservers("Command out of bounds.");
            return;
        }
        if(!win) {
            try{
                JamConfig board = this.currentConfig;
                JamCar car = board.getCar(board.charAtCord(sx, sy));
                if (board.canMoveSelectToDestination(sx, sy, dx, dy)) {
                    car.moveto(dx, dy);
                    board.updateBoard();
                    this.alertObservers("Moved car " + car.getId() + " to: (" + dx + ", " + dy + ")");
                    if (board.isSolution())
                        this.win = true;
                }
                else{
                    this.alertObservers(this.getButtonText(sx, sy) + " cannot move there!");
                }
            }
            catch (NullPointerException e) {
                this.alertObservers("No Car Selected!");
            }
        }
        if(win){
            this.alertObservers("You WIN! Good for you.");
        }
    }

    /**
     * checks if selected cords and destination are within the bounds of the board.
     * @param sx selected x cord
     * @param sy selected y cord
     * @param dx destination x cord
     * @param dy destination y cord
     * @return true if inputs within bounds
     */
    private boolean moveWithinBounds(int sx, int sy, int dx, int dy) {
        return sx >= 0 && sx < this.getXDimension()
                && dx >= 0 && dx < this.getXDimension()
                && sy >= 0 && sy < this.getYDimension()
                && dy >= 0 && dy < this.getYDimension();
    }

    /**
     * returns config board x dimension
     * @return x dimension
     */
    public int getXDimension(){return this.currentConfig.getXDim();}
    /**
     * returns config board y dimension
     * @return y dimension
     */
    public int getYDimension(){return this.currentConfig.getYDim();}

    /**
     * returns the css color value string of a board value
     * @param x x cord value
     * @param y y cord value
     * @return css color
     */
    public String getColor(int x, int y){
        char val = this.currentConfig.charAtCord(x,y);
        if(val == JamConfig.BLANK)
            return JamGUI.WHITE;
        else
            return this.currentConfig.getCar(val).getColor();
    }

    /**
     * returns the character at a cord if it is not a blank space
     * @param x x cord to check
     * @param y y cord to check
     * @return value at coordinate
     */
    public String getButtonText(int x, int y) {
        char val = this.currentConfig.charAtCord(x,y);
        if(val == JamConfig.BLANK)
            return "";
        return val+"";
    }

    /**
     * sets the border of empty spaces to black or the color of a filled space
     * @param buttonColor css string color of button
     * @return css string color of border
     */
    public String getBorder(String buttonColor) {
        if(buttonColor.equals(JamGUI.WHITE))
            return JamGUI.BLACK;
        return buttonColor;
    }

    /**
     * returns string path of car image file
     * @param x x cord of button
     * @param y y cord of button
     * @return image file path string
     */
    public String getCarImage(int x, int y) {
        char val = this.currentConfig.charAtCord(x,y);
        if(val != JamConfig.BLANK)
            return this.currentConfig.getCar(val).getImage().replaceAll("\\\\", "/");
        return null;
    }

    /**
     * checks if coordinate is the front of a car object
     * @param x x cord
     * @param y y cord
     * @return true if front of a car
     */
    public boolean isFront(int x, int y) {
        char val = this.currentConfig.charAtCord(x,y);
        return this.currentConfig.getCar(val).getFrontOfCar().equals(new Coordinate(x,y));
    }

    /**
     * checks if there is a car at selected coordinate
     * @param x x cord
     * @param y y cord
     * @return true if there is a car at cord
     */
    public boolean carAt(int x, int y) {
        return this.currentConfig.charAtCord(x,y) != JamConfig.BLANK;
    }

    /**
     * returns if the game is won
     * @return win boolean
     */
    public boolean isWon() {
        return this.win;
    }
}
