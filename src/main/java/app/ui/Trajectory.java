package app.ui;

import app.utils.Vector3;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Color;

public class Trajectory {

    private double scale = 1.0;
    private double radius = 0.05 / 1e3;
    private boolean skipNext = false;

    Group trajectory = new Group();

    public Group getTrajectory() {
        return trajectory;
    }

    public void createSegment(Vector3 pos1, Vector3 pos2) {
        if (skipNext) {
            skipNext = false;
            return;
        }
        Point3D point1 = new Point3D(pos1.getX()/1e6, pos1.getZ()/1e6, pos1.getY()/1e6);
        Point3D point2 = new Point3D(pos2.getX()/1e6, pos2.getZ()/1e6, pos2.getY()/1e6);

        Point3D difference = point2.subtract(point1);
        double height = difference.magnitude();
        Point3D midpoint = point1.midpoint(point2);

        Point3D axisOfRotation = new Point3D(0, 1, 0).crossProduct(difference);
        double angle = Math.acos(new Point3D(0, 1, 0).dotProduct(difference) / height);
        angle = Math.toDegrees(angle);

        double radius = this.radius * scale;
        Cylinder cylinder = new Cylinder(radius, height);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.RED);
        cylinder.setMaterial(material);
        cylinder.getTransforms().add(new Translate(midpoint.getX(), midpoint.getY(), midpoint.getZ()));

        if (!axisOfRotation.equals(Point3D.ZERO)) {
            cylinder.getTransforms().add(new Rotate(angle, axisOfRotation));
        }
        cylinder.getTransforms().addAll(new Translate(radius/2, radius/2, radius/2));
        trajectory.getChildren().add(cylinder);
    }

    public void setScale(double scale) {
        this.scale = scale;
        for (var child : trajectory.getChildren()) {
            if (child instanceof Cylinder) {
                Cylinder cylinder = (Cylinder) child;
                cylinder.setRadius(radius * scale);
                cylinder.getTransforms().removeLast();
                cylinder.getTransforms().add(new Translate(radius * scale / 2, radius * scale / 2, radius * scale / 2));
            }
        }
    }

    public void clear() {
        trajectory.getChildren().clear();
        skipNext = true;
    }

}
