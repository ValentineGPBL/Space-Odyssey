package app.ui.overlay;

import app.ui.SpaceScene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class Overlay {
    BorderPane borderPane = new BorderPane();
    MenuBar menuBar = new MenuBar();

    public Overlay(SpaceScene spaceScene) {
        SimulationMenu simulationMenu = new SimulationMenu(spaceScene);
        menuBar.getMenus().add(simulationMenu.getMenu());

        CameraMenu cameraMenu = new CameraMenu(spaceScene);
        menuBar.getMenus().add(cameraMenu.getMenu());

        ViewMenu viewMenu = new ViewMenu(spaceScene);
        menuBar.getMenus().add(viewMenu.getMenu());

        MissionMenu missionMenu = new MissionMenu(spaceScene);
        menuBar.getMenus().add(missionMenu.getMenu());
        
        borderPane.setTop(menuBar);
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
