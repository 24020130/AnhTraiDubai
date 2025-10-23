package org.example.baitaplamgame.Ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HUDPanel extends Pane {

    private Text scoreText;
    private Text livesText;
    private Text levelText;

    private int score = 0;
    private int lives = 3;
    private int level = 1;

    public HUDPanel(double width, double height) {
        setPrefSize(width, height);
        setPickOnBounds(false); // Không chặn click của các phần dưới
        setMouseTransparent(true); // Cho phép GamePanel nhận input

        scoreText = new Text(20, 30, "Score: " + score);
        livesText = new Text(width / 2 - 30, 30, "Lives: " + lives);
        levelText = new Text(width - 100, 30, "Level: " + level);

        for (Text t : new Text[]{scoreText, livesText, levelText}) {
            t.setFont(Font.font(20));
            t.setFill(Color.WHITE);
        }

        getChildren().addAll(scoreText, livesText, levelText);
    }

    public void updateScore(int s) {
        score = s;
        scoreText.setText("Score: " + score);
    }

    public void updateLives(int l) {
        lives = l;
        livesText.setText("Lives: " + lives);
    }

    public void updateLevel(int l) {
        level = l;
        levelText.setText("Level: " + level);
    }
}
