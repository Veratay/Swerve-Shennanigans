package aperture.simulator;

import aperture.simulator.math.RobotModel;
import org.joml.Vector3f;

import static aperture.simulator.Simulator.positions;
import static aperture.simulator.Simulator.robotModel;
public class RobotRenderer {

    public static void render() {
        Vector3f robotPos = new Vector3f((float) -positions.x, 0,(float) positions.y);

    }

    private float toInch(double cm) {
        return (float) ((float)cm*0.393701);
    }
}
