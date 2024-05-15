package org.aperture.common;

import android.graphics.Point;
import android.graphics.Rect;

import aperture.simulator.Simulator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.aperture.common.command.Command;
import org.aperture.common.command.CommandScheduler;
import org.aperture.common.command.LambdaCommand;
import org.aperture.common.coordinates.XyhVector;
import org.aperture.common.path.Path;
import org.aperture.common.path.TrapezoidCurve;
import org.aperture.control.RobotController;
import org.aperture.control.movement.BezierMove;
import org.aperture.hardware.RobotHardware;

public abstract class OpModeCommon extends LinearOpMode {
    public enum MarkerPos {
        LEFT,
        CENTER,
        RIGHT
    }
    public CommandScheduler scheduler = new CommandScheduler("autoScheduler");

    public TrapezoidCurve speedCurve = new TrapezoidCurve(0.1,0.3,1.0,0.7,0.2);
    public XyhVector thresh = new XyhVector(2,2,Math.toRadians(5));
    XyhVector velThresh = new XyhVector(3,3,Math.toRadians(15)); //cm/s and deg/s

    public RobotHardware robot;
    public RobotController controller;

    private final float ORANGE_HUE = 19.0f;
    private final float BLUE_HUE = 226.0f;
    private final float HUE_TOLERANCE = 50.0f;

    private final Point RED_BACKDROP_CENTER_POINT = new Point(60, 360);
    private final Point RED_BACKDROP_RIGHT_POINT = new Point(365, 380);
    private final Point RED_STACK_CENTER_POINT = new Point(60, 360);
    private final Point RED_STACK_RIGHT_POINT = new Point(365, 380);


    private final Point BLUE_BACKDROP_CENTER_POINT = new Point(470, 350);
    private final Point BLUE_BACKDROP_LEFT_POINT = new Point(162, 370);

    private final Point BLUE_STACK_CENTER_POINT = new Point(190, 220);
    private final Point BLUE_STACK_RIGHT_POINT = new Point(490, 220);

    private final int BOX_WIDTH = 90;
    private final int BOX_HEIGHT = 50;
    private final int NUM_BOX_PIXELS = BOX_WIDTH * BOX_HEIGHT;
    private final int NUM_PIXEL_THRESHOLD = (int) (NUM_BOX_PIXELS * .3f);

    private final Rect centerBackdropRectRed = new Rect(RED_BACKDROP_CENTER_POINT.x, RED_BACKDROP_CENTER_POINT.y,
            RED_BACKDROP_CENTER_POINT.x + BOX_WIDTH, RED_BACKDROP_CENTER_POINT.y + BOX_HEIGHT);
    private final Rect otherBackdropRectRed = new Rect(RED_BACKDROP_RIGHT_POINT.x, RED_BACKDROP_RIGHT_POINT.y,
            RED_BACKDROP_RIGHT_POINT.x + BOX_WIDTH, RED_BACKDROP_RIGHT_POINT.y + BOX_HEIGHT);
    private final Rect centerStackRectRed = new Rect(RED_STACK_CENTER_POINT.x, RED_STACK_CENTER_POINT.y,
            RED_STACK_CENTER_POINT.x + BOX_WIDTH, RED_STACK_CENTER_POINT.y + BOX_HEIGHT);
    private final Rect otherStackRect = new Rect(RED_STACK_RIGHT_POINT.x, RED_STACK_RIGHT_POINT.y,
            RED_STACK_RIGHT_POINT.x + BOX_WIDTH, RED_STACK_RIGHT_POINT.y + BOX_HEIGHT);


    private final Rect centerBackdropRectBlue = new Rect(BLUE_BACKDROP_CENTER_POINT.x, BLUE_BACKDROP_CENTER_POINT.y,
            BLUE_BACKDROP_CENTER_POINT.x + BOX_WIDTH, BLUE_BACKDROP_CENTER_POINT.y + BOX_HEIGHT);

    private final Rect centerStackRectBlue = new Rect(BLUE_STACK_CENTER_POINT.x, BLUE_STACK_CENTER_POINT.y,
            BLUE_STACK_CENTER_POINT.x + BOX_WIDTH, BLUE_STACK_CENTER_POINT.y + BOX_HEIGHT);
    private final Rect otherBackdropRectBlue = new Rect(BLUE_BACKDROP_LEFT_POINT.x, BLUE_BACKDROP_LEFT_POINT.y,
            BLUE_BACKDROP_LEFT_POINT.x + BOX_WIDTH, BLUE_BACKDROP_LEFT_POINT.y + BOX_HEIGHT);
    private final Rect otherStackRectBlue = new Rect(BLUE_STACK_RIGHT_POINT.x, BLUE_STACK_RIGHT_POINT.y,
            BLUE_STACK_RIGHT_POINT.x + BOX_WIDTH, BLUE_STACK_RIGHT_POINT.y + BOX_HEIGHT);

    private final Rect [] centerRectRED = { centerBackdropRectRed, centerStackRectRed };
    private final Rect [] otherRectRED = { otherBackdropRectRed, otherStackRect };

    private final Rect [] centerRectBlue = { centerBackdropRectBlue, centerStackRectBlue };
    private final Rect [] otherRectBlue = { otherBackdropRectBlue, otherStackRectBlue };

    public void initCommon() {
        robot = new RobotHardware(hardwareMap);
        controller = new RobotController(robot);
        controller.run();
        robot.tick(telemetry);
        scheduler.runCommand(startCommand);
        scheduler.runCommand(initCommand);
    }

    public void initRedWebcam() {

    }
    public void initBlueWebcam() {

    }


    public MarkerPos getMarkerPosRed(boolean backdrop) throws InterruptedException {
        switch (Simulator.markerPos) {
            case 0:
                return MarkerPos.LEFT;
            case 1:
                return MarkerPos.CENTER;
            default:
                return MarkerPos.RIGHT;
        }
    }

    public MarkerPos getMarkerPosBlue(boolean backdrop) throws InterruptedException {
        switch (Simulator.markerPos) {
            case 0:
                return MarkerPos.LEFT;
            case 1:
                return MarkerPos.CENTER;
            default:
                return MarkerPos.RIGHT;
        }
    }

    public Command initCommand = new LambdaCommand("init", ()->true);
    public Command startCommand = new LambdaCommand("start",()-> opModeIsActive() && !opModeInInit() && initCommand.deepChildrenHaveFinished());


    public Command moveBezier(String descriptor, XyhVector... path) {
        return moveBezier(descriptor,false,path);
    }
    public Command moveBezier(String descriptor, boolean continuing, XyhVector... path) {
        return new BezierMove(robot,new Path(path),speedCurve,continuing).getCommand(descriptor,thresh,velThresh);
    }

    public Command waitCommand(double ms) {
        return new Command() {
            ElapsedTime timer = new ElapsedTime();

            @Override
            public void init() {
                timer.reset();
            }

            @Override
            public boolean run() {
                return timer.milliseconds()>ms;
            }

            @Override
            public String getDescriptor() {
                return "wait";
            }
        };
    }

    public Command stall() {
        return new LambdaCommand("stall",()->false);
    }

    public void tick() throws InterruptedException {
        scheduler.run();
        controller.run();
        robot.tick(telemetry);
    }

    public void setStartingPos(XyhVector startPos) {
        robot.pos.set(startPos);
        Simulator.setStartingPos(startPos.x,startPos.y,startPos.h);
        telemetry.update();
    }
}
