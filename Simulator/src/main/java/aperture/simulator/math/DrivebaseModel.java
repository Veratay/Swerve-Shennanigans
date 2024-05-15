package aperture.simulator.math;

import org.joml.Vector2d;

public class DrivebaseModel implements RungeKutta4.TimeInvariantDiffEqSystem<DrivebaseModel.Input> {
    public static class Input {
        public final double FRPower;
        public final double FLPower;
        public final double BRPower;
        public final double BLPower;

        public Input(double FR,double FL,double BR,double BL) {
            this.FRPower = FR;
            this.FLPower = FL;
            this.BRPower = BR;
            this.BLPower = BL;
        }
    }

    //DcMotorDynamics is stateless, so its fine to just use 1 object to represent the 4 motors.
    private final DcMotorDynamics motorModel;

    public final double width;
    public final double length;
    public final double wheelRadius;
    public final double wheelNormalForce;
    public final double rotationalInteria;

    final MagicTireFormula tireFormula;

    public DrivebaseModel(DcMotorDynamics motorModel, double width, double length, double wheelRadius, double mass, double rotationalInteria, MagicTireFormula tire) {
        this.motorModel = motorModel;
        this.width = width;
        this.length = length;
        this.wheelRadius = wheelRadius;
        this.wheelNormalForce = mass/4.0;
        this.tireFormula = tire;
        this.rotationalInteria = rotationalInteria;
    }

    public double[] lastCalculatedSlips = new double[4];
    public double[] lastCalculatedForces = new double[4];
    @Override
    public double[] calc(double[] state, Input input) {
        //states 0-7: motor
        //state 8-10: vx, vy, vtheta
        double[] FR = motorModel.calc(new double[] { state[0],state[1] }, new Double[] {input.FRPower, -tireFormula.calc(getWheelSlip(state,0))*this.wheelRadius });
        double[] FL = motorModel.calc(new double[] { state[2],state[3] }, new Double[] {input.FLPower, -tireFormula.calc(getWheelSlip(state,1))*this.wheelRadius});
        double[] BR = motorModel.calc(new double[] { state[4],state[5] }, new Double[] {input.BRPower, -tireFormula.calc(getWheelSlip(state,2))*this.wheelRadius});
        double[] BL = motorModel.calc(new double[] { state[6],state[7] }, new Double[] {input.BLPower, -tireFormula.calc(getWheelSlip(state,3))*this.wheelRadius});

//        System.out.println("FR" + Arrays.toString(FR) + " power:" + input.FRPower);
//        System.out.println("FL" + Arrays.toString(FL) + " power:" + input.FLPower);
//        System.out.println("BR" + Arrays.toString(BR) + " power:" + input.BRPower);
//        System.out.println("BL" + Arrays.toString(BL) + " power:" + input.BLPower);

        double distToCenter = Math.sqrt((width*width + length*length)/4.0);

        double[] rollerAngles = new double[] {
                Math.toRadians(135),
                Math.toRadians(45),
                Math.toRadians(45),
                Math.toRadians(135),
        };
        double angleFromCenter = Math.atan2(length,width);
        double[] anglesFromCenter = new double[] {
                angleFromCenter,
                Math.toRadians(180)-angleFromCenter,
                -angleFromCenter,
                Math.toRadians(180)+angleFromCenter
        };
        double[] wheelForces = new double[4];

        double fx = 0;
        double fy = 0;
        double ft = 0;
        for(int i=0; i<4; i++) {
            Vector2d rollerAxis = new Vector2d(Math.cos(rollerAngles[i]),Math.sin(rollerAngles[i]));
            double wheelSlip = getWheelSlip(state,i);

//            Vector2d vel = new Vector2d(state[8],state[9]);
//            Vector2d axleWheel = new Vector2d(0,wheelRadius*state[1+i*2]);
//
            double torqueTheta = anglesFromCenter[i]+Math.toRadians(90);
//            Vector2d torqueAxis = new Vector2d(Math.cos(torqueTheta),Math.sin(torqueTheta));
//            Vector2d moment = new Vector2d(torqueAxis).mul(distToCenter*state[10]);
//            Vector2d rollerAxis = new Vector2d(Math.cos(rollerAngles[i]),Math.sin(rollerAngles[i]));
//
//            double groundVel = vel.dot(rollerAxis);
//            double wheelVel = axleWheel.dot(rollerAxis);
//            double momentVel = moment.dot(rollerAxis);
//
//            double wheelSlip = wheelVel-groundVel-momentVel;
//
            double u = tireFormula.calc(wheelSlip);
            System.out.println("wheelSlip " + wheelSlip + " u " + u);

            wheelForces[i] = wheelNormalForce*u;

            Vector2d wheelForce = rollerAxis.mul(wheelForces[i]);
            fx += wheelForce.x;
            fy += wheelForce.y;
            ft += distToCenter*wheelForce.dot(new Vector2d(Math.cos(torqueTheta),Math.sin(torqueTheta)));

            lastCalculatedSlips[i] = wheelSlip;
            lastCalculatedForces[i] = wheelForces[i];

//            System.out.println(torqueAxis.toString() + wheelForce.toString());
//            System.out.println("fx " + fx + " fy " +fy + " ft " + ft);
        }

//        System.out.println("wheelForces" + Arrays.toString(wheelForces));

//        double fx = Math.cos(Math.toRadians(45))*(-wheelForces[0]+wheelForces[1]+wheelForces[2]-wheelForces[3]);
//        double fy = Math.sin(Math.toRadians(45))*(wheelForces[0]+wheelForces[1]+wheelForces[2]+wheelForces[3]);
//        double ft = a*distToCenter;

//        System.out.println("fx " + fx + " fy " +fy + " ft " + ft);
//        System.out.println("state: " + Arrays.toString(state));

        return new double[] {
                FR[0],FR[1],
                FL[0],FL[1],
                BR[0],BR[1],
                BL[0],BL[1],
                fx/(wheelNormalForce*4),
                fy/(wheelNormalForce*4),
                ft/rotationalInteria
        };
    }

    public double getWheelSlip(double[] state, int wheel) {
        double distToCenter = Math.sqrt((width*width + length*length)/4.0);

        double[] rollerAngles = new double[] {
                Math.toRadians(135),
                Math.toRadians(45),
                Math.toRadians(45),
                Math.toRadians(135),
        };
        double angleFromCenter = Math.atan2(length,width);
        double[] anglesFromCenter = new double[] {
                angleFromCenter,
                Math.toRadians(180)-angleFromCenter,
                -angleFromCenter,
                Math.toRadians(180)+angleFromCenter
        };
        Vector2d vel = new Vector2d(state[8],state[9]);
        Vector2d axleWheel = new Vector2d(0,wheelRadius*state[1+wheel*2]);

        double torqueTheta = anglesFromCenter[wheel]+Math.toRadians(90);
        Vector2d torqueAxis = new Vector2d(Math.cos(torqueTheta),Math.sin(torqueTheta));
        Vector2d moment = new Vector2d(torqueAxis).mul(distToCenter*state[10]);
        Vector2d rollerAxis = new Vector2d(Math.cos(rollerAngles[wheel]),Math.sin(rollerAngles[wheel]));

        double groundVel = vel.dot(rollerAxis);
        double wheelVel = axleWheel.dot(rollerAxis);
        double momentVel = moment.dot(rollerAxis);

        return wheelVel-groundVel-momentVel;
    }
}
