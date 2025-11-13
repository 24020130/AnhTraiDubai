package org.example.baitaplamgame.Level;

import javafx.scene.layout.Pane;
import org.example.baitaplamgame.Model.Boss;
import org.example.baitaplamgame.Model.Brick;
import org.example.baitaplamgame.Model.DummyBrick; // ✅ thêm dòng này

public class Level6 extends Level {
    private Boss boss;

    public Level6(int levelNumber) {
        super(levelNumber);
    }

    @Override
    public void generateLevelFromFile(String filePath, Pane root) {
        boss = new Boss(300, 100, root);
        Brick dummy = new DummyBrick(-100, -100);
        getBricks().add(dummy);
    }

    public Boss getBoss() {
        return boss;
    }
}