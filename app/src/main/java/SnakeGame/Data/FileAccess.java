package SnakeGame.Data;

import SnakeGame.Configs;

import java.io.*;

public class FileAccess {

    private static File file = new File("data.txt");;

    public static void readFile() {
        try(FileInputStream reader = new FileInputStream(file.getPath())) {
            Configs.highScore = reader.read();
            Configs.chaosHighScore = reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeNewHighScore() {
        try(FileOutputStream writer = new FileOutputStream(file.getPath())) {
            writer.write(Configs.highScore);
            writer.write(Configs.chaosHighScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
