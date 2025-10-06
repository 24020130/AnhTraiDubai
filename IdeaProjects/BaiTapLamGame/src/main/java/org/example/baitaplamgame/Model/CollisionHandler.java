package org.example.baitaplamgame.Model;

import javafx.scene.layout.Pane;

import java.util.List;

public class CollisionHandler {
    public static boolean checkCollision(GameObject a, GameObject b) {
        if (a == null || b == null) return false;
        if (a.getBounds() == null || b.getBounds() == null) return false;
        return a.getBounds().intersects(b.getBounds());
    }



    public static boolean handleBallBrickCollision(Ball ball, Brick brick, List<PowerUp> powerUps, Pane root) {
        if (checkCollision(ball, brick)) {
            brick.takeHit();
            ball.bounceOff(brick);

            if (brick.isDestroyed()) {
                root.getChildren().remove(brick.getView());

                    PowerUp p = new ExpandPowerUp(brick.getX(), brick.getY());
                    powerUps.add(p);
                    if (!root.getChildren().contains(p.getView())) {
                        root.getChildren().add(p.getView());
                    }

                return true; //
            }
        }
        return false;
    }

    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!checkCollision(ball, paddle)) return;

        double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
        double ballCenter = ball.getX() + ball.getWidth() / 2;
        double offset = (ballCenter - paddleCenter) / (paddle.getWidth() / 2);
        double maxBounceAngle = Math.toRadians(75);
        double angle = offset * maxBounceAngle;

        double speed = ball.getSpeed();
        double newVx = speed * Math.sin(angle);
        double newVy = -speed * Math.cos(angle);

        ball.setVelocity(newVx, newVy);
    }


}
