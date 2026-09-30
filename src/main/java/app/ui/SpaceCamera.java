package app.ui;

import app.phys.Entity;
import app.phys.Planet;
import app.phys.PlanetarySystem;
import app.utils.Vector3;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

enum CameraMode {
    FREELOOK,
    ORBIT
}

public class SpaceCamera extends PerspectiveCamera {

    private static final double DRAG_SENSITIVITY = 0.1;
    private static final double TRANSLATION_FACTOR = 1;
    private static final double ZOOM_FACTOR = 0.001;

    private PlanetarySystem system;
    private CameraMode mode = CameraMode.FREELOOK;
    private Entity target = null;
    private double zoom = 1.0;

    private Rotate pitch = new Rotate(0., Rotate.X_AXIS); // Pitch, +Up, -Down
    private Rotate yaw = new Rotate(0., Rotate.Y_AXIS); // Yaw, +Left, -Right

    private double lastMouseX;
    private double lastMouseY;

    public String getMode() {
        return mode.toString();
    }

    public SpaceCamera(PlanetarySystem system) {
        super(true);
        setNearClip(0.0001);
        setFarClip(1e9); // Ensure the far clip is large enough to see distant objects
        getTransforms().addAll(yaw, pitch);
        this.system = system;
    }

    public void enableFreeLook(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnScroll(this::handleScroll);
        scene.setOnMouseMoved(this::handleMouseMove);
        scene.setOnMouseDragged(this::handleMouseMove);
    }

    private void handleKeyPress(KeyEvent event) {
        CameraMode prevMode = mode;
        mode = CameraMode.FREELOOK;
        switch (event.getCode()) {
            case UP -> translate(getForward().multiply(TRANSLATION_FACTOR)); // Move forward
            case DOWN -> translate(getForward().multiply(-TRANSLATION_FACTOR)); // Move backward
            case LEFT -> translate(getRight().multiply(-TRANSLATION_FACTOR)); // Move left
            case RIGHT -> translate(getRight().multiply(TRANSLATION_FACTOR)); // Move right
            default -> mode = prevMode;
        }

        if(event.getCode() == KeyCode.E) {
            system.setStepSize(system.getStepSize() * 2);
        } else if(event.getCode() == KeyCode.A) {
            system.setStepSize(system.getStepSize() * 0.5);
        }
    }

    private void handleScroll(ScrollEvent event) {
        double zoomFactor = event.getDeltaY() * TRANSLATION_FACTOR * ZOOM_FACTOR;
        if(mode == CameraMode.FREELOOK) {
            translate(getForward().multiply(zoomFactor));
        } else if(mode == CameraMode.ORBIT && target != null) {
            zoom += zoomFactor;
        }
    }

    private Point3D getForward() {
        Transform transform = getLocalToSceneTransform();
        return transform.deltaTransform(new Point3D(0, 0, 1)).normalize();
    }

    private Point3D getRight() {
        Transform transform = getLocalToSceneTransform();
        return transform.deltaTransform(new Point3D(1, 0, 0)).normalize();
    }

    private Point3D getUp() {
        Transform transform = getLocalToSceneTransform();
        return transform.deltaTransform(new Point3D(0, 1, 0)).normalize();
    }

    private void translate(Point3D translation) {
        setTranslateX(getTranslateX() + translation.getX());
        setTranslateY(getTranslateY() + translation.getY());
        setTranslateZ(getTranslateZ() + translation.getZ());
    }

    private void handleMouseMove(MouseEvent event) {
        if(event.isSecondaryButtonDown()) {
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;
            yaw.setAngle((yaw.getAngle() + deltaX * DRAG_SENSITIVITY) % 360);
            pitch.setAngle(Math.clamp(pitch.getAngle() + -deltaY * DRAG_SENSITIVITY, -90, 90));
        } else if(event.isMiddleButtonDown()) {
            mode = CameraMode.FREELOOK;
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;
            translate(getRight().multiply(-deltaX * DRAG_SENSITIVITY * TRANSLATION_FACTOR));
            translate(getUp().multiply(-deltaY * DRAG_SENSITIVITY * TRANSLATION_FACTOR));
        }
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
    }

    public void track(Entity target) {
        this.target = target;
        mode = CameraMode.ORBIT;
        //zoom = Math.log10(target.getRadius() / 1e6) + 1;
    }

    public void handleUpdate() {
        if(mode == CameraMode.ORBIT && target != null) {
            Vector3 targetPos = target.getPosition();
            setTranslateX(targetPos.getX() / 1e6);
            setTranslateY(targetPos.getZ() / 1e6);
            setTranslateZ(targetPos.getY() / 1e6);
            translate(getForward().multiply(-Math.pow(10, zoom)));
        }
    }

}