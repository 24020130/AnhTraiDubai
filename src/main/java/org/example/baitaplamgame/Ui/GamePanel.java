package org.example.baitaplamgame.Ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class GamePanel extends Pane {

    private Circle ball;
    private Rectangle paddle;

    public GamePanel(double width, double height) {
        setPrefSize(width, height);
        setStyle("-fx-background-color: #101820;"); // Màu nền tối

        // === Vẽ thanh đỡ (paddle) ===
        paddle = new Rectangle(width / 2 - 50, height - 40, 100, 15);
        paddle.setFill(Color.LIGHTBLUE);

        // === Vẽ quả bóng ===
        ball = new Circle(width / 2, height / 2, 8, Color.ORANGE);

        getChildren().addAll(paddle, ball);
    }

    public Circle getBall() {
        return ball;
    }

    public Rectangle getPaddle() {
        return paddle;
    }
}
