package org.aperture.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aperture.hardware.RobotHardware;
import org.aperture.common.coordinates.XyhVector;

@TeleOp
public class OdometryTest extends LinearOpMode {
    public void runOpMode() {
        RobotHardware robot = new RobotHardware(hardwareMap);
        waitForStart();

        while (opModeIsActive()) {

            robot.startMove(-gamepad1.left_stick_y,-gamepad1.left_stick_x,-gamepad1.right_stick_x , 1.0);

            telemetry.addData("powers", "f: " + -gamepad1.left_stick_y + "s:" + -gamepad1.left_stick_x + "t: " + -gamepad1.right_stick_x);

            robot.intakeSlides.setPower(gamepad1.dpad_down ? -1.0 : gamepad1.dpad_up ? 1.0 : 0.0);

            robot.wristServo.setPos(0.6);
            robot.tick(telemetry);
        }
    }
}

