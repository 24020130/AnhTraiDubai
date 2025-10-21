package org.example.baitaplamgame.Ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GamePanel extends Pane {
    private boolean menuVisible = false;
    private VBox menuPanel;
    private BoxBlur blur;

    public GamePanel(double width, double height) {
        setPrefSize(width, height);
        setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a1a, #2f2f2f);");

        // --- Panel menu ---
        menuPanel = new VBox(20);
        menuPanel.setPadding(new Insets(40, 30, 40, 30));
        menuPanel.setAlignment(Pos.CENTER_LEFT);
        menuPanel.setPrefWidth(250);
        menuPanel.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-border-color: #00ff88; -fx-border-width: 2;");
        menuPanel.setTranslateX(-250); // Ẩn lúc đầu

        blur = new BoxBlur(5, 5, 3);

        // --- Nút menu chính ---
        Button menuBtn = new Button("☰");
        menuBtn.setFont(Font.font(24));
        menuBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        menuBtn.setLayoutX(20);
        menuBtn.setLayoutY(20);
        menuBtn.setOnAction(e -> toggleMenu());

        // --- Các nút trong menu ---
        Button startBtn = createMenuButton("▶ Start Game");
        Button settingBtn = createMenuButton("⚙ Settings");
        Button exitBtn = createMenuButton("✖ Exit");

        exitBtn.setOnAction(e -> System.exit(0));

        menuPanel.getChildren().addAll(startBtn, settingBtn, exitBtn);

        getChildren().addAll(menuBtn, menuPanel);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setFont(Font.font("Consolas", 18));
        btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00ff88, #00aa66);" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00ffaa, #00cc77);" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: linear-gradient(to right, #00ff88, #00aa66);" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void toggleMenu() {
        double menuWidth = menuPanel.getPrefWidth();

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), menuPanel);
        FadeTransition fade = new FadeTransition(Duration.millis(400), menuPanel);

        if (!menuVisible) {
            slide.setToX(0);
            fade.setFromValue(0);
            fade.setToValue(1);
            setEffect(blur);
        } else {
            slide.setToX(-menuWidth);
            fade.setFromValue(1);
            fade.setToValue(0);
            setEffect(null);
        }

        ParallelTransition transition = new ParallelTransition(slide, fade);
        transition.play();

        menuVisible = !menuVisible;
    }

}
