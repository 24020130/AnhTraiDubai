package org.example.baitaplamgame.Network;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private OnMessageListener listener;
    private GameManager gm;
    private Stage gameStage;
    private Runnable onGameEndToMenu;
    private Runnable onGameStart;
    private boolean gameEndSent = false;

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void setOnGameEndToMenu(Runnable callback) {
        this.onGameEndToMenu = callback;
    }

    public void setOnGameStart(Runnable callback) {
        this.onGameStart = callback;
    }

    public void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new java.net.InetSocketAddress(port));

                if (listener != null) listener.onMessage("Server đang chờ client...");

                clientSocket = serverSocket.accept();
                if (listener != null) listener.onMessage("Client đã kết nối!");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                Platform.runLater(() -> {
                    Pane pane = new Pane();
                    gm = new GameManager(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                    gameEndSent = false;

                    if (onGameEndToMenu != null)
                        gm.setOnGameEndToMenu(() -> Platform.runLater(() -> {
                            if (gameStage != null && gameStage.isShowing()) gameStage.close();
                            onGameEndToMenu.run();
                        }));

                    gm.setOnPaddleMove(y -> send("OPPONENT_POS:" + y));

                    gm.setOnGameEnd((isServerWin) -> {
                        if (gameEndSent) return;
                        gameEndSent = true;

                        if (isServerWin) {
                            send("GAME_OVER:LOSE");
                            showResultAndReturnMenu("WIN");
                        } else {
                            send("GAME_OVER:WIN");
                            showResultAndReturnMenu("LOSE");
                        }
                    });

                    gameStage = new Stage();
                    gameStage.setTitle("🏠 Host - Multiplayer Game");
                    Scene scene = new Scene(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                    gameStage.setScene(scene);
                    gm.setupInput(scene);
                    scene.getRoot().requestFocus();

                    gameStage.setOnCloseRequest(event -> stopServer());
                    gameStage.show();

                    if (onGameStart != null) onGameStart.run();

                    gm.startLevelNumber(6);
                    send("START_GAME");
                });

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Client: " + line);
                    if (listener != null) listener.onMessage("Client: " + line);

                    if (line.startsWith("MOVE:")) {
                        double y = Double.parseDouble(line.split(":")[1]);
                        Platform.runLater(() -> {
                            if (gm != null && gm.getPaddle() != null)
                                gm.getPaddle().setY(y);
                        });
                    } else if (line.startsWith("GAME_OVER:")) {
                        String result = line.split(":")[1];
                        Platform.runLater(() -> handleGameEnd(result));
                    }
                }

            } catch (IOException e) {
                if (listener != null) listener.onMessage("Kết nối bị gián đoạn: " + e.getMessage());
                e.printStackTrace();
            } finally {
                stopServer();
            }
        }).start();
    }

    private void handleGameEnd(String result) {
        if (!gameEndSent) {
            gameEndSent = true;
            showResultAndReturnMenu(result);
        }
    }

    private void showResultAndReturnMenu(String result) {
        if (gm != null) {
            gm.showGameResult(result);
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(ev -> {
            if (gameStage != null && gameStage.isShowing()) {
                gameStage.close();
            }
            if (onGameEndToMenu != null) onGameEndToMenu.run();
        });
        pause.play();
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    public void stopServer() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();

            Platform.runLater(() -> {
                if (gameStage != null && gameStage.isShowing()) gameStage.close();
            });

            if (listener != null) listener.onMessage("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
