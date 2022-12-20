package puzzles.tilt.ptui;

import puzzles.common.Observer;
import puzzles.tilt.model.TiltModel;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class TiltPTUI implements Observer<TiltModel, String> {
    private TiltModel model;

    @Override
    public void update(TiltModel model, String message) {
        switch (message.split(" ")[0]) {
            case "move" -> {
                model.printModel();
            }
            case "loadComplete" -> {
                System.out.println("> Loaded " + message.split(" ")[1]);
                model.printModel();
                model.printInstructions();
            }
            case "loadFail" -> {
                // print couldn't load or something
                System.out.println("> Failed to load. " + message.split(" ")[1]);
                model.printModel();
            }
            case "invalid" -> {
                System.out.println("> Illegal move. A blue slider will fall through the hole!");
                model.printModel();
            }
            case "reset" -> {
                System.out.println("> Puzzle reset!");
                model.printModel();
            }
            case "hint" -> {
                System.out.println("> Next step!");
                model.printModel();
            }
            case "solved" -> {
                System.out.println("> Already solved!");
                model.printModel();
            }
            case "NS" -> {
                System.out.println("No solution!");
                model.printModel();
            }
            default -> {
                // Achievement get: how did we get here.
            }
        }
    }
    public TiltPTUI(String filename) {
        try {
            model = new TiltModel(filename);
        } catch (FileNotFoundException e) {
            System.out.println("File path not found, please verify path and try again.");
            System.exit(1);
        }
        model.addObserver(this);
    }
    private void run() {
        Scanner in = new Scanner(System.in);
        String exp;
        model.printModel();
        model.printInstructions();
        while (true) {
            exp = in.nextLine().strip();
            if (exp.startsWith("l") || exp.startsWith("load")) {
                model.loadNew(exp.split(" ")[1]);
            }
            else if (exp.startsWith("t") || exp.startsWith("tilt")) {
                if (model.isSolved()) {
                    System.out.println("Already solved!");
                } else {
                    model.tryMove(model.directionFromString(exp.split(" ")[1]));
                }
            }
            else if (exp.equals("q") || exp.equals("quit")) {
                // okay bye, i dunno what to put here.
                System.exit(0);
            }
            else if (exp.equals("r") || exp.equals("reset")) {
                model.reset();
            }
            else if (exp.equals("h") || exp.equals("hint")) {
                if (model.isSolved()) {
                    System.out.println("Already solved!");
                    model.printModel();
                } else {
                    model.textHint();
                }
            }
            else {
                model.printInstructions();
            }
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltPTUI filename");
        }
        // yeah i'm not very original with this am i
        TiltPTUI ui = new TiltPTUI(args[0]);
        ui.run();
    }
}
