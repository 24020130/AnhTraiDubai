package org.example.baitaplamgame.PowerUp;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Ball;
import org.example.baitaplamgame.Model.Paddle;
import org.example.baitaplamgame.Model.PowerUp;
import org.example.baitaplamgame.Utlis.ImageLoader;

public class MultiBallPower extends PowerUp {

    public MultiBallPower(double x, double y) {
        super(x, y, "multiball");
        view.setImage(ImageLoader.ITEM_STAR); // hình ngôi sao
        view.setFitWidth(25);
        view.setFitHeight(25);
        view.setX(x);
        view.setY(y);
    }


    @Override
    public void applyEffect(Paddle paddle) {
        Ball mainBall = paddle.getBall();
        if (mainBall == null) return;

        Pane root = (Pane) mainBall.getView().getParent();
        if (root == null) return;

        // Giả sử paddle có tham chiếu đến GameManager
        if (paddle.getGameManager() != null) {
            paddle.getGameManager().spawnExtraBalls(mainBall, 3); // ✅ sinh thêm 3 bóng
        }
    }


    @Override
    public void removeEffect(Paddle paddle) {
        // Không cần xóa hiệu ứng — bóng tạo ra tồn tại vĩnh viễn
    }
}
