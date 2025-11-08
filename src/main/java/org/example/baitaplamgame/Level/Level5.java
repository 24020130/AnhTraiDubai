package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Boss;
import org.example.baitaplamgame.Model.Brick;
import org.example.baitaplamgame.Model.DummyBrick; // ✅ thêm dòng này

public class Level5 extends Level {
    private Boss boss;

    public Level5(int levelNumber) {
        super(levelNumber);
    }

    @Override
    public void generateLevelFromFile(String filePath, Pane root) {
        // ✅ Tạo Boss xuất hiện giữa màn hình
        boss = new Boss(300, 100, root);

        // ✅ Thêm 1 dummy brick để tránh danh sách trống (tránh game tự "Next Level")
        Brick dummy = new DummyBrick(-100, -100);
        getBricks().add(dummy);
    }

    public Boss getBoss() {
        return boss;
    }
}
