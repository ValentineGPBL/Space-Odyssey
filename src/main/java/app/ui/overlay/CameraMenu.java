package app.ui.overlay;

import app.phys.Entity;
import app.phys.PlanetarySystem;
import app.ui.SpaceCamera;
import app.ui.SpaceScene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.Scene;

public class CameraMenu implements DropDown{
    private PlanetarySystem planetarySystem;
    private SpaceCamera planetCamera;
    private Scene scene;

    private Menu menu = new Menu("Camera");
    
    public CameraMenu(SpaceScene spaceScene) {
        this.planetarySystem = spaceScene.getPlanetarySystem();
        this.planetCamera = spaceScene.getSpaceCamera();
        this.scene = spaceScene.getScene();

        menu.getItems().add(getFollowMenu());
        menu.getItems().add(getFreeCamMenu());
    }

    public Menu getMenu() {
        return menu;
    }

    private MenuItem getFreeCamMenu() {
        MenuItem freeCamItem = new MenuItem("Free Cam");
        freeCamItem.setOnAction(event -> {
            planetCamera.enableFreeLook(scene);
        });
        return freeCamItem;
    }

    private Menu getFollowMenu() {
        Menu followMenu = new Menu("Follow");

        for (Entity entity : planetarySystem.getEntities()) {
            MenuItem entityItem = new MenuItem(entity.getName());

            entityItem.setOnAction(event -> {
                planetCamera.track(entity);
            });
            followMenu.getItems().add(entityItem);
        }

        return followMenu;
    }
    
}
