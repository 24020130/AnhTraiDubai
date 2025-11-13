package org.example.baitaplamgame.Ui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;
import org.example.baitaplamgame.Utlis.SoundManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GamePanel extends Pane {
    private Runnable onStart;
    private int currentUnlockedLevel = 1;
    private int currentSkinIndex = 0;
    private static final String[] SKINS = {"default.png", "blue.png", "green.png"};
    private Consumer<Integer> onStartServer;
    private Consumer<String> onStartClient;

    private static final Color NEON_RED = Color.web("#ff3333");
    private static final Color NEON_ORANGE = Color.web("#ff9900");
    private static final Color NEON_BLUE = Color.web("#00ffff");
    private static final Color DARK_BG = Color.web("#180000");
    private static final Color DARK_ACCENT = Color.web("#330000");
    private static final Color DEEP_SPACE_BLACK = Color.web("#0a0000");
    private static final Color CYBER_CRIMSON_DARK = Color.web("#2c0000");

    private List<Button> mainButtons;
    private StackPane centerButtonContainer;
    private int currentMainButtonIndex = 0;

    private static final double LARGE_BUTTON_WIDTH = 650;
    private static final double LARGE_BUTTON_HEIGHT = 110;
    private static final double SMALL_NAV_BUTTON_WIDTH = 70;

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public void setOnStartServer(Consumer<Integer> onStartServer) {
        this.onStartServer = onStartServer;
    }

    public void setOnStartClient(Consumer<String> onStartClient) {
        this.onStartClient = onStartClient;
    }

    public GamePanel() {
        File progressFile = new File("progress.txt");
        if (progressFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(progressFile))) {
                String line = br.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    currentUnlockedLevel = Integer.parseInt(line.trim());
                } else {
                    currentUnlockedLevel = 1;
                }
            } catch (IOException | NumberFormatException e) {
                currentUnlockedLevel = 1;
            }
        } else {
            currentUnlockedLevel = 1;
        }

        boolean backgroundLoaded = false;
        try {
            Image backgroundImage = new Image(getClass().getResource("/images/main_menu_bg.png").toExternalForm());
            ImageView backgroundView = new ImageView(backgroundImage);
            backgroundView.setFitWidth(Config.WINDOW_WIDTH);
            backgroundView.setFitHeight(Config.WINDOW_HEIGHT);
            getChildren().add(backgroundView);
            backgroundLoaded = true;
        } catch (Exception e) {
            System.err.println("Không thể tải hình nền main_menu_bg.png. Dùng nền lưới Cyber Grid thay thế.");
        }
        if (!backgroundLoaded) {
            Pane backgroundGrid = createCyberGridBackground();
            getChildren().add(backgroundGrid);
        }
        VBox container = new VBox(40);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(60));
        container.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        container.setBackground(Background.EMPTY);

        Text titleText = new Text("BREAKOUT CYBER");
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font("Consolas", FontWeight.BOLD, 70));

        DropShadow mainGlow = new DropShadow(50, NEON_RED);
        mainGlow.setSpread(0.8);
        DropShadow secondaryGlow = new DropShadow(25, NEON_ORANGE);
        secondaryGlow.setOffsetX(4);
        secondaryGlow.setOffsetY(4);
        Blend blendEffect = new Blend();
        blendEffect.setBottomInput(mainGlow);
        blendEffect.setTopInput(secondaryGlow);
        titleText.setEffect(blendEffect);

        FadeTransition titleFade = new FadeTransition(Duration.seconds(2), titleText);
        titleFade.setFromValue(1.0);
        titleFade.setToValue(0.7);
        titleFade.setCycleCount(Animation.INDEFINITE);
        titleFade.setAutoReverse(true);
        titleFade.play();
        Consumer<VBox> startGameAction = this::openUsernameInput;
        Consumer<VBox> multiplayerAction = this::openMultiplayerMenu;
        Consumer<VBox> settingsAction = this::openSettingsMenu;
        Consumer<VBox> playerSkinAction = this::openSkinSelectionMenu;
        Consumer<VBox> guideAction = c -> showGuide();
        Button btnStart = createLargeNeonButton("START GAME");
        Button btnMultiplayer = createLargeNeonButton("MULTIPLAYER");
        Button btnSettings = createLargeNeonButton("SETTINGS");
        Button btnPLayer = createLargeNeonButton("PLAYER SKIN");
        Button btnGuide = createLargeNeonButton("HƯỚNG DẪN");
        btnStart.setOnAction(e -> startGameAction.accept(container));
        btnMultiplayer.setOnAction(e -> multiplayerAction.accept(container));
        btnSettings.setOnAction(e -> settingsAction.accept(container));
        btnPLayer.setOnAction(e -> playerSkinAction.accept(container));
        btnGuide.setOnAction(e -> guideAction.accept(container));

        mainButtons = new ArrayList<>();
        mainButtons.add(btnStart);
        mainButtons.add(btnMultiplayer);
        mainButtons.add(btnSettings);
        mainButtons.add(btnPLayer);
        mainButtons.add(btnGuide);
        centerButtonContainer = new StackPane();
        centerButtonContainer.setPrefSize(LARGE_BUTTON_WIDTH, LARGE_BUTTON_HEIGHT);
        Button navLeft = createSmoothNeonButton("<", SMALL_NAV_BUTTON_WIDTH);
        Button navRight = createSmoothNeonButton(">", SMALL_NAV_BUTTON_WIDTH);

        HBox mainNavigationBox = new HBox(60, navLeft, centerButtonContainer, navRight);
        mainNavigationBox.setAlignment(Pos.CENTER);

        navLeft.setOnAction(e -> navigateMainButtons(-1));
        navRight.setOnAction(e -> navigateMainButtons(1));

        updateMainButtonDisplay(0);

        Button btnTable = createSmoothNeonButton("BẢNG XẾP HẠNG", 250);
        Button btnExit = createSmoothNeonButton("EXIT GAME", 250);

        HBox utilityBox = new HBox(40, btnTable, btnExit);
        utilityBox.setAlignment(Pos.CENTER);

        btnTable.setOnAction(e -> openLeaderboard(container));
        btnExit.setOnAction(e -> Platform.exit());

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);

        container.getChildren().addAll(titleText, spacer1, mainNavigationBox, utilityBox, spacer2);
        getChildren().add(container);
    }

    private Pane createCyberGridBackground() {
        Pane grid = new Pane();
        grid.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        grid.setStyle("-fx-background-color: " + DEEP_SPACE_BLACK.toString().replace("0x", "#"));

        Color gridColor = NEON_RED.deriveColor(0, 1, 1, 0.1);

        grid.setBorder(new Border(new BorderStroke(
                gridColor,
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(1),
                new Insets(50)
        )));

        Color gridColorFine = NEON_ORANGE.deriveColor(0, 1, 1, 0.05);
        grid.setBorder(new Border(
                new BorderStroke(gridColor, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(1), new Insets(10)),
                grid.getBorder().getStrokes().get(0)
        ));

        return grid;
    }


    private void openUsernameInput(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox nameBox = new VBox(20);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY
        )));
        nameBox.setBorder(new Border(new BorderStroke(
                NEON_ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2)
        )));
        nameBox.setPadding(new Insets(40));
        nameBox.setMaxWidth(450);

        Label label = new Label("ENTER YOUR USERNAME:");
        label.setTextFill(NEON_ORANGE);
        label.setFont(Font.font("Consolas", 24));

        TextField nameField = new TextField();
        nameField.setPromptText("Your name...");
        nameField.setMaxWidth(300);
        nameField.setStyle("-fx-background-color: " + DARK_BG.toString().replace("0x", "#") + "; -fx-text-fill: white; -fx-border-color: " + NEON_RED.toString().replace("0x", "#") + "; -fx-font-size: 18px; -fx-padding: 8px;");

        HBox btnBox = new HBox(20);
        btnBox.setAlignment(Pos.CENTER);

        Button btnPlay = createSmoothNeonButton("PLAY", 150);
        Button btnBack = createSmoothNeonButton("BACK", 150);

        final StackPane centeredNamePane = new StackPane(overlay, nameBox);
        centeredNamePane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        btnPlay.setOnAction(ev -> {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                Config.PLAYER_NAME = username;
                getChildren().remove(centeredNamePane);
                container.setEffect(null);
                showLevelSelection();
            } else {
                nameField.setPromptText("⚠ PLEASE ENTER YOUR NAME!");
                nameField.setStyle("-fx-background-color: " + DARK_BG.toString().replace("0x", "#") + "; -fx-border-color: red; -fx-font-size: 18px; -fx-padding: 8px;");
            }
        });

        btnBack.setOnAction(ev -> {
            getChildren().remove(centeredNamePane);
            container.setEffect(null);
        });

        btnBox.getChildren().addAll(btnPlay, btnBack);
        nameBox.getChildren().addAll(label, nameField, btnBox);

        getChildren().add(centeredNamePane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), nameBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void openLeaderboard(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox tableBox = new VBox(15);
        tableBox.setAlignment(Pos.CENTER);
        tableBox.setPadding(new Insets(30));
        tableBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY
        )));
        tableBox.setBorder(new Border(new BorderStroke(
                NEON_ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2)
        )));
        tableBox.setMaxWidth(500);

        Label rankingTitle = new Label("🏆 TOP PLAYERS 🏆");
        rankingTitle.setTextFill(NEON_ORANGE);
        rankingTitle.setFont(Font.font("Consolas", 28));

        VBox list = new VBox(12);
        list.setAlignment(Pos.CENTER);
        try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/scores.txt"))) {
            String line;
            int rank = 1;
            while ((line = reader.readLine()) != null && rank <= 10) {
                Label row = new Label(rank + ". " + line);
                row.setTextFill(Color.web("#ffcc99"));
                row.setFont(Font.font("Consolas", 20));
                list.getChildren().add(row);
                rank++;
            }

            if (rank == 1) {
                Label empty = new Label("NO SCORES YET!");
                empty.setTextFill(Color.LIGHTGRAY);
                list.getChildren().add(empty);
            }
        } catch (IOException ex) {
            Label empty = new Label("NO SCORE FILE FOUND!");
            empty.setTextFill(Color.LIGHTGRAY);
            list.getChildren().add(empty);
        }

        Button btnBack = createSmoothNeonButton("BACK", 150);

        tableBox.getChildren().addAll(rankingTitle, list, btnBack);
        StackPane rankingPane = new StackPane(overlay, tableBox);
        rankingPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        rankingPane.setAlignment(Pos.CENTER);
        getChildren().add(rankingPane);

        btnBack.setOnAction(ev -> {
            getChildren().remove(rankingPane);
            container.setEffect(null);
        });
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), tableBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }


    private void updateMainButtonDisplay(int newIndex) {
        if (newIndex < 0 || newIndex >= mainButtons.size()) {
            return;
        }

        Button oldButton = centerButtonContainer.getChildren().isEmpty() ? null : (Button) centerButtonContainer.getChildren().get(0);
        Button newButton = mainButtons.get(newIndex);

        if (oldButton == newButton) {
            return;
        }

        int oldIndex = centerButtonContainer.getChildren().isEmpty() ? 0 : currentMainButtonIndex;
        currentMainButtonIndex = newIndex;

        if (oldButton != null) {
            int direction;
            if (newIndex == 0 && oldIndex == mainButtons.size() - 1) direction = 1;
            else if (newIndex == mainButtons.size() - 1 && oldIndex == 0) direction = -1;
            else direction = (newIndex > oldIndex) ? 1 : -1;

            TranslateTransition exitTranslate = new TranslateTransition(Duration.millis(350), oldButton);
            exitTranslate.setInterpolator(Interpolator.EASE_BOTH);
            exitTranslate.setFromX(0);
            exitTranslate.setToX(-direction * 300);
            FadeTransition exitFade = new FadeTransition(Duration.millis(350), oldButton);
            exitFade.setFromValue(1.0);
            exitFade.setToValue(0.0);

            ParallelTransition exitTransition = new ParallelTransition(exitTranslate, exitFade);
            exitTransition.setOnFinished(e -> {
                centerButtonContainer.getChildren().clear();
                addNewButtonWithAnimation(newButton, direction);
            });
            exitTransition.play();
        } else {
            addNewButtonWithAnimation(newButton, 0);
        }
    }

    private void addNewButtonWithAnimation(Button newButton, int direction) {
        newButton.setOpacity(0.0);

        if (direction != 0) {
            newButton.setTranslateX(-direction * 300);
        } else {
            newButton.setTranslateX(0);
        }

        centerButtonContainer.getChildren().add(newButton);

        TranslateTransition enterTranslate = new TranslateTransition(Duration.millis(350), newButton);
        enterTranslate.setInterpolator(Interpolator.EASE_BOTH);
        enterTranslate.setFromX(newButton.getTranslateX());
        enterTranslate.setToX(0);
        FadeTransition enterFade = new FadeTransition(Duration.millis(350), newButton);
        enterFade.setFromValue(0.0);
        enterFade.setToValue(1.0);

        ParallelTransition enterTransition = new ParallelTransition(enterTranslate, enterFade);
        enterTransition.play();
    }

    private void navigateMainButtons(int direction) {
        int total = mainButtons.size();
        int newIndex = (currentMainButtonIndex + direction) % total;
        if (newIndex < 0) {
            newIndex += total;
        }
        updateMainButtonDisplay(newIndex);
    }

    private Button createLargeNeonButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 36));
        button.setTextFill(NEON_ORANGE);
        button.setPrefWidth(LARGE_BUTTON_WIDTH);
        button.setPrefHeight(LARGE_BUTTON_HEIGHT);

        Color baseBg = DARK_ACCENT;
        Color neonBorder = NEON_RED;

        button.setBackground(new Background(new BackgroundFill(
                baseBg, new CornerRadii(15), Insets.EMPTY
        )));
        button.setBorder(new Border(new BorderStroke(
                neonBorder, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(4)
        )));

        DropShadow baseGlow = new DropShadow(20, NEON_RED);
        baseGlow.setSpread(0.4);
        button.setEffect(baseGlow);

        Duration duration = Duration.millis(200);
        ScaleTransition scaleIn = new ScaleTransition(duration, button);
        scaleIn.setToX(1.08);
        scaleIn.setToY(1.08);

        ScaleTransition scaleOut = new ScaleTransition(duration, button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        DropShadow hoverGlow = new DropShadow(60, Color.WHITE);
        hoverGlow.setSpread(0.9);

        button.setOnMouseEntered(e -> {
            button.setTextFill(Color.WHITE);
            scaleIn.play();
            button.setEffect(hoverGlow);
        });

        button.setOnMouseExited(e -> {
            button.setTextFill(NEON_ORANGE);
            scaleOut.play();
            button.setEffect(baseGlow);
        });

        return button;
    }

    private Button createSmoothNeonButton(String text, double width) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        button.setTextFill(NEON_ORANGE);
        button.setPrefWidth(width);
        button.setPrefHeight(50);

        Color baseBg = DARK_ACCENT;
        Color neonBorder = NEON_RED;

        button.setBackground(new Background(new BackgroundFill(
                baseBg, new CornerRadii(12), Insets.EMPTY
        )));
        button.setBorder(new Border(new BorderStroke(
                neonBorder, BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(3)
        )));

        DropShadow baseGlow = new DropShadow(15, NEON_RED);
        baseGlow.setSpread(0.3);
        button.setEffect(baseGlow);

        Duration duration = Duration.millis(200);
        ScaleTransition scaleIn = new ScaleTransition(duration, button);
        scaleIn.setToX(1.12);
        scaleIn.setToY(1.12);

        ScaleTransition scaleOut = new ScaleTransition(duration, button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        DropShadow hoverGlow = new DropShadow(50, Color.WHITE);
        hoverGlow.setSpread(0.8);

        button.setOnMouseEntered(e -> {
            button.setTextFill(Color.WHITE);
            scaleIn.play();
            button.setEffect(hoverGlow);
        });

        button.setOnMouseExited(e -> {
            button.setTextFill(NEON_ORANGE);
            scaleOut.play();
            button.setEffect(baseGlow);
        });

        return button;
    }

    private void showGuide() {
        Image image = new Image(getClass().getResource("/images/huongdan.png").toExternalForm());
        ImageView imageView = new ImageView(image);

        imageView.setFitWidth(Config.WINDOW_WIDTH);
        imageView.setFitHeight(Config.WINDOW_HEIGHT);

        AnchorPane overlay = new AnchorPane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.getChildren().add(imageView);

        Button backButton = createSmoothNeonButton("BACK", 120);
        backButton.setLayoutX(50);
        backButton.setLayoutY(50);
        overlay.getChildren().add(backButton);

        backButton.setOnAction(e -> getChildren().remove(overlay));

        getChildren().add(overlay);
    }

    private void openMultiplayerMenu(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox mpBox = new VBox(20);
        mpBox.setAlignment(Pos.CENTER);
        mpBox.setPadding(new Insets(40));
        mpBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY)));
        mpBox.setBorder(new Border(new BorderStroke(
                NEON_ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2))));
        mpBox.setMaxWidth(550);

        Label lbl = new Label("🎮 NETWORKING 🎮");
        lbl.setTextFill(NEON_ORANGE);
        lbl.setFont(Font.font("Consolas", 28));

        Button btnHost = createSmoothNeonButton("CREATE ROOM (HOST)", 300);
        Button btnJoin = createSmoothNeonButton("JOIN ROOM (CLIENT)", 300);
        Button btnBack = createSmoothNeonButton("BACK", 150);

        Label status = new Label();
        status.setTextFill(Color.web("#ffcc99"));
        status.setFont(Font.font("Consolas", 18));

        final StackPane centeredMPPane = new StackPane(overlay, mpBox);
        centeredMPPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        btnHost.setOnAction(ev -> startAsHost(status));
        btnJoin.setOnAction(ev -> openJoinDialog(status, centeredMPPane));
        btnBack.setOnAction(ev -> {
            getChildren().remove(centeredMPPane);
            container.setEffect(null);
        });

        mpBox.getChildren().addAll(lbl, btnHost, btnJoin, status, btnBack);

        getChildren().add(centeredMPPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), mpBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void startAsHost(Label status) {
        status.setText("🕓 Opening room on port 5000...");

        if (onStartServer != null) {
            onStartServer.accept(5000);
        }
    }

    private void openJoinDialog(Label status, StackPane centeredMPPane) {
        VBox ipBox = new VBox(15);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.setPadding(new Insets(30));
        ipBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY)));

        ipBox.setBorder(new Border(new BorderStroke(
                NEON_RED, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(3))));

        Label lblIp = new Label("ENTER HOST IP:");
        lblIp.setTextFill(NEON_ORANGE);
        lblIp.setFont(Font.font("Consolas", 20));

        TextField txtIp = new TextField();
        txtIp.setPromptText("E.g.: 192.168.1.10");
        txtIp.setMaxWidth(250);
        txtIp.setStyle("-fx-background-color: " + DARK_BG.toString().replace("0x", "#") + "; -fx-text-fill: white; -fx-border-color: " + NEON_ORANGE.toString().replace("0x", "#") + "; -fx-font-size: 16px; -fx-padding: 8px;");

        Button btnConnect = createSmoothNeonButton("CONNECT", 180);

        ipBox.getChildren().addAll(lblIp, txtIp, btnConnect);
        StackPane.setAlignment(ipBox, Pos.CENTER);
        centeredMPPane.getChildren().add(ipBox);

        btnConnect.setOnAction(ev -> {
            String ip = txtIp.getText().trim();
            if (!ip.isEmpty()) {

                if (onStartClient != null) {
                    onStartClient.accept(ip);

                    getChildren().remove(ipBox);
                }
            } else {
                txtIp.setPromptText("PLEASE ENTER IP!");
            }
        });

        Button btnBackIP = createSmoothNeonButton("CANCEL", 120);
        btnBackIP.setOnAction(e -> centeredMPPane.getChildren().remove(ipBox));
        VBox.setMargin(btnBackIP, new Insets(10, 0, 0, 0));
        ipBox.getChildren().add(btnBackIP);
    }

    private void showLevelSelection() {
        getChildren().clear();
        Pane levelPane = new Pane();
        levelPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        levelPane.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, null,
                        new Stop[]{
                                new Stop(0, Color.web("#1a0000")),
                                new Stop(1, Color.web("#400000"))
                        }),
                CornerRadii.EMPTY, Insets.EMPTY
        )));

        Text title = new Text("SELECT LEVEL");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 48));
        title.setFill(NEON_RED);
        title.setEffect(new DropShadow(40, NEON_RED));
        title.setLayoutY(120);
        title.setLayoutX(Config.WINDOW_WIDTH / 2 - title.getLayoutBounds().getWidth() / 2);

        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(50);
        grid.setAlignment(Pos.CENTER);
        grid.setLayoutY(Config.WINDOW_HEIGHT / 2 - 100);
        grid.setLayoutX(Config.WINDOW_WIDTH / 2 - (3 * 200 + 2 * 50) / 2);

        for (int i = 1; i <= 6; i++) {
            Button levelBtn = createLevelButton("LEVEL " + i, i);
            grid.add(levelBtn, (i - 1) % 3, (i - 1) / 3);
        }

        Button backBtn = createSmoothNeonButton("BACK", 150);
        backBtn.setLayoutX(50);
        backBtn.setLayoutY(50);

        backBtn.setOnAction(e -> {
            getChildren().remove(levelPane);
            GamePanel mainMenu = new GamePanel();
            Scene scene = getScene();
            if (scene != null) {
                scene.setRoot(mainMenu);
            } else {
                getChildren().add(mainMenu);
            }
        });

        levelPane.getChildren().addAll(title, grid, backBtn);
        getChildren().add(levelPane);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), levelPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private Button createLevelButton(String text, int level) {
        Button btn = new Button(text);
        btn.setPrefSize(200, 120);
        btn.setFont(Font.font("Consolas", FontWeight.BOLD, 22));

        boolean unlocked = level <= currentUnlockedLevel;

        if (unlocked) {
            btn.setTextFill(NEON_RED);
            btn.setBackground(new Background(new BackgroundFill(DARK_ACCENT, new CornerRadii(18), Insets.EMPTY)));
            btn.setBorder(new Border(new BorderStroke(NEON_RED, BorderStrokeStyle.SOLID, new CornerRadii(18), new BorderWidths(4))));
            btn.setEffect(new DropShadow(20, NEON_RED));

            Duration levelDuration = Duration.millis(250);
            ScaleTransition scaleIn = new ScaleTransition(levelDuration, btn);
            scaleIn.setToX(1.1);
            scaleIn.setToY(1.1);

            ScaleTransition scaleOut = new ScaleTransition(levelDuration, btn);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);

            DropShadow hoverGlow = new DropShadow(50, Color.WHITE);

            btn.setOnMouseEntered(e -> {
                btn.setTextFill(Color.WHITE);
                btn.setEffect(hoverGlow);
                scaleIn.play();
            });
            btn.setOnMouseExited(e -> {
                btn.setTextFill(NEON_RED);
                btn.setEffect(new DropShadow(20, NEON_RED));
                scaleOut.play();
            });

            btn.setOnAction(e -> startLevel(level));

        } else {
            btn.setTextFill(Color.web("#555555"));
            btn.setBackground(new Background(new BackgroundFill(Color.web("#110000"), new CornerRadii(18), Insets.EMPTY)));
            btn.setBorder(new Border(new BorderStroke(Color.web("#660000"), BorderStrokeStyle.DASHED, new CornerRadii(18), new BorderWidths(4))));
            btn.setEffect(new DropShadow(15, Color.web("#330000")));

            btn.setOnAction(e -> {
                Label lockedLabel = new Label("🔒 LEVEL " + level + " IS LOCKED!");
                lockedLabel.setTextFill(Color.web("#ffaaaa"));
                lockedLabel.setFont(Font.font("Consolas", 20));
                lockedLabel.setBackground(new Background(new BackgroundFill(DARK_ACCENT, new CornerRadii(12), Insets.EMPTY)));
                lockedLabel.setPadding(new Insets(15));
                StackPane.setAlignment(lockedLabel, Pos.TOP_CENTER);
                getChildren().add(lockedLabel);

                FadeTransition fadeMsg = new FadeTransition(Duration.seconds(2), lockedLabel);
                fadeMsg.setFromValue(1);
                fadeMsg.setToValue(0);
                fadeMsg.setOnFinished(ev -> getChildren().remove(lockedLabel));
                fadeMsg.play();
            });
        }

        return btn;
    }

    private void startLevel(int level) {
        System.out.println("Starting Level " + level + " for player: " + Config.PLAYER_NAME);

        getChildren().clear();

        Pane gameplayPane = new Pane();
        gameplayPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        gameplayPane.setStyle("-fx-background-color: black;");

        Button quickExit = createSmoothNeonButton("↩ MENU", 120);
        quickExit.setLayoutX(20);
        quickExit.setLayoutY(20);
        quickExit.setOnAction(e -> {
            getChildren().remove(gameplayPane);
            showLevelSelection();
        });
        gameplayPane.getChildren().add(quickExit);

        getChildren().add(gameplayPane);

        GameManager gameManager = new GameManager(gameplayPane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        gameManager.setOnExitToMenu(() -> Platform.runLater(() -> {
            getChildren().remove(gameplayPane);
            showLevelSelection();
        }));

        gameManager.setOnLevelComplete(completedLevel -> Platform.runLater(() -> {
            System.out.println("✅ Level " + completedLevel + " hoàn thành! Đang quay lại menu chọn level.");
            gameManager.stopGame();
            unlockNextLevel(completedLevel);
            getChildren().remove(gameplayPane);
            showLevelSelection();
        }));

        gameManager.setOnGameEndToMenu(() -> Platform.runLater(() -> {
            System.out.println("🎉 WINNER - Quay lại menu chính");
            getChildren().remove(gameplayPane);

            GamePanel newMenu = new GamePanel();
            Scene scene = getScene();
            if (scene != null) {
                scene.setRoot(newMenu);
            } else {
                getChildren().add(newMenu);
            }
        }));


        gameManager.setOnGameOver(() -> Platform.runLater(() -> {
            System.out.println("💀 Game Over — quay lại menu chính");
            getChildren().clear();

            GamePanel newMenu = new GamePanel();
            Scene scene = getScene();
            if (scene != null) {
                scene.setRoot(newMenu);
            } else {
                getChildren().add(newMenu);
            }
        }));

        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(Duration.millis(50));
        pt.setOnFinished(e -> {
            Scene scene = getScene();
            if (scene != null) {
                gameManager.setupInput(scene);
                this.setFocusTraversable(true);
                this.requestFocus();

                gameManager.startLevelNumber(level);
            } else {
                System.err.println("⚠️ Không thể bắt đầu game vì Scene vẫn là null.");
            }
        });
        pt.play();


        FadeTransition fade = new FadeTransition(Duration.seconds(0.6), gameplayPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }


    private void unlockNextLevel(int completedLevel) {
        if (completedLevel >= currentUnlockedLevel && currentUnlockedLevel < 6) {
            currentUnlockedLevel = completedLevel + 1;
            System.out.println("🎉 Mở khóa Level " + currentUnlockedLevel);

            try (java.io.PrintWriter writer = new java.io.PrintWriter("progress.txt")) {
                writer.println(currentUnlockedLevel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openSettingsMenu(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox settingsBox = new VBox(25);
        settingsBox.setAlignment(Pos.CENTER);
        settingsBox.setPadding(new Insets(50));
        settingsBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY
        )));
        settingsBox.setBorder(new Border(new BorderStroke(
                NEON_ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2)
        )));
        settingsBox.setMaxWidth(500);

        Label title = new Label("⚙️ SETTINGS ⚙️");
        title.setTextFill(NEON_ORANGE);
        title.setFont(Font.font("Consolas", 28));

        Label volumeLabel = new Label("MASTER VOLUME:");
        volumeLabel.setTextFill(Color.web("#ffcc99"));
        volumeLabel.setFont(Font.font("Consolas", 20));

        javafx.scene.control.Slider volumeSlider = new javafx.scene.control.Slider(0, 1.0, 0.5);

        volumeSlider.setValue(SoundManager.getVolume());

        volumeSlider.setPrefWidth(350);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.5);
        volumeSlider.setBlockIncrement(0.1);

        volumeSlider.setStyle(
                "-fx-control-inner-background: " + DARK_BG.toString().replace("0x", "#") + ";" +
                        "-fx-accent: " + NEON_BLUE.toString().replace("0x", "#") + ";" +
                        "-fx-font-size: 14px;" +
                        ".thumb {-fx-background-color: " + NEON_RED.toString().replace("0x", "#") + "; -fx-background-radius: 5;}" +
                        ".track {-fx-background-color: " + DARK_ACCENT.toString().replace("0x", "#") + "; -fx-background-radius: 5;}"
        );

        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double vol = newValue.doubleValue();
            SoundManager.setVolume(vol);
            System.out.println("Volume set to: " + vol);
        });

        final StackPane centeredPane = new StackPane(overlay, settingsBox);
        centeredPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        Button btnBack = createSmoothNeonButton("BACK", 150);
        btnBack.setOnAction(ev -> {
            getChildren().remove(centeredPane);
            container.setEffect(null);
        });

        settingsBox.getChildren().addAll(title, volumeLabel, volumeSlider, btnBack);

        getChildren().add(centeredPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), settingsBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void openSkinSelectionMenu(VBox container) {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        container.setEffect(blur);

        RectanglePane overlay = new RectanglePane();
        overlay.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        VBox skinBox = new VBox(30);
        skinBox.setAlignment(Pos.CENTER);
        skinBox.setPadding(new Insets(50));
        skinBox.setBackground(new Background(new BackgroundFill(
                DARK_ACCENT, new CornerRadii(15), Insets.EMPTY
        )));
        skinBox.setBorder(new Border(new BorderStroke(
                NEON_ORANGE, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(2)
        )));
        skinBox.setMaxWidth(500);

        Label title = new Label("🎮 SELECT PLAYER SKIN 🎨");
        title.setTextFill(NEON_ORANGE);
        title.setFont(Font.font("Consolas", 28));

        ImageView skinView = new ImageView();
        skinView.setFitWidth(120);
        skinView.setFitHeight(120);
        skinView.setPreserveRatio(true);
        skinView.setEffect(new DropShadow(25, Color.WHITE));

        StackPane skinContainer = new StackPane(skinView);
        skinContainer.setPrefSize(120, 120);

        Label skinNameLabel = new Label();
        skinNameLabel.setTextFill(Color.web("#ff9999"));
        skinNameLabel.setFont(Font.font("Consolas", 20));

        for (int i = 0; i < SKINS.length; i++) {
            if (SKINS[i].equals(Config.CURRENT_PLAYER_SKIN)) {
                currentSkinIndex = i;
                break;
            }
        }

        java.util.function.Consumer<Integer> loadSkinImage = (index) -> {
            String skinFileName = SKINS[index];
            Config.CURRENT_PLAYER_SKIN = skinFileName;

            var resource = getClass().getResourceAsStream("/skins/" + skinFileName);
            if (resource != null) {
                Image newSkinImage = new Image(resource);
                skinView.setImage(newSkinImage);
            } else {
                System.err.println("Không tìm thấy skin: /skins/" + skinFileName);
            }

            String name = skinFileName.substring(0, skinFileName.lastIndexOf('.')).toUpperCase();
            skinNameLabel.setText("SKIN: " + name);
        };

        java.util.function.Consumer<Integer> slideSkin = (direction) -> {
            skinBox.setDisable(true);

            Duration slideDuration = Duration.millis(400);
            double slideDistance = 250;

            javafx.animation.TranslateTransition tOut = new javafx.animation.TranslateTransition(slideDuration, skinView);
            tOut.setFromX(0);
            tOut.setToX(-direction * slideDistance);

            FadeTransition fOut = new FadeTransition(slideDuration, skinView);
            fOut.setFromValue(1.0);
            fOut.setToValue(0.0);

            tOut.setOnFinished(e -> {
                currentSkinIndex = (currentSkinIndex + direction + SKINS.length) % SKINS.length;
                loadSkinImage.accept(currentSkinIndex);

                skinView.setTranslateX(direction * slideDistance);
                skinView.setOpacity(0.0);

                javafx.animation.TranslateTransition tIn = new javafx.animation.TranslateTransition(slideDuration, skinView);
                tIn.setFromX(direction * slideDistance);
                tIn.setToX(0);

                FadeTransition fIn = new FadeTransition(slideDuration, skinView);
                fIn.setFromValue(0.0);
                fIn.setToValue(1.0);

                tIn.setOnFinished(ev -> skinBox.setDisable(false));

                javafx.animation.ParallelTransition ptIn = new javafx.animation.ParallelTransition(tIn, fIn);
                ptIn.play();
            });

            javafx.animation.ParallelTransition ptOut = new javafx.animation.ParallelTransition(tOut, fOut);
            ptOut.play();
        };

        Button btnNext = createSmoothNeonButton(">", 60);
        Button btnPrev = createSmoothNeonButton("<", 60);

        btnNext.setOnAction(e -> slideSkin.accept(1));
        btnPrev.setOnAction(e -> slideSkin.accept(-1));

        final StackPane centeredSkinPane = new StackPane(overlay, skinBox);
        centeredSkinPane.setPrefSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        Button btnSelect = createSmoothNeonButton("SELECT AND RETURN", 300);
        btnSelect.setOnAction(e -> {
            getChildren().remove(centeredSkinPane);
            container.setEffect(null);
        });

        HBox navBox = new HBox(20, btnPrev, skinContainer, btnNext);
        navBox.setAlignment(Pos.CENTER);

        skinBox.getChildren().addAll(title, skinNameLabel, navBox, btnSelect);

        getChildren().add(centeredSkinPane);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), skinBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        loadSkinImage.accept(currentSkinIndex);
    }

    private static class RectanglePane extends Pane {}
}