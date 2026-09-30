package app.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        new IntroScreen().start(primaryStage);
//        new SpaceScene().start(primaryStage);

        IntroScreen introScreen = new IntroScreen();
        introScreen.start(primaryStage); // start with the intro screen
    }

    public static void main(String[] args) {
        launch(args);
    }
}