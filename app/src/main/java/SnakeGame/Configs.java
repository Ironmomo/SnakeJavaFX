package SnakeGame;

import javafx.scene.paint.Color;

public class Configs {
    public final static int gameBoardWidth = 420;
    public final static int gameBoardHeight = 420;
    public final static int snakeWidth = 20;
    public final static int snakeHeight = 20;
    public final static Color snakeColor = Color.BLACK;
    public final static int snakePosDefaultX = Configs.snakeWidth * (Configs.gameBoardColumns/2);
    public final static int snakePosDefaultY = Configs.snakeHeight * (Configs.gameBoardRows/2);
    public final static Color effectItemColor = Color.GREEN;
    public final static int windowSize = 750;
    public final static int gameBoardColumns = (gameBoardWidth - snakeWidth) / snakeWidth;
    public final static int gameBoardRows = (gameBoardHeight - snakeWidth) / snakeHeight;
    public final static Color foodColor = Color.RED;
    public static int highScore = 0;
    public static int chaosHighScore = 0;
    public final static int defaultPointValueForGameBoardElement = 0;
    public final static int gameBoardElementWidth = 20;
    public final static int gameBoardElementHeight = 20;
    public final static int maxNumOfFoodItems = 1;
    public final static int maxNumOfEffectItems = 2;
    public final static int foodValue = 1;
    public final static int maxValueToGrow = 3;
    public final static int maxValueToShrink = 6;
    public final static int maxValueToChangePoints = 4;
    public final static int foodItemTimeToLive = 20;
    public final static int positivEffectTimeToLive = 5;
    public final static int negativEffectTimeToLive = 8;
}
