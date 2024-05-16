package aperture.simulator.math;

import org.joml.Matrix2d;
import org.joml.Vector2d;
import org.opencv.photo.CalibrateCRF;

import java.util.Arrays;

public class SwerveDrivebaseModel implements RungeKutta4.TimeInvariantDiffEqSystem<SwerveDrivebaseModel.Input> {
    public static class Input {
        double[] WheelTurnInputs;
        double[] WheelPowerInputs;

        public Input(double[] powers, double[] turns) {
            WheelPowerInputs= turns;
            WheelPowerInputs = powers;
        }
    }

    public final double width;
    public final double length;
    public final double wheelRadius;
    public final MagicTireFormula tractionModel;
    public final double wheelNormalForce;
    public final double rotationalInteria;
    public final double lateralTrail;

    final DcMotorDynamics driveMotorModel;
    final DcMotorDynamics turnMotorModel;

    public SwerveDrivebaseModel(DcMotorDynamics driveMotorModel, DcMotorDynamics turnMotorModel, double width, double length, double wheelRadius, double mass, double rotationalInteria, MagicTireFormula tractionModel, double alignWeight) {
        this.driveMotorModel = driveMotorModel;
        this.turnMotorModel = turnMotorModel;
        this.width = width;
        this.length = length;
        this.wheelRadius = wheelRadius;
        this.wheelNormalForce = mass/2.0;
        this.tractionModel = tractionModel;
        this.rotationalInteria = rotationalInteria;
        this.lateralTrail = alignWeight;
    }

    int moduleStateSize = 5;

    double[] lastCalculatedSlips = new double[2];
    double[] lastCalculatedForces = new double[2];
    @Override
    public double[] calc(double[] state, Input input) {

        int x = moduleStateSize*2;
        int y = moduleStateSize*2+1;
        int h = moduleStateSize*2+2;
        double[] res = new double[moduleStateSize*4 + 3];

        Vector2d[] positions = new Vector2d[] {
                new Vector2d(width/2.0,length/2.0),
                new Vector2d(-width/2.0,length/2.0),
                new Vector2d(-width/2.0,-length/2.0),
                new Vector2d(width/2.0,-length/2.0),
        };

        for(int i=0; i<4; i++) {

            int driveMotorCurrent = i*moduleStateSize;
            int driveMotorVelocity = i*moduleStateSize+1;

            int turnMotorCurrent = i*moduleStateSize+2;
            int turnMotorVelocity = i*moduleStateSize+3;
            int direction = i*moduleStateSize+4;

            double turnSpeed = state[turnMotorVelocity];
            double wheelSpeed = state[driveMotorVelocity];

            Vector2d wheelAxis = new Vector2d(Math.cos(state[direction]),Math.sin(state[direction]));
            Vector2d robotVel = new Vector2d(state[x],state[y]);
            Vector2d slip = calcSlip(
                    wheelSpeed,
                    wheelAxis,
                    positions[i],
                    robotVel,
                    state[h]
            );

            Vector2d tractionForce = new Vector2d(tractionModel.calc(slip.x),tractionModel.calc(slip.y));

            robotVel.normalize();
            double aligningMoment = tractionForce.y*lateralTrail*(new Vector2d(state[y],-state[x]).dot(wheelAxis));
            double[] turnMotorState = turnMotorModel.calc(new double[] { state[turnMotorCurrent], state[turnMotorVelocity] },new Double[]{ input.WheelTurnInputs[i], aligningMoment});
            double[] driveMotorState = driveMotorModel.calc(new double[] { state[driveMotorCurrent], state[driveMotorVelocity] },new Double[]{ input.WheelPowerInputs[i], -tractionForce.x*wheelRadius/2.0});

            Vector2d wheelForce = tractionForce.mul(wheelNormalForce);
            Vector2d robotForce = wheelForce.mul(new Matrix2d().rotate(state[direction]));

            Vector2d momentTangent = new Vector2d(-positions[i].y,positions[i].x).normalize();
            double moment = robotForce.dot(momentTangent)*width/2.0;

            res[turnMotorCurrent] = turnMotorState[0];
            res[turnMotorVelocity] = turnMotorState[1];
            res[driveMotorCurrent] = driveMotorState[0];
            res[driveMotorVelocity] = driveMotorState[1];
            res[direction] = turnSpeed;

            res[x] += robotForce.x/wheelNormalForce*2.0;
            res[y] += robotForce.y/wheelNormalForce*2.0;
            res[h] += moment/rotationalInteria;

            lastCalculatedForces[i] = wheelForce.y;
            lastCalculatedSlips[i] = slip.x;
        }

        return res;
    }

    public double[] getStartingConfig() {
        double[] res = new double[moduleStateSize*2 + 3];

        res[4] = Math.toRadians(0);
        res[5+4] = Math.toRadians(0);

        return res;
    }

    Vector2d calcSlip(double wheelSpeed, Vector2d wheelAxis, Vector2d wheelPosition, Vector2d robotVelocity, double robotTurnSpeed) {
        wheelAxis.normalize();
        //calculates force applied to wheel, so ground velocities are negated.
        Vector2d wheel = new Vector2d(wheelAxis).mul(wheelSpeed*wheelRadius);

        double levelLength = wheelPosition.length();
        double wheelAngle = Math.atan2(wheelPosition.y,wheelPosition.x);

        Vector2d momentForce = new Vector2d(Math.cos(wheelAngle+Math.toRadians(90)),Math.sin(wheelAngle+Math.toRadians(90))).mul(-levelLength*robotTurnSpeed);

        Vector2d ground = new Vector2d(robotVelocity).negate();

        Vector2d wheelAxisPerpendicular = new Vector2d(wheelAxis).perpendicular().negate();

        double longitudinalSlip = wheel.dot(wheelAxis) + momentForce.dot(wheelAxis)  + ground.dot(wheelAxis);
        double lateralSlip = wheel.dot(wheelAxisPerpendicular) + momentForce.dot(wheelAxisPerpendicular) + ground.dot(wheelAxisPerpendicular);

        return new Vector2d(longitudinalSlip,lateralSlip);
    }
}
