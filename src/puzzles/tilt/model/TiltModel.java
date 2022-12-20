package puzzles.tilt.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TiltModel {
    /** the collection of observers of this model */
    private final List<Observer<TiltModel, String>> observers = new LinkedList<>();
    private static Collection<Configuration> currentSolution;
    private static final String LOADED = "loadComplete";
    private static final String RESET = "reset";
    private static final String LOADFAIL = "loadFail";
    private static final String MOVE = "move";
    private static final String SOLVED = "solved";
    private static final String HINT = "hint";
    private static final String INVALID = "invalid";
    private static final String NO_SOLUTION = "NS";

    /** the current configuration */
    private TiltConfig currentConfig;
    private String loadedLevel;
    private int moves;
    public TiltModel(String filename) throws FileNotFoundException {
        try {
            currentConfig = new TiltConfig(new File(filename));
            loadedLevel = filename;
            updateSolution();
        } catch (IOException e) {
            if (currentConfig == null) {
                throw new FileNotFoundException();
            } else {
                alertObservers(LOADFAIL + " " + filename);
            }
        }
        moves = 0;
    }

    /**
     * Attempts to tilt the board using the input.
     * alerts MOVE for non-invalid moves, INVALID otherwise.
     * @param d desired direction to tilt board using DIRECTION enum.
     */
    public void tryMove(TiltConfig.DIRECTION d) {
        // I couldn't remember how to use Optional so I did this instead.
        if (currentConfig.moveResult(d) == null) {
            alertObservers(INVALID);
            return;
        }
        currentConfig = currentConfig.moveResult(d);
        alertObservers(MOVE);
    }

    /**
     * Loads new board based on filename inputted.
     * alerts observers for LOADED and LOADFAIL for respective situations
     * @param filename string filename desired.
     */
    public void loadNew(String filename) {
        try {
            currentConfig = new TiltConfig(new File(filename));
            loadedLevel = filename;
            alertObservers(LOADED + " " + filename);
            updateSolution();
            moves = 0;
        } catch (IOException e) {
            alertObservers(LOADFAIL + " " + filename);
        }
    }

    /**
     * Resets the board to its original state, and recalculates the solution.
     */
    public void reset() {
        try {
            currentConfig = new TiltConfig(new File(loadedLevel));
        } catch (Exception ignored){} // ignore the exception here. would be a little weird if you load it once but then can't load it again yknow.
        updateSolution(); // Probably shouldn't ignore the exception but like seriously who would do that kind of edge case.
        alertObservers(RESET);
    }

    /**
     * Returns the necessary direction for the board to be tilted to make progress towards the solution.
     * @return null if there is no solution to the board state, DIRECTION otherwise.
     */
    public TiltConfig.DIRECTION getHint() {
        if (currentSolution != null) { // There IS a solution currently
            if (!currentSolution.contains(currentConfig)) { // And if it doesn't contain our current state, we need to recalculate the solution based on the current state.
                updateSolution();
                return getHint(); // Then try again.
            }
            // try each direction. If the direction tried results in progress towards the solution, return that direction.
            Object[] solution_path = currentSolution.toArray(); // God i'm gonna hate myself for this
            for (int i = 0; i < solution_path.length; i++) {
                if (solution_path[i].equals(currentConfig)) {
                    for (TiltConfig.DIRECTION d : TiltConfig.DIRECTION.values()) {
                        if (currentConfig.moveResult(d) != null && currentConfig.moveResult(d).equals(solution_path[i + 1])) {
                            return d;
                        }
                    }
                }
            }
        }
        // if we get here, then there is no solution to the current board state.
        return null;
    }

    /**
     * Only to be used in PTUI
     * Alerts NO_SOLUTION for board with no solution for current state
     * Otherwise, transitions the board to the next state towards the solution
     * Alerts MOVE after transition.
     */
    public void textHint() {
        if (getHint() == null) {
            alertObservers(NO_SOLUTION);
        } else {
            TiltConfig.DIRECTION d = getHint();
            currentConfig = currentConfig.moveResult(d);
            alertObservers(MOVE);
        }
    }

    /**
     * If the board is solved, returns true.
     * @return true if solved, false otherwise
     */
    public boolean isSolved() {
        return currentConfig.isSolution();
    }

    /**
     * Translates a string direction into the appropriate enum for the desired movement.
     * @param str N, S, E, or W strings.
     * @return associated DIRECTION
     */
    public TiltConfig.DIRECTION directionFromString(String str) {
        switch (str) {
            case "N" -> {
                return TiltConfig.DIRECTION.NORTH;
            }
            case "S" -> {
                return TiltConfig.DIRECTION.SOUTH;
            }
            case "E" -> { // <-- okay i don't know why but these need to be reversed. Weird.
                return TiltConfig.DIRECTION.WEST;
            }
            case "W" -> { // <--
                return TiltConfig.DIRECTION.EAST;
            }
        }
        return null; // would cause a crash if we weren't guaranteed valid inputs.
    }

    /**
     * Updates the current solution.
     * Expensive, do not call often for large boards.
     */
    private void updateSolution() {
        currentSolution = Solver.solveWithoutPrinting(currentConfig, new HashMap<>(), new LinkedList<>());
    }

    /**
     * Returns char[][] board state
     * @return char[][] board state
     */
    public char[][] getBoard() {
        return currentConfig.getBoard();
    }

    /**
     * Returns dimension of board
     * @return int dimension of board
     */
    public int getDimension() {
        return currentConfig.getDimension();
    }

    /**
     * Returns number of moves performed.
     * @return number of moves performed
     */
    public int getMoves() {
        return moves;
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<TiltModel, String> observer) {
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
     * Prints the current model's state. Intended use in PTUI
     */
    public void printModel() {
        System.out.println(currentConfig);
    }

    /**
     * Prints the instructions. Intended use in PTUI
     */
    public void printInstructions() {
        // lol
        System.out.println("h(int)              -- hint next move\nl(oad) filename     -- load new puzzle file\nt(ilt) {N|S|E|W}    -- tilt the board in the given direction\nq(uit)              -- quit the game\nr(eset)             -- reset the current game");
    }
}
