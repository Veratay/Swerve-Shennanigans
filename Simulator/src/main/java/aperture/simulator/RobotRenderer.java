package aperture.simulator;

import aperture.simulator.math.RobotModel;
import aperture.simulator.math.SwerveDrivebaseModel;
import org.joml.*;

import java.lang.Math;

import static aperture.simulator.Simulator.positions;
import static aperture.simulator.Simulator.robotModel;
public class RobotRenderer {

    public static void render() {

        //robot center
        renderBoxRobotRelative(new Vector3f(0,0,0),new Quaternionf(),new Vector3f(1,1,1),new Vector3f(0,0,0));
        renderBoxRobotRelative(new Vector3f((float) RobotModel.length,0,0),new Quaternionf(),new Vector3f(1,1,1),new Vector3f(0,0,0));

        for(int i=0; i<4; i++) {
            Vector2d wheelPos = robotModel.swerveDrivebaseModel.positions[i];
            float wheelRot = (float) robotModel.swerveSolver.state[5*i+4];
            renderBoxRobotRelative(new Vector3f((float) wheelPos.x,  0,(float)wheelPos.y),new Quaternionf(new AxisAngle4f(wheelRot,0,1,0)),new Vector3f(3,3,1), new Vector3f(1,0,1));
//            renderBoxRobotRelative(new Vector3f((float) wheelPos.x,  0,(float)wheelPos.y),new Quaternionf(new AxisAngle4f(wheelRot+(float)(Math.PI/2),0,1,0)),new Vector3f(1,robotModel.swerveSolver.state[],1), new Vector3f(1,0,1));

        }
//        renderBoxRobotRelative(new Vector3f(10,0,0),new Quaternionf(new AxisAngle4f((float) Math.toRadians(0),0,1,0)),new Vector3f(1,1,1), new Vector3f(1,0,0));

    }

    private static void renderBoxRobotRelative(Vector3f pos, Quaternionf rot, Vector3f scale, Vector3f color) {
        Vector3f finalPos = new Vector3f((float) positions.x, 0,(float) positions.y);
        float h = (float) ((float) positions.h);
        pos.rotateY(-h);
        finalPos.add(pos);
        rot.rotateY(h);
        finalPos.x = -finalPos.x;
        finalPos.x = toInch(finalPos.x);
        finalPos.y = toInch(finalPos.y);
        finalPos.z = toInch(finalPos.z);
        Renderer.renderBox(finalPos,rot.getEulerAnglesXYZ(new Vector3f()),scale,color);
    }
    private static float toInch(double cm) {
        return (float) ((float)cm*0.393701);
    }
}
