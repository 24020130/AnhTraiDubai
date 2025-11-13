package org.example.baitaplamgame.Utlis;

import javafx.scene.image.Image;

public class ImageLoader {


    private static Image loadImage(String path) {
        var stream = ImageLoader.class.getResourceAsStream(path);
        if (stream == null) {
            System.err.println("⚠️ Không tìm thấy ảnh: " + path);
            return null;
        }
        return new Image(stream);
    }

    // Các ảnh của game
    public static final Image PADDLE_IMAGE = loadImage("/images/player.png");
    public static final Image PLAYER2_IMAGE = loadImage("/images/player.png");
    public static final Image BALL_IMAGE = loadImage("/images/ball.png");
    public static final Image BRICK_IMAGE = loadImage("/images/brick.png");
    public static final Image ITEM_IMAGE = loadImage("/images/item.png");
    public static final Image BRICK_GREEN_IMAGE = loadImage("/images/brickGreen.png");
    public static final Image BRICK_FAST_IMAGE = loadImage("/images/FastBrick.png");
    public static final Image ITEM_1_BACKGROUND = loadImage("/images/item1.png");
    public static final Image ITEM_STAR = loadImage("/images/Item_star.png");
    public static final Image MULTI_BALL_BRICK = loadImage("/images/Multiballbrick.png");
    public static final Image ITEM_SHIRK_PADDLE = loadImage("/images/ItemShirk.png");
    public static final Image BRICK_SHIRK_PADDLE = loadImage("/images/ShirkPaddle.png");
    public static final Image BACKGROUND_LEVEL1 = loadImage("/images/bg1.png");
    public static final Image BACKGROUND_LEVEL2 = loadImage("/images/background2.png");
    public static final Image BACKGROUND_LEVEL3 = loadImage("/images/bg3.png");
    public static final Image BACKGROUND_LEVEL4 = loadImage("/images/bg4.png");
    public static final Image BRICK_RED_CRACK1 = loadImage("/images/breakbrick1.png");
    public static final Image BRICK_RED_CRACK2 = loadImage("/images/breakbrick1.png");
    public static final Image BOSS_IMAGE = loadImage("/images/boss.png");
    public static final Image SKIN_DEFAULT = loadImage("/skins/default.png");
    public static final Image SKIN_BLUE = loadImage("/skins/blue.png");
    public static final Image SKIN_GREEN = loadImage("/skins/green.png");
    public static final Image SUPPORT_RIGHT = loadImage("/images/Laser_sp.png");
    public static final Image LAZE_SP = loadImage("/images/laze.png");
    public static final Image SUPPORT1_RIGHT = loadImage("/images/support2.png");
    public static final Image SUPPORT2_RIGHT = loadImage("/images/support3.png");
}
