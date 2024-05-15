package org.aperture.control;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.aperture.common.command.Command;
import org.aperture.common.command.CompositeCommand;
import org.aperture.common.command.LambdaCommand;
import org.aperture.common.path.TrapezoidCurve;
import org.aperture.hardware.*;


public class RobotController {

    //intake constants
    private final static double INTAKE_RESET_POWER = -0.3;
    private final static double INTAKE_MAX_TICKS = 1000;
    private final static double INTAKE_MAX_POWER = 0.6;
    private final static double INTAKE_SAFE_DIST = 250; //position where the door can move and the arm can move simultaneously.
    private final static double INTAKE_THRESH = 50;
    private final static double INTAKE_HOLD_POWER = 0.0;
    private final static double INTAKE_HOLD_POWER_OFF_BUTTON = -0.1;
    private final static double INTAKE_RESET_TARGET_POS = -15;
    private final static TrapezoidCurve intakePowerCurve = new TrapezoidCurve(0.2,0.2,INTAKE_MAX_POWER,0.2,0.2);

    //door constants
    private final static double DOOR_CLOSE_POS = 0.215;
    private final static double DOOR_OPEN_POS = 0.8;

    //depo constants
    private final static double DEPO_RESET_POWER = -0.7;
    private final static double DEPO_MAX_TICKS = 870; //could be higher
    private final static double DEPO_MAX_POWER = 1.0;
    private final static double DEPO_DOWN_MUL = 0.5;
    private final static double DEPO_SAFE_DIST = 200;
    private final static double DEPO_THRESH = 15;
    private final static double DEPO_HOLD_POWER = 0.0;
    private final static double DEPO_HOLD_POWER_OFF_BUTTON = -0.1;
    private final static double DEPO_RESET_TARGET_POS = -50;

    //claw constants
    public enum ClawPos {
        CLOSED_1_PIXEL(0.15,300),
        CLOSED_2_PIXELS(0.3,500),
        REST(0.5,300),
        OPEN(0.75,500);

        public final double pos;
        public final int timerMs;

        ClawPos(double pos, int timerMs) {
            this.timerMs = timerMs;
            this.pos = pos;
        }
    }

    //wrist constants
    private final static double WRIST_POS_DEFAULT = 0.595;
    private final static double WRIST_POS_1 = 0.76;
    private final static double WRIST_POS_2 = 0.41;
    private final static double WRIST_POS_REVERSE = 0.0;

    //timing constants
    private final static double ARM_MOVE_TIME = 1000; //ms
    private final static double ARM_OUT_OF_WAY_TIME = 300;
    private final static double DOWN_TIME = 300; //ms
    private final static double UP_TIME = 150; //ms
    private final static double DOOR_OPEN_TIME = 500; //ms
    private final static double DOOR_CLOSE_TIME = 500; //ms
    private final static double WRIST_MOVE_TIME = 300;

    //intake constants
    public final static double ACTIVE_INTAKE_POWER = -1.0;
    public final static double OUTTAKE_POWER = 0.5;

    //arm constants
    public final static double AUTO_INIT_SHOULDER_POS = 0.55;
    public final static double REST_SHOULDER_POS = 0.3;
    public final static double DOWN_SHOULDER_POS = 0.205;
    public final static double OUT_OF_WAY_SHOULDER_POS = 0.5;
    public final static double DEPO_SHOULDER_POS = 0.8;

    public final static double AUTO_INIT_ELBOW_POS = 0.15;
    public final static double REST_ELBOW_POS = 0.84;
    public final static double DOWN_ELBOW_POS = 0.815;
    public final static double DEPO_ELBOW_POS = 0.55;

    public State state = new State();
    private final PIDController intakeSlidePID = new PIDController(0.01,0.000,0.00025);
    private final PIDController depoSlidePID = new PIDController(0.05,0,0.0005);

    public class State {
        //output
        public boolean targetDoorClosed = false;
        public ClawPos clawPos = ClawPos.REST;
        public double intakePower = 0;
        public double targetIntakePos = 0;
        private double lastTargetIntakePos = 0;
        private double intakePowerRampStart = 0;
        public double targetDepoPos = 0;
        public double shoulderPos = REST_SHOULDER_POS;
        public double elbowPos = REST_ELBOW_POS;
        public int wristPos = 0;

        //sensed
        public double currentIntakePos = 0;
        public double currentDepoPos = 0;

        //state
        public boolean encodersZeroed = false;
        public boolean doorMoving = false;
        public boolean armMoving = false;
        public boolean armOutOfWay = false;
        void read() {
            if(depoLimitSwitch.isPressed()) depoSlides.resetEncoder();
            if(intakeLimitSwitch.isPressed()) intakeSlides.resetEncoders();
            if(depoLimitSwitch.isPressed() && intakeLimitSwitch.isPressed()) encodersZeroed = true;
            currentIntakePos = intakeSlides.getPos();
            currentDepoPos = depoSlides.pos;

            if(targetIntakePos!=lastTargetIntakePos) {
                intakePowerRampStart = lastTargetIntakePos;
                lastTargetIntakePos = targetIntakePos;
            }
        }

        void write() {
            door.setPos(targetDoorClosed ? DOOR_CLOSE_POS : DOOR_OPEN_POS);
            claw.setPos(clawPos.pos);
            intakeMotor.setPower(intakePower);

            if(encodersZeroed) {
                double power = Range.clip(intakeSlidePID.run(targetIntakePos-currentIntakePos),-1.0,1.0);
                double t = Range.clip((targetIntakePos-currentIntakePos)/lastTargetIntakePos,0,1.0);
                double rampPower = intakePowerCurve.compute(t);

                intakeSlides.setPower(rampPower*power);
                System.out.println("IntakePower: base="+power + " ramp=" + rampPower + " total=" + intakeSlides.getPower());
                if(currentIntakePos>INTAKE_MAX_TICKS) intakeSlides.setPower(0);

                power = Range.clip(depoSlidePID.run(targetDepoPos-currentDepoPos),-DEPO_MAX_POWER,DEPO_MAX_POWER);
                if(power <0 ) power *= DEPO_DOWN_MUL;
                depoSlides.setPower(power);
                if(currentDepoPos>DEPO_MAX_TICKS) depoSlides.setPower(0);
            } else {
                if(!intakeLimitSwitch.isPressed()) intakeSlides.setPower(INTAKE_RESET_POWER);
                if(!depoLimitSwitch.isPressed()) depoSlides.setPower(DEPO_RESET_POWER);
            }

            if(targetIntakePos<=0 && intakeSlides.getPower() > INTAKE_HOLD_POWER) {
                intakeSlides.setPower(Math.min(intakeSlides.getPower(), intakeLimitSwitch.isPressed() ? INTAKE_HOLD_POWER : INTAKE_HOLD_POWER_OFF_BUTTON));
            }
//            if(hardware.robotVel.x<0 && intakeSlides.getPower()<0) {
//                intakeSlides.setPower(intakeSlides.getPower()*INTAKE_BACKWARDS_MUL*(1-Math.min(MAX_BACKWARDS_VEL-Math.abs(hardware.robotVel.x),1)));
//            }

            if(targetDepoPos<=0 && depoSlides.getPower() > DEPO_HOLD_POWER) {
                depoSlides.setPower(depoLimitSwitch.isPressed() ? DEPO_HOLD_POWER : DEPO_HOLD_POWER_OFF_BUTTON);
            }

            shoulder.setPos(shoulderPos);
            elbow.setPos(elbowPos);

            if (wristPos == 0) {
                wrist.setPos(WRIST_POS_DEFAULT);
            } else if (wristPos == 1) {
                wrist.setPos(WRIST_POS_1);
            } else if (wristPos == 2) {
                wrist.setPos(WRIST_POS_2);
            } else {
                wrist.setPos(WRIST_POS_REVERSE);
            }
        }
    }

    public void run() {
        state.write();
        state.read();
    }
    private final CachedTouchSensor intakeLimitSwitch;
    private final CachedTouchSensor depoLimitSwitch;

    //actuators
    private final CachedMotor<?> intakeMotor;
    private final DualMotor intakeSlides;
    private final CachedMotor<?> depoSlides;
    private final DualServo shoulder;
    private final CachedServo elbow;
    private final CachedServo wrist;
    private final CachedServo claw;
    private final CachedServo door;
    private final RobotHardware hardware;

    public RobotController(RobotHardware robot) {
        intakeLimitSwitch = robot.intakeTouch;
        depoLimitSwitch = robot.depoTouch;
        intakeSlides = robot.intakeSlides;
        intakeMotor = robot.intakeMotor;
        shoulder = robot.shoulderServo;
        depoSlides = robot.depoSlides;
        elbow = robot.elbowServo;
        wrist = robot.wristServo;
        door = robot.intakeServo;
        claw = robot.clawServo;
        hardware = robot;
    }

    //All of these commands are guaranteed to complete regardless of the robots current state given that slides reach,
    //unless a competing command is issues.

    //only one intake, depo, door and arm command can run at once.
    //will be either a extendIntake,extendIntakeAtLeast, or retractIntake command.
    Command runningIntakeCommand = null;
    //will be either a extendDepo,extendDepoAtLeast, or retractDepo command.
    Command runningDepoCommand = null;
    //will be a setDoor command.
    Command runningDoorCommand = null;
    //will be a setArm command.
    Command runningArmCommand = null;

    //------Basic Commands-----
    public Command extendIntake(double ticks) {
        int targetTicks = (int) Range.clip(ticks,INTAKE_SAFE_DIST,INTAKE_MAX_TICKS);
        return new Command() {
            public void init() { if(runningIntakeCommand!=null && runningIntakeCommand!=this) { runningIntakeCommand.cancelIfRunning(); } runningIntakeCommand=this; }
            public boolean run() {
                state.targetIntakePos = targetTicks;
                return Math.abs(targetTicks -state.currentIntakePos)<INTAKE_THRESH;
            }
            public String getDescriptor() { return "extendIntake("+targetTicks+")"; }
        };
    }
    public Command extendIntakeAtLeast(double ticks) {
        int targetTicks = (int) Range.clip(ticks,INTAKE_SAFE_DIST,INTAKE_MAX_TICKS);
        return new Command() {
            public void init() { if(runningIntakeCommand!=null && runningIntakeCommand!=this) { runningIntakeCommand.cancelIfRunning(); } runningIntakeCommand=this; }
            public boolean run() {
                state.targetIntakePos = targetTicks;
                return targetTicks-state.currentIntakePos<INTAKE_THRESH;
            }
            public String getDescriptor() { return "extendIntakeAtLeast("+targetTicks+")"; }
        };
    }
    public Command extendDepo(double ticks) {
        return new Command() {
            public void init() { if(runningDepoCommand!=null && runningDepoCommand!=this) { runningDepoCommand.cancelIfRunning(); } runningDepoCommand=this; }
            public boolean run() {
                //wait for arm to finish turning over before
                state.targetDepoPos = Range.clip(ticks,state.armOutOfWay ? 0 : DEPO_SAFE_DIST,DEPO_MAX_TICKS);
                return Math.abs(ticks-state.currentDepoPos)<DEPO_THRESH && ticks==state.targetDepoPos;
            }
            public String getDescriptor() { return "extendDepo("+ticks+")"; }
        };
    }
    public Command extendDepoAtLeast(double ticks) {
        return new Command() {
            public void init() { if(runningDepoCommand!=null && runningDepoCommand!=this) { runningDepoCommand.cancelIfRunning(); } runningDepoCommand=this; }
            public boolean run() {
                state.targetDepoPos = Range.clip(ticks,state.armOutOfWay ? 0 : DEPO_SAFE_DIST,DEPO_MAX_TICKS);
                return ticks-state.currentDepoPos<DEPO_THRESH;
            }
            public String getDescriptor() { return "extendDepoAtLeast("+ticks+")"; }
        };
    }
    public Command retractIntake() {
        return new Command() {
            public void init() { if(runningIntakeCommand!=null && runningIntakeCommand!=this) runningIntakeCommand.cancelIfRunning(); runningIntakeCommand = this; }
            public boolean run() {
                if(canIntakeRetract()) {
                    state.targetIntakePos = INTAKE_RESET_TARGET_POS;
                    if(intakeLimitSwitch.isPressed() || state.currentIntakePos < INTAKE_THRESH) { state.targetIntakePos = 0; return true; }
                } else {
                    //wait for door/arm to finish moving.
                    state.targetIntakePos = INTAKE_SAFE_DIST;
                }
                return false;
            }
            public String getDescriptor() { return "retractIntake"; }
        };
    }
    public Command setDoor(boolean closed) {
        return new Command() {
            final ElapsedTime timer = new ElapsedTime();
            double ms = 0;
            boolean skip = false;
            @Override
            public void init() {
                if(runningDoorCommand!=null && runningDoorCommand!=this) runningDoorCommand.cancelIfRunning();
                runningDoorCommand = this;
                skip = state.targetDoorClosed ==closed;
                timer.reset();
                state.doorMoving = true;
                ms = closed ? DOOR_CLOSE_TIME : DOOR_OPEN_TIME;
            }

            @Override
            public boolean run() {
                if(skip) {
                    state.doorMoving = false;
                    return true;
                }
                if(canDoorMove()) {
                    state.targetDoorClosed = closed;
                    boolean r = timer.milliseconds() > ms; //wait for door to finish moving.
                    if(r) state.doorMoving = false;
                    return r;
                } else {
                    //move intake slides out so that the door can move.
                    state.targetIntakePos=Math.max(INTAKE_SAFE_DIST,state.targetIntakePos);
                    return false;
                }
            }

            @Override
            public String getDescriptor() { return closed ? "closeDoor" : "openDoor"; }
        };
    }
    public Command retractDepo() {
        return new Command() {
            public void init() { if(runningDepoCommand!=null && runningDepoCommand!=this) runningDepoCommand.cancelIfRunning(); runningDepoCommand = this; }
            public boolean run() {
                if(canDepoRetract()) {
                    state.targetDepoPos = DEPO_RESET_TARGET_POS;
                    if(depoLimitSwitch.isPressed() || state.currentDepoPos<DEPO_THRESH) { state.targetDepoPos = 0; return true; }
                } else {
                    //wait for door/arm to finish moving.
                    state.targetDepoPos = DEPO_SAFE_DIST;
                }
                return false;
            }
            public String getDescriptor() { return "retractDepo"; }
        };
    }
    public Command setArm(double shoulderPos, double elbowPos) { return setArm(shoulderPos,elbowPos,ARM_MOVE_TIME); }
    public Command setArm(double shoulderPos, double elbowPos, double moveTime) {
        return new Command() {
            final ElapsedTime timer = new ElapsedTime();
            boolean underDoor = false;
            boolean skip = false;
            public void init() {
                if(runningArmCommand!=null && runningArmCommand!=this) runningArmCommand.cancelIfRunning();
                runningArmCommand = this;

                if(shoulderPos<OUT_OF_WAY_SHOULDER_POS) state.armOutOfWay = false;
                timer.reset();
                state.armMoving = true;
                //if already under door and moving to a position under the door,
                //then canArmMove condition can be skipped because there is no collision risk.
                underDoor = state.shoulderPos<=REST_SHOULDER_POS && shoulderPos<=REST_SHOULDER_POS;
                skip = state.shoulderPos==shoulderPos && state.elbowPos==elbowPos;
            }

            public boolean run() {
                if(skip) {
                    state.armMoving = false;
                    return true;
                }
                if(underDoor || canArmMove()) {
                    //start timer if not started already.
                    if(state.elbowPos!=elbowPos || state.shoulderPos!=shoulderPos) {
                        timer.reset();
                    }
                    state.elbowPos=elbowPos;
                    state.shoulderPos=shoulderPos;

                    //outOfWay toggle is time based because no encoder for shoulder is plugged in.
                    if(timer.milliseconds()>ARM_OUT_OF_WAY_TIME && shoulderPos>OUT_OF_WAY_SHOULDER_POS) state.armOutOfWay = true;
                    boolean r = timer.milliseconds() > moveTime; //wait for arm to finish moving.
                    if(r) state.armMoving = false;
                    return r;
                } else {
                    //move depo slides out so that the door can move.
                    state.targetDepoPos=Math.max(DEPO_SAFE_DIST,state.targetDepoPos);
                    return false;
                }
            }

            public String getDescriptor() { return "setArm(shoulder:"+shoulderPos+",elbow:"+elbowPos+")"; }
        };
    }

    //The following commands are time based and will never stall. maybe consider only allowing one to run at once?.
    public Command setActiveIntakePower(double power) {
        return new LambdaCommand("activeIntakePower("+power+")",()->{
            System.out.println("APERTURE COMMAND WOOOOOOOOO");
            state.intakePower=power;
        });
    }
    public Command setClawPos(ClawPos clawPos) { return new Command() {
        final ElapsedTime timer = new ElapsedTime();
        @Override
        public void init() {
            timer.reset();
        }

        @Override
        public boolean run() {
            state.clawPos = clawPos;
            return timer.milliseconds()>clawPos.timerMs;
        }

        @Override
        public String getDescriptor() {
            return "setClawPos("+clawPos.toString()+")";
        }
    };}

    Command runningWristCommand = null;
    public Command setWristPos(int pos) {
        return new Command() {
            final ElapsedTime timer = new ElapsedTime();
            public void init() {
                if(runningWristCommand !=null && runningWristCommand !=this) runningWristCommand.cancelIfRunning();
                runningWristCommand = this;
                timer.reset();
            }

            public boolean run() {
                state.wristPos=pos;
                return timer.milliseconds()>WRIST_MOVE_TIME;
            }

            public String getDescriptor() {
                return "setWrist("+pos+")";
            }
        };
    }

    //-----Composite Commands-----
    //SAFETY: transfer cannot be run in parallel with other robot controller commands.
    public Command transfer(boolean twoPixels) {
        //outer composite command so transfer command only exits when down close and up motions are complete.
        return new CompositeCommand("transfer", Command.box(
                new LambdaCommand("cancelBeforeTransfer",()-> {
                    state.armMoving = false;
                    state.doorMoving = false;
                }).then(
                new CompositeCommand("transferComposite",
                        //most of the time some of these commands are redundant and will exit immediately.
                        retractDepo(),
                        retractIntake(),
                        setActiveIntakePower(0.0),
                        setDoor(false),
                        setWristPos(0),
                        setArm(REST_SHOULDER_POS,REST_ELBOW_POS,ARM_MOVE_TIME),
                        setClawPos(ClawPos.REST)
                ))
                        .then(setArm(DOWN_SHOULDER_POS,DOWN_ELBOW_POS,DOWN_TIME))
                        .then(setClawPos(twoPixels ? ClawPos.CLOSED_2_PIXELS : ClawPos.CLOSED_1_PIXEL))
                        .then(setArm(REST_SHOULDER_POS,REST_ELBOW_POS,UP_TIME))
        ));
    }
    public Command depo(double depoExtension, double shoulderPos, double elbowPos) {
        return new CompositeCommand("depo",
                extendDepo((int) depoExtension),
                setArm(shoulderPos,elbowPos,ARM_MOVE_TIME)
        );
    }
    public Command drop() {
        return new CompositeCommand("drop",Command.box(
                setClawPos(ClawPos.OPEN)
                        .then(new CompositeCommand("return",
                                retractDepo(),
                                setArm(REST_SHOULDER_POS,REST_ELBOW_POS,ARM_MOVE_TIME),
                                setWristPos(0)
                        ))
                        .then(setClawPos(ClawPos.REST))
        ));
    }

    /*
    Explanation of these conditions:
    These conditions do not guarantee "sensible" movement that coincides with how pixels are taken in and transferred,
    but only ensure that the arm and door will not hit each other during retraction.

    Intake+MoveDoor and Depo+MoveArm are symmetrical.

    On a flat line:

                     Retraction Zone
    ----------*-----[_______________]-----*----------
            Intake                       Depo

    NOTE: "retracted" in the following context means for the depo/intake to be below their respective SAFE_DIST thresholds.

    For the door to move either the intake has to be extended or it has to be retracted while the depo is not.
    Likewise, for the arm to move either the depo has to be extended or it has to be retracted while the intake is not.

    The intake can always safely retract when the depo is not retracted, and when the depo is retracted, it can retract when the door and arm are not moving.
    The same logic applies to the depo.
    */

    private boolean canDoorMove() {
        return intakeInSafeDist() || depoInSafeDist();
    }
    private boolean canIntakeRetract() {
        System.out.println("canRetract debug: " + depoInSafeDist() + " " + !state.armMoving + " " + !state.doorMoving);
        return depoInSafeDist() || (!state.armMoving && !state.doorMoving);
    }
    private boolean canDepoRetract() {
        return intakeInSafeDist() || (!state.armMoving && !state.doorMoving);
    }
    private boolean canArmMove() {
        return intakeInSafeDist() || depoInSafeDist() || state.armOutOfWay;
    }

    public boolean intakeInSafeDist() {
        //intake current and target are above intake safe dist within a threshold.
        return INTAKE_SAFE_DIST-state.currentIntakePos<INTAKE_THRESH && INTAKE_SAFE_DIST-state.targetIntakePos<INTAKE_THRESH;
    }
    public boolean depoInSafeDist() {
        //depo current and target are above intake safe dist within a threshold.
        return DEPO_SAFE_DIST-state.currentDepoPos<DEPO_THRESH && DEPO_SAFE_DIST-state.targetDepoPos<DEPO_THRESH;
    }
}
