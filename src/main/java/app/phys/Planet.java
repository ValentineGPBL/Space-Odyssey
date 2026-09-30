package app.phys;

import app.utils.*;
import javafx.util.Pair;

public class Planet extends Entity {
    
    private Planet parentBody = null;
    private OrbitalParams orbitalParams = null;

    /**
     * Constructs a Planet with specified position, velocity, mass, and radius.
     * @param name The name of the planet
     * @param position An initial position for the planet
     * @param velocity An initial velocity for the planet
     * @param mass The mass of the planet in kilograms
     * @param radius The radius of the planet in kilometers
     */
    public Planet(String name, Vector3 position, Vector3 velocity, double mass, double radius) {
        super(name, position, velocity, mass, radius);
    }

    /**
     * Constructs a Planet with specified position, velocity, mass, radius, and parent body.
     * @param name The name of the planet
     * @param position An initial position for the planet
     * @param velocity An initial velocity for the planet
     * @param mass The mass of the planet in kilograms
     * @param radius The radius of the planet in kilometers
     * @param parentBody The parent body this planet orbits (null for Sun)
     */
    public Planet(String name, Vector3 position, Vector3 velocity, double mass, double radius, Planet parentBody) {
        this(name, position, velocity, mass, radius);
        this.parentBody = parentBody;
        if(parentBody != null) {
            this.orbitalParams = new OrbitalParams(parentBody, position, velocity);
        } else {
            this.orbitalParams = new OrbitalParams(0, 0, 0, 0, 0, 0, 0);
        }
    }

    /**
     * Constructs a Planet with specified position, velocity, mass, radius, parent body, and orbital parameters.
     * @param name The name of the planet
     * @param position An initial position for the planet
     * @param velocity An initial velocity for the planet
     * @param mass The mass of the planet in kilograms
     * @param radius The radius of the planet in kilometers
     * @param parentBody The parent body this planet orbits (null for Sun)
     * @param orbitalParams The orbital parameters of the planet
     */
    public Planet(String name, Vector3 position, Vector3 velocity, double mass, double radius, Planet parentBody, OrbitalParams orbitalParams) {
        this(name, position, velocity, mass, radius, parentBody);
        this.orbitalParams = orbitalParams;
    }

    /**
     * Calculates the sphere of influence (SOI) of this planet.
     * @return The sphere of influence radius in kilometers.
     */
    public double getSOI() {
        if(parentBody == null)
            return Double.POSITIVE_INFINITY; // No parent body, infinite SOI (e.g., Sun)
        return this.getDistance(parentBody) * Math.pow(getMass() / parentBody.getMass(), 2.0 / 5.0);
    }

    /**
     * Gets the parent body of this planet.
     * @return The parent body, or null if this planet has no parent (e.g., Sun).
     */
    public Planet getParentBody() {
        return parentBody;
    }

    /**
     * Gets the orbital parameters of this planet.
     * @return The orbital parameters of the planet.
     */
    public OrbitalParams getOrbitalParams() {
        return orbitalParams;
    }

    /**
     * Gets the state of the planet at a given time t.
     * @param t The time at which you would like to get the state
     * @return The state of the planet at time t
     */
    public Pair<Vector3, Vector3> getState(double t) {
        Pair<Vector3, Vector3> state = this.orbitalParams.getState(t);
        if(getParentBody() != null) {
            Pair<Vector3, Vector3> parentState = this.getParentBody().getState(t);
            state.getKey().add(parentState.getKey());
            state.getValue().add(parentState.getValue());
        }
        return state;
    }

    /**
     * Sets the orbital parameters of this planet.
     * @param orbitalParams The new orbital parameters to set.
     */
    public void setOrbitalParams(OrbitalParams orbitalParams) {
        this.orbitalParams = orbitalParams;
    }

    @Override
    public Planet clone() {
        Vector3 posClone = getPosition().clone();
        return new Planet(getName(), posClone, getVelocity().clone(), getMass(), getRadius(), parentBody != null ? parentBody.clone() : null);
    }

}

