//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision.apriltag;

import org.opencv.core.Point;

public class AprilTagDetection {
    public int id;
    public int hamming;
    public float decisionMargin;
    public Point center;
    public Point[] corners;
    public AprilTagMetadata metadata;
    public AprilTagPoseFtc ftcPose;
    public AprilTagPoseRaw rawPose;
    public long frameAcquisitionNanoTime;

    public AprilTagDetection() {
    }
}
