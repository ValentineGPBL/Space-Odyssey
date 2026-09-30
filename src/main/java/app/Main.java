package app;

import javafx.application.Application;

// Run using ./mvnw clean compile exec:java -Dexec.mainClass=app.Main -Dexec.args="arg1 arg2"
public class Main {
    public static void main(String[] args) {
        if(args.length == 0) {
            Application.launch(app.ui.Main.class, args);
        } else {
            app.experiments.Main.main(args);
        }
    }
}
