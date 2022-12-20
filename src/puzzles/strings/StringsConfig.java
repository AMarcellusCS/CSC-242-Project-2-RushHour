package puzzles.strings;
/**
 * Configuration implementation of String puzzle.
 *
 * @author Adrian Marcellus
 */
import puzzles.common.solver.Configuration;
import java.util.ArrayList;
import java.util.Collection;

public class StringsConfig implements Configuration {
    private String start;
    private String goal;

    /**
     * constructor for StringConfig
     * @param start the start string
     * @param goal the goal string
     */
    public StringsConfig(String start, String goal) {
        this.start = start;
        this.goal = goal;
    }

    /**
     * checks if start is equal to the goal
     * @return true if equal
     */
    @Override
    public boolean isSolution() {
        return this.start.equals(this.goal);
    }

    /**
     * returns the neighbors of the start string
     * @return collection of configuration neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> nbrs = new ArrayList<Configuration>();
        for(int i = 0; i < start.length(); i++){
            int charNum = (int)(Character.toUpperCase(this.start.charAt(i)));
            char upNbr = getValue(charNum+1);
            char downNbr = getValue(charNum-1);
            nbrs.add(new StringsConfig((this.start.substring(0,i) + upNbr + this.start.substring(i+1)),this.goal));
            nbrs.add(new StringsConfig((this.start.substring(0,i) + downNbr + this.start.substring(i+1)),this.goal));
        }
        return nbrs;
    }

    /**
     * helper function to get altered char value
     * @param charNum value to compare
     * @return casted value
     */
    private char getValue(int charNum) {
        if(charNum > 90)
            return (char)(65);
        else if (charNum<65)
            return (char)(90);
        else
            return (char)charNum;

    }

    /**
     * to string override
     * @return start value
     */
    @Override
    public String toString() {
        return this.start;
    }

    /**
     * hashcode of start and goal
     * @return hashcode of combined string
     */
    public int hashCode(){
        return (this.start+this.goal).hashCode();
    }

    /**
     * checks if objects are equal
     * @param other other object to compare
     * @return true if equal
     */
    public boolean equals(Object other){
        if(other instanceof StringsConfig)
            return this.start.equals(((StringsConfig) other).getStart());
        return false;
    }

    /**
     * Accessor for start string
     * @return start string
     */
    public String getStart(){
        return this.start;
    }

    /**
     * header for root start string and goal
     * @return string header
     */
    public String header(){
        return "Start: " + this.start + ", End: " + this.goal;
    }
}
