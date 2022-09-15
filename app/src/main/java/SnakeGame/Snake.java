package SnakeGame;

import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * This class represents the snake which is the element actively controlled by the player
 * It contains all important methods to calc the coordination while moving and checks if the snake can eat a GameBoardElement.
 * It also checks if the snake does any not allowed behavior which lead to game over
 */
public class Snake {
    private List<Rectangle> body;
    private Speed speed;
    private double headPositionX;
    private double headPositionY;
    private Lock mutex;

    public Snake(Lock mutex) {
        this.mutex = mutex;
        body = new ArrayList<>();
        Rectangle head = new Rectangle(Configs.snakeWidth,Configs.snakeHeight,Configs.snakeColor);
        head.setLayoutX(Configs.snakePosDefaultX);
        head.setLayoutY(Configs.snakePosDefaultY);
        body.add(head);
        speed = Speed.RIGHT;
        headPositionX = head.getLayoutX();
        headPositionY = head.getLayoutY();
    }

    public void setSpeed(Speed newSpeed) {
        speed = newSpeed;
    }

    public List<Rectangle> getBody() {
        return body;
    }

    /**
     * Checks if snake moves in itself
     * @return true if head over body-element else false
     */
    public boolean checkCrash() {
        for (int i = 1; i < body.size() ; i++) {
            Rectangle el = body.get(i);
            if(headPositionX == el.getLayoutX() && headPositionY == el.getLayoutY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method does move the snake one step by taking the last element of the snake and use it as the new head.
     * Position is calculated by current speed
     */
    public void move() {
        headPositionX = (headPositionX + speed.getSpeedX()) % Configs.gameBoardWidth;
        headPositionY = (headPositionY + speed.getSpeedY()) % Configs.gameBoardHeight;
        if(headPositionX < 0) {
            headPositionX = Configs.gameBoardWidth - Configs.snakeWidth;
        }
        if(headPositionY < 0) {
            headPositionY = Configs.gameBoardHeight - Configs.snakeHeight;
        }
        mutex.lock();
        body.get(body.size()-1).setLayoutX(headPositionX);
        body.get(body.size()-1).setLayoutY(headPositionY);
        body.add(0,body.remove(body.size()-1));
        if(!body.get(0).isVisible()) {
            body.get(0).setVisible(true);
        }
        mutex.unlock();
    }

    /**
     * Makes snake eat the GameBoardElement and activates its effect.
     * The method returns the points added to the game. Some effects change the points added to the game.
     */
    public int eat(GameBoardElement element) {
        GameBoardElement.Effect effect = element.getEffect();
        int toChange = effect.callEffect();
        int points = Configs.defaultPointValueForGameBoardElement;
        switch (effect){
            case shrink: {
                //check that body not smaller then minimum size of 1
                if((body.size() - toChange) < 2) {
                    mutex.lock();
                    Rectangle head = body.get(0);
                    body.clear();
                    body.add(head);
                    mutex.unlock();
                } else {
                    mutex.lock();
                    for (int i = 0; i < toChange; i++) {
                        body.remove(body.size()-1);
                    }
                    mutex.unlock();
                }
                break;
            }
            case grow: {
                for (int i = 0; i < toChange; i++) {
                    addBodyElement();
                }
                break;
            }
            case changePoints: case food: {
                addBodyElement();
                points += toChange;
                break;
            }
        }
        return points;
    }

    private void addBodyElement() {
        Rectangle rectangle = new Rectangle(20,20,Configs.snakeColor);
        //-1 and not Visible until last element gets new head in upcoming move
        rectangle.setVisible(false);
        rectangle.setLayoutX(-1);
        mutex.lock();
        rectangle.setLayoutX(body.get(body.size()-1).getLayoutX());
        rectangle.setLayoutY(body.get(body.size()-1).getLayoutY());
        body.add(rectangle);
        mutex.unlock();
    }

    /**
     * Used to set the velocity Vector in the 2 dimensional GameBoard.
     */
    public enum Speed {
        UP(0,-20),LEFT(-20,0),RIGHT(20,0),DOWN(0,20);
        private int speedX, speedY;

        Speed(int x, int y) {
            speedX = x;
            speedY = y;
        }

        public int getSpeedY() {
            return speedY;
        }

        public int getSpeedX() {
            return speedX;
        }
    }
}
