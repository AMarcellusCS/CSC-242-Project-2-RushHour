package puzzles.jam.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.jam.model.Coordinate;
import puzzles.jam.model.JamModel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

/**
 * Graphical User Interface for Rush Hour game.
 * @author Adrian Marcellus
 */
public class JamGUI extends Application  implements Observer<JamModel, String>  {
    /** button font size */
    private final static int BUTTON_FONT_SIZE = 20;
    /** default scalar */
    private final static int DEFAULT = 50;
    /** image size */
    private final static int ICON_SIZE = 60;
    /** white css string value */
    public static final String WHITE = "#ffffff";
    /** black css string value */
    public static final String BLACK = "#000000";
    /** background css string value */
    public static final String BACKGROUND = "#f57070";
    /** win background css string value */
    private static final String WIN_BACKGROUND = "#8de16c";
    /** JamModel processing commands and holding observers */
    private JamModel model;
    /** root pane to set into scene */
    private BorderPane root;
    /** stage shown */
    private Stage stage;
    /** total size multiplier */
    private final int SIZE_MULTIPLIER = 100;
    /** button on select */
    private boolean select = true;
    /** coordinate of lest select */
    private Coordinate selected;

    /**
     * Application Initialization creates model and root for scene.
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        this.model = new JamModel(new File(filename));
        this.model.addObserver(this);
        this.root = new BorderPane();
    }

    /**
     * Application start for GUI
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Image icon = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/puzzles/jam/gui/resources/X-hori.png")));
        stage.getIcons().add(icon);

        this.getTop("Welcome to RUSH HOUR");
        this.getCenter();
        this.getBottom();

        Scene scene = new Scene(this.root);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Sets the bottom HBox to the root borderpane bottom.
     */
    private void getBottom(){
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.BOTTOM_CENTER);
        bottom.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BLACK + ";");
        if(this.model.isWon())
            bottom.setStyle("-fx-background-color: " + WIN_BACKGROUND + "; -fx-border-color: " + BLACK + ";");
        bottom.getChildren().addAll(this.getLoad(), this.getReset(), this.getHint());
        this.root.setBottom(bottom);
    }

    /**
     * returns a hint button with control
     * @return button node
     */
    private Node getHint() {
        Button hint = new Button("Hint");
        this.getBottomButtonStyle(hint);
        hint.setOnAction((event) -> {
            this.model.hint();
        });
        return hint;
    }
    /**
     * returns a reset button with control
     * @return button node
     */
    private Node getReset() {
        Button reset = new Button("Reset");
        this.getBottomButtonStyle(reset);
        reset.setOnAction((event) -> {
            this.model.reset();
        });
        return reset;
    }
    /**
     * returns a load button with control
     * @return button node
     */
    private Node getLoad() {
        Button load = new Button("Load");
        this.getBottomButtonStyle(load);
        load.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load a game board.");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/data/jam"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            this.model.loadGame(selectedFile);
        });
        return load;
    }

    /**
     * sets a uniform style for buttons
     * @param button button node to alter style
     */
    private void getBottomButtonStyle(Button button){
        button.setPrefHeight(DEFAULT);
        button.setPrefWidth(SIZE_MULTIPLIER);
        button.setStyle("-fx-background-radius: " + BUTTON_FONT_SIZE + ";");
    }

    /**
     * Makes a GridPane representing a game board and sets to root borderpane center.
     */
    private void getCenter() {
        GridPane buttons = new GridPane();
        this.stage.setWidth(this.model.getYDimension() * SIZE_MULTIPLIER);
        this.stage.setHeight((this.model.getXDimension() * SIZE_MULTIPLIER) + (2 * DEFAULT + 25));
        buttons.setAlignment(Pos.CENTER);
        for(int x = 0; x < this.model.getXDimension(); x++){
            for(int y = 0; y < this.model.getYDimension(); y++) {
                Button temp = new Button();
                String buttonColor = this.model.getColor(x, y);
                String buttonBorder = this.model.getBorder(buttonColor);
                temp.setStyle(
                "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                "-fx-background-color: " + buttonColor + ";" +
                "-fx-font-weight: bold; -fx-background-radius: 0; -fx-border-color: " + buttonBorder + ";"
                );
                try {
                    if (this.model.isFront(x,y)) {
                        Image car = new Image(this.getClass().getResourceAsStream(this.model.getCarImage(x, y)));
                        ImageView carView = new ImageView(car);
                        carView.setFitHeight(ICON_SIZE);
                        carView.setFitWidth(ICON_SIZE);
                        temp.setGraphic(carView);
                    }
                    else
                        temp.setText(this.model.getButtonText(x,y));
                }
                catch (NullPointerException e) {
                    //Continue
                }
                temp.setPrefWidth(SIZE_MULTIPLIER);
                temp.setPrefHeight(SIZE_MULTIPLIER);
                int finalX = x;
                int finalY = y;
                temp.setOnAction(event -> {
                    if(this.select) {
                        if(this.model.carAt(finalX,finalY)) {
                            this.selected = new Coordinate(finalX, finalY);
                            this.select = false;
                            this.getTop("Selected: " + this.model.getButtonText(finalX,finalY) + " at " + this.selected);
                        }
                        else
                            this.getTop("No car selected!");
                    }
                    else{
                        this.model.move(this.selected.getX(), this.selected.getY(), finalX, finalY);
                        this.select = true;
                    }

                });
                buttons.add(temp, y, x);
            }
        }
        this.root.setCenter(buttons);
    }

    /**
     * Sets the top HBox to the root borderpane top with a message.
     * @param message message to update
     */
    public void getTop(String message){
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: " + BLACK + ";" + "-fx-font-weight: bold;");
        HBox top = new HBox(label);
        top.setPrefHeight(DEFAULT);
        top.setPrefWidth(DEFAULT);
        top.setAlignment(Pos.CENTER);
        top.setStyle("-fx-background-color: " + BACKGROUND + "; -fx-border-color: " + BLACK + ";");
        if(this.model.isWon())
            top.setStyle("-fx-background-color: " + WIN_BACKGROUND + "; -fx-border-color: " + BLACK + ";");
        this.root.setTop(top);
    }

    /**
     * Update the GUI observer
     * @param jamModel the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(JamModel jamModel, String message) {
        if(this.root != null) {
            this.getTop(message);
            this.getCenter();
            this.getBottom();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
