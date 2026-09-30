package app.ui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class IntroScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        int width = 1280;
        int height = 800;

        ImageView background = new ImageView(new Image(getClass().getResource("/images/introBackground.png").toExternalForm()));
        background.setFitWidth(width);
        background.setFitHeight(height);
        background.setPreserveRatio(false);

        Text title = new Text("TITANIC SPACE ODYSSEY");
        title.setFont(Font.font("Arial", 80));
        title.setStyle("-fx-fill: white; -fx-font-weight: bold;");

        Text group = new Text("GROUP 5");
        group.setFont(Font.font("Arial", 24));
        group.setStyle("-fx-fill: white;");

        VBox titleBox = new VBox(10, title, group);
        titleBox.setStyle("-fx-alignment: top-center; -fx-padding: 150 0 0 0;");

        StackPane root = new StackPane(background, titleBox);
        Scene scene = new Scene(root, width, height);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Intro");
        primaryStage.show();

        // auto-transition after 2 sec
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        //PauseTransition pause = new PauseTransition(Duration.ZERO);
        pause.setOnFinished(e -> {
            try {
//                Parent inputRoot = FXMLLoader.load(getClass().getResource("/fxml/InputScreen.fxml"));
//                Scene inputScene = new Scene(inputRoot, width, height);
//                primaryStage.setScene(inputScene);
//                primaryStage.setTitle("Titanic Space Odyssey – Input");
                SpaceScene spaceScene = new SpaceScene();
                spaceScene.start(primaryStage); // Transition to SpaceScene
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
