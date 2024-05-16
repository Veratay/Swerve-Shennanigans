package aperture.simulator.math;

import aperture.simulator.Simulator;

import java.util.Arrays;

public class RobotModel {
    public static class Positions {
        //motors
        public double x,y,h;
    }

    public static class Input {
        //motors
        public double Wheel1Power,Wheel1Turn, Wheel2Power,Wheel2Turn, Wheel3Power,Wheel3Turn, Wheel4Power,Wheel4Turn;
    }

    public Positions robotPositions = new Positions();

    public RobotModel() {
        DcMotorDynamics driveMotor = new DcMotorDynamics(torqueConstant,resistance,inductance,gear_ratio/1.2,driveBaseMotorLoad,driveBaseMotorFriction);
        DcMotorDynamics turnMotor = new DcMotorDynamics(torqueConstant,resistance,inductance,gear_ratio/1.2,driveBaseMotorLoad,driveBaseMotorFriction);
        swerveDrivebaseModel = new SwerveDrivebaseModel(driveMotor,turnMotor,width,length,wheelRadius,mass,rotationalInertia,
                new MagicTireFormula(0.013,2.9,100,1),0.01);
        swerveSolver = new RungeKutta4<>(swerveDrivebaseModel.getStartingConfig(),swerveDrivebaseModel);
    }

    private final SwerveDrivebaseModel swerveDrivebaseModel;
    private final RungeKutta4<SwerveDrivebaseModel.Input> swerveSolver;

    private final static double torqueConstant =  0.0188605;
    private final static double resistance = 1.15674;
    private final static double inductance = 0.01;

    public static double driveBaseMotorLoad = 0.0025;
    public static double gear_ratio = 19.2;
    public static double width = 38.1;
    public static double length = 45.72;
    public static double wheelRadius = 4.8;
    public static double mass = 42;
    public static double rotationalInertia = 1/12.0 * mass*(width*width + length*length); //assuming equally distributed
    public static double rollingFrictionConstant = 0.5;
    public static double driveBaseMotorFriction =  mass*rollingFrictionConstant/4.0/gear_ratio; //rolling friction on wheels
    public static double voltage = 12;

    private final static int plotSamples = 500;
    private final static double plotPollRate = 0.01; //seconds
    public static Double[][] motorSpeedPlot = new Double[4][plotSamples];
    public static Double[][] motorCurrentPlot = new Double[4][plotSamples];
    public static Double[][] wheelSlipPlot = new Double[4][plotSamples];
    public static Double[][] wheelForcePlot = new Double[4][plotSamples];
    public static Double[][] robotSpeedPlot = new Double[4][plotSamples];
    public static Double[] ys = new Double[plotSamples];

    static {
        for(int i=0; i<plotSamples; i++) {
            ys[i]=i*plotPollRate;
        }
        for(int w=0; w<4; w++) {
            for(int i=0; i<plotSamples; i++) {
                wheelSlipPlot[w][i]=0.0;
                wheelForcePlot[w][i]=0.0;
                motorCurrentPlot[w][i]=0.0;
                motorSpeedPlot[w][i]=0.0;
                robotSpeedPlot[w][i]=0.0;
            }
        }
    }
    double timeSinceLastPoll = 0;

    int subSteps = 50;
    public void tick(Input input, double dt) {

        double x =0;
        double y =0;

        timeSinceLastPoll+=dt;
        if(timeSinceLastPoll>plotPollRate && !Simulator.FREEZE) {
            for (int w=0; w<4; w++) {

                for(int i=1; i<plotSamples; i++) {
                    wheelSlipPlot[w][i-1] = wheelSlipPlot[w][i];
                    wheelForcePlot[w][i-1] = wheelForcePlot[w][i];
                    motorSpeedPlot[w][i-1] = motorSpeedPlot[w][i];
                    motorCurrentPlot[w][i-1] = motorCurrentPlot[w][i];
                    robotSpeedPlot[w][i-1] = robotSpeedPlot[w][i];
                }
            }
        }

        SwerveDrivebaseModel.Input input1 = new SwerveDrivebaseModel.Input(new double[] {
                input.Wheel1Power*voltage,
                input.Wheel2Power*voltage,
                input.Wheel3Power*voltage,
                input.Wheel4Power*voltage
        },new double[] {
                input.Wheel1Turn*voltage,
                input.Wheel2Turn*voltage,
                input.Wheel3Turn*voltage,
                input.Wheel4Turn*voltage
        });
        for (int i=0; i<subSteps; i++) {
            swerveSolver.step(input1,dt/(double) subSteps);
        }

        y = -swerveSolver.state[10]*dt;
        x = swerveSolver.state[11]*dt;
        robotPositions.h += swerveSolver.state[12]*dt;


        if(timeSinceLastPoll>plotPollRate) {
            swerveDrivebaseModel.calc(swerveSolver.state,input1);
            for(int w=0; w<2; w++) {
                wheelSlipPlot[w][plotSamples-1] = swerveDrivebaseModel.lastCalculatedSlips[w];
                wheelForcePlot[w][plotSamples-1] = swerveDrivebaseModel.lastCalculatedForces[w];
                motorSpeedPlot[w][plotSamples-1] = swerveSolver.state[1+w*5];
                motorCurrentPlot[w][plotSamples-1] = swerveSolver.state[w*5];
                robotSpeedPlot[w][plotSamples-1] = swerveSolver.state[11];
            }
                timeSinceLastPoll = 0;
        }

        robotPositions.x += x*Math.cos(robotPositions.h) + y*-Math.sin(robotPositions.h);
        robotPositions.y += x*Math.sin(robotPositions.h) + y*Math.cos(robotPositions.h);
    }
}
