package app.phys.wind;

import app.utils.Vector3;

/**
 * Wraps another WindModel, scaling its output by exp(–height/scaleHeight).
 */
public class AltitudeScalingWindModel implements WindModel {

    private final WindModel innerModel;
    private final Vector3 center;
    private final double radius;
    private final double scaleHeight;

    /**
     * @param innerModel  the base wind model to scale
     * @param center      3D center of the body (km)
     * @param radius      radius of the body (km)
     * @param scaleHeight altitude distance (km)
     */
    public AltitudeScalingWindModel(WindModel innerModel, Vector3 center, double radius, double scaleHeight) {
        this.innerModel = innerModel;
        this.center = center;
        this.radius = radius;
        this.scaleHeight = scaleHeight;
    }

    @Override
    public Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity) {
        double height = position.distanceTo(center) - radius;
        if (height < 0) height = 0;
        double factor = Math.exp(-height / scaleHeight);
        return innerModel
                .getWindAcceleration(time, position, velocity)
                .mul(factor);
    }
}
