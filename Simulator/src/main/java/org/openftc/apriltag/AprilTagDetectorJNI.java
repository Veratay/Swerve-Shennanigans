//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openftc.apriltag;

import java.util.ArrayList;
import org.opencv.core.Mat;

public class AprilTagDetectorJNI {
    public AprilTagDetectorJNI() {
    }

    public static native long createApriltagDetector(String var0, float var1, int var2);

    public static native void setApriltagDetectorDecimation(long var0, float var2);

    public static native long runApriltagDetector(long var0, long var2, int var4, int var5);

    public static ArrayList<AprilTagDetection> runAprilTagDetectorSimple(long ptrDetector, Mat grey, double tagSize, double fx, double fy, double cx, double cy) {
        ArrayList<AprilTagDetection> detections = new ArrayList();
        long ptrDetectionArray = runApriltagDetector(ptrDetector, grey.dataAddr(), grey.width(), grey.height());
        if (ptrDetectionArray != 0L) {
            detections = ApriltagDetectionJNI.getDetections(ptrDetectionArray, tagSize, fx, fy, cx, cy);
            ApriltagDetectionJNI.freeDetectionList(ptrDetectionArray);
        }

        return detections;
    }

    public static native void releaseApriltagDetector(long var0);

    public static enum TagFamily {
        TAG_36h11("tag36h11"),
        TAG_25h9("tag25h9"),
        TAG_16h5("tag16h5"),
        TAG_standard41h12("tagStandard41h12");

        public final String string;

        private TagFamily(String string) {
            this.string = string;
        }
    }
}
