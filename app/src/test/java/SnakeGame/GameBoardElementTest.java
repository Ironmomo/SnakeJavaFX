package SnakeGame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GameBoardElementTest {

    @Nested
    @DisplayName("Test Effect Enum")
    public class EffectTest {

        @Test
        void test() {
            for (int i = 0; i < 100; i++) {
                Assertions.assertEquals(1,GameBoardElement.Effect.food.callEffect());
                Assertions.assertTrue(() -> {
                    int num = GameBoardElement.Effect.shrink.callEffect();
                    return num >= 1 && num <= Configs.maxValueToShrink;
                });
                Assertions.assertTrue(() -> {
                    int num = GameBoardElement.Effect.grow.callEffect();
                    return num >= 1 && num <= Configs.maxValueToGrow;
                });
                Assertions.assertTrue(() -> {
                    int num = GameBoardElement.Effect.changePoints.callEffect();
                    return (num >= 1 && num <= Configs.maxValueToChangePoints) || (num <= -1 && num >= Configs.maxValueToChangePoints*-1);
                });
            }
        }
    }
}
