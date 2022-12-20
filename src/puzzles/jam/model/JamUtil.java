package puzzles.jam.model;

import java.io.File;
import java.util.HashMap;

public class JamUtil {
    /**
     * static hashmap of character to color css string values
     */
    private static final HashMap<Character, String> colors = new HashMap<>();
    static {
        colors.put('X', "#DF0101");
        colors.put('A', "#8de16c");
        colors.put('B', "#ee4a10");
        colors.put('C', "#091fad");
        colors.put('D', "#a82d89");
        colors.put('E', "#9078e5");
        colors.put('F', "#0a4f1a");
        colors.put('G', "#434644");
        colors.put('H', "#9fa29f");
        colors.put('I', "#e1cc53");
        colors.put('J', "#494336");
        colors.put('K', "#475e43");
        colors.put('L', "#b2b6b0");
        colors.put('O', "#ffff1a");
        colors.put('P', "#6f41be");
        colors.put('Q', "#1b5ee3");
        colors.put('R', "#1a9334");
        colors.put('S', "#2c2b2b");
    }

    /**
     * static call to get color with character
     * @param id character representing a car used to query hashmap
     * @return css string color value
     */
    public static String getColor(char id){return colors.get(id);}

    /**
     * static call to get image path of a car
     * @param id character representing a car used to query hashmap
     * @param vertical if the car is vertical
     * @return string image path for car
     */
    public static String getImage(char id, boolean vertical){
        File folder = new File("src/puzzles/jam/gui/resources/");
        File[] listOfTextFiles = folder.listFiles();
        assert listOfTextFiles != null;
        for (File file : listOfTextFiles)
            if (file.isFile() && file.getName().charAt(0) == id) {
                if(vertical && file.getName().contains("vert"))
                    return file.getPath().substring(3);
                else if (!vertical && file.getName().contains("hori"))
                    return file.getPath().substring(3);
            }
        return null;
    }
}
