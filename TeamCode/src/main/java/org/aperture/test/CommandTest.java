//package org.aperture.test;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.aperture.common.InputMapper;
//import org.aperture.control.PixelControllerCommand;
//import org.aperture.hardware.RobotHardware;
//
//@TeleOp
//public class CommandTest extends LinearOpMode {
//    double EXPIRE_TIME = 300;
//    double CANCEL_TIME = 0; //dont cancel.
//    @Override
//    public void runOpMode() throws InterruptedException {
//        RobotHardware robot = new RobotHardware(hardwareMap);
//        PixelControllerCommand pixelControllerCommand = new PixelControllerCommand(robot);
//        InputMapper inputMapper = new InputMapper(gamepad1,gamepad2,0);
//
//        waitForStart();
//
//        while (opModeIsActive()) {
//            if(inputMapper.justPressed(InputMapper.Actions.EXTEND)) pixelControllerCommand.requestExtension(EXPIRE_TIME,CANCEL_TIME);
//            if(inputMapper.justPressed(InputMapper.Actions.RETRACT)) pixelControllerCommand.requestRetraction(EXPIRE_TIME,CANCEL_TIME);
//            if(inputMapper.justPressed(InputMapper.Actions.DEPO)) pixelControllerCommand.requestDepo(600,0.75,0.35,0,EXPIRE_TIME,CANCEL_TIME);
//            if(inputMapper.justPressed(InputMapper.Actions.DROP)) pixelControllerCommand.requestDrop(EXPIRE_TIME,CANCEL_TIME);
//            if(inputMapper.justPressed(InputMapper.Actions.TOGGLE_ACTIVE_INTAKE)) pixelControllerCommand.activeIntakeToggle = !pixelControllerCommand.activeIntakeToggle;
//            pixelControllerCommand.intakeExtensionDistChange(inputMapper.get(InputMapper.Actions.CHANGE_EXTENSION)*RobotHardware.dt*1000);
//
//            double y = -gamepad1.left_stick_x;
//            double x = -gamepad1.left_stick_y;
//            double z = -gamepad1.right_stick_x;
//
//            robot.startMove(x,y,z , 1.0);
//
//            pixelControllerCommand.run();
//            inputMapper.read();
//            telemetry.addData("depo slides power",robot.depoSlides.getPower());
//            telemetry.addData("depo slides ticks",robot.depoSlides.pos);
//            telemetry.addData("intake slides power",robot.intakeSlides.getPower());
//            telemetry.addData("intake slides pos", robot.intakeSlides.getPos());
//            robot.tick(telemetry);
//        }
//    }
//}