package org.example.baitaplamgame.Ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuPanel extends VBox {

    private Button playButton;
    private Button exitButton;

    public MenuPanel(double width, double height) {
        setPrefSize(width, height);
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: linear-gradient(to bottom, #0f2027, #203a43, #2c5364);");

        Text title = new Text("Brick Breaker");
        title.setFont(Font.font("Verdana", 36));
        title.setStyle("-fx-fill: white; -fx-font-weight: bold;");

        playButton = new Button("Play");
        playButton.setPrefWidth(200);
        playButton.setFont(Font.font(20));

        exitButton = new Button("Exit");
        exitButton.setPrefWidth(200);
        exitButton.setFont(Font.font(20));

        getChildren().addAll(title, playButton, exitButton);
    }

    public Button getPlayButton() {
        return playButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
