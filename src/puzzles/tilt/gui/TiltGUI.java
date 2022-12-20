package puzzles.tilt.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.tilt.model.TiltConfig;
import puzzles.tilt.model.TiltModel;

import java.io.FileNotFoundException;

public class TiltGUI extends Application implements Observer<TiltModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private final String hintStyle = "-fx-text-fill: red";
    private TiltModel model;
    private GridPane gridBoard;
    private Label status;

    // for demonstration purposes
    private Image greenDisk = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green.png"));
    private Image blueDisk = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"blue.png"));
    private Image hole = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"hole.png"));
    private Image block = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "block.png"));

    public void init() {
        String filename = getParameters().getRaw().get(0);
        try {
            model = new TiltModel(filename);
        } catch (FileNotFoundException e) {
            System.out.println("File path not found, please verify path and try again.");
            System.exit(1);
        }
        model.addObserver(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox main = new VBox();
        main.setAlignment(Pos.CENTER);
        gridBoard = new GridPane();
        status = new Label();
        HBox controlPane = new HBox();
        GridPane controls = new GridPane();
        gridBoard.setGridLinesVisible(true);
        Button north = new Button("NORTH");
        Button south = new Button("SOUTH");
        Button east = new Button("EAST");
        Button west = new Button("WEST");
        north.setMinWidth(124);
        south.setMinWidth(124);
        east.setMinWidth(124);
        west.setMinWidth(124);
        north.setOnAction((x) -> {
            model.tryMove(TiltConfig.DIRECTION.NORTH);
            clearHintFormatting(north,east,south,west);
        });
        south.setOnAction((x) -> {
            model.tryMove(TiltConfig.DIRECTION.SOUTH);
            clearHintFormatting(north,east,south,west);
        });
        east.setOnAction((x) -> {
            model.tryMove(TiltConfig.DIRECTION.WEST);
            clearHintFormatting(north,east,south,west);
        });
        west.setOnAction((x) -> {
            model.tryMove(TiltConfig.DIRECTION.EAST);
            clearHintFormatting(north,east,south,west);
        });
        controls.add(north,1,0);
        controls.add(west,0,1);
        controls.add(south,1,2);
        controls.add(east,2,1);
        VBox managerPane = new VBox();
        Button hint = new Button("HINT");
        Button load = new Button("LOAD");
        load.setOnAction((x) -> {
            //show dialog thing
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load a game board.");
                model.loadNew(fileChooser.showOpenDialog(stage).getAbsolutePath());
            } catch (NullPointerException e) {
                status.setText("No file was chosen.");
            }
        });
        Button reset = new Button("RESET");
        reset.setOnAction((x) -> {
            model.reset();
        });
        hint.setOnAction((x) -> {
            TiltConfig.DIRECTION d = model.getHint();
            clearHintFormatting(north,east,south,west);
            if (d != null) {
                switch (d) {
                    case SOUTH -> south.setStyle(hintStyle);
                    case WEST -> east.setStyle(hintStyle);
                    case NORTH -> north.setStyle(hintStyle);
                    case EAST -> west.setStyle(hintStyle);

                }
            } else {
                if (model.getMoves() != 0) {
                    status.setText("There is no solution to the state of the board.");
                }
                else {
                    status.setText("This puzzle has no solution.");
                }
            }
        });
        regenBoard();
        matchModel();
        managerPane.getChildren().add(hint);
        managerPane.getChildren().add(load);
        managerPane.getChildren().add(reset);
        managerPane.getChildren().forEach((x) -> ((Button) x).setMinWidth(125));
        controlPane.getChildren().add(managerPane);
        main.getChildren().add(status);
        controlPane.getChildren().add(controls);
        main.getChildren().add(gridBoard);
        main.getChildren().add(controlPane);
        Scene scene = new Scene(main);
        stage.setScene(scene);
        stage.show();
    }


    private void clearHintFormatting(Button north, Button south, Button east, Button west) {
        north.setStyle("-fx-text-fill: black");
        west.setStyle("-fx-text-fill: black");
        east.setStyle("-fx-text-fill: black");
        south.setStyle("-fx-text-fill: black");
    }
    private void regenBoard() {
        while (gridBoard.getChildren().size() > 0) {
            gridBoard.getChildren().remove(0);
        }
        for (int i = 0; i < model.getDimension(); i++) {
            for (int j = 0; j < model.getDimension(); j++) {
                Label l = new Label();
                l.setMinSize(125,125);
                gridBoard.add(l, j, i);
            }
        }
    }
    @Override
    public void update(TiltModel tiltModel, String message) {
        switch(message.split(" ")[0]) {
            case "move" -> {
                matchModel();
                if (model.isSolved()) status.setText("You completed the puzzle!");
                else status.setText("Tilted the board...");
            }
            case "invalid" -> status.setText("You cannot slide a blue slider into a hole!");
            case "reset" -> {
                status.setText("Reset board.");
                matchModel();
            }
            case "loadComplete" -> {
                status.setText("Loaded " + message.split(" ")[1]);
                regenBoard();
                matchModel();
            }
            case "loadFail" -> status.setText("Unable to load board.");
            case "NS" -> status.setText("There is no solution given the board's current state. Reset or load new game.");
        }
    }
    private void matchModel() {
        float scalar = Math.min(1f, 6f / model.getDimension());
        float size = 125f;
        int discrepancy; // Had some strange issues where the children of the board piece didnt match what was actually the case.
        for (int i = 0; i < model.getDimension(); i++) {
            for (int j = 0; j < model.getDimension(); j++) {
                discrepancy = gridBoard.getChildren().size() - model.getDimension()* model.getDimension();
                Label ref = ((Label) gridBoard.getChildren().get(model.getDimension()*i+j+discrepancy));
                switch(model.getBoard()[i][j]) {
                    case 'G' -> ref.setGraphic(new ImageView(greenDisk));
                    case 'O' -> ref.setGraphic(new ImageView(hole));
                    case 'B' -> ref.setGraphic(new ImageView(blueDisk));
                    case '*' -> ref.setGraphic(new ImageView(block));
                    default ->  //This is for empties
                            ref.setGraphic(null);
                }
                ImageView e = (ImageView) ref.getGraphic();
                if (e != null) {
                    e.setScaleX(scalar);
                    e.setScaleY(scalar);
                }
                ref.setMinSize(size*scalar,size*scalar);
                ref.setMaxSize(size*scalar,size*scalar);

            }
        }
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
