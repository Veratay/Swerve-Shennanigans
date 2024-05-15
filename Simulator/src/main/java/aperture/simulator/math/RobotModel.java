package aperture.simulator.math;

import aperture.simulator.Simulator;

import java.util.Arrays;

public class RobotModel {

    private final static double INTAKE_SPEED = 700;
    private final static double DEPO_SPEED = 400;
    private final static double MOVE_SPEED = 120;
    private final static double ROTATE_MUL = 0.1;

    public final static double INTAKE_MAX = 1000;
    public final static double INTAKE_MAX_DIST = 90; //cm

    public final static double DEPO_MAX = 1000;
    public final static double DEPO_MAX_DIST = INTAKE_MAX_DIST*.75; //cm

    public static class Positions {
        //motors
        public double x,y,h,intakePos,depoPos = 0;
        //servos
        public double armPos,elbowPos,wristPos = 0;

        public boolean intakeActive, doorClosed, clawClosed = false;
    }

    public static class Input {
        //motors
        public double FR,FL,BR,BL, intake,depo, active, SL1,SL2,SR1,SR2;
        //servos
        public double arm,elbow,wrist,door,claw;
    }

    public Positions robotPositions = new Positions();

    public RobotModel() {
        drivebaseModel = new DrivebaseModel(new DcMotorDynamics(torqueConstant,resistance,inductance,gear_ratio/1.21421356237,driveBaseMotorLoad,driveBaseMotorFriction),width,length,wheelRadius,mass,rotationalInertia,
                new MagicTireFormula(0.013,2.8,200,1));
        drivebaseSolver = new RungeKutta4<>(new double[11],drivebaseModel);

        swerveDrivebaseModel = new SwerveDrivebaseModel(new DcMotorDynamics(torqueConstant,resistance,inductance,gear_ratio/1.2,driveBaseMotorLoad,driveBaseMotorFriction),width,length,wheelRadius,mass,rotationalInertia,
                new MagicTireFormula(0.013,2.8,200,1));
        swerveSolver = new RungeKutta4<>(swerveDrivebaseModel.getStartingConfig(),swerveDrivebaseModel);
    }

    public final DrivebaseModel drivebaseModel;

    private final RungeKutta4<DrivebaseModel.Input> drivebaseSolver;

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
    public static double w_1 = 500;
    public static double w_2 = 1/50.0;

    public static double intakeSlidesLoad = 1;
    public static double intakeSlidesFriction = 0;

    public static double depoSlidesLoad = 1;
    public static double depoSlidesFriction = 0;

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
//        dt = 0.01;
        robotPositions.intakePos += input.intake*INTAKE_SPEED*dt;
        robotPositions.intakePos = Math.min(Math.max(0,robotPositions.intakePos),INTAKE_MAX);
        robotPositions.depoPos += input.depo*DEPO_SPEED*dt;
        robotPositions.depoPos = Math.min(Math.max(0,robotPositions.depoPos),DEPO_MAX);

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

        System.out.println("CURRENTLY ACTIVE: " + (Simulator.SWERVE ? "SWERVE" : "MECANUM"));

        if(true) {
            for (int i=0; i<subSteps; i++) {
                swerveSolver.step(new SwerveDrivebaseModel.Input(input.SL1*voltage,input.SL2*voltage,input.SR1*voltage,input.SR2*voltage),dt/(double) subSteps);
            }

            y = -swerveSolver.state[10]*dt;
            x = swerveSolver.state[11]*dt;
            robotPositions.h += swerveSolver.state[12]*dt;


            if(timeSinceLastPoll>plotPollRate) {
                swerveDrivebaseModel.calc(swerveSolver.state,new SwerveDrivebaseModel.Input(input.FR*voltage,input.FL*voltage,input.BR*voltage,input.BL*voltage));
                for(int w=0; w<2; w++) {
                    wheelSlipPlot[w][plotSamples-1] = swerveDrivebaseModel.lastCalculatedSlips[w];
                    wheelForcePlot[w][plotSamples-1] = swerveDrivebaseModel.lastCalculatedForces[w];
                    motorSpeedPlot[w][plotSamples-1] = swerveSolver.state[1+w*5];
                    motorCurrentPlot[w][plotSamples-1] = swerveSolver.state[w*5];
                    robotSpeedPlot[w][plotSamples-1] = swerveSolver.state[11];
                }
//            System.out.println("Wheel Slips + " + Arrays.deepToString(wheelSlipPlot));
//                timeSinceLastPoll = 0;
            }

        }
        if(true) {
            for(int i=0; i<subSteps; i++) {
                drivebaseSolver.step(new DrivebaseModel.Input(input.FR*voltage,input.FL*voltage,input.BR*voltage,input.BL*voltage),dt/(double)subSteps);
            }

            if(timeSinceLastPoll>plotPollRate) {
                drivebaseModel.calc(drivebaseSolver.state,new DrivebaseModel.Input(input.FR*voltage,input.FL*voltage,input.BR*voltage,input.BL*voltage));
                for(int w=0; w<4; w++) {
                    wheelSlipPlot[w][plotSamples-1] = drivebaseModel.lastCalculatedSlips[w];
                    wheelForcePlot[w][plotSamples-1] = drivebaseModel.lastCalculatedForces[w];
                    motorSpeedPlot[w][plotSamples-1] = drivebaseSolver.state[1+w*2];
                    motorCurrentPlot[w][plotSamples-1] = drivebaseSolver.state[w*2];
                    robotSpeedPlot[w][plotSamples-1] = drivebaseSolver.state[9];
                }
//            System.out.println("Wheel Slips + " + Arrays.deepToString(wheelSlipPlot));
                timeSinceLastPoll = 0;
            }

            if(!Simulator.SWERVE) {

                y = -drivebaseSolver.state[8]*dt;
                x = drivebaseSolver.state[9]*dt;
                robotPositions.h += drivebaseSolver.state[10]*dt;
            }
        }



        robotPositions.x += x*Math.cos(robotPositions.h) + y*-Math.sin(robotPositions.h);
        robotPositions.y += x*Math.sin(robotPositions.h) + y*Math.cos(robotPositions.h);



        robotPositions.intakeActive = input.active != 0;
        robotPositions.doorClosed = input.door < 0.5;
        robotPositions.clawClosed = input.claw < 0.25;

        robotPositions.armPos += (input.arm-robotPositions.armPos)*dt*4;
        robotPositions.elbowPos += (input.elbow-robotPositions.elbowPos)*dt*4;
        robotPositions.wristPos = input.wrist;
    }
}
