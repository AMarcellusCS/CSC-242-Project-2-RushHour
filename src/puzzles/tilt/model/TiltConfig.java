package puzzles.tilt.model;


import puzzles.common.solver.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class TiltConfig implements Configuration {
    private int dimension;
    private static int blues; // can be static but not final, since it's the same between configs of the same puzzle
    private char[][] board;
    private static final char BLOCK = '*';
    private static final char EMPTY = '.';
    private static final char HOLE = 'O';
    private static final char GREEN = 'G';
    private static final char BLUE = 'B';
    public enum DIRECTION {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public TiltConfig(File file) throws FileNotFoundException{
        try {
            Scanner reader = new Scanner(file);
            dimension = Integer.parseInt(reader.nextLine()); // Declare size of board
            board = new char[dimension][dimension]; // And initialize
            int i = 0;
            while (reader.hasNext()) {
                String str = reader.nextLine();
                for (int j = 0; j < board.length; j++) {
                    char in = str.charAt(j*2);
                    board[i][j] = in;
                }
                i++;
            }
            blues = countBlues(board);
            reader.close();
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
    }
    private TiltConfig(char[][] board, int dim) {
        this.board = board;
        dimension = dim;
    }

    @Override
    public boolean isSolution() {
        for (char[] chars : board) {
            for (char aChar : chars) {
                if (aChar == GREEN) return false;
            }
        }
        return true;
    }


    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> configs = new HashSet<>();
        for (DIRECTION d: DIRECTION.values()) {
            TiltConfig UDReference = copy();
            TiltConfig LRReference = copy();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    // j is a column selector, but it's an inner loop, so a problem arises
                    // if we want to efficiently deal with UDLR at the same time
                    if (d == DIRECTION.EAST || d == DIRECTION.WEST) {
                        LRReference.board[i] = shift(LRReference.board[i], d);
                    }
                }
                // But, we can use the fact that the row isn't important to select a column easily
                // board[0] contains the same columns as board[5]
                // thus, we can use i to represent the column selector instead.
                // Because we've mapped the vertical positions to horizontal ones, we can easily apply shift.
                // as the shift method has been implemented for arbitrary directions.
                applyMappedChars(UDReference.board, shift(remapVertical(UDReference.board, i), d), i);
            }
            // Keep in mind, if a blue is ever decremented, it isn't a valid config
            // Therefore, it isn't a neighbor.
            if (blues == countBlues(LRReference.board)) configs.add(LRReference);
            if (blues == countBlues(UDReference.board)) configs.add(UDReference);
        }
        return configs;
    }

    /**
     * API to generate a single resulting board for a particular direction.
     * @param direction Direction for board to be tilted. Uses DIRECTION enum.
     * @return null if resulting board is invalid, otherwise resulting board.
     */
    public TiltConfig moveResult(DIRECTION direction) {
        TiltConfig copy = copy();
        for (int i = 0; i < copy.board.length; i++) {
            for (int j = 0; j < copy.board[i].length; j++) {
                if (direction == DIRECTION.EAST || direction == DIRECTION.WEST) {
                    copy.board[i] = shift(copy.board[i], direction);
                }
            }
            if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
                applyMappedChars(copy.board, shift(remapVertical(copy.board, i), direction), i);
            }
        }
        return copy.isValid();
    }

    /**
     * Validates the instance this method is called on
     * @return pointer this for valid boards, null otherwise.
     */
    private TiltConfig isValid() {
        return blues == countBlues(this.board) ? this : null;
    }

    /**
     * Counts blue sliders in a given board
     * @param board 2d board array
     * @return number of blue sliders
     */
    private int countBlues(char[][] board) {
        int count = 0;
        for (char[] chars : board) {
            for (char aChar : chars) {
                if (aChar == BLUE) count++;
            }
        }
        return count;
    }

    /**
     * Shifts a row in DIRECTION for a given tilt.
     * Call remapVertical and pass in its result for columns.
     * @param row row (or column) to be shifted.
     * @param dir DIRECTION for given tilt
     * @return shifted array
     */
    private char[] shift(char[] row, DIRECTION dir) {
        // First, look for a slider. Then, try to move it as far as possible
        // While there is a slider that can move, we must try to move it.
        char to; // row[i]
        char from; // row[i+1]
        // Basically, we're crawling across the board multiple times until we're definitely done.
        // since a slider can only move up to dim-1 times.
        for (int j = 0; j < dimension - 1; j++) {
            switch (dir) {
                case WEST, SOUTH -> {
                    // we're basically comparing all pairs of adjacent cells multiple times
                    for (int i = 0; i < row.length - 1; i++) {
                        to = row[i+1];
                        from = row[i];
                        if (to != HOLE && canMove(from, to, dir) && isSlider(from)) {
                            // If it is a slider that is not going into a hole and it can move
                            row[i+1] = row[i];
                            row[i] = EMPTY;
                        } else if (to == HOLE && isSlider(from)) {
                            // if it is a slider going into a hole
                            row[i] = EMPTY;
                        }
                    }
                }
                case EAST, NORTH -> {
                    for (int i = row.length - 1; i >= 1; i--) {
                        // Now do the exact same operation, but reversed
                        to = row[i-1];
                        from = row[i];
                        if (to != HOLE && canMove(to, from, dir) && isSlider(from)) {
                            row[i-1] = row[i];
                            row[i] = EMPTY;
                        } else if (to == HOLE && isSlider(from)) {
                            row[i] = EMPTY;
                        }
                    }
                }
            }
        }
        return row;
    }

    /**
     * Remaps a desired column of values from 2d array into a 1d array with the same indices
     * @param ref 2d array to be used
     * @param col column remap desired
     * @return 1d remapped array
     */
    private char[] remapVertical(char[][] ref, int col) {
        char[] vertMap = new char[ref.length];
        // Remaps a given column in a 2d array into a 1d array with the same indices
        for (int i = 0; i < ref.length; i++) {
            vertMap[i] = ref[i][col];
        }
        return vertMap;
    }

    /**
     * Writes the values from a 1d array into a specific column of a 2d array
     * @param ref2d 2d arraytobe written into
     * @param ref 1d array to use to write with
     * @param col column to write into
     */
    private void  applyMappedChars(char[][] ref2d, char[] ref, int col) {
        for (int i = 0; i < ref.length; i++) {
            ref2d[i][col] = ref[i];
        }
    }

    /**
     * Boolean method to determine if a given cell is a slider
     * @param a given cell
     * @return true for sliders, false otherwise
     */
    private boolean isSlider(char a) {
        return a == GREEN || a == BLUE;
    }

    /**
     * Determines if a cell can move into another cell. Behavior depends on direction of movement
     * @param a negative-most position in array
     * @param b positive-most position in array
     * @param dir direction of movement
     * @return true for possible movements, false otherwise.
     */
    private boolean canMove(char a, char b, DIRECTION dir) {
        // Translation:
        /*
        char a represents the negative-most position. That is, the upper char or the lefter char.
        char b represents the positive-most position. That is, the lowest char or the righter char.
        In essence, if we are tilting left, we are trying to move b into a's position.
         */
        switch (dir) {
            case EAST, NORTH -> {
                return a == EMPTY;
            }
            case WEST, SOUTH -> {
                return b == EMPTY;
            }
            default -> {
                return false; // Cannot ever occur
            }
        }
    }

    public int getDimension() {
        return dimension;
    }

    public char[][] getBoard() {
        return board;
    }


    /**
     * Returns a new TiltConfig instance with the same values as the instance this method is called on.
     * Deeply clones board, all new references. Needed as [][].clone() copies references for the 1d inner arrays.
     * @return copy of TiltConfig instance.
     */
    private TiltConfig copy() {
        char[][] newBoard = new char[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = board[i].clone();
        }
        return new TiltConfig(newBoard, dimension);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TiltConfig that = (TiltConfig) o;
        return dimension == that.dimension && Arrays.deepEquals(board, that.board);
    }

    /**
     * Default Intellij hashcode implementation
     * @return hashcode of object.
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(dimension);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder bulk = new StringBuilder();
        StringBuilder str;
        for (char[] chars : board) {
            str = new StringBuilder();
            for (char aChar : chars) {
                str.append(aChar).append(" ");
            }
            bulk.append(str.toString().strip()).append("\n");
        }
        return bulk.toString();
    }

}
