package SnakeGame;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class SnakeTest {

    private Snake snake;

    @Nested
    @DisplayName("Positiv")
    public class positivTestCase {

        @BeforeEach
        void setup() {
            snake = new Snake(new ReentrantLock());
        }

        /**
         * Tests if snake body gets instantiated correctly
         */
        @Test
        @DisplayName("Correct instantiate")
        void instantiate() {
            Assertions.assertEquals(1,snake.getBody().size());
        }

        /**
         * Tests if the snake does move like expected by the speed value set
         */
        @Test
        @DisplayName("Test move and change speed")
        void move() {
            Assertions.assertEquals(Configs.snakePosDefaultX,snake.getBody().get(0).getLayoutX(),"Set rectangle to correct position first");
            Assertions.assertEquals(Configs.snakePosDefaultY,snake.getBody().get(0).getLayoutY(),"Set rectangle to correct position first");
            snake.move();
            Assertions.assertEquals(Configs.snakePosDefaultX+20,snake.getBody().get(0).getLayoutX(),"Set rectangle to correct position first");
            Assertions.assertEquals(Configs.snakePosDefaultY,snake.getBody().get(0).getLayoutY(),"Set rectangle to correct position first");
            snake.setSpeed(Snake.Speed.DOWN);
            snake.move();
            Assertions.assertEquals(Configs.snakePosDefaultX+20,snake.getBody().get(0).getLayoutX(),"Set rectangle to correct position first");
            Assertions.assertEquals(Configs.snakePosDefaultY+20,snake.getBody().get(0).getLayoutY(),"Set rectangle to correct position first");
        }

        /**
         * Tests if checkCrash checks for a snake crash correctly.
         * To setup the snake for a crashTest use the setupSnakeForTest() at first.
         * This allows to build more moving scenarios easily.
         */
        @Test
        @DisplayName("Check for crash opposite direction")
        void checkCrashOpposite() {
            setupSnakeForTest();
            //Make snake move in opposite direction and collapse
            snake.setSpeed(Snake.Speed.LEFT);
            snake.move();
            Assertions.assertTrue(snake.checkCrash());
        }

        /**
         * Makes the snake grow to a length of 11 on a straight line
         */
        private void setupSnakeForTest() {
            GameBoardElement gameBoardElementMock = Mockito.mock(GameBoardElement.class);
            Point position = new Point(Configs.snakePosDefaultX,Configs.snakePosDefaultY);
            Mockito.when(gameBoardElementMock.getPosition()).thenReturn(position);
            Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.food);

            //make snake eat 10 food effects to grow 10 times
            for (int i = 0; i < 10; i++) {
                snake.eat(gameBoardElementMock);
            }
            Assertions.assertEquals(11,snake.getBody().size(),"Wrong state to execute test");

            //make snake move in default direction to bring snake in stretched position
            for (int i = 0; i < snake.getBody().size(); i++) {
                Assertions.assertTrue(snake.getBody().get(i).isVisible(),"Wrong state to execute test");
                for (int j = i+1; j < snake.getBody().size(); j++) {
                    Assertions.assertFalse(snake.getBody().get(j).isVisible(),"Wrong state to execute test");
                }
                snake.move();
                Assertions.assertFalse(snake.checkCrash());
            }
            //checked if setup correctly
            for (Rectangle be: snake.getBody()) {
                Assertions.assertTrue(be.isVisible(),"Wrong state to execute test");
            }
        }

        /**
         * This nested class does test the process when the snake eats a GameBoardElement
         * The snake needs to activate the effect of the GameBoardElement and calculates the points added to the game.
         */
        @Nested
        public class CheckForItemTest {
            private GameBoardElement gameBoardElementMock;
            private Point position;

            @BeforeEach
            void setup() {
                gameBoardElementMock = Mockito.mock(GameBoardElement.class);
                position = new Point(Configs.snakePosDefaultX,Configs.snakePosDefaultY);
                //Setup gameBoardElementMock
                Mockito.when(gameBoardElementMock.getPosition()).thenReturn(position);
                Mockito.when(gameBoardElementMock.stillAlive()).thenReturn(true);
            }

            @Test
            @DisplayName("GameBoardElement with effect food")
            void checkForItemFood() {
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.food);
                Assertions.assertEquals(Configs.defaultPointValueForGameBoardElement+Configs.foodValue,snake.eat(gameBoardElementMock));
                Assertions.assertEquals(2,snake.getBody().size());
                Mockito.verify(gameBoardElementMock).getEffect();
                Mockito.verifyNoMoreInteractions(gameBoardElementMock);
            }

            @Test
            @DisplayName("GameBoardElement with effect shrink")
            void checkForItemShrink() {
                //Check with snake body == 1
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.shrink);
                Assertions.assertEquals(Configs.defaultPointValueForGameBoardElement,snake.eat(gameBoardElementMock));
                Assertions.assertTrue(() -> snake.getBody().size() == 1);
                Mockito.verify(gameBoardElementMock).getEffect();
                Mockito.verifyNoMoreInteractions(gameBoardElementMock);

                //Check with snake body size > 1
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.food);
                for (int i = 0; i < 10; i++) {
                    snake.eat(gameBoardElementMock);
                }
                Assertions.assertEquals(11,snake.getBody().size());
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.shrink);
                snake.eat(gameBoardElementMock);
                Assertions.assertTrue(() -> snake.getBody().size() < 11 && snake.getBody().size() >= (11 - Configs.maxValueToShrink));
            }

            @Test
            @DisplayName("GameBoardElement with effect grow")
            void checkForItemGrow() {
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.grow);
                Assertions.assertEquals(Configs.defaultPointValueForGameBoardElement,snake.eat(gameBoardElementMock));
                Assertions.assertTrue(() -> snake.getBody().size() > 1 && snake.getBody().size() <= (Configs.maxValueToGrow + 1));
            }

            @Test
            @DisplayName("GameBoardElement with effect changePoints")
            void checkFotItemChangePoints() {
                Mockito.when(gameBoardElementMock.getEffect()).thenReturn(GameBoardElement.Effect.changePoints);
                for (int i = 0; i < 100; i++) {
                    Assertions.assertTrue(() -> {
                        int num = snake.eat(gameBoardElementMock);
                        return (num >= 1 && num <= Configs.maxValueToChangePoints) || (num <= -1 && num >= Configs.maxValueToChangePoints*-1);
                    });
                    Assertions.assertEquals(2+i,snake.getBody().size());
                }
            }
        }
    }
}
