package org.aperture.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.aperture.common.OpModeCommon;
import org.aperture.common.command.Command;
import org.aperture.common.command.CompositeCommand;
import org.aperture.common.command.ConditionalBranches;
import org.aperture.common.command.LambdaCommand;
import org.aperture.common.coordinates.XyhVector;
import org.aperture.control.RobotController;

import java.util.Arrays;

@Autonomous
public class BasicAutoBlueClose extends OpModeCommon {

    //-89 -58 -7

    double width = 38;
    double length = 46;
    XyhVector startPos = new XyhVector(-180+width/2.0,60-length/2.0,Math.toRadians(-90));
    XyhVector preloadDepoRight = new XyhVector(startPos.x+88,startPos.y+92,Math.toRadians(-90));
    XyhVector preloadDepoCenter = new XyhVector(startPos.x+73,startPos.y+92,Math.toRadians(-75));
    XyhVector preloadDepoLeft = new XyhVector(startPos.x+59,  startPos.y+90, Math.toRadians(-83));
    XyhVector preloadRightPush = new XyhVector(preloadDepoRight.x,preloadDepoRight.y-26, Math.toRadians(-90));
    XyhVector parkPos = new XyhVector(-150,120,Math.toRadians(-90));

    private final static double SLIDES_DEPO_POS = 50;

    private final static double INITIAL_INTAKE_EXTENSION_POS = 400;
    private final static double INTAKE_EXTENSION_POS = 1000;

    @Override
    public void runOpMode() throws InterruptedException {

        initCommon();

        controller.state.shoulderPos = RobotController.AUTO_INIT_SHOULDER_POS;
        controller.state.elbowPos = RobotController.AUTO_INIT_ELBOW_POS;
        controller.state.clawPos = RobotController.ClawPos.CLOSED_1_PIXEL;
        controller.state.armOutOfWay = true;
        setStartingPos(startPos);

        super.tick();

        initBlueWebcam();
        MarkerPos markerPos = getMarkerPosBlue(true);
        System.out.println("MARKER POS: " + markerPos.toString());

        initCommand
                .then(controller.setClawPos(RobotController.ClawPos.CLOSED_1_PIXEL))
                .then(controller.setArm( RobotController.AUTO_INIT_SHOULDER_POS, RobotController.AUTO_INIT_ELBOW_POS))
        ;

        XyhVector preloadDepoPos = null;
        double intakeExtension = 0;
        switch (markerPos) {
            case RIGHT:
                preloadDepoPos=preloadDepoRight;
                intakeExtension = 850;
                break;
            case CENTER:
                preloadDepoPos=preloadDepoCenter;
                intakeExtension = INITIAL_INTAKE_EXTENSION_POS+500;
                break;
            case LEFT:
                preloadDepoPos=preloadDepoLeft;
                intakeExtension = INITIAL_INTAKE_EXTENSION_POS+180;
                break;

        }
        Command specialRightMove = new LambdaCommand("specialRightMove",()->true);
        specialRightMove
                .then(moveBezier("rightMove",true,preloadDepoRight, preloadRightPush))
                .branch(controller.drop())
                .branch(controller.retractIntake())
                .then(moveBezier("parkMove", preloadRightPush, parkPos));

        startCommand
                .then(new CompositeCommand("depositPreload",
                        controller.depo(SLIDES_DEPO_POS,0.75,0.53),
                        Command.box(moveBezier("initialMove",startPos, preloadDepoPos)
                                        .then(new CompositeCommand("asd",
                                                Command.box(
                                                        waitCommand(200)
                                                        .then(controller.setClawPos(RobotController.ClawPos.OPEN))
                                                                .then(waitCommand(200))
                                                            .then(controller.setArm(RobotController.AUTO_INIT_SHOULDER_POS,RobotController.AUTO_INIT_ELBOW_POS))
                                                ),
                                                Command.box(
                                                        controller.extendIntakeAtLeast(INITIAL_INTAKE_EXTENSION_POS)
                                                                .then(controller.setActiveIntakePower(0.2))
                                                                .then(waitCommand(1500))
                                                                .then(controller.setActiveIntakePower(0))
                                                                .then(controller.extendIntakeAtLeast(intakeExtension))
                                                )
                                        ))
                                .then(new ConditionalBranches("rightSpecialCondition", Arrays.asList(
                                        ()->markerPos==MarkerPos.RIGHT,
                                        ()->true
                                ),Arrays.asList(
                                        specialRightMove,
                                        null
                                ),false))
                        )
                ))
                .branch(controller.drop())
                .branch(controller.retractIntake())
                .then(moveBezier("parkMove", preloadDepoPos, parkPos))
        ;

        while (opModeInInit()) {
            super.tick();
        }

        waitForStart();

        while (opModeIsActive() && !startCommand.deepChildrenHaveFinished()) {
            super.tick();
        }
    }
}
