module org.example.baitaplamgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    opens org.example.baitaplamgame to javafx.fxml;
    exports org.example.baitaplamgame;

}
