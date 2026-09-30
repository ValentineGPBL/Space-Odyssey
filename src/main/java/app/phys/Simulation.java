package app.phys;

import app.solver.RK4;
import app.solver.Solver;
import app.utils.Matrix;
import app.utils.Vector;

public class Simulation {

    public static final double G = 6.6743e-11 * 1e-9;

    private int entityCount;
    private double[] masses;

    private final Solver<Matrix> solver;

    public Simulation(Entity[] entities) {
        this(entities, 60); // 1h time step
    }

    public Simulation(Entity[] entities, double timeStep) {
        this.entityCount = entities.length;
        // NOTE: Merging by column would make more sense, but it would then require us to create subarrays when separating velocities and positions.
        this.solver = new RK4<Matrix>(this::fn, timeStep, 0, getPositionsMatrix(entities).mergeRows(getVelocityMatrix(entities)));

        this.masses = new double[entities.length];
        for (int i = 0; i < entities.length; i++)
            masses[i] = entities[i].getMass() * G;
    }

    private Matrix getPositionsMatrix(Entity[] entities) {
        Matrix positions = new Matrix(entities.length, 3);
        for (int i = 0; i < entities.length; i++)
            positions.setRow(i, entities[i].getPosition());
        
        return positions;
    }

    private Matrix getVelocityMatrix(Entity[] entities) {
        Matrix velocities = new Matrix(entities.length, 3);
        for (int i = 0; i < entities.length; i++)
            velocities.setRow(i, entities[i].getVelocity());
        
        return velocities;
    }

    // Calculate the pull of the sun on the entity a
    // NOTE: We ignore the pull of the planets on the sun
    //       since it is negligible, but would also move
    //       the sun (which is used as the origin).
    private void attract(Matrix state, Matrix newState, int a) {
        Vector pos = state.getRow(a);
        double dis2 = pos.lengthSquared();
        if(dis2 == 0) return;
        double dis3 = dis2 * Math.sqrt(dis2);
        
        // Could be optimized as getRow allocates a new vector, but we only need to increment the row
        newState.setRow(a + entityCount, pos.mul(-masses[0] / dis3));
    }

    // Calculate the pull of the entity a on the entity b (and vice versa)
    private void attract(Matrix state, Matrix newState, int a, int b) {
        Vector relPos = state.getRow(a).sub(state.getRow(b));
        double dis2 = relPos.lengthSquared();
        if(dis2 == 0) return; // Avoid division by zero
        double dis3 = dis2 * Math.sqrt(dis2);
        
        // Need to add entityCount to the index as we want to modify acceleration
        // while the first half of newState are the velocities
        newState.setRow(a + entityCount, newState.getRow(a + entityCount).add(relPos.clone().mul(-masses[b] / dis3)));
        newState.setRow(b + entityCount, newState.getRow(b + entityCount).add(relPos.mul(masses[a] / dis3)));
    }

    // NOTE: State is row-merged positions and velocities
    private Matrix fn(double t, Matrix state) {
        // First half is positions, second half is velocities
        double[] stateArr = state.getComponents();

        // First half is velocities, second half is accelerations
        Matrix newState = new Matrix(entityCount * 2, 3);
        double[] newStateArr = newState.getComponents();

        /*for(int i = 1; i < entityCount; i++)
            attract(state, newState, i);
        
        attract(state, newState, 3, 4); // Earth and Moon
        attract(state, newState, 7, 8); // Saturn and Titan

        for(int i = 1; i < entityCount - 1; i++)
            attract(state, newState, i, entityCount - 1);*/

        for(int i = 0; i < entityCount; i++)
            for(int j = i + 1; j < entityCount; j++)
                attract(state, newState, i, j);

        // Increment the change in position by the velocity
        for(int i = 0; i < entityCount * 3; i++) {
            newStateArr[i]  = stateArr[i + entityCount * 3] + newStateArr[i + entityCount * 3];
        }

        return newState;
    }

    public Matrix step() {
        return solver.step();
    }

    public void setStepSize(double timeStep) {
        solver.setStepSize(timeStep);
    }

    public Matrix getState() {
        return solver.getY();
    }

}