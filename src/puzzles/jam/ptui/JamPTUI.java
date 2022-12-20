package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.jam.model.JamModel;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Text user interface for Rush Hour game.
 * @author Adrian Marcellus
 */
public class JamPTUI implements Observer<JamModel, String> {
    /** JamModel processing commands and holding observers */
    private JamModel model;

    /**
     * Constructor for TUI
     * @param fileName
     */
    public JamPTUI(String fileName){
        this.model = new JamModel(new File(fileName));
        this.model.addObserver(this);
    }

    /**
     * Update TUI observer
     * @param jamModel the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(JamModel jamModel, String message) {
        System.out.println(message);
        System.out.println(this.model);
    }

    /**
     * Run TUI, parses commands from file path.
     */
    private void run() {
        Scanner in = new Scanner(System.in);
        this.commands();
        String[] prevCommand = {"q"};
        boolean correctUsage = true;
        while (true) {
            String[] line;
            if(correctUsage)
                line = in.nextLine().split(" ");
            else{
                line = prevCommand;
                correctUsage = true;
            }
            switch (Character.toLowerCase(line[0].charAt(0))) {
                case 's' -> {
                    prevCommand = in.nextLine().split(" ");
                    if(prevCommand[0].charAt(0) == 's')
                        this.model.move(
                                Integer.parseInt(line[1]),
                                Integer.parseInt(line[2]),
                                Integer.parseInt(prevCommand[1]),
                                Integer.parseInt(prevCommand[2]));
                    else
                        correctUsage = false;
                }
                case 'h' -> this.model.hint();
                case 'l' -> this.model.loadGame(new File(line[1]));
                case 'r' -> this.model.reset();
                case 'q' -> {
                    System.out.println("Quiting...");
                    return;
                }
                default -> {
                    System.out.println("Unrecognized command.");
                    this.commands();
                    System.out.println(this.model);
                }
            }
        }
    }

    /**
     * prints commands for TUI
     */
    public void commands() {
        System.out.println("h(int)");
        System.out.println("l(oad)");
        System.out.println("s(elect) r c");
        System.out.println("q(uit)");
        System.out.println("r(eset)");
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
        else {
            JamPTUI ui = new JamPTUI(args[0]);
            ui.run();
        }
    }
}
