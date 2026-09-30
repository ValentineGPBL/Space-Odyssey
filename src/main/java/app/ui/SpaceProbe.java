package app.ui;

import java.io.IOException;
import java.net.URL;


import org.fxyz3d.importers.Model3D;
import org.fxyz3d.importers.obj.ObjImporter;
import app.utils.Vector3;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class SpaceProbe {

    private Node probe;
    private Scale scale = new Scale(1, 1, 1);

    public SpaceProbe(double radius) {
        try {
            probe = createProbe(radius);
        } catch (Exception e) {
            System.out.println("Error creating probe");
            System.out.println(e);
        }
    }

    public void updateProbePosition(Vector3 position) {
        Vector3 adjustedPosition = position.clone().div(1e6);//.sub(offset);
        probe.setTranslateX(adjustedPosition.getX()); // Backwards / Forwards
        probe.setTranslateY(adjustedPosition.getZ()); // Downards / Upwards
        probe.setTranslateZ(adjustedPosition.getY()); // Left / Right
    }

    private Node createProbe(double radius) throws IOException{
        ObjImporter.setScale((float) (radius / 1473.1812477111816 / 1e6 / 1e3));
        ObjImporter objImporter = new ObjImporter();
        URL modelUrl = getClass().getResource("/probe_model/Untitled.obj");
        Model3D model = objImporter.load(modelUrl);
        Node modelNode = model.getRoot();

        // depth: Forwards backwards
        // height: Upwards downwards
        // width: Left right
        Bounds bounds = model.getRoot().getLayoutBounds();
        
        modelNode.getTransforms().addAll(
            /*
            new Translate(
                -bounds.getMinX() + bounds.getWidth() / 2, // Forwards / Backwards
                -bounds.getMinY() + bounds.getHeight() / 2, // Upwards / Downwards
                -bounds.getMinZ() + bounds.getDepth() / 2 // Left / Right
            ),
            */
            new Rotate(180, Rotate.Z_AXIS), // Roll rotation
            new Rotate(-90, Rotate.Y_AXIS), // Yaw rotation
            scale
        );


        return modelNode;
    }

    public void setScale(double scaleFactor) {
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        scale.setZ(scaleFactor);
    }

    public Node getProbe() {
        return probe;
    }
}
