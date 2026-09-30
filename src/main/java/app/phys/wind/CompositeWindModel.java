package app.phys.wind;

import app.utils.Vector3;
import java.util.Arrays;
import java.util.List;


public class CompositeWindModel implements WindModel {

    private final List<WindModel> models;

    /** @param models one or more wind models to sum */
    public CompositeWindModel(WindModel... models) {
        this.models = Arrays.asList(models);
    }

    @Override
    public Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity) {
        Vector3 totalAcceleration = new Vector3(0, 0, 0);
        for (WindModel model : models) {
            totalAcceleration.add(model.getWindAcceleration(time, position, velocity));
        }
        return totalAcceleration;
    }
}
