package aperture.simulator;

import aperture.simulator.math.DrivebaseModel;
import aperture.simulator.math.RobotModel;
import org.joml.Vector3f;

import static aperture.simulator.Simulator.positions;
import static aperture.simulator.Simulator.robotModel;
public class RobotRenderer {
    Renderer.LoadedObj mecanumWheel;
    //things to render
    /*
    drivepodleft
    drivepodright
    drivepodcenter
    wheels
    slides
    arm
    elbow
    gripper
    pixels at the end
    intake slides
     */

    private final static float drivePodWidth = 3;
    private final static float drivePodOffset = 40;
    private final static float drivePodHeight = 10;
    public final static float drivePodLength = 45;
    private final static Vector3f drivePodLeftColor = new Vector3f(0f,0f,1f);
    private final static Vector3f drivePodRightColor = new Vector3f(1f,0f,0f);

    private final static float intakeHeight = 10.0f;

    private final static Vector3f intakeColor = new Vector3f(1f,0f,1f);

    private final static float doorOffset = 5.0f;
    private final static Vector3f doorColor = new Vector3f(1f,0f,0f);
    private final static float activeIntakeOffset = 3.0f;
    private final static Vector3f activeIntakeColor = new Vector3f(0f,1f,0f);

    private final static float depoBaseSize = 40;
    private final static float depoLowXPos = -drivePodLength/2 + 10;
    private final static float depoLowYPos = 2;
    private final static float depoAngle = (float) Math.toRadians(35);
    private final static Vector3f depoColor = new Vector3f(0f,1f,0f);

    private final static float armLength = 20;
    private final static float elbowLength = 10;
    private final static Vector3f armColor = new Vector3f(1f,0.5f,0f);
    public static void render() {
        Vector3f robotPos = new Vector3f((float) -positions.x, 0,(float) positions.y);
        Vector3f leftPodPos = new Vector3f(0,0,drivePodOffset/2).rotateY((float)positions.h).add(robotPos);
        Vector3f rightPodPos = new Vector3f(0,0,-drivePodOffset/2).rotateY((float)positions.h).add(robotPos);
        Vector3f podSize = new Vector3f(drivePodLength/2.0f,drivePodHeight, drivePodWidth/2.0f);

        float intakeLen = (float) (positions.intakePos/RobotModel.INTAKE_MAX*RobotModel.INTAKE_MAX_DIST);
        Vector3f leftIntakePos = new Vector3f(-(drivePodLength/2 + intakeLen/2),intakeHeight,drivePodOffset/2).rotateY((float)positions.h).add(robotPos);
        Vector3f rightIntakePos = new Vector3f(-(drivePodLength/2 + intakeLen/2),intakeHeight,-drivePodOffset/2).rotateY((float)positions.h).add(robotPos);
        Vector3f intakeSize = new Vector3f(intakeLen/2,2f,2f);

        Vector3f doorPos = new Vector3f(-(drivePodLength/2 + intakeLen - doorOffset),intakeHeight/2,0).rotateY((float)positions.h).add(robotPos);
        Vector3f doorSize = new Vector3f(1f,intakeHeight/2,drivePodOffset/2);

        Vector3f activePos = new Vector3f(-(drivePodLength/2 + intakeLen + activeIntakeOffset),intakeHeight/2,0).rotateY((float)positions.h).add(robotPos);
        Vector3f activeSize = new Vector3f(1f,intakeHeight/2,drivePodOffset/2);

        float depoLen = (float) (positions.depoPos/RobotModel.DEPO_MAX*RobotModel.DEPO_MAX_DIST + depoBaseSize);
        Vector3f depoLeftPos = new Vector3f(depoLen/2,0,0).rotateZ(depoAngle).add(depoLowXPos,depoLowYPos,drivePodOffset/2).rotateY((float) positions.h).add(robotPos);
        Vector3f depoRightPos = new Vector3f(depoLen/2,0,0).rotateZ(depoAngle).add(depoLowXPos,depoLowYPos,-drivePodOffset/2).rotateY((float) positions.h).add(robotPos);
        Vector3f depoSize = new Vector3f(depoLen/2,2f,2f);

        //0.26 = 180 0.75 = 30
        float armRad = (float) Math.toRadians(180-((1-positions.armPos)-0.26)/0.49*150);
        //0.65 = 270 0.35 = -30
        float elbowRad = (float) Math.toRadians(-30+(positions.elbowPos-0.35)*1000);

        Vector3f armStartPos = new Vector3f(depoLen-depoBaseSize/2,0,0).rotateZ(depoAngle).add(depoLowXPos,depoLowYPos,0);
        Vector3f elbowStartPos = new Vector3f(armStartPos).add((float)Math.cos(armRad)*armLength,(float)Math.sin(armRad)*armLength,0);
        Vector3f clawPos = new Vector3f(elbowStartPos).add((float)Math.cos(elbowRad)*elbowLength,(float)Math.sin(elbowRad)*elbowLength,0);

        Vector3f armPos = new Vector3f(armStartPos).add(elbowStartPos).div(2).rotateY((float) positions.h).add(robotPos);
        Vector3f elbowPos = new Vector3f(elbowStartPos).add(clawPos).div(2).rotateY((float) positions.h).add(robotPos);
        Vector3f armSize = new Vector3f(armLength/2,2,2);
        Vector3f elbowSize = new Vector3f(elbowLength/2,2,2);

        leftPodPos.mul(0.393701f);
        rightPodPos.mul(0.393701f);
        podSize.mul(0.393701f);

        leftIntakePos.mul(0.393701f);
        rightIntakePos.mul(0.393701f);
        intakeSize.mul(0.393701f);

        doorPos.mul(0.393701f);
        doorSize.mul(0.393701f);

        activePos.mul(0.393701f);
        activeSize.mul(0.393701f);

        depoLeftPos.mul(0.393701f);
        depoRightPos.mul(0.393701f);
        depoSize.mul(0.393701f);

        armPos.mul(0.393701f);
        armSize.mul(0.393701f);
        elbowPos.mul(0.393701f);
        elbowSize.mul(0.393701f);


        Renderer.renderBox(leftPodPos,new Vector3f(0,(float)positions.h,0),podSize,drivePodLeftColor);
        Renderer.renderBox(rightPodPos,new Vector3f(0,(float)positions.h,0),podSize,drivePodRightColor);

        Renderer.renderBox(leftIntakePos,new Vector3f(0,(float)positions.h,0),intakeSize,intakeColor);
        Renderer.renderBox(rightIntakePos,new Vector3f(0,(float)positions.h,0),intakeSize,intakeColor);

        if(positions.doorClosed) Renderer.renderBox(doorPos,new Vector3f(0,(float)positions.h,0),doorSize,doorColor);
        if(positions.intakeActive) Renderer.renderBox(activePos,new Vector3f(0,(float)positions.h,0),activeSize,activeIntakeColor);

        Renderer.renderBox(depoLeftPos,new Vector3f(0,(float) positions.h,depoAngle),depoSize,depoColor);
        Renderer.renderBox(depoRightPos,new Vector3f(0,(float) positions.h,depoAngle),depoSize,depoColor);

        Renderer.renderBox(armPos,new Vector3f(0,(float) positions.h,armRad),armSize,armColor);
        Renderer.renderBox(elbowPos,new Vector3f(0,(float) positions.h,elbowRad),elbowSize,armColor);
    }

    private float toInch(double cm) {
        return (float) ((float)cm*0.393701);
    }
}
