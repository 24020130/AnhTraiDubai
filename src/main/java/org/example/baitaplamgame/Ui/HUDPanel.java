package org.example.baitaplamgame.Ui;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class HUDPanel extends VBox {

    private final Text levelText;
    private final Text scoreText;
    private final Text livesText;
    private final Text bossHpText; // thêm dòng này
    private final double panelWidth = 220;

    private Runnable onSave, onExit;

    public HUDPanel(double windowWidth, double windowHeight) {
        setPrefWidth(panelWidth);
        setPrefHeight(windowHeight);
        setLayoutX(windowWidth - panelWidth);
        setLayoutY(0);

        setSpacing(20);
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
        bossHpText = createText("Boss HP: 0 / 0"); // dòng Boss HP
        bossHpText.setVisible(false); // ẩn lúc đầu

        // ===== BUTTONS =====
        Button saveBtn = createButton("💾 Save");
        saveBtn.setOnAction(e -> {
            if (onSave != null) onSave.run();
        });

        Button exitBtn = createButton("🏠 Exit");
        exitBtn.setOnAction(e -> {
            if (onExit != null) onExit.run();
        });

        VBox buttonBox = new VBox(15, saveBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(60, 0, 0, 0));

        getChildren().addAll(title, levelText, scoreText, livesText, bossHpText, buttonBox);
    }

    private Text createText(String s) {
        Text t = new Text(s);
        t.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        t.setFill(Color.web("#FFF5E6"));
        t.setEffect(new DropShadow(5, Color.web("#FF6600")));
        return t;
    }

    private Button createButton(String label) {
        Button btn = new Button(label);
        btn.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("""
-fx-background-color: linear-gradient(to right, #FF8000, #FF3300);
            -fx-background-radius: 20;
            -fx-cursor: hand;
        """);
        btn.setPrefWidth(150);
        btn.setEffect(new DropShadow(8, Color.web("#FF5500")));
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1));
        return btn;
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

    public void setOnSave(Runnable action) { this.onSave = action; }
    public void setOnExit(Runnable action) { this.onExit = action; }

    // ====================== BOSS HP ======================
    public void showBossHP(boolean visible) {
        bossHpText.setVisible(visible);
    }

    public void updateBossHP(int currentHP, int maxHP) {
        if (!bossHpText.isVisible()) return;
        if (currentHP < 0) currentHP = 0;
        bossHpText.setText("Boss HP: " + currentHP + " / " + maxHP);
    }
}