package puzzles.water;
/**
 * Configuration implementation of water puzzle.
 *
 * @author Adrian Marcellus
 */
import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WaterConfig implements Configuration {
    private int numberOfBuckets;
    private int[] bucketsVal;
    private int[] bucketsMaxVal;
    private int goal;

    /**
     * Constructor for root water configuration
     * @param args configuration parameters
     */
    public WaterConfig(String[] args){
        this.goal = Integer.parseInt(args[0]);
        this.numberOfBuckets = args.length-1;
        this.bucketsVal = new int[this.numberOfBuckets];
        this.bucketsMaxVal = new int[this.numberOfBuckets];
        for(int i = 0; i < numberOfBuckets; i++){
            this.bucketsMaxVal[i] = Integer.parseInt(args[i+1]);
        }
    }

    /**
     * Constructor to create altered water config.
     * @param bucketsMax max bucket capacity
     * @param bucketsVal current bucket capacity
     * @param goal target volume
     * @param numberOfBuckets number of buckets
     */
    public WaterConfig(int[] bucketsMax, int[] bucketsVal, int goal, int numberOfBuckets){
        this.bucketsMaxVal = bucketsMax;
        this.bucketsVal = bucketsVal;
        this.goal = goal;
        this.numberOfBuckets = numberOfBuckets;
    }

    /**
     * Checks if any bucket equals the goal
     * @return true if goal found
     */
    @Override
    public boolean isSolution() {
        for(int i = 0; i < this.numberOfBuckets; i++){
            if(this.bucketsVal[i] == this.goal)
                return true;
        }
        return false;
    }

    /**
     * Find neighbors of configuration.
     * @return collection of configuration neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> nbrs = new ArrayList<Configuration>();
        for(int i = 0; i < this.numberOfBuckets; i++){
            if(this.bucketsVal[i] != 0){
                int[] copy = this.bucketsVal.clone();
                copy[i] = 0;
                nbrs.add(new WaterConfig(this.bucketsMaxVal, copy, this.goal, this.numberOfBuckets));
            }
            if(this.bucketsVal[i] != this.bucketsMaxVal[i]) {
                int[] copy = this.bucketsVal.clone();
                copy[i] = this.bucketsMaxVal[i];
                nbrs.add(new WaterConfig(this.bucketsMaxVal, copy, this.goal, this.numberOfBuckets));
                for (int y = 0; y < this.numberOfBuckets; y++) {
                    if (y != i  && this.bucketsVal[i] + this.bucketsVal[y] > 0) {
                        copy = this.bucketsVal.clone();
                        if (this.bucketsVal[i] + this.bucketsVal[y] > this.bucketsMaxVal[i]){
                            copy[i] = this.bucketsMaxVal[i];
                            copy[y] = (this.bucketsVal[y] - (this.bucketsMaxVal[i] - this.bucketsVal[i]));
                            nbrs.add(new WaterConfig(this.bucketsMaxVal, copy, this.goal, this.numberOfBuckets));
                        } else {
                            copy[i] = this.bucketsVal[i] + this.bucketsVal[y];
                            copy[y] = 0;
                            nbrs.add(new WaterConfig(this.bucketsMaxVal, copy, this.goal, this.numberOfBuckets));
                        }
                    }
                }
            }
        }
        return nbrs;
    }

    /**
     * Hashcode for the current value array
     * @return array hashcode
     */
    public int hashCode(){
        return Arrays.hashCode(this.bucketsVal);
    }

    /**
     * checks if two objects are equal
     * @param other object to compare
     * @return true if equal
     */
    public boolean equals(Object other){
        if(other instanceof WaterConfig)
            return Arrays.equals(this.bucketsVal, (((WaterConfig) other).getBucketsVal()));
        return false;
    }

    /**
     * accessor for buckets current values
     * @return
     */
    public int[] getBucketsVal(){
        return this.bucketsVal;
    }

    /**
     * to string override
     * @return string of current values
     */
    @Override
    public String toString(){
        return Arrays.toString(this.bucketsVal);
    }

    /**
     * used for root print out
     * @return string printout
     */
    public String header() {
        return "Amount: " + this.goal + ", Buckets: " + Arrays.toString(this.bucketsMaxVal);
    }
}
