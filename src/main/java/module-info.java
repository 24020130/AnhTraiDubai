module org.example.baitaplamgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    opens org.example.baitaplamgame to javafx.fxml;
    exports org.example.baitaplamgame;
}
