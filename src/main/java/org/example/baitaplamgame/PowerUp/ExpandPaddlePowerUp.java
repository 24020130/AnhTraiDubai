package org.example.baitaplamgame.PowerUp;

import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Model.PowerUp;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class ExpandPaddlePowerUp extends PowerUp {
    public ExpandPaddlePowerUp(double x, double y) {
        super(x, y, "expand");
        this.view = new ImageView(ImageLoader.ITEM_IMAGE);
        view.setFitWidth(25);
        view.setFitHeight(25);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void applyEffect(Paddle paddle) {
        double newWidth = paddle.getWidth() * 1.5;
        paddle.setWidth(newWidth);
        collected = true;
        System.out.println("Paddle đã được mở rộng!");

        /**
         * sau 10 giay thu nho lai.
         */
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 10 giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            javafx.application.Platform.runLater(() -> removeEffect(paddle));
        }).start();
    }


    @Override
    public void removeEffect(Paddle paddle) {
        double originalWidth = paddle.getWidth() / 1.5;
        paddle.setWidth(originalWidth);
        System.out.println("Paddle trở lại kích thước ban đầu!");
    }

}

