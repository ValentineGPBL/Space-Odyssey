package app.ui;

import java.security.Key;
import java.util.List;
import java.util.function.Consumer;

import app.phys.Entity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SpaceOverlay {

    @FXML
    private StackPane rootPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vbox;

    public void initialize(Scene scene, List<Entity> entity, Consumer<Entity> callback) {
        this.rootPane.prefWidthProperty().bind(scene.widthProperty());
        this.rootPane.prefHeightProperty().bind(scene.heightProperty());
        this.scrollPane.addEventFilter(KeyEvent.KEY_PRESSED, ev -> ev.consume());
        for (Entity e : entity) {
            Button btn = new Button(e.getName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setFocusTraversable(false);
            btn.setOnAction(event -> {
                callback.accept(e);
            });
            vbox.getChildren().add(btn);
        }
    }

}
