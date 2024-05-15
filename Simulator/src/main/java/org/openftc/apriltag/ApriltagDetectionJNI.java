//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openftc.apriltag;

import java.util.ArrayList;
import org.firstinspires.ftc.robotcore.external.matrices.GeneralMatrixF;
import org.opencv.core.Point;

public class ApriltagDetectionJNI {
    public ApriltagDetectionJNI() {
    }

    public static native int getId(long var0);

    public static native int getHamming(long var0);

    public static native float getDecisionMargin(long var0);

    public static native double[] getCenterpoint(long var0);

    public static native double[][] getCorners(long var0);

    public static native double[] getPoseEstimate(long var0, double var2, double var4, double var6, double var8, double var10);

    public static native long[] getDetectionPointers(long var0);

    public static native void freeDetectionList(long var0);

    public static ArrayList<AprilTagDetection> getDetections(long ptrDetections, double tagSize, double fx, double fy, double cx, double cy) {
        long[] detectionPointers = getDetectionPointers(ptrDetections);
        ArrayList<AprilTagDetection> detections = new ArrayList(detectionPointers.length);
        long[] var14 = detectionPointers;
        int var15 = detectionPointers.length;

        for(int var16 = 0; var16 < var15; ++var16) {
            long ptrDetection = var14[var16];
            AprilTagDetection detection = new AprilTagDetection();
            detection.id = getId(ptrDetection);
            detection.hamming = getHamming(ptrDetection);
            detection.decisionMargin = getDecisionMargin(ptrDetection);
            double[] center = getCenterpoint(ptrDetection);
            detection.center = new Point(center[0], center[1]);
            double[][] corners = getCorners(ptrDetection);
            detection.corners = new Point[4];

            for(int p = 0; p < 4; ++p) {
                detection.corners[p] = new Point(corners[p][0], corners[p][1]);
            }

            detection.pose = new AprilTagPose();
            double[] pose = getPoseEstimate(ptrDetection, tagSize, fx, fy, cx, cy);
            detection.pose.x = pose[0];
            detection.pose.y = pose[1];
            detection.pose.z = pose[2];
            float[] rotMtxVals = new float[9];

            for(int i = 0; i < 9; ++i) {
                rotMtxVals[i] = (float)pose[3 + i];
            }

            detection.pose.R = new GeneralMatrixF(3, 3, rotMtxVals);
            detections.add(detection);
        }

        return detections;
    }

    static {
        System.loadLibrary("apriltag");
    }
}
