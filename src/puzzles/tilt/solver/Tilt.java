package puzzles.tilt.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.tilt.model.TiltConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class Tilt {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tilt filename");
        }
        File inFile = new File(args[0]);

        try {
            TiltConfig tc = new TiltConfig(inFile);
            Collection<Configuration> solution = Solver.solve(tc,new HashMap<>(),new LinkedList<>());
            if (solution == null) {
                System.out.println("No Solution.");
            } else {
                int i = 0;
                for (Configuration configuration : solution) {
                    System.out.println("Step " + i++ + ": " + configuration);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
