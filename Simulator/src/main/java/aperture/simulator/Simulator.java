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
    public static int LOOPTIME = 20;
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
        return new String();
    }

    public static RobotModel robotModel = new RobotModel();

    private static PhantomMotor Wheel1Drive = new PhantomMotor();
    private static PhantomMotor Wheel2Drive = new PhantomMotor();
    private static PhantomMotor Wheel3Drive = new PhantomMotor();
    private static PhantomMotor Wheel4Drive = new PhantomMotor();
    
    private static PhantomMotor Wheel1Turn = new PhantomMotor();
    private static PhantomMotor Wheel2Turn = new PhantomMotor();
    private static PhantomMotor Wheel3Turn = new PhantomMotor();
    private static PhantomMotor Wheel4Turn = new PhantomMotor();


    public static HardwareMap getHardwareMap() {

        HashMap<String,HardwareDevice> devices = new HashMap<>(); {{
            devices.put("Wheel1Turn",Wheel1Turn);
            devices.put("Wheel2Turn",Wheel1Turn);
            devices.put("Wheel3Turn",Wheel1Turn);
            devices.put("Wheel4Turn",Wheel1Turn);
            devices.put("Wheel1Drive",Wheel1Drive);
            devices.put("Wheel2Drive",Wheel1Drive);
            devices.put("Wheel3Drive",Wheel1Drive);
            devices.put("Wheel4Drive",Wheel1Drive);

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

        input.Wheel1Power = Wheel1Drive.getPower();
        input.Wheel2Power = Wheel2Drive.getPower();
        input.Wheel3Power = Wheel3Drive.getPower();
        input.Wheel4Power = Wheel4Drive.getPower();

        input.Wheel1Turn = Wheel1Turn.getPower();
        input.Wheel2Turn = Wheel2Turn.getPower();
        input.Wheel3Turn = Wheel3Turn.getPower();
        input.Wheel4Turn = Wheel4Turn.getPower();

        robotModel.tick(input,dt);

        try {
            Thread.sleep(LOOPTIME);
        } catch (InterruptedException ignored) {
            System.out.println("Finished :)");
        }
    }
}
