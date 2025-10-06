package org.example.baitaplamgame.Model;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.Utlis.Config;

public class Paddle extends MovableObject {
    private double speed;
    private PowerUp currentPowerUp;
    private final ImageView view;

    public Paddle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;

        // Nếu bạn có ảnh paddle thì dùng ImageLoader.PADDLE_IMAGE, nếu không có thì có thể tạo Rectangle trong View
        view = new ImageView(ImageLoader.PADDLE_IMAGE);
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setX(x);
        view.setY(y);
    }

    @Override
    public void update() {
        // nếu bạn dùng move() để cập nhật x,y thì gọi move() ở GameManager hoặc ở đây
        updateView();
    }

    public void moveLeft() {
        x = Math.max(0, x - speed);
        updateView();
    }

    public void moveRight() {
        x = Math.min(Config.WINDOW_WIDTH - width, x + speed);
        updateView();
    }

    public void applyPowerUp(PowerUp p) {
        if (currentPowerUp != null) currentPowerUp.removeEffect(this); // nếu cần hủy effect cũ
        currentPowerUp = p;
        p.applyEffect(this);
    }

    private void updateView() {
        view.setX(x);
        view.setY(y);
        view.setFitWidth(width);
        view.setFitHeight(height);
    }

    // --- getters / setters cho width/height để PowerUp dùng được ---
    @Override
    public double getX() { return x; }
    @Override
    public double getY() { return y; }

    public double getWidth() {
        return width;
    }

    public void setWidth(double newWidth) {
        // giữ trung tâm paddle khi thay đổi kích thước
        double center = x + width / 2.0;
        this.width = newWidth;
        this.x = center - newWidth / 2.0;

        // giới hạn vào trong màn hình
        if (x < 0) x = 0;
        if (x + width > Config.WINDOW_WIDTH) x = Config.WINDOW_WIDTH - width;

        updateView();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double newHeight) {
        this.height = newHeight;
        updateView();
    }

    public ImageView getView() {
        return view;
    }

    // nếu GameObject đã có getBounds(), có thể dùng lại; nhưng bạn có thể override nếu cần
    public Bounds getBounds() {
        return view.getBoundsInParent();
    }

}
