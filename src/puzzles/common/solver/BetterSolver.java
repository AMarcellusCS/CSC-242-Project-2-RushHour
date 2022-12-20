package puzzles.common.solver;
/**
 * Solver for any configuration implementation using breadth first search.
 *
 * @author Adrian Marcellus
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class BetterSolver {

    /**
     * Solve method.
     * @param start root configuration
     */
     public static void solve(Configuration start){
         HashMap<Configuration, Configuration> visited = new HashMap<Configuration, Configuration>();;
         LinkedList<Configuration> queue =  new LinkedList<Configuration>();;
         int totalConfig = 1;
         queue.add(start);
         visited.put(start, null);
         while(!queue.isEmpty()){
             Configuration current = queue.removeFirst();
             if(current.isSolution()){
                printSolution(current, visited, totalConfig);
                return;
             }
             else{
                 Collection<Configuration> nbrs = current.getNeighbors();
                 totalConfig += nbrs.size();
                 for(Configuration child : nbrs){
                     if(!visited.containsKey(child)){
                         queue.add(child);
                         visited.put(child, current);
                     }
                 }
             }
         }
         System.out.println("Total Configs: " + totalConfig);
         System.out.println("Unique Configs: " + visited.size());
         System.out.println("No solution");
     }

    /**
     * prints solution for solved configuration path
     * @param current solved configuration
     */
    private static void printSolution(Configuration current, HashMap<Configuration, Configuration> visited, int totalConfig) {
        ArrayList<Configuration> path = new ArrayList<Configuration>();
        path.add(current);
        while(true){
            current = visited.get(current);
            if(current == null)
                break;
            path.add(current);
        }
        System.out.println("Total Configs: " + totalConfig);
        System.out.println("Unique Configs: " + visited.size());
        int iterator = 0;
        while(!path.isEmpty()){
            System.out.println("Step " + iterator + ": \n" + path.remove(path.size()-1));
            iterator++;
        }
    }

    /**
     * returns the path from current configuration to solution
     * @param start initial configuration
     * @return arraylist of path
     */
    public static ArrayList<Configuration> solvePath(Configuration start){
        HashMap<Configuration, Configuration> visited = new HashMap<Configuration, Configuration>();
        LinkedList<Configuration> queue =  new LinkedList<Configuration>();
        queue.add(start);
        visited.put(start, null);
        while(!queue.isEmpty()){
            Configuration current = queue.removeFirst();
            if(current.isSolution()){
                ArrayList<Configuration> path = new ArrayList<Configuration>();
                path.add(current);
                while(true){
                    current = visited.get(current);
                    if(current == null)
                        break;
                    path.add(current);
                }
                return path;
            }
            else{
                Collection<Configuration> nbrs = current.getNeighbors();
                for(Configuration child : nbrs){
                    if(!visited.containsKey(child)){
                        queue.add(child);
                        visited.put(child, current);
                    }
                }
            }
        }
        return null;
    }
}

