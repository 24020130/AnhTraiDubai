package org.example.baitaplamgame.PowerUp;

import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Model.PowerUp;

public class ExpandPaddlePowerUp extends PowerUp {
    public ExpandPaddlePowerUp(double x, double y, String type) {
        super(x, y, type);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        paddle.setWidth(paddle.getWidth() * 1.5);
    }
    @Override
    public void removeEffect(Paddle paddle) {
        paddle.setWidth(paddle.getWidth() / 1.5);
    }
}
