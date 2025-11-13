package org.example.baitaplamgame.Model;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.ImageLoader;
import org.example.baitaplamgame.PowerUp.BossBullet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Brick {
    private double velocityX = 1.5;
    private long lastShotTime = 0;
    private long lastHitTime = 0;
    // ở đầu class Boss (fields)
    private javafx.animation.TranslateTransition shakeAnim; // giữ animation để tạo 1 lần
    private boolean halfHealthTriggered = false; // đánh dấu đã vào giai đoạn half-health
    private final List<BossBullet> bullets = new ArrayList<>();
    private final Pane root;
    private boolean enraged = false;
    private ColorAdjust bossEffect = new ColorAdjust();

    public Boss(double x, double y, Pane root) {
        super(x, y, 250, 200, 14, "boss");
        this.root = root;
        this.hitPoints = 30;

        this.view = new ImageView(ImageLoader.BOSS_IMAGE);
        this.view.setFitWidth(250);
        this.view.setFitHeight(200);
        this.view.setLayoutX(x);
        this.view.setLayoutY(y);

        // Gắn hiệu ứng màu cho boss
        view.setEffect(bossEffect);

        root.getChildren().add(this.view);
    }

    public void update() {
        // di chuyển Boss như cũ ...
        x += velocityX;
        if (x <= 0 || x + width >= Config.WINDOW_WIDTH - 220) {
            velocityX *= -1;
        }
        view.setLayoutX(x);
        view.setLayoutY(y);

        // bắn đạn
        long now = System.currentTimeMillis();
        if (now - lastShotTime > 1500) {
            shoot();
            lastShotTime = now;
        }

        if (!enraged && hitPoints <= getMaxHealth() / 2) {
            enraged = true;
            velocityX *= 1.5;
            bossEffect.setHue(-0.3);
            System.out.println("🔥 Boss nổi giận!");

            if (shakeAnim == null) {
                shakeAnim = new TranslateTransition(Duration.millis(8000), view);
                shakeAnim.setFromX(-3);
                shakeAnim.setToX(3);
                shakeAnim.setAutoReverse(true);
                shakeAnim.setCycleCount(Animation.INDEFINITE);
                shakeAnim.play();
            }

            halfHealthTriggered = true;
        }

        // ✅ chỉ update đạn, không xóa
        for (BossBullet b : bullets) {
            b.update();
        }
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
        return 30;
    }
    public boolean hasTriggeredHalfHealthPhase() {
        return halfHealthTriggered;
    }

    public void setTriggeredHalfHealthPhase(boolean v) {
        this.halfHealthTriggered = v;
    }

}
