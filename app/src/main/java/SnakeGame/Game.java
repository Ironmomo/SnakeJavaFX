package SnakeGame;

import SnakeGame.Data.FileAccess;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * This class represants a game round. It holds the points achieved and controls the gameflow.
 */
public class Game implements Runnable{
    //Every game has a Snake to interact to.
    private Snake snake;
    //Number of points achieved in the game
    private int points, becomeALegend;
    //List to hold all active GameBoardElements
    private List<GameBoardElement> gameBoardElements;
    //Used to Communicate from Game >> mainController
    private MainControllerFactory mainControllerFactory;
    //The level sets the Thread.sleep() time and therefore sets the velocity of the snake
    private GameLevel level;
    //True aslong as the game is active
    public AtomicBoolean running = new AtomicBoolean(true);
    //Sets the next direction the snake will take
    private Snake.Speed nextSpeed = Snake.Speed.RIGHT;
    //True to active special effect items
    private boolean chaos;
    //Lock to prevent Race Condition
    private Lock mutex;

    public Game(MainControllerFactory mainControllerFactory, GameLevel level, boolean chaos ,Lock mutex) {
        this.mainControllerFactory = mainControllerFactory;
        this.level = level;
        this.chaos = chaos;
        this.mutex = mutex;
        points = 0;
        becomeALegend = Configs.gameBoardColumns*Configs.gameBoardRows;
        snake = new Snake(mutex);
        gameBoardElements = new ArrayList<>();
    }

    /**
     * Game loop
     */
    public void run() {
        while (running.get()) {
            snake.setSpeed(nextSpeed);
            snake.move();
            if(snake.checkCrash()) {
                running.set(false);
            } else {
                GameBoardElement gameBoardElement = checkElementsPosition();
                if(gameBoardElement != null) {
                    int pointsToAdd = snake.eat(gameBoardElement);
                    if(pointsToAdd != 0) {
                        addPoints(pointsToAdd);
                    }
                }
                createGameItem();
                checkScore();
                mainControllerFactory.draw();
                try {
                    Thread.sleep(level.getSpeed());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        FileAccess.writeNewHighScore();
    }

    /**
     * This method checks if there is an element in gameBoardElements at the same position as the snake.
     * It also checks if the gameBoardElements are still alive(valid).
     * Both cases if true will remove the gameBoardElement from the gameBoardElements.
     * @return gameBoardElement if alive and at snake head position. Returns null if no gameBoardElement is at position of snake
     */
    private GameBoardElement checkElementsPosition() {
        GameBoardElement element = null;
        Rectangle head = snake.getBody().get(0);
        double snakePosX = head.getLayoutX();
        double snakePosY = head.getLayoutY();
        Iterator<GameBoardElement> iterator = gameBoardElements.iterator();
        mutex.lock();
        while(iterator.hasNext()) {
            GameBoardElement el = iterator.next();
            if(!el.stillAlive()) {
                iterator.remove();
            } else {
                Point position = el.getPosition();
                if(position.getX() == snakePosX && position.getY() == snakePosY) {
                    element = el;
                    iterator.remove();
                    break;
                }
            }
        }
        mutex.unlock();
        return element;
    }

    /**
     * This method creates a Game item at a random pos and adds it to the gameBoardElements
     */
    private void createGameItem() {
        int numOfFoodItem = 0, numOfEffectItems = 0;
        for (GameBoardElement element: gameBoardElements
             ) {
            switch (element.getEffect()) {
                case food: numOfFoodItem +=1; break;
                default: numOfEffectItems += 1; break;
            }
        }
        if (numOfFoodItem < Configs.maxNumOfFoodItems) {
            configGameItem(GameBoardElement.Effect.food, Configs.foodItemTimeToLive);
        }
        if(numOfEffectItems < Configs.maxNumOfEffectItems && chaos) {
            //1% Chance to create new Item
            if(Math.random() >= 0.99) {
                double num = Math.random();
                if(0 <= num && num < 0.333) {
                    configGameItem(GameBoardElement.Effect.grow, Configs.negativEffectTimeToLive);
                } else if(0.333 <= num && num < 0.666) {
                    configGameItem(GameBoardElement.Effect.shrink, Configs.positivEffectTimeToLive);
                } else {
                    configGameItem(GameBoardElement.Effect.changePoints, Configs.positivEffectTimeToLive);
                }
            }
        }
    }

    /**
     * This method creates the GameBoardElement as a Rectangle
     * @param effect of the GameBoardElement
     * @param timeToLive for the GameBoardElement in seconds
     */
    private void configGameItem(GameBoardElement.Effect effect, int timeToLive) {
        Point coordinate = calcRandomCoordination();
        mutex.lock();
        gameBoardElements.add(new GameBoardElement(coordinate, effect, timeToLive));
        mutex.unlock();
    }

    /**
     * @return a random and free coordination on the GameBoard
     */
    private Point calcRandomCoordination() {
        int x = -1, y = 0;
        for (int i = 0; i < Configs.gameBoardColumns*Configs.gameBoardRows; i++) {
            x = Configs.snakeWidth * (int) (Math.random() * Configs.gameBoardColumns);
            y = Configs.snakeHeight * (int) (Math.random() * Configs.gameBoardRows);
            final int xf = x;
            final int yf = y;
            //Checks if there is a GameElement or snake body element at the random position
            if(!(snake.getBody().stream().anyMatch(e -> e.getLayoutX() == xf && e.getLayoutY() == yf) || gameBoardElements.stream().map(e -> e.getPosition()).anyMatch(e -> e.getX() == xf && e.getY() == yf))) {
                break;
            }
        }
        return new Point(x,y);
    }

    /**
     * This method checks if highscore has been beaten
     */
    private void checkScore() {
        if(chaos) {
            if(points > Configs.chaosHighScore) {
                Configs.chaosHighScore = points;
            }
        } else {
            if(points > Configs.highScore) {
                Configs.highScore = points;
            }
        }
        if(becomeALegend == snake.getBody().size()) {
            snake.getBody().stream().forEach(x -> x.setFill(Color.GREEN));
        }
    }

    private void addPoints(int add) {
        points += add;
        if(add > 0) {
            mainControllerFactory.showMessage(new Message("+ " + add, Message.MessageType.pointsAdded));

        }else {
            mainControllerFactory.showMessage(new Message("- " + add, Message.MessageType.pointsRemoved));
        }
    }

    public void setSpeed(Snake.Speed speed) {
        nextSpeed = speed;
    }

    public List<Rectangle> getSnake() {
        return snake.getBody();
    }

    public List<GameBoardElement> getGameBoardElements() {
        return gameBoardElements;
    }

    public int getPoints() {
        return points;
    }

    /**
     * Used to set the snake movement speed by changing Thread.sleep() time.
     */
    public enum GameLevel {
        easy(200),medium(100),fast(50);
        int speed;

        GameLevel(int speed) {
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
        }
    }
}
