package puzzles.water;

import puzzles.common.solver.BetterSolver;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Adrian Marcellus
 */
public class Water {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    ("Usage: java Water amount bucket1 bucket2 ...")
            );
        } else {
            WaterConfig config = new WaterConfig(args);
            System.out.println(config.header());
            BetterSolver.solve(config);
        }
    }
}
