package org.aperture.control.movement;

import org.aperture.LogConfig;
import org.aperture.common.command.Command;
import org.aperture.control.PIDControllerXyh;
import org.aperture.common.path.Followable;
import org.aperture.common.path.TrapezoidCurve;
import org.aperture.common.coordinates.XyhVector;
import org.aperture.hardware.RobotHardware;

public class BezierMove {
    //PID
    private final PIDControllerXyh PID = new PIDControllerXyh(
            new XyhVector(0.1, 0.1, 1.0),
            new XyhVector(0.0, 0.0, 0.0),
            new XyhVector(0.0, 0.0, 0.0)
    );

    //Constants
    private static final double CHASE_POINT_OFFSET = 7; //cm
    private static final double BEZIER_MIN_VEL = 0.2;

    //Instance specific
    private final Followable path;
    private final RobotHardware robot;
    private final boolean continuing;
    private final TrapezoidCurve speedCurve;

    //working variables
    private double lastT = 0;
    private double maxRecordedPower = 0;

    public BezierMove(RobotHardware robot, Followable path, TrapezoidCurve speedCurve, boolean continuing) {
        this.path = path;
        this.robot = robot;
        this.totalDist = path.totalLength();
        this.continuing = continuing;
        this.speedCurve = speedCurve;
    }

    private double curDist = 0;
    public double getCurDist() {
        return curDist;
    }
    private final double totalDist;
    public double getTotalDist() {
        return totalDist;
    }
    public XyhVector getEndPos() {
        return path.compute(1);
    }
    public XyhVector run() {

        // These variables are critical for Bezier Curves, as they allow it to increment as time passes
        double t = path.closestPoint(robot.pos);
        t = Math.max(lastT, t);
        lastT = t;
        double chaseT = path.lengthToT(path.length(t)+CHASE_POINT_OFFSET);
        chaseT = Math.min(chaseT, 1.0);
        XyhVector p = path.compute(chaseT);

        // Target for where things should be as the program is running.
        // These errors help the integrated P controller determine the values for the startMove method
        XyhVector err = new XyhVector();
        err.sub(p,robot.pos);

        XyhVector velocity = PID.run(err);
        curDist = path.length(t);

        double speed = speedCurve.compute(t);
        if(continuing) { speed = speedCurve.maxSpeed; }

        // Velocity on the field is translated into the robot velocities drive, strafe, turn:
        XyhVector power = new XyhVector();
        power.rotate(velocity, robot.pos.h);

        power.clip(BEZIER_MIN_VEL, speed);

        // Actual robot movement, fingers crossed that it'll work!
        robot.startMove(power.x, power.y, power.h, 1.0);

        // capture the max power levels for parameter optimization:
        maxRecordedPower = Math.max(maxRecordedPower, power.maxAbs());

        if(LogConfig.BEZIER_MOVE) {
            System.out.println("BEZIER DEBUG: ===========================================");
            System.out.println("BEZIER DEBUG: t = " + t);
            System.out.println("BEZIER DEBUG: chaseT = " + chaseT);
            System.out.println("BEZIER DEBUG: pos = " + robot.pos.toStringDeg());
            System.out.println("BEZIER DEBUG: targetPos = " + p.toStringDeg());
            System.out.println("BEZIER DEBUG: error = " + err.toStringDeg());
            System.out.println("BEZIER DEBUG: hErr = " + err.h);
            System.out.println("BEZIER DEBUG: velocity = " + velocity.toStringDeg());
            System.out.println("BEZIER DEBUG: speed = " + speed);
            System.out.println("BEZIER DEBUG: power = " + power.toStringDeg());
        }

        XyhVector endErr = new XyhVector(path.compute(1));
        endErr.sub(robot.pos);
        return endErr;
    }

    public Command getCommand(String descriptor, XyhVector thresh, XyhVector velThresh) {
        return new Command() {
            @Override
            public void init() {}

            @Override
            public boolean run() {
                boolean r = BezierMove.this.run().proximity(new XyhVector(),thresh)
                        && (robot.fieldVel.proximity(new XyhVector(),velThresh) || continuing);
                if(r) robot.startMove(0,0,0,1.0);
                return r;
            }

            @Override
            public String getDescriptor() {
                return descriptor;
            }
        };
    }
}
