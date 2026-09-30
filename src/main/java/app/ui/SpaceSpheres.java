package app.ui;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.paint.Color;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

import app.phys.Entity;

public class SpaceSpheres {

    private static final String TEXTURE_PATH = "/textures/";
    private ArrayList<Sphere> spheres = new ArrayList<Sphere>();

    public ArrayList<Sphere> getSpheres() {
        return spheres;
    }

    /**
     * Creates celestial bodies as spheres and adds them to a group.
     *
     * @return Group containing all celestial bodies.
     */
    public Group createCelestialBodies(List<Entity> celestialBodies) {
        Group group = new Group();

        for(Entity body : celestialBodies) {
            Sphere sphere = new Sphere(body.getRadius() / 1e6);

            // Adjust spacing for better visual clarity
            sphere.setTranslateX(body.getPosition().getX() / 1e6);
            sphere.setTranslateY(body.getPosition().getY() / 1e6);
            sphere.setTranslateZ(body.getPosition().getZ() / 1e6);

            PhongMaterial material = new PhongMaterial();
            String textureFile = body.getName().toLowerCase() + ".jpg";
            if (textureFile != null) {
                try {
                    Image texture = new Image(getClass().getResource(TEXTURE_PATH + textureFile).toExternalForm());
                    material.setDiffuseMap(texture);
                } catch (NullPointerException e) {
                    System.err.println("Texture not found for: " + body.getName());
                }
            }

            material.setSpecularColor(Color.BLACK); // Reduce specular highlights

            sphere.setMaterial(material);
            group.getChildren().add(sphere);
            spheres.add(sphere);
        }

        return group;
    }

}