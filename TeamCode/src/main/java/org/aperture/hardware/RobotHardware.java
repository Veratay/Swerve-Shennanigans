package org.aperture.hardware;

import aperture.simulator.Simulator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.aperture.LogConfig;
import org.aperture.common.coordinates.XyhVector;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class RobotHardware {
    //Constants
    private final static double LENGTH = 28.38;       // distance between encoder 1 and 2 in cm
    private final static double B = 9.5;       // distance between the midpoint of encoder 1 and 2 and encoder 3
    private final static double R = 3.8/2.0;        // wheel radius in cm
    private final static double N = 8192;       // encoder ticks per revolution, REV encoder
    private final static double CM_PER_TICK = 2.0 * Math.PI * R / N;

    //loop-time
    private final ElapsedTime loopTimer = new ElapsedTime();
    private final ElapsedTime timer = new ElapsedTime();
    public static double dt;
    private double readTime;
    private double writeTime;

    //----------Hardware references----------
    //motors
    private final CachedMotor<?> frontRightMotor;
    private final CachedMotor<?> frontLeftMotor;
    private final CachedMotor<?> backRightMotor;
    private final CachedMotor<?> backLeftMotor;
    private final CachedMotor<?> encoderRight;
    private final CachedMotor<?> encoderLeft;
    private final CachedMotor<?> encoderAux;
    public final CachedMotor<?> intakeMotor;
    public final CachedMotor<?> depoSlides;
    public final DualMotor intakeSlides;

    //servos
    public final CachedServo intakeServo;
    public final CachedServo elbowServo;
    public final CachedServo wristServo;
    public final CachedServo clawServo;
    public final CachedServo slServo;
    public final CachedServo srServo;
    public final DualServo shoulderServo;

    //sensors
    public final CachedTouchSensor intakeTouch;
    public final CachedTouchSensor depoTouch;

    //working vars
    public XyhVector pos = new XyhVector();
    public XyhVector robotVel = new XyhVector();
    public XyhVector fieldVel = new XyhVector();

    public RobotHardware(HardwareMap hardwareMap) {

        //-----Drive Motors-----
        DcMotor FL = (DcMotor) hardwareMap.get("motorFrontLeft");
        FL.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeftMotor = new CachedMotor<>(FL,DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        DcMotor FR = (DcMotor) hardwareMap.get("motorFrontRight");
        FR.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor = new CachedMotor<>(FR,DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        DcMotor BL = (DcMotor) hardwareMap.get("motorBackLeft");
        BL.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor = new CachedMotor<>(BL,DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        DcMotor BR = (DcMotor) hardwareMap.get("motorBackRight");
        BR.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor = new CachedMotor<>(BR,DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeftMotor.trackPos = false;
        frontRightMotor.trackPos = false;
        backRightMotor.trackPos = false;
        backLeftMotor.trackPos = false;

        //-----Intake-----
        DcMotor intakeLeft = (DcMotor) hardwareMap.get("intakeLeft");
        DcMotor intakeRight = (DcMotor) hardwareMap.get("intakeRight");
        DcMotor intakeDcMotor = (DcMotor) hardwareMap.get("intakeMotor");

        intakeLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        intakeLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeRight.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeSlides = new DualMotor(
                new CachedMotor<>(intakeLeft, DcMotor.RunMode.RUN_WITHOUT_ENCODER),
                new CachedMotor<>(intakeRight, DcMotor.RunMode.RUN_WITHOUT_ENCODER)
        );
        //intakeMotor = new CachedMotor<>(intakeDcMotor, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor = new CachedMotor<>(intakeDcMotor, DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.resetEncoder();

        intakeMotor.trackPos = false;

        intakeSlides.resetEncoders();
        intakeMotor.resetEncoder();

        intakeServo = new CachedServo((Servo) hardwareMap.get("intakeServo"));

        intakeTouch = new CachedTouchSensor((TouchSensor) hardwareMap.get("intakeTouch"));

        //-----Odometry-----
        //have to construct CachedMotor again so that trackPos is not disabled.
        encoderLeft = new CachedMotor<>(BL, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        encoderAux = new CachedMotor<>(FL, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        encoderRight = new CachedMotor<>(FR, DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        encoderLeft.resetEncoder();
        encoderAux.resetEncoder();
        encoderRight.resetEncoder();

        //-----Depo-----
        DcMotor depoMotor = (DcMotor) hardwareMap.get("depoMotor");
        depoMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        depoSlides = new CachedMotor<>(depoMotor, DcMotor.RunMode.RUN_USING_ENCODER);
        depoSlides.resetEncoder();

        clawServo = new CachedServo((Servo) hardwareMap.get("clawServo"));
        wristServo = new CachedServo((Servo) hardwareMap.get("wristServo"));
        elbowServo = new CachedServo((Servo) hardwareMap.get("elbowServo"));

        slServo = new CachedServo((Servo) hardwareMap.get("shoulderLeftServo"));
        srServo = new CachedServo((Servo) hardwareMap.get("shoulderRightServo"));

        shoulderServo = new DualServo(slServo,srServo);

        depoTouch = new CachedTouchSensor((TouchSensor) hardwareMap.get("depoTouch"));

        read();
        write();
    }

    public void tick(Telemetry telemetry) {
        write();
        read();
        odometry(telemetry);
        telemetry(telemetry);
        telemetry.update();
    }

    private void read() {
        dt = loopTimer.seconds();
        loopTimer.reset();
        timer.reset();

        //doesn't actually do anything, but leaving this in here in case
        //the drive motor encoders are connected.
        frontLeftMotor.read();
        frontRightMotor.read();
        backLeftMotor.read();
        backRightMotor.read();

        encoderRight.read();
        encoderLeft.read();
        encoderAux.read();

        intakeSlides.read();
        depoSlides.read();

        intakeTouch.read();
        depoTouch.read();

        readTime = timer.seconds();
    }

    private void write() {
        timer.reset();

        frontLeftMotor.write();
        frontRightMotor.write();
        backRightMotor.write();
        backLeftMotor.write();

        intakeSlides.write();
        intakeMotor.write();
        depoSlides.write();

        slServo.write();
        srServo.write();
        elbowServo.write();
        wristServo.write();
        clawServo.write();

        intakeServo.write();

        writeTime = timer.seconds();
    }

    public void startMove(double drive, double strafe, double turn, double modifier) {
        if(drive!=0 && strafe != 0) {
            double hyp = Math.sqrt(Math.pow(strafe,2)+Math.pow(drive,2));
            double scalar = Math.max(Math.abs(drive),Math.abs(strafe))/hyp;
            drive *= scalar;
            strafe *= scalar;
        }

        double frontLeftPower  = (drive - strafe - turn) * modifier;
        double frontRightPower = (drive + strafe + turn) * modifier;
        double backLeftPower   = (drive + strafe - turn) * modifier;
        double backRightPower  = (drive - strafe + turn) * modifier;

        double max = Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower),  Math.abs(backRightPower))
        );

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        frontLeftMotor.setPower(frontLeftPower);
        frontRightMotor.setPower(frontRightPower);
        backLeftMotor.setPower(backLeftPower);
        backRightMotor.setPower(backRightPower);
    }

    private void odometry(Telemetry telemetry) {
        int dn1 = encoderLeft.pos - encoderLeft.oldPos;
        int dn2 = encoderRight.pos - encoderRight.oldPos;
        int dn3 = encoderAux.pos - encoderAux.oldPos;

        // the robot has moved and turned a tiny bit between two measurements:
        double dTheta = CM_PER_TICK * (dn2-dn1) / LENGTH;
        double dx = CM_PER_TICK * (dn1+dn2) / 2.0;
        double dy = -CM_PER_TICK * (dn3 - (dn2-dn1) * B / LENGTH);

        robotVel.x = dx/dt;
        robotVel.y = dy/dt;
        robotVel.h = dTheta/dt;

        //derived from https://github.com/acmerobotics/road-runner/blob/master/doc/pdf/Mobile_Robot_Kinematics_for_FTC.pdf constant velocity odo.
        double a = Math.sin(dTheta)/dTheta;
        double b = (1-Math.cos(dTheta))/dTheta;

        //lim sin(x)/x as x->0 = 1
        if(Double.isNaN(a)) a = 1;
        //lim (1-cos(x))/x as x->0 = sin(x)/1 = 0
        if(Double.isNaN(b)) b = 0;

        double c = a * dx - b * dy;
        double d = b * dx + a * dy;

        double theta = pos.h;

        double dxField = Math.cos(theta) * dx - Math.sin(theta) * dy;
        double dyField = Math.sin(theta) * dx + Math.cos(theta) * dy;
        fieldVel.x = dxField / dt;
        fieldVel.y = dyField / dt;
        fieldVel.h = dTheta / dt;

        pos.x += dxField;
        pos.y += dyField;
        pos.h = normalizeRadians(theta+dTheta);

        double[] posSim = Simulator.odometry();
        fieldVel.x = (posSim[0]-pos.x)/dt;
        fieldVel.y = (posSim[1]-pos.y)/dt;
        fieldVel.h = (posSim[2]-pos.h)/dt;
        pos.x = posSim[0];
        pos.y = posSim[1];
        pos.h = posSim[2];

        if(LogConfig.ODOMETRY) {
            telemetry.addData("Pos:",pos.toStringDeg());
            telemetry.addData("dTheta,dx,dy",dTheta+","+dx+","+dy);
            telemetry.addData("dn1,dn2,dn2",dn1+","+dn2+","+dn3);
            telemetry.addData("dxField,dyField",dxField+","+dyField);
            telemetry.addData("odoLeft",encoderLeft.pos);
            telemetry.addData("odoRight",encoderRight.pos);
            telemetry.addData("odoAux", encoderAux.pos);
        }
    }

    public void telemetry(Telemetry telemetry) {
        if(LogConfig.LOOPTIME) {
            telemetry.addData("Loop-time (ms): ",dt*1000);
            telemetry.addData("Write-time (ms): ", writeTime*1000);
            telemetry.addData("Read-time (ms): ", readTime*1000);
        }
    }
}

