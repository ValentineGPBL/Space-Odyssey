package app.ui.Landing_ui;

import app.utils.Vector3;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class LandingRenderer2D {

    private final GraphicsContext gc;
    private final Image titanBackground;
    private final Image probeImage;

    private final double canvasWidth;
    private final double canvasHeight;

    // GUI conversion parameters
    private final double topMargin = 50;
    private final double bottomMargin = 50;
    // Maximum simulation height (initial altitude)
    private final double simMax = 200.0;
    private double scale;
    private final double desiredProbeHeight = 100;

    // Increased lateralMultiplier to further exaggerate visual deviation.
    private final double lateralMultiplier = 15.0;
    // Additional vertical offset (in pixels) so the image lands 20 px lower.
    private final double landingYOffset = 20.0;

    public LandingRenderer2D(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        this.gc = gc;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.scale = (canvasHeight - topMargin - bottomMargin) / simMax;

        // load images and log if not found
        this.titanBackground = new Image(getClass().getResourceAsStream("/images/surface.jpg"));
        if (titanBackground.isError()) {
            System.err.println("Error loading /images/surface.jpg");
        }
        this.probeImage = new Image(getClass().getResourceAsStream("/images/rocket.png"));
        if (probeImage.isError()) {
            System.err.println("Error loading /images/rocket.png");
        }
    }

    public void render(Vector3 probePosition) {
        clearCanvas();
        drawBackground();
        drawCoordinateAxes();
        drawProbe(probePosition);
    }

    private void clearCanvas() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);
    }

    private void drawBackground() {
        gc.drawImage(titanBackground, 0, 0, canvasWidth, canvasHeight);
    }

    private void drawCoordinateAxes() {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        double xAxis = canvasWidth / 2;
        gc.strokeLine(xAxis, 0, xAxis, canvasHeight);
        double yAxis = topMargin + (simMax - 0) * scale;
        gc.strokeLine(0, yAxis, canvasWidth, yAxis);
    }

    public void drawProbe(Vector3 position) {
        // Exaggerate small lateral deviations by multiplying the x shift.
        double screenX = canvasWidth / 2 + position.getX() * scale * lateralMultiplier;
        // Add vertical offset so the image lands 20 px lower.
        double screenY = topMargin + (simMax - position.getY()) * scale + landingYOffset;
        double scaleFactor = desiredProbeHeight / probeImage.getHeight();
        double imageWidth = probeImage.getWidth() * scaleFactor;
        double imageHeight = probeImage.getHeight() * scaleFactor;
        gc.drawImage(probeImage, screenX - imageWidth / 2, screenY - imageHeight, imageWidth, imageHeight);
    }
}