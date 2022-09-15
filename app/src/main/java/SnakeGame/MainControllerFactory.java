package SnakeGame;

import javafx.application.Platform;

public class MainControllerFactory {
    private MainController mainController;

    public MainControllerFactory(MainController mainController) {
        this.mainController = mainController;
    }

    public void draw() {
        Platform.runLater(() -> {
            mainController.draw();
        });
    }

    public void showMessage(Message msg) {
        switch (msg.getMsgType()) {
            default -> mainController.drawMessage(msg);
        }
    }


}
