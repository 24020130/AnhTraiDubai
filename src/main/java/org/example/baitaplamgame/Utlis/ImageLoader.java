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
    public static final Image BACKGROUND_IMAGE = loadImage("/images/img.png");
    public static final Image ITEM_1_BACKGROUND = loadImage("/images/item1.png");
    public static final Image ITEM_STAR = loadImage("/images/Item_star.png");
    public static final Image MULTI_BALL_BRICK = loadImage("/images/Multiballbrick.png");
    public static final Image ITEM_SHIRK_PADDLE = loadImage("/images/ItemShirk.png");
    public static final Image BRICK_SHIRK_PADDLE = loadImage("/images/ShirkPaddle.png");
    public static final Image BLUE_BRICK = loadImage("/images/Blue_Brick.png");
    public static final Image GREEN_BRICK = loadImage("/images/Green_Brick.png");
    public static final Image ORANGE_BRICK = loadImage("/images/Orange_Brick.png");
    public static final Image PURPLE_BRICK = loadImage("/images/Purple_Brick.png");
    public static final Image JADE_BRICK = loadImage("/images/Jade_Brick.png");
    public static final Image YELLOW_BRICK = loadImage("/images/Yellow_Brick.png");
    public static final Image BACKGROUND_LEVEL1 = loadImage("/images/bg1.png");
    public static final Image BACKGROUND_LEVEL2 = loadImage("/images/bg2.png");
    public static final Image BACKGROUND_LEVEL3 = loadImage("/images/bg3.png");
    public static final Image BACKGROUND_LEVEL4 = loadImage("/images/bg4.png");
}
