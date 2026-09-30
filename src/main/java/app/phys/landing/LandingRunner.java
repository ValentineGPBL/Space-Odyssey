package app.phys.landing;

import java.util.List;

import app.phys.Planet;
import app.phys.PlanetarySystem;
import app.phys.Probe;
import app.utils.PhysUtils;
import app.utils.Vector3;

public class LandingRunner {
    public static void main(String[] args) {
        PlanetarySystem sim = new PlanetarySystem();
        sim.setStepSize(1);
        Planet titan = (Planet)sim.get("Titan");
        Probe probe = sim.getProbe();

        PlanetarySystem newSim = new PlanetarySystem(List.of(titan), probe);
        titan = (Planet)newSim.get("Titan");
        probe = newSim.getProbe();
        titan.setPosition(new Vector3());
        titan.setVelocity(new Vector3());
        probe.setPosition(new Vector3(0, 0, 3000));
        //probe.setVelocity(new Vector3(1, 0, 0).mul(PhysUtils.speedAtDistance(titan, 3000)));
        //probe.applyThrust2D(1.8);
        FeedbackController controller = new FeedbackController(probe, titan);

        newSim.updateState();

        
        while(true) {
            //probe.applyThrust2D(0.000001);
            controller.applyCorrection();
            
            //System.out.println(probe.getPosition());
            //System.out.println(probe.getAngle());
            newSim.updateState();
            newSim.step();
            try {
                Thread.sleep(100);
            } catch(Exception e) {}
        }
        
    }
}