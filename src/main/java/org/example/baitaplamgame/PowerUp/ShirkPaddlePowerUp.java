package org.example.baitaplamgame.PowerUp;

import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Model.PowerUp;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class ShirkPaddlePowerUp extends PowerUp {
    public ShirkPaddlePowerUp(double x, double y) {
        super(x, y, "shirkpaddle");

        view.setImage(ImageLoader.ITEM_SHIRK_PADDLE);
        view.setFitWidth(25);
        view.setFitHeight(25);
        view.setX(x);
        view.setY(y);
    }
    public void applyEffect(Paddle paddle) {
        double newWidth = paddle.getWidth() / 2;
        paddle.setWidth(newWidth);
        collected = true;
        System.out.println("Paddle đã được mở rộng!");

        /**
         * sau 5 giay thu nho lai.
         */
        new Thread(() -> {
            try {
                Thread.sleep(5000); // 5 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            javafx.application.Platform.runLater(() -> removeEffect(paddle));
        }).start();
    }


    @Override
    public void removeEffect(Paddle paddle) {
        double originalWidth = paddle.getWidth() * 2;
        paddle.setWidth(originalWidth);
        System.out.println("Paddle trở lại kích thước ban đầu!");
    }


}
