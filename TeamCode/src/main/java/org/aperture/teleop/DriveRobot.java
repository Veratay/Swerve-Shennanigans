package org.aperture.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.aperture.common.InputMapper;
import org.aperture.common.OpModeCommon;
import org.aperture.common.command.Command;
import org.aperture.common.command.LambdaCommand;
import org.aperture.control.RobotController;

import java.util.concurrent.atomic.AtomicBoolean;

@TeleOp
public class DriveRobot extends OpModeCommon {
    private final static double EXPIRE_TIME = 300; //ms
    private final static double MAX_RUN_TIME = 5000; //ms

    private final static double INTAKE_FULL_EXTEND = 1000;
    private final static double INTAKE_MED_EXTEND = 500;
    private final static double INTAKE_SHORT_EXTEND = 250;

    private final static double[] DEPO_HEIGHTS = new double[] {
            10,
            50,
            100,
            150,
            200,
            250,
            300,
            350,
            400,
            450,
            500,
            550,
            600,
            750,
            800,
            850,
    };
    @Override
    public void runOpMode() throws InterruptedException {
        InputMapper inputMapper = new InputMapper(gamepad1,gamepad2,0.5);

        int whichDepo = 0;
        int whichWrist = 0;

        initCommon();

        initCommand.then(controller.setArm(RobotController.REST_SHOULDER_POS,RobotController.REST_ELBOW_POS));

        while(opModeInInit()) {
            super.tick();
        }

        AtomicBoolean transferring = new AtomicBoolean(false);

        while (opModeIsActive()) {
            inputMapper.read();
            if (inputMapper.isPressed(InputMapper.Actions.INTAKE))
                scheduler.runCommand(controller.setActiveIntakePower(RobotController.ACTIVE_INTAKE_POWER));
            else if (inputMapper.isPressed(InputMapper.Actions.OUTTAKE))
                scheduler.runCommand(controller.setActiveIntakePower(RobotController.OUTTAKE_POWER));
            else if(inputMapper.justReleased(InputMapper.Actions.OUTTAKE) || inputMapper.justReleased(InputMapper.Actions.INTAKE))
                scheduler.runCommand(controller.setActiveIntakePower(0.0));

            if (!transferring.get()) {
                if (inputMapper.justPressed(InputMapper.Actions.TRANSFER)) {
                    transferring.set(true);
                    scheduler.runCommand(Command.box(
                            controller.transfer(true)
                                    .then("turnTransferOff",()-> transferring.set(false))
                    ));
                }
                if (inputMapper.justPressed(InputMapper.Actions.DOOR_TOGGLE)) {
                    scheduler.runCommand(controller.setDoor(!controller.state.targetDoorClosed));
                }

                if (inputMapper.justPressed(InputMapper.Actions.DEPO_FULL_DOWN))
                    whichDepo = 0;
                if (inputMapper.justPressed(InputMapper.Actions.DEPO_DOWN))
                    whichDepo = Math.max(0, whichDepo - 1);
                if (inputMapper.justPressed(InputMapper.Actions.DEPO_FULL_UP))
                    whichDepo = DEPO_HEIGHTS.length - 1;
                if (inputMapper.justPressed(InputMapper.Actions.DEPO_UP))
                    whichDepo = Math.min(DEPO_HEIGHTS.length - 1, whichDepo + 1);

                if(
                        inputMapper.justPressed(InputMapper.Actions.DEPO_DOWN) ||
                        inputMapper.justPressed(InputMapper.Actions.DEPO_FULL_DOWN) ||
                        inputMapper.justPressed(InputMapper.Actions.DEPO_FULL_UP) ||
                        inputMapper.justPressed(InputMapper.Actions.DEPO_UP)
                ) {
                    whichWrist=0;
                    scheduler.runCommand(controller.depo(DEPO_HEIGHTS[whichDepo], RobotController.DEPO_SHOULDER_POS, RobotController.DEPO_ELBOW_POS));
                }

                if (inputMapper.justPressed(InputMapper.Actions.LET_GO_OR_PICKUP))
                    scheduler.runCommand(controller.drop());

                if (inputMapper.justPressed(InputMapper.Actions.INTAKE_SHORT_EXTEND))
                    scheduler.runCommand(controller.extendIntake(INTAKE_SHORT_EXTEND));
                if (inputMapper.justPressed(InputMapper.Actions.INTAKE_MED_EXTEND))
                    scheduler.runCommand(controller.extendIntake(INTAKE_MED_EXTEND));
                if (inputMapper.justPressed(InputMapper.Actions.INTAKE_FULL_EXTEND))
                    scheduler.runCommand(controller.extendIntake(INTAKE_FULL_EXTEND));

                if(
                        inputMapper.justPressed(InputMapper.Actions.INTAKE_SHORT_EXTEND) ||
                                inputMapper.justPressed(InputMapper.Actions.INTAKE_MED_EXTEND) ||
                                inputMapper.justPressed(InputMapper.Actions.INTAKE_FULL_EXTEND)
                ) {
                    scheduler.runCommand(Command.box(
                            new LambdaCommand("waitDoorSoNoDepoPop",()->controller.intakeInSafeDist())
                                    .then(controller.setDoor(true))
                                    .then(controller.setActiveIntakePower(RobotController.ACTIVE_INTAKE_POWER))
                    ));
                } else if(inputMapper.justPressed(InputMapper.Actions.RETRACT_INTAKE)) {
                   scheduler.runCommand(controller.retractIntake());
                }

                if(inputMapper.justPressed(InputMapper.Actions.WRIST_CLOCKWISE)) whichWrist+=1;
                if(inputMapper.justPressed(InputMapper.Actions.WRIST_COUNTERCLOCKWISE)) whichWrist-=1;

                if(whichWrist<-2) whichWrist=-2;
                if(whichWrist>2) whichWrist=2;

                if(inputMapper.justPressed(InputMapper.Actions.WRIST_CLOCKWISE)
                        || inputMapper.justPressed(InputMapper.Actions.WRIST_COUNTERCLOCKWISE)) {
                    int wristPos = 0;
                    if(whichWrist==-1) {
                        wristPos = 2;
                    } else if(whichWrist==1) {
                        wristPos = 1;
                    } else if(whichWrist!=0) {
                        wristPos = 4;
                    }
                    scheduler.runCommand(controller.setWristPos(wristPos));
                }
            }
            telemetry.addData("intakeSlidesPower",robot.intakeSlides.getPower());
            telemetry.addData("backwardsVel",-robot.robotVel.x);
            super.tick();
            robot.startMove(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, 1.0);
        }
    }
}
