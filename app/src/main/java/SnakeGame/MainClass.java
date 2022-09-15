package SnakeGame;

import SnakeGame.Data.FileAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainClass extends Application {

    private MainController controller;
    private Lock mutex;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileAccess.readFile();
        mutex = new ReentrantLock();
        //Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        //Set Size and default Values
        BorderPane root = (BorderPane) loader.load();
        VBox vBox = (VBox) root.getCenter();
        AnchorPane anchorPane = (AnchorPane) vBox.getChildren().get(1);
        anchorPane.setPrefSize(Configs.gameBoardWidth,Configs.gameBoardHeight);
        root.setCenter(anchorPane);
        //Set Stage
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(Configs.windowSize);
        primaryStage.setHeight(Configs.windowSize);
        primaryStage.show();
        controller = loader.getController();
        controller.startUp(mutex);
    }

    @Override
    public void stop() throws Exception {
        controller.cleanUp();
        super.stop();
    }
}
