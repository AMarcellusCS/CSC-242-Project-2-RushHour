package puzzles.strings;

import puzzles.common.solver.BetterSolver;

/**
 * Main class for the strings puzzle.
 *
 * @author Adrian Marcellus
 */
public class Strings {
    /**
     * Run an instance of the strings puzzle.
     *
     * @param args [0]: the starting string;
     *             [1]: the finish string.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            StringsConfig config = new StringsConfig(args[0], args[1]);
            System.out.println(config.header());
            BetterSolver.solve(config);
        }
    }
}
