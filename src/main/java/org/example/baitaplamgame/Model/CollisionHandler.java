package org.example.baitaplamgame.Model;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.PowerUp.ExpandPaddlePowerUp;
import org.example.baitaplamgame.PowerUp.FastBallPower;
import org.example.baitaplamgame.PowerUp.MultiBallPower;
import org.example.baitaplamgame.PowerUp.ShirkPaddlePowerUp;

import java.util.Iterator;
import java.util.List;

public class CollisionHandler {
    /**
     * check va cham 2 doi tuong
     * @param a
     * @param b
     * @return
     */
    public static boolean checkCollision(GameObject a, GameObject b) {
        if (a == null || b == null) return false;
        if (a.getBounds() == null || b.getBounds() == null) return false;
        return a.getBounds().intersects(b.getBounds());
    }

    /**
     * check va cham bong voi tuong.
     * @param ball
     * @param brick
     * @param powerUps
     * @param root
     * @return
     */

    public static boolean handleBallBrickCollision(Ball ball, Brick brick, List<PowerUp> powerUps, Pane root) {
        if (checkCollision(ball, brick)) {
            brick.takeHit();
            ball.bounceOff(brick);
            if (brick.isDestroyed()) {
                root.getChildren().remove(brick.getView());
                PowerUp p = null;
                if (brick instanceof GreenBrick) {
                    p = new ExpandPaddlePowerUp(brick.getX(), brick.getY());
                } else if (brick instanceof FastBrick) {
                    p = new FastBallPower(brick.getX(), brick.getY());
                } else if (brick instanceof MultiBrick) {
                    p = new MultiBallPower(brick.getX(), brick.getY());
                } else if(brick instanceof ShrinkPaddle) {
                    p = new ShirkPaddlePowerUp(brick.getX(), brick.getY());
                }
                if(p != null){
                    powerUps.add(p);
                    root.getChildren().add(p.getView());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * check va cham bong voi nguoi choi.
     * @param ball
     * @param paddle
     */
    public static void handleBallPaddleCollision(Ball ball, Paddle paddle) {
        if (!checkCollision(ball, paddle)) return;

        double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
        double ballCenter = ball.getX() + ball.getWidth() / 2;
        double offset = (ballCenter - paddleCenter) / (paddle.getWidth() / 2);
        double maxBounceAngle = Math.toRadians(45);
        double angle = offset * maxBounceAngle;

        double speed = ball.getSpeed();
        double newVx = speed * Math.sin(angle);
        double newVy = -speed * Math.cos(angle);

        ball.setVelocity(newVx, newVy);
    }

    /**
     * check va cham item voi nguoi choi
     * @param powerUps
     * @param paddle
     * @param root
     */
    public static void handlePowerUpCollision(List<PowerUp> powerUps, Paddle paddle, Pane root) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();
            p.update();
            if (checkCollision(p, paddle) && !p.isCollected()) {
                p.applyEffect(paddle);
                p.setCollected(true);
                root.getChildren().remove(p.getView());
                iterator.remove();
            }
            else if (p.getY() > root.getHeight()) {
                root.getChildren().remove(p.getView());
                iterator.remove();
            }
        }
    }
    /**
     * Check va chạm Meteor với Paddle
     * @param meteors danh sách Meteor
     * @param paddle Paddle của người chơi
     * @param root Pane chính
     * @return số mạng mất đi
     */
    public static int handleMeteorPaddleCollision(List<Meteor> meteors, Paddle paddle, Pane root) {
        int lostLives = 0;
        Iterator<Meteor> iterator = meteors.iterator();

        while (iterator.hasNext()) {
            Meteor m = iterator.next();
            m.update(root);

            if (!m.isActive()) {
                iterator.remove();
                continue;
            }

            Bounds meteorHitbox = m.getHitbox(); // Dùng hitbox chính xác
            Bounds paddleBounds = paddle.getView().getBoundsInParent();

            // Va chạm + Meteor đã rơi đủ sâu để chạm paddle
            boolean collision = meteorHitbox.intersects(paddleBounds);
            boolean deepEnough = m.getLayoutY() + m.getBoundsInLocal().getHeight() * 0.8 >= paddle.getY();

            if (collision && deepEnough) {
                lostLives++;
                m.destroy(root);
                iterator.remove();
            }
            else if (m.getLayoutY() > root.getHeight()) {
                m.destroy(root);
                iterator.remove();
            }
        }

        return lostLives;
    }
}


