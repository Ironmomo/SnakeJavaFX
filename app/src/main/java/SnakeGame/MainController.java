package SnakeGame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class MainController {

    @FXML
    private CheckBox chaosMode;

    @FXML
    private CheckBox easy;

    @FXML
    private CheckBox medium;

    @FXML
    private CheckBox hard;

    @FXML
    private AnchorPane gameField;

    @FXML
    private Label pointsLabel;

    @FXML
    private Label highScore;

    @FXML
    private Label infoTxt;

    private Game game;
    private MainControllerFactory mainControllerFactory;
    private Thread gameThread;
    private Lock mutex;

    /**
     * This method is called when pressed on the start button.
     * It verifies the game level and if chaos mode is enabled.
     * It then creates a new instance of game and starts the game flow in a new thread
     * @param event passed by javafx
     */
    @FXML
    void startGame(ActionEvent event) {
        Game.GameLevel lvl;
        if(easy.isSelected()) {
            lvl = Game.GameLevel.easy;
        } else if(medium.isSelected()) {
            lvl = Game.GameLevel.medium;
        } else {
            lvl = Game.GameLevel.fast;
        }
        game = new Game(mainControllerFactory, lvl, chaosMode.isSelected(), mutex);
        addListener();
        gameThread = new Thread(game);
        gameThread.start();
    }

    /**
     * It creates a boarder to visually point out the gamefield area.
     * It creates a new instance for mainControllerFactory
     */
    @FXML
    void initialize() {
        gameField.setStyle("-fx-border-color: green; -fx-border-width: 2px 2px 2px 2px");
        mainControllerFactory = new MainControllerFactory(this);
    }

    /**
     * This method draws the UI. It checks in the model for the gameBoardElement and the snake and it updates the points
     */
    void draw() {
        int highScoreInt = chaosMode.isSelected() ? Configs.chaosHighScore : Configs.highScore;
        highScore.setText(String.valueOf(highScoreInt));
        pointsLabel.setText(String.valueOf(game.getPoints()));
        gameField.getChildren().clear();
        List<Rectangle> snakeBody = game.getSnake();
        List<GameBoardElement> gameBoardElements = game.getGameBoardElements();
        mutex.lock();
        for (Rectangle body : snakeBody
             ) {
            gameField.getChildren().add(body);
        }
        for (GameBoardElement n : gameBoardElements
             ) {
            double x = n.getPosition().getX();
            double y = n.getPosition().getY();
            Rectangle element = new Rectangle(Configs.gameBoardElementWidth,Configs.gameBoardElementHeight);
            element.setLayoutX(x);
            element.setLayoutY(y);
            switch (n.getEffect()) {
                case food: element.setFill(Configs.foodColor); break;
                case shrink: case grow: case changePoints: element.setFill(Configs.effectItemColor);
            }
            gameField.getChildren().add(element);
        }
        mutex.unlock();
    }

    /**
     * Setup listener for key events to control the movement of the snake
     */
    void addListener() {
        pointsLabel.getScene().setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case A: case LEFT: game.setSpeed(Snake.Speed.LEFT); break;
                case W: case KP_UP: game.setSpeed(Snake.Speed.UP); break;
                case S: case KP_DOWN: game.setSpeed(Snake.Speed.DOWN); break;
                case D: case KP_RIGHT: game.setSpeed(Snake.Speed.RIGHT); break;
            }
        });
    }

    /**
     * Evaluates the checkbox to set the level
     * @param event
     */
    @FXML
    void levelSet(ActionEvent event) {
        Object source = event.getSource();
        if (hard.equals(source)) {
            easy.setSelected(false);
            medium.setSelected(false);
        } else if (medium.equals(source)) {
            easy.setSelected(false);
            hard.setSelected(false);
        } else if (easy.equals(source)) {
            medium.setSelected(false);
            hard.setSelected(false);
        }
    }

    /**
     * Used to draw a msg to inform the user about events
     * @param msg
     */
    public void drawMessage(Message msg) {
        infoTxt.setText(msg.getTxt());
        infoTxt.setTextFill(msg.getMsgType().getTxtColor());
        infoTxt.setVisible(true);
    }

    /**
     * Stops the game thread
     */
    public void cleanUp() {
        if (game != null) {
            game.running.set(false);
        }
    }

    /**
     * Used to init default values
     * @param mutex
     */
    public void startUp(Lock mutex) {
        medium.setSelected(true);
        this.mutex = mutex;
    }

}
