package org.example.baitaplamgame.Network;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private OnMessageListener listener;

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void connect(String serverIp, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(serverIp, port);
                System.out.println("✅ Đã kết nối đến server!");
                if (listener != null)
                    listener.onMessage("Đã kết nối đến server!");

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server: " + line);
                    if (listener != null) listener.onMessage("Server: " + line);

                    if (line.equals("START_GAME")) {
                        // Khi server gửi tín hiệu START_GAME → mở game
                        Platform.runLater(() -> {
                            Pane pane = new Pane();
                            GameManager gm = new GameManager(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                            gm.startGame();

                            Stage stage = new Stage();
                            stage.setTitle("🎮 Client - Multiplayer Game");
                            Scene scene = new Scene(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                            stage.setScene(scene);

                            gm.setupInput(scene); // ✅ Cho phép điều khiển paddle
                            stage.show();
                        });
                    }
                }

            } catch (IOException e) {
                if (listener != null)
                    listener.onMessage("❌ Không thể kết nối đến server!");
                e.printStackTrace();
            }
        }).start();
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
