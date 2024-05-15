package org.aperture.utility;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aperture.hardware.RobotHardware;

@TeleOp
public class OuttakeTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RobotHardware robot = new RobotHardware(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            robot.intakeMotor.setPower(0.3);
            //robot.intakeMotor.setPower(-1.0);
            robot.tick(telemetry);
        }

    }
}
