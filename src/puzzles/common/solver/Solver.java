package puzzles.common.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;

public class Solver {
    public static Collection<Configuration> solve(Configuration config, HashMap<Configuration, Configuration> map, Queue<Configuration> queue) {
        int configs = 1;
        queue.add(config); // enqueue
        map.put(config,null); // If this is the initial node, add NODE visited from NULL
        while (!queue.isEmpty()) { //Is the queue empty? If it is, we've exhausted all possible searches, and didn't find the goal
            Configuration reference = queue.remove(); // Dequeue element
            if (reference.isSolution()) { // is it the solution? If so, we're done.
                ArrayList<Configuration> solution_path = new ArrayList<>();
                Configuration temp = reference;
                while (temp != null) {
                    solution_path.add(0,temp);
                    temp = map.get(temp);
                }
                System.out.println("Total configurations: " + configs);
                //System.out.println("Unique configurations: " + map.keySet().size());
                return solution_path;
            }
            else { // If not, then get its neighbors and add them to the queue if they haven't been visited before.
                for (Configuration c: reference.getNeighbors()) {
                    configs++;
                    if (!map.containsKey(c)) {
                        map.put(c,reference);
                        queue.add(c);
                    }
                }
            }
        }
        System.out.println("Total configurations: " + configs);
        System.out.println("Unique configurations: " + map.keySet().size());
        return null;
    }
    public static Collection<Configuration> solveWithoutPrinting(Configuration config, HashMap<Configuration, Configuration> map, Queue<Configuration> queue) {
        queue.add(config); // enqueue
        map.put(config,null); // If this is the initial node, add NODE visited from NULL
        while (!queue.isEmpty()) { //Is the queue empty? If it is, we've exhausted all possible searches, and didn't find the goal
            Configuration reference = queue.remove(); // Dequeue element
            if (reference.isSolution()) { // is it the solution? If so, we're done.
                ArrayList<Configuration> solution_path = new ArrayList<>();
                Configuration temp = reference;
                while (temp != null) {
                    solution_path.add(0,temp);
                    temp = map.get(temp);
                }
                return solution_path;
            }
            else { // If not, then get its neighbors and add them to the queue if they haven't been visited before.
                for (Configuration c: reference.getNeighbors()) {
                    if (!map.containsKey(c)) {
                        map.put(c,reference);
                        queue.add(c);
                    }
                }
            }
        }
        return null;
    }
}

