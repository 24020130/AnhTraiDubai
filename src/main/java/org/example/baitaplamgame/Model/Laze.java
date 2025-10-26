package org.example.baitaplamgame.Model;

import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.ImageLoader;
import java.awt.*;

public class Laze extends GameObject {
    private final double speed = 7;

    public Laze(double x, double y, double width, double height) {
        super(x, y, width, height);

        // Sử dụng view kế thừa từ GameObject
        view = new ImageView(ImageLoader.LAZE_SP);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void update() {
        y -= speed;
        view.setY(y);
    }

    @Override
    public ImageView getView() {
        return view;
    }

    public boolean isOutOfScreen() {
        return view.getY() + view.getFitHeight() < 0;
    }

    @Override
    public void render(Graphics g) {
        // Không cần vẽ gì thêm, vì đã có view trong JavaFX
    }
}
