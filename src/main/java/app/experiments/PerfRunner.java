package app.experiments;

import app.phys.PlanetarySystem;

public class PerfRunner {
    
    public static void main(String[] args) {
        double time = 60 * 60 * 24 * 365;
        for(int x : new int[]{ 3600, 240, 60, 15, 1 }) {
            PlanetarySystem system = new PlanetarySystem();
            system.setStepSize(x);
            long start = System.currentTimeMillis();
            system.advance(time);
            long end = System.currentTimeMillis();
            System.out.println("Step size: " + x + "s, Time elapsed: " + (end - start) + "ms");
        }
        
    }

}
