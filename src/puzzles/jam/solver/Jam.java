package puzzles.jam.solver;

import puzzles.common.solver.BetterSolver;
import puzzles.jam.model.JamConfig;

public class Jam {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        } else {
            BetterSolver.solve(new JamConfig(args[0]));
        }
    }
}