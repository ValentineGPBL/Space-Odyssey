package app.utils;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

public class UIUtils {
    
    public static Point3D toPoint3D(Node node) {
        return new Point3D(
            node.getTranslateX(),
            node.getTranslateY(),
            node.getTranslateZ()
        );
    }

    public static Point3D toPoint3D(Vector3 vector) {
        return new Point3D(
            vector.getX() / 1e6,
            vector.getZ() / 1e6,
            vector.getY() / 1e6
        );
    }

    public static void lookAt(Node source, Node target) {
        Point3D sourcePoint = toPoint3D(source);
        Point3D targetPoint = toPoint3D(target);
        lookAt(source, targetPoint.subtract(sourcePoint));
    }

    public static void lookAt(Node source, Vector3 simPoint) {
        Point3D sourcePoint = toPoint3D(source);
        Point3D targetPoint = toPoint3D(simPoint);
        lookAt(source, targetPoint.subtract(sourcePoint));
    }
    
    public static void lookAt(Node source, Point3D forward) {
        forward = forward.normalize();
        double dot = forward.dotProduct(Rotate.X_AXIS);
        double angle = Math.toDegrees(Math.acos(dot));
        source.setRotationAxis(Rotate.X_AXIS.crossProduct(forward));
        source.setRotate(angle);
    }

    public static<T, U> Pair<T, U> loadFXML(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(UIUtils.class.getResource("/fxml/" + fxmlPath + ".fxml"));
        try {
            return new Pair<T,U>(
                loader.load(),
                loader.getController()
            );
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

}
