package org.example.baitaplamgame.PowerUp;

import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Model.Ball;
import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Model.PowerUp;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class FastBallPower extends PowerUp {
    private double originalVx;
    private double originalVy;

    public FastBallPower(double x, double y) {
        super(x, y, "fast");
        this.view = new ImageView(ImageLoader.ITEM_1_BACKGROUND);
        view.setFitHeight(25);
        view.setFitWidth(25);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        Ball ball = paddle.getBall();
        if (ball != null) {
            double originalSpeed = ball.getSpeed();

            double newSpeed = originalSpeed * 1.5;
            ball.setSpeed(newSpeed);
            System.out.println("FastBall activated! Speed = " + newSpeed);

            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                javafx.application.Platform.runLater(() -> {
                    ball.setSpeed(originalSpeed);
                    System.out.println("FastBall ended. Speed back to " + originalSpeed);
                });
            }).start();
        }
    }

    @Override
    public void removeEffect(Paddle paddle) {
    }

}
