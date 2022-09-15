package SnakeGame;

import javafx.scene.paint.Color;

public class Message {

    private String txt;
    private MessageType msgType;

    public Message(String txt, MessageType msgType) {
        this.txt = txt;
        this.msgType = msgType;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getTxt() {
        return txt;
    }

    public enum MessageType {
        pointsAdded(Color.GREEN),pointsRemoved(Color.RED),snakeGrow(Color.RED),snakeShrink(Color.GREEN);

        private Color txtColor;
        MessageType(Color color) {
            this.txtColor = color;
        }

        public Color getTxtColor() {
            return txtColor;
        }
    }
}
