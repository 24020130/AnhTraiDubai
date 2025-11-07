package org.example.baitaplamgame.Ui;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class HUDPanel extends VBox {

    private final Text levelText;
    private final Text scoreText;
    private final Text livesText;
    private final double panelWidth = 220;

    public HUDPanel(double windowWidth, double windowHeight) {
        setPrefWidth(panelWidth);
        setPrefHeight(windowHeight);
        setLayoutX(windowWidth - panelWidth);
        setLayoutY(0);

        setSpacing(35);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(100, 0, 0, 0));

        setStyle("""
            -fx-background-color: linear-gradient(to bottom right, #2A0900, #4A0E00, #7B1B00);
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-border-color: linear-gradient(to right, #FF8000, #FF3300);
            -fx-border-width: 2;
            -fx-effect: dropshadow(three-pass-box, rgba(255,100,0,0.5), 20, 0.3, 0, 0);
            """);

        Text title = new Text("PLAYER STATUS");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        title.setFill(Color.web("#FFA500"));
        title.setEffect(new DropShadow(15, Color.web("#FF3300")));

        levelText = createText("Level: 1");
        scoreText = createText("Score: 0");
        livesText = createText("Lives: 3");

        getChildren().addAll(title, levelText, scoreText, livesText);
    }

    private Text createText(String s) {
        Text t = new Text(s);
        t.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        t.setFill(Color.web("#FFF5E6")); // trắng vàng ấm
        t.setEffect(new DropShadow(5, Color.web("#FF6600")));
        return t;
    }

    public void updateHUD(int level, int score, int lives) {
        levelText.setText("Level: " + level);
        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + lives);
    }

    public void slideIn() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), this);
        setTranslateX(panelWidth);
        tt.setToX(0);
        tt.play();
    }
}