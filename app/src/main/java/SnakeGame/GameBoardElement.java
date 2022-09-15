package SnakeGame;

import javafx.scene.Node;

import java.awt.*;
import java.util.Date;
import java.util.function.Supplier;

/**
 * This class is used for the GameBoardElements on the GameBoard.
 * When chaos mode in game is true, a GameBoardElement can have different kind of effects. (See Effect enum)
 * A GameBoardElement has a Node element
 */
public class GameBoardElement {

    private Point position;
    private Effect effect;
    private long birth;
    private int timeToLive;

    public GameBoardElement(Point position, Effect effect, int timeToLive) {
        this.position = position;
        this.effect = effect;
        this.birth = new Date().getTime();
        this.timeToLive = timeToLive;
        }

    public boolean stillAlive() {
        return (birth + timeToLive) < new Date().getTime();
    }

    public Point getPosition() {
        return position;
    }

    public Effect getEffect() {
        return effect;
    }

    public enum Effect {
        grow(() -> (int) (Math.random() * Configs.maxValueToGrow) + 1),
        shrink(() -> (int) (Math.random() * Configs.maxValueToShrink) + 1),
        changePoints(() -> {
            int factor = 1;
            if(Math.random() > 0.7) {
                factor = -1;
            }
            return (int) ((Math.random() * Configs.maxValueToChangePoints) +1) * factor;
        }),
        food(() -> Configs.foodValue);
        private Supplier<Integer> function;
        Effect(Supplier<Integer> function) {
            this.function = function;
        }

        public int callEffect() {
            return function.get();
        }
    }

}
