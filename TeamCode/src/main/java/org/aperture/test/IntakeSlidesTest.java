//package org.aperture.test;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.aperture.control.PIDController;
//import org.aperture.hardware.RobotHardware;
//
//@TeleOp
//@Config
//public class IntakeSlidesTest extends LinearOpMode {
//
//    public static double P = 0.005;
//    public static double I = 0;
//    public static double D = 0.00025;
//    @Override
//    public void runOpMode() throws InterruptedException {
//        RobotHardware robot = new RobotHardware(hardwareMap);
//
//        PIDController depoSlidePID = new PIDController(P,I,D);
//        //depo max 675
//
//        double targetPos = 0;
//        robot.depoSlides.resetEncoder();
//        robot.wristServo.setPos(0.595);
//        waitForStart();
//
//        while (opModeIsActive()) {
//            depoSlidePID.P = P;
//            depoSlidePID.I = I;
//            depoSlidePID.D = D;
//            if(robot.depoTouch.isPressed()) robot.depoSlides.resetEncoder();
//            if(gamepad1.a) targetPos = 400;
//            telemetry.addData("D: ", ((targetPos-robot.depoSlides.pos-depoSlidePID.oldErr)/RobotHardware.dt)*D);
//
//            double power = depoSlidePID.run(targetPos-robot.depoSlides.pos );
//            if(power <0 ) power *= DOWN_MUL;
//            robot.depoSlides.setPower(power);
//
//            targetPos = Math.max(0, targetPos-gamepad1.left_stick_y*5);
//            telemetry.addData("depoPos",robot.depoSlides.pos);
//            telemetry.addData("depoPower",robot.depoSlides.getPower());
//            telemetry.addData("targetPos",targetPos);
//            telemetry.addData("P: ", P*targetPos-robot.depoSlides.pos);
//
//            robot.tick(telemetry);
//        }
//    }
//}
