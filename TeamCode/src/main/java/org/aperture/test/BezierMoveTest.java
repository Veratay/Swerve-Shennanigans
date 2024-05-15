package org.aperture.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aperture.control.movement.BezierMove;
import org.aperture.common.path.Followable;
import org.aperture.common.path.Path;
import org.aperture.common.path.TrapezoidCurve;
import org.aperture.hardware.RobotHardware;
import org.aperture.common.coordinates.XyhVector;

@TeleOp
public class BezierMoveTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        RobotHardware robot = new RobotHardware(hardwareMap);
        robot.pos.set(0,0,0);
        Followable path = new Path(new XyhVector[] {
                new XyhVector(),
                new XyhVector(50,0,0)
        });

        TrapezoidCurve trapezoidCurve = new TrapezoidCurve(0.2,0.2,0.5,0.2,0.2);

        BezierMove move = new BezierMove(robot,path,trapezoidCurve,false);

        waitForStart();

        while (opModeIsActive()) {
            move.run();
            robot.tick(telemetry);
        }
    }
}
