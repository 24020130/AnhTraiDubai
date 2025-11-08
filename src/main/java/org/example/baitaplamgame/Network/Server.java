package org.example.baitaplamgame.Network;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.baitaplamgame.Model.GameManager;
import org.example.baitaplamgame.Utlis.Config;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private OnMessageListener listener;

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    public void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("🚀 Server đang chờ client kết nối...");
                if (listener != null)
                    listener.onMessage("Server đang chờ client...");

                clientSocket = serverSocket.accept();
                System.out.println("✅ Client đã kết nối!");
                if (listener != null)
                    listener.onMessage("Client đã kết nối!");

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Khi client đã kết nối -> mở game
                Platform.runLater(() -> {
                    Pane pane = new Pane();
                    GameManager gm = new GameManager(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                    gm.startGame();

                    Stage stage = new Stage();
                    stage.setTitle("🏠 Host - Multiplayer Game");
                    Scene scene = new Scene(pane, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
                    stage.setScene(scene);

                    gm.setupInput(scene); // ✅ Cho phép di chuyển paddle
                    stage.show();

                    send("START_GAME"); // Báo cho client bắt đầu
                });

                // Lắng nghe tin nhắn từ client
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Client: " + line);
                    if (listener != null) listener.onMessage("Client: " + line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }

    public void stopServer() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
