package aperture.simulator;

//import aperture.robotics.RobotHardware;
import androidx.annotation.NonNull;
import aperture.simulator.hardware.PhantomMotor;
import aperture.simulator.hardware.PhantomServo;
import aperture.simulator.math.RobotModel;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.joml.Matrix4f;

import java.math.BigDecimal;
import java.util.HashMap;

public class Simulator {

    public static boolean FREEZE = false;
    public static boolean SWERVE = false;
    //0 right, 1 center, 2 left.
    public static int markerPos = 0;
    static ElapsedTime timer = new ElapsedTime();

    public static LinearOpMode opMode;
    static Renderer renderer;
    public static void init(LinearOpMode opMode) {
        Simulator.opMode = opMode;
        renderer = new Renderer();
        opMode.initGamepads();
    }
    private static final float Z_FAR = 1000.f;
    private static final float Z_NEAR = 0.01f;
    public static void calibrateCamera(float fx, float fy, float cx, float cy) {
        Matrix4f mat = new Matrix4f(
                fx/cx,0,0,0,
                0,fy/cy,0,0,
                0,0,-(Z_FAR+Z_NEAR)/(Z_FAR-Z_NEAR),-1,
                0,0,(-2*Z_FAR*Z_NEAR)/(Z_FAR-Z_NEAR),0
        );
        renderer.setProjMatrix(mat);
    }

    static Thread opModeThread;

    public static void run() {
        opModeThread = opMode.startOpModeInThread();
        opModeThread.start();
        while (opMode.opModeIsActive()) {
//            update();
            opMode.update();
            renderer.render();
        }
        opModeThread.interrupt();
    }

    public static void stop() {
        opMode.stopOpMode();
    }

    public static String getDebugString() {
        return String.format("Pos: %.2f, %.2f, %.2f \n Intake power %.2f \n Right trigger: %.2f",positions.x,positions.y,positions.h,intake.getPower(), opMode.gamepad1.right_trigger);

    }

    public static RobotModel robotModel = new RobotModel();

    private static PhantomMotor FR = new PhantomMotor();
    private static PhantomMotor FL = new PhantomMotor();
    private static PhantomMotor BR = new PhantomMotor();
    private static PhantomMotor BL = new PhantomMotor();

    private static PhantomMotor SL1 = new PhantomMotor();
    private static PhantomMotor SL2 = new PhantomMotor();
    private static PhantomMotor SR1 = new PhantomMotor();
    private static PhantomMotor SR2 = new PhantomMotor();

    private static PhantomMotor intake = new PhantomMotor();
    private static PhantomMotor depo = new PhantomMotor();
    private static PhantomMotor active = new PhantomMotor();
    private static PhantomMotor phantomMotor = new PhantomMotor();

    private static PhantomServo arm = new PhantomServo();
    private static PhantomServo elbow = new PhantomServo();
    private static PhantomServo wrist = new PhantomServo();
    private static PhantomServo claw = new PhantomServo();
    private static PhantomServo door = new PhantomServo();


    public static HardwareMap getHardwareMap() {

        HashMap<String,HardwareDevice> devices = new HashMap<>(); {{
            devices.put("motorFrontRight",FR);
            devices.put("motorFrontLeft",FL);
            devices.put("motorBackRight",BR);
            devices.put("motorBackLeft",BL);

            devices.put("SR1",SR1);
            devices.put("SR2",SR2);
            devices.put("SL1",SL1);
            devices.put("SL2",SL2);


            devices.put("odoLeft",phantomMotor);

            devices.put("intakeLeft",intake);
            devices.put("intakeRight",intake);
            devices.put("intakeMotor",active);
            devices.put("depoMotor",depo);

            devices.put("intakeServo",door);
            devices.put("elbowServo",elbow);
            devices.put("wristServo",wrist);
            devices.put("clawServo",claw);
            devices.put("shoulderLeftServo",arm);
            devices.put("shoulderRightServo",arm);

            devices.put("intakeTouch", (TouchSensor) () -> positions.intakePos < 10);
            devices.put("depoTouch", (TouchSensor) () -> positions.depoPos < 10);

            devices.put("cameraLeft", new WebcamName() {
                @NonNull
                @Override
                public String getUsbDeviceNameIfAttached() {
                    return "cameraLeft";
                }

                @Override
                public boolean isAttached() {
                    return false;
                }

                @Override
                public boolean isWebcam() {
                    return true;
                }

                @Override
                public boolean isCameraDirection() {
                    return false;
                }

                @Override
                public boolean isSwitchable() {
                    return false;
                }

                @Override
                public boolean isUnknown() {
                    return false;
                }
            });
        }}

        return new HardwareMap(new PhantomHardwareMapping<>(devices));
    }

    public static double[] odometry() {
        return new double[] { positions.x, positions.y, positions.h };
    }
    public static void setStartingPos(double x, double y, double h) { positions.x = x; positions.y = y; positions.h = h; }
    public static Orientation getAngularOrientation(double bias) {
        return new Orientation();
    }

    static RobotModel.Input input = new RobotModel.Input();

    public static volatile RobotModel.Positions positions = robotModel.robotPositions;

    public static void update() {
        double dt = timer.seconds();
        timer.reset();
 
        input.FR = FR.getPower();
        input.FL = FL.getPower();
        input.BR = BR.getPower();
        input.BL = BL.getPower();

        input.SL1 = SL1.getPower();
        input.SL2 = SL2.getPower();
        input.SR1 = SL1.getPower();
        input.SR2 = SL2.getPower();

        input.intake = intake.getPower();
        input.depo = depo.getPower();
        input.door = door.pos;
        input.elbow = elbow.pos;
        input.arm = arm.pos;
        System.out.println("armPos: " + input.arm);
        input.wrist = wrist.pos;
        input.claw = claw.pos;
        input.active = active.getPower();

        robotModel.tick(input,dt);

        intake.pos = positions.intakePos;
        depo.pos = positions.depoPos;
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {
            System.out.println("Finished :)");
        }
    }
}
