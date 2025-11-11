package org.example.baitaplamgame.Model;

import javafx.geometry.Bounds;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.PowerUp.BossBullet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Brick {
    private double velocityX = 2.5;
    private long lastShotTime = 0;
    private long lastHitTime = 0;
    private final List<BossBullet> bullets = new ArrayList<>();
    private final Pane root;

    // 🟥 Thêm biến mới cho hiệu ứng nổi giận
    private boolean enraged = false;
    private ColorAdjust bossEffect = new ColorAdjust();

    public Boss(double x, double y, Pane root) {
        super(x, y, 360, 240, 30, "boss");
        this.root = root;
        this.hitPoints = 30;

        this.view = new ImageView(ImageLoader.BOSS_IMAGE);
        this.view.setFitWidth(360);
        this.view.setFitHeight(240);
        this.view.setLayoutX(x);
        this.view.setLayoutY(y);

        // Gắn hiệu ứng màu cho boss
        view.setEffect(bossEffect);

        root.getChildren().add(this.view);
    }

    public void update() {
        x += velocityX;
        if (x <= 0 || x + width >= Config.WINDOW_WIDTH - 220) {
            velocityX *= -1;
        }

        view.setLayoutX(x);
        view.setLayoutY(y);

        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            shoot();
            lastShotTime = now;
        }

        // 💥 Khi máu còn 50% thì boss nổi giận
        if (!enraged && hitPoints <= 15) {
            enraged = true;
            velocityX *= 1.5;            // Tăng tốc độ di chuyển
            bossEffect.setHue(-0.3);     // Đổi tông màu hơi đỏ
            System.out.println("🔥 Boss nổi giận! Tăng tốc độ và tấn công nhanh hơn!");
        }

        // 💢 Rung nhẹ khi nổi giận
        if (enraged) {
            double shake = Math.random() * 4 - 2;
            view.setLayoutX(x + shake);
        }

        // Cập nhật đạn của boss
        bullets.removeIf(b -> {
            b.update();
            if (b.getY() > Config.WINDOW_HEIGHT) {
                root.getChildren().remove(b.getView());
                return true;
            }
            return false;
        });
    }

    @Override
    public void render(Graphics g) {}

    private void shoot() {
        // Bắn nhanh hơn khi nổi giận
        int maxBullets = enraged ? 8 : 5;
        if (bullets.size() > maxBullets) return;

        BossBullet bullet = new BossBullet(x + width / 2 - 5, y + height, root);
        bullets.add(bullet);
    }

    public List<BossBullet> getBullets() {
        return bullets;
    }

    @Override
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public void takeDamage() {
        long now = System.currentTimeMillis();
        if (now - lastHitTime < 300) return; // tránh trừ máu quá nhanh
        lastHitTime = now;

        hitPoints--;
        System.out.println("Boss HP: " + hitPoints);

        // Khi chết thì xoá khỏi màn hình
        if (hitPoints <= 0) {
            root.getChildren().remove(view);
            System.out.println("💀 Boss bị tiêu diệt!");
        }
    }

    @Override
    public Bounds getBounds() {
        return super.getBounds();
    }

    public int getHealth() {
        return hitPoints;
    }

    public int getMaxHealth() {
        return 30; // Boss có 30 máu tối đa
    }
}
