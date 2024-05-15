package aperture.simulator.math;

import org.joml.Matrix2d;
import org.joml.Vector2d;
import org.opencv.photo.CalibrateCRF;

public class SwerveDrivebaseModel implements RungeKutta4.TimeInvariantDiffEqSystem<SwerveDrivebaseModel.Input> {
    public static class Input {
        double SL1,SL2,SR1,SR2;

        public Input(double SL1, double SL2, double SR1, double SR2) {
            this.SR1 = SR1;
            this.SL1 = SL1;
            this.SL2 = SL2;
            this.SR2 = SR2;
        }
    }

    public final double width;
    public final double length;
    public final double wheelRadius;
    public final MagicTireFormula tractionModel;
    public final double wheelNormalForce;
    public final double rotationalInteria;
    public final double turnGearRatio = 1.0;

    final DcMotorDynamics motorModel;

    public SwerveDrivebaseModel(DcMotorDynamics motorModel, double width, double length, double wheelRadius, double mass, double rotationalInteria, MagicTireFormula tractionModel) {
        this.motorModel = motorModel;
        this.width = width;
        this.length = length;
        this.wheelRadius = wheelRadius;
        this.wheelNormalForce = mass/2.0;
        this.tractionModel = tractionModel;
        this.rotationalInteria = rotationalInteria;
    }

    int moduleStateSize = 5;

    double lastCalculatedMotorCurrent = 0;
    double lastCaclulatedMotorSpeed = 0;

    double[] lastCalculatedSlips = new double[2];
    double[] lastCalculatedForces = new double[2];
    @Override
    public double[] calc(double[] state, Input input) {

        int x = moduleStateSize*2;
        int y = moduleStateSize*2+1;
        int h = moduleStateSize*2+2;
        double[] res = new double[moduleStateSize*2 + 3];
        for(int i=0; i<2; i++) {

            int current1 = i*moduleStateSize;
            int velocity1 = i*moduleStateSize+1;

            int current2 = i*moduleStateSize+2;
            int velocity2 = i*moduleStateSize+3;
            int direction = i*moduleStateSize+4;

            double[] motor1State = motorModel.calc(new double[] { state[current1], state[velocity1] },new Double[]{ i==0 ? input.SL1 : input.SR1, 0.0 });
            double[] motor2State = motorModel.calc(new double[] { state[current2], state[velocity2] },new Double[]{ i==0 ? input.SL2 : input.SR2, 0.0 });

            double turnSpeed = (state[velocity1]-state[velocity2])/(2.0*turnGearRatio);
            double wheelSpeed = (state[velocity1]+state[velocity2])/2.0;

            Vector2d slip = calcSlip(
                    wheelSpeed,
                    new Vector2d(Math.cos(state[direction]),Math.sin(state[direction])),
                    i==0?new Vector2d(-width/2.0,0):new Vector2d(width/2.0,0),
                    new Vector2d(state[x],state[y]),
                    state[h]
            );

            Vector2d wheelForce = new Vector2d(tractionModel.calc(slip.x),tractionModel.calc(slip.y)).mul(wheelNormalForce);
            Vector2d robotForce = wheelForce.mul(new Matrix2d().rotate(state[direction]));

            double moment = robotForce.dot(new Vector2d(0,i==0 ? -1 : 1))*width/2.0;

            res[current1] = motor1State[0];
            res[velocity1] = motor1State[1];
            res[current2] = motor2State[0];
            res[velocity2] = motor2State[1];
            res[direction] = turnSpeed;

            res[x] -= robotForce.x/wheelNormalForce*2.0;
            res[y] += robotForce.y/wheelNormalForce*2.0;
            res[h] += moment/rotationalInteria;

            lastCalculatedForces[i] = wheelForce.y;
            lastCalculatedSlips[i] = slip.x;
        }

        return res;
    }

    public double[] getStartingConfig() {
        double[] res = new double[moduleStateSize*2 + 3];

        res[4] = Math.toRadians(90);
        res[5+4] = Math.toRadians(90);

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

        Vector2d wheelAxisPerpendicular = new Vector2d(wheelAxis).perpendicular();

        double longitudinalSlip = wheel.dot(wheelAxis) + momentForce.dot(wheelAxis) + ground.dot(wheelAxis);
        double lateralSlip = wheel.dot(wheelAxisPerpendicular) + momentForce.dot(wheelAxisPerpendicular) + ground.dot(wheelAxisPerpendicular);

        return new Vector2d(longitudinalSlip,lateralSlip);
    }
}
