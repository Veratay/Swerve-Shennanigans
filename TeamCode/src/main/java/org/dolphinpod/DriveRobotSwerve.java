package org.dolphinpod;

import aperture.simulator.Simulator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class DriveRobotSwerve extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor Wheel1Turn = (DcMotor) hardwareMap.get("Wheel1Turn");
        DcMotor Wheel2Turn = (DcMotor) hardwareMap.get("Wheel2Turn");
        DcMotor Wheel3Turn = (DcMotor) hardwareMap.get("Wheel3Turn");
        DcMotor Wheel4Turn = (DcMotor) hardwareMap.get("Wheel4Turn");

        DcMotor Wheel1Drive = (DcMotor) hardwareMap.get("Wheel1Drive");
        DcMotor Wheel2Drive = (DcMotor) hardwareMap.get("Wheel2Drive");
        DcMotor Wheel3Drive = (DcMotor) hardwareMap.get("Wheel3Drive");
        DcMotor Wheel4Drive = (DcMotor) hardwareMap.get("Wheel4Drive");

        waitForStart();

        boolean wasBPressed = false;
        while (opModeIsActive()) {

            if(gamepad1.b && !wasBPressed) {
                Simulator.FREEZE = !Simulator.FREEZE;
            }
            wasBPressed = gamepad1.b;

            telemetry.update();
        }
    }
}
