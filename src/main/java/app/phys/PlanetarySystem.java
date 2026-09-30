package app.phys;

import java.util.ArrayList;
import java.util.List;

import app.phys.wind.AltitudeScalingWindModel;
import app.phys.wind.CompositeWindModel;
import app.phys.wind.GaussianWindModel;
import app.phys.wind.RandomWalkWindModel;
import app.phys.wind.WindModel;
import app.ui.SolarSystemInitializer;
import app.utils.Matrix;
import app.utils.Vector3;

public class PlanetarySystem {
    
    private static final List<Planet> bodyList = SolarSystemInitializer.parseBodyList(PlanetarySystem.class.getResourceAsStream("/IC.csv"));

    private List<Planet> planets;
    private Probe probe = new Probe(new Vector3(), new Vector3(), 2523, 1);
    private List<Entity> entities;

    private Simulation sim;
    private double stepSize = 60;
    private long steps;
    private double startTime = 0;

    // Initialize a system with IC.csv
    public PlanetarySystem() {
        List<Planet> planets = new ArrayList<>();
        for (Planet planet : bodyList) {
            planets.add(planet.clone());
        }
        
        this.planets = planets;
        this.entities = new ArrayList<>();
        this.entities.addAll(planets);

        Probe probe = new Probe(new Vector3(), new Vector3(), 2523, 1);
        this.probe = probe;
        this.entities.add(probe);
        
        this.sim = new Simulation(this.entities.toArray(new Entity[0]), this.stepSize);
    }

    private WindModel getWindModel(Planet planet) {
        WindModel windModel = new AltitudeScalingWindModel(
            new CompositeWindModel(
            new RandomWalkWindModel(0.0005, 0.99),
            new GaussianWindModel(0.001)
            ),
        planet.getPosition(), planet.getRadius(), 50.0
        );
        return windModel;
    }

    public PlanetarySystem(List<Planet> planets, Probe probe) {
        this.planets = planets;
        this.probe = probe;
        this.entities = new ArrayList<>();
        this.entities.addAll(planets);
        if(probe != null)
            this.entities.add(probe);
        this.sim = new Simulation(this.entities.toArray(new Entity[0]), this.stepSize);
    }

    private Matrix _step() {
        this.steps++;
        return this.sim.step();
    }

    public Matrix step() {
        Matrix state = _step();
        updateSystem(state);
        probe.setAngle(probe.getAngle()+probe.getAngularVelocity());
        return state;
    }

    public Matrix advance(double time) {
        Matrix state = sim.getState();
        for(int x = 0; x < time / stepSize; x++) {
            state = _step();
        }
        updateSystem(state);
        return state;
    }

    public Matrix advanceUntil(double time) {
        Matrix state = sim.getState();
        while(getTime() < time)
            state = _step();
        updateSystem(state);
        return state;
    }

    private void updateSystem(Matrix positions) {
        for(int i = 0; i < entities.size(); i++) {
            entities.get(i).setPosition(new Vector3(positions.getRow(i)));
            entities.get(i).setVelocity(new Vector3(positions.getRow(i + entities.size())));
        }
    }

    public void updateState() {
        Matrix state = sim.getState();
        for(int i = 0; i < entities.size(); i++) {
            state.setRow(i, entities.get(i).getPosition());
            state.setRow(i + entities.size(), entities.get(i).getVelocity());
        }
    }

    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.sim.setStepSize(stepSize);
        startTime = getTime();
        steps = 0;
        this.stepSize = stepSize;
    }

    public double getTime() {
        return startTime + stepSize * steps;
    }

    public void resetTime() {
        startTime = 0;
        steps = 0;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public Probe getProbe() {
        return probe;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public int indexOf(Entity entity) {
        return entities.indexOf(entity);
    }

    public Entity get(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    public Entity get(int index) {
        if (index < 0 || index >= entities.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return entities.get(index);
    }

    @Override
    public PlanetarySystem clone() {
        List<Planet> clonedPlanets = new ArrayList<>();
        for (Planet planet : planets) {
            clonedPlanets.add(planet.clone());
        }
        Probe clonedProbe = probe.clone();
        PlanetarySystem newPlanetarySystem = new PlanetarySystem(clonedPlanets, clonedProbe);
        newPlanetarySystem.setStepSize(stepSize);
        newPlanetarySystem.startTime = startTime;
        newPlanetarySystem.steps = steps;
        return newPlanetarySystem;
    }

}
