package org.aperture.utility;

/**
 * @author FTC Team #5064 Aperture Science
 * 2023-2024 Season, CenterStage
 *
 * Version 1.0
 */

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.aperture.hardware.CachedServo;
import org.aperture.hardware.DualServo;
import org.aperture.hardware.RobotHardware;

@TeleOp(name="Servo Cycle Dual", group="teleop")
//@Disabled
public class ServoCycleDual extends LinearOpMode {

    private RobotHardware robot;
    private boolean prevGamepad = false;
    private boolean prevBumper = false;
    private int which = 0;

    @Override
    public void runOpMode() {

        robot = new RobotHardware(hardwareMap);;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        DualServo[] servos = { robot.shoulderServo };
        String[] names = { "shoulder" };
        Double[] positions = { 0.5 };

        waitForStart();

        while (opModeIsActive()) {
            // servo mod
            if (gamepad1.a && !prevGamepad) positions[which] += .05;
            if (gamepad1.b && !prevGamepad) positions[which] -= .05;
            if (gamepad1.x && !prevGamepad) positions[which] += .005;
            if (gamepad1.y && !prevGamepad) positions[which] -= .005;
            prevGamepad = gamepad1.a || gamepad1.b || gamepad1.x || gamepad1.y;

            if (gamepad1.left_bumper && !prevBumper) which--;
            if (which < 0) which = servos.length - 1;
            if (gamepad1.right_bumper && !prevBumper) which++;
            if (which > servos.length - 1) which = 0;
            prevBumper = gamepad1.left_bumper || gamepad1.right_bumper;

            // modify range
            positions[which] = Range.clip(positions[which], 0, 1);

            // set position
            servos[which].setPos(positions[which]);
            servos[which].write();

            // add telemetry
            telemetry.addLine(names[which]);
            String strPos = String.format("%.3f", positions[which]);
            telemetry.addData("Position", strPos);
            telemetry.update();
        }
    }
}