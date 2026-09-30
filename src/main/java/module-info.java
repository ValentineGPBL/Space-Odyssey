module app {
    requires org.fxyz3d.importers;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    //requires jdk.incubator.vector;

    opens app.ui to javafx.controls, javafx.fxml, javafx.graphics;
    opens app.ui.Landing_ui to javafx.graphics;
    exports app.ui.Landing_ui;

}
