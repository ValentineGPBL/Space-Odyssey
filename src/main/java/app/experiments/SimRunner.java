package app.experiments;

import app.phys.PlanetarySystem;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;

public class SimRunner {
    
    public static void main(String[] args) {
        PlanetarySystem system = new PlanetarySystem();
        system.setStepSize(1);
        Vector launch = new Vector(new double[] {2.5744068308951356,-2.8869355073145933});
        Vector3 angle = PhysUtils.fromAzimuthElevation(launch);
        system.get(system.indexOf(system.getProbe())).setPosition(system.get(system.indexOf(system.get("Earth"))).getPosition().clone().add(angle.clone().mul(system.get(system.indexOf(system.get("Earth"))).getRadius())));
        system.get(system.indexOf(system.getProbe())).setVelocity(angle.clone().mul(60));
        system.updateState();
        System.out.println(system.getEntities().size());
        int i0 = 0;
        while(system.get("Probe").getDistance(system.get("Titan")) > 3e4) {
            system.step();
            if(i0++ % (60 * 60 * 24) == 0) {
                System.out.println(system.get("Probe").getDistance(system.get("Titan")));
            }
        }

        while(true) {
            for(int i = 0; i < system.getEntities().size(); i++) {
                String name = system.get(i).getName();
                if(name.equals("Titan") || name.equals("Probe")) {
                    Vector3 pos = system.get(i).getPosition();
                    System.out.println(pos.getX() + "," + pos.getY() + "," + pos.getZ());
                }
            }
            system.step();
            System.out.println(system.get("Probe").getDistance(system.get("Titan")));
            /*try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

}
