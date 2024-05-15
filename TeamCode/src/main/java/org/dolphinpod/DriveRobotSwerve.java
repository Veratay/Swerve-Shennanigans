package org.dolphinpod;

import aperture.simulator.Simulator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class DriveRobotSwerve extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor SL1 = (DcMotor) hardwareMap.get("SL1");
        DcMotor SL2 = (DcMotor) hardwareMap.get("SL2");
        DcMotor SR1 = (DcMotor) hardwareMap.get("SR1");
        DcMotor SR2 = (DcMotor) hardwareMap.get("SR2");

        DcMotor FR = (DcMotor) hardwareMap.get("motorFrontRight");
        DcMotor FL = (DcMotor) hardwareMap.get("motorFrontLeft");
        DcMotor BR = (DcMotor) hardwareMap.get("motorBackRight");
        DcMotor BL = (DcMotor) hardwareMap.get("motorBackLeft");



        waitForStart();

        boolean wasAPressed = false;
        boolean wasBPressed = false;
        while (opModeIsActive()) {

            Simulator.SWERVE = false;
            SL1.setPower(-gamepad1.left_stick_y);
            SL2.setPower(-gamepad1.left_stick_y);
            SR1.setPower(-gamepad1.left_stick_y);
            SR2.setPower(-gamepad1.left_stick_y);
            FL.setPower(-gamepad1.left_stick_y);
            FR.setPower(-gamepad1.left_stick_y);
            BR.setPower(-gamepad1.left_stick_y);
            BL.setPower(-gamepad1.left_stick_y);


            if(gamepad1.a && !wasAPressed) {
                Simulator.SWERVE = !Simulator.SWERVE;
            }
            wasAPressed = gamepad1.a;

            if(gamepad1.b && !wasBPressed) {
                Simulator.FREEZE = !Simulator.FREEZE;
            }
            wasBPressed = gamepad1.b;

            telemetry.update();
        }
    }
}
