package org.example.baitaplamgame.Model;

import java.awt.*;

public class HardBrick extends Brick {
    public HardBrick(double x, double y, double width, double height) {
        super(x, y, width, height, 3, "hard");
    }

    @Override
    public void takeHit() {
        if (!isDestroyed()) {
            hitPoints--;
            playShakeEffect(); // Gọi hiệu ứng rung mỗi khi bị va chạm
            if (isDestroyed()) {
                view.setVisible(false); // Ẩn gạch khi bị phá hủy
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // Không cần thiết nếu dùng JavaFX, có thể bỏ trống
    }
}