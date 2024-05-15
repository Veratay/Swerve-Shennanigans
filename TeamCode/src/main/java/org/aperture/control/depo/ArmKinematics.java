package org.aperture.control.depo;

import androidx.annotation.Nullable;

import org.aperture.common.coordinates.Vector3;
import org.aperture.common.coordinates.XyhVector;

public class ArmKinematics {
    //coordinate system:
    //(0,0) is the lowest position that the axle of the shoulder can be.
    //0 rad is +x

    //-----Constants-----
    //offset of bottom of slide from odometry center (negative)
    private static final double slideBottomX = 0;
    //offset from ground of slide bottom
    private static final double slideBottomY = 0;

    //length of the difference between the positions of the shoulder center of rotation from min of slide extension to max
    private static final double slideLength = 0;
    //angle between slides and ground
    private static final double slideTheta = 0;
    //distance from shoulder center of rotation to elbow c.o.r.
    private static final double humerusLength = 0;
    //distance from elbow c.o.r to end of claw
    private static final double forearmLength = 0;

    //because of rounding errors, maxX will sometimes result in a NaN when passed into inverse. this fixes that.
    private static final double mathErrCompensation = 0.01;

    public static class InverseResult {
        private final ArmPose solution1;
        private final ArmPose solution2;

        private InverseResult(ArmPose solution1, ArmPose solution2) {
            this.solution1 = solution1;
            this.solution2 = solution2;
        }

        @Nullable
        public ArmPose getPose(double curSlideT) {
            if(solution1==null && solution2==null) return null;
            if(solution1!=null && solution2==null) return solution1;
            if(solution1 == null) return solution2;
            if (Math.abs(solution1.slideT - curSlideT) < Math.abs(solution2.slideT - curSlideT)) return solution1; else return solution2;
        }
    }

    public static InverseResult inverse(XyhVector workPlanePos) {
        double x = workPlanePos.x;
        double y = workPlanePos.y;
        double h = workPlanePos.h;

        double s = Math.tan(slideTheta);
        double m = slideTheta - Math.PI/2.0;
        double a = Math.acos( Math.cos(slideTheta)*(forearmLength *( -s*Math.cos(h) + Math.sin(h) ) + x*s - y) / humerusLength );

        if(Double.isNaN(a)) {
            return new InverseResult(null,null);
        }

        double x0 = a + m;
        double y0 = h - x0;
        double z0 = (-x + humerusLength*Math.cos(x0) + forearmLength*Math.cos(h)) / (-slideLength*Math.cos(slideTheta));

        double x1 = -a + m;
        double y1 = h - x1;
        double z1 = (-x + humerusLength*Math.cos(x1) + forearmLength*Math.cos(h)) / (-slideLength*Math.cos(slideTheta));

        boolean z1Valid = z0 >= 0 && z0 <= 1;
        boolean z2Valid = z1 >= 0 && z1 <= 1;

        ArmPose solution1 = new ArmPose(x0,y0,z0);
        ArmPose solution2 = new ArmPose(x1,y1,z1);

        if(!z1Valid) solution1 = null;
        if(!z2Valid) solution2 = null;

        return new InverseResult(solution1,solution2);
    }

    public static double maxX(double z, double h) {
        double shoulder = Math.asin(Math.max( (z - forearmLength*Math.sin(h) - slideLength*Math.sin(slideTheta) )/humerusLength, -1));
        double a = ( (Math.min(1, Math.abs(Math.cos(shoulder-slideTheta - Math.PI/2.0))))*humerusLength )/Math.cos(slideTheta);
        return ( a + z - forearmLength*(-Math.tan(slideTheta)*Math.cos(h)+Math.sin(h)) ) / Math.tan(slideTheta) - mathErrCompensation;
    }

    public static Vector3 forward(ArmPose armPose, XyhVector pos) {
        //pose to work-plane coordinates
        double x = armPose.slideT*slideLength*Math.cos(slideTheta) + humerusLength*Math.cos(armPose.shoulderRad) + forearmLength*Math.cos(armPose.shoulderRad+armPose.elbowRad);
        double z = armPose.slideT*slideLength*Math.sin(slideTheta) + humerusLength*Math.sin(armPose.shoulderRad) + forearmLength*Math.sin(armPose.shoulderRad+armPose.elbowRad);

        //work-plane to robot coordinates
        x += slideBottomX;
        z += slideBottomY;

        //robot coordinates to field coordinates
        Vector3 res = new Vector3(x,0,z);
        res.rotateAroundVector(new Vector3(0,0,1),pos.h);
        res.add(new Vector3(pos.x,pos.y,0));
        return res;
    }

    public static XyhVector bestFeasibleTarget(XyhVector pos, Vector3 target) {
        Vector3 originalTarget = target.copy();

        //direction of possible x values on board
        Vector3 lineDirection = new Vector3(1,0,0);

        //origin of work plane in field coordinates
        Vector3 workPlaneOrigin = new Vector3(pos.x+Math.cos(pos.h)*slideBottomX, pos.y+Math.sin(pos.h)*slideBottomX, slideBottomY);
        target.sub(workPlaneOrigin);

        //now, target origin is work plane origin, so rotate so work plane is the xz plane
        target.rotateAroundVector(new Vector3(0,0,1), -pos.h);
        lineDirection.rotateAroundVector(new Vector3(0,0,1), -pos.h);

        Vector3 planeNormal = new Vector3(0,1,0);
        Vector3 pointOnPlane = new Vector3();

        //find where line of possible target x(field coordinate) intersects with work plane
        target.intersectionWithPlane(lineDirection,planeNormal,pointOnPlane);

        //debug
        assert(originalTarget.z==target.z);
        assert(target.y==0);

        double maxX = maxX(target.z,pos.h);

        return new XyhVector(Math.min(target.x,maxX),target.z,pos.h);
    }
}