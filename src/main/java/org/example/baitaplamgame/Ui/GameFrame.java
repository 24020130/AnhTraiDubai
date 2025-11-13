package org.example.baitaplamgame.Ui;

import javafx.scene.layout.StackPane;
import javafx.application.Platform;

public class GameFrame extends StackPane {

    private GamePanel gamePanel;
    private HUDPanel hudPanel;
    private MenuPanel menuPanel;

    public GameFrame(double width, double height) {
        setPrefSize(width, height);
        gamePanel = new GamePanel();
        hudPanel = new HUDPanel(width, height);
        menuPanel = new MenuPanel(width, height);
        gamePanel.setPrefSize(width, height);
        hudPanel.setPrefSize(width, height);
        menuPanel.setPrefSize(width, height);
        getChildren().addAll(gamePanel, hudPanel, menuPanel);
        menuPanel.getPlayButton().setOnAction(e -> startGame());
        menuPanel.getExitButton().setOnAction(e -> Platform.exit());
        showMenu();
    }

    public void showMenu() {
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
        hudPanel.setVisible(false);
    }

    public void startGame() {
        menuPanel.setVisible(false);
        gamePanel.setVisible(true);
        hudPanel.setVisible(true);
    }
}
