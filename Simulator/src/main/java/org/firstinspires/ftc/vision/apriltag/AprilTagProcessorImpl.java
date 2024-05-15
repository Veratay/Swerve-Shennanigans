//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision.apriltag;

//import android.graphics.Canvas;
import android.graphics.Canvas;
import com.qualcomm.robotcore.util.MovingStatistics;
import java.util.ArrayList;
import java.util.Iterator;

import org.firstinspires.ftc.robotcore.external.matrices.GeneralMatrixF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.imgproc.Imgproc;
import org.openftc.apriltag.AprilTagDetectorJNI;
import org.openftc.apriltag.ApriltagDetectionJNI;

public class AprilTagProcessorImpl extends AprilTagProcessor {
    public static final String TAG = "AprilTagProcessorImpl";
    private long nativeApriltagPtr;
    private Mat grey = new Mat();
    private ArrayList<AprilTagDetection> detections = new ArrayList();
    private ArrayList<AprilTagDetection> detectionsUpdate = new ArrayList();
    private final Object detectionsUpdateSync = new Object();
    private boolean drawAxes;
    private boolean drawCube;
    private boolean drawOutline;
    private boolean drawTagID;
    private Mat cameraMatrix;
    private double fx;
    private double fy;
    private double cx;
    private double cy;
    private final AprilTagLibrary tagLibrary;
    private float decimation;
    private boolean needToSetDecimation;
    private final Object decimationSync = new Object();
//    private AprilTagCanvasAnnotator canvasAnnotator;
    private final DistanceUnit outputUnitsLength;
    private final AngleUnit outputUnitsAngle;
    private volatile PoseSolver poseSolver;
    private MovingStatistics solveTime;
    private final Object drawSync;
    private AprilTagCanvasAnnotator canvasAnnotator;

    public AprilTagProcessorImpl(double fx, double fy, double cx, double cy, DistanceUnit outputUnitsLength, AngleUnit outputUnitsAngle, AprilTagLibrary tagLibrary, boolean drawAxes, boolean drawCube, boolean drawOutline, boolean drawTagID, TagFamily tagFamily, int threads) {
        this.poseSolver = PoseSolver.OPENCV_ITERATIVE;
        this.solveTime = new MovingStatistics(50);
        this.drawSync = new Object();
        this.fx = fx;
        this.fy = fy;
        this.cx = cx;
        this.cy = cy;
        this.tagLibrary = tagLibrary;
        this.outputUnitsLength = outputUnitsLength;
        this.outputUnitsAngle = outputUnitsAngle;
        this.drawAxes = drawAxes;
        this.drawCube = drawCube;
        this.drawOutline = drawOutline;
        this.drawTagID = drawTagID;
        this.nativeApriltagPtr = AprilTagDetectorJNI.createApriltagDetector(tagFamily.ATLibTF.string, 3.0F, threads);
    }

    protected void finalize() {
        if (this.nativeApriltagPtr != 0L) {
            AprilTagDetectorJNI.releaseApriltagDetector(this.nativeApriltagPtr);
            this.nativeApriltagPtr = 0L;
        } else {
            System.out.println("AprilTagDetectionPipeline.finalize(): nativeApriltagPtr was NULL");
        }

    }

    public void init(int width, int height, CameraCalibration calibration) {
        if (calibration != null && this.fx == 0.0D && this.fy == 0.0D && this.cx == 0.0D && this.cy == 0.0D && (calibration.focalLengthX != 0.0F || calibration.focalLengthY != 0.0F || calibration.principalPointX != 0.0F || calibration.principalPointY != 0.0F)) {
            this.fx = (double)calibration.focalLengthX;
            this.fy = (double)calibration.focalLengthY;
            this.cx = (double)calibration.principalPointX;
            this.cy = (double)calibration.principalPointY;
//            Log.d("AprilTagProcessorImpl", String.format("User did not provide a camera calibration; but we DO have a built in calibration we can use.\n [%dx%d] (may be scaled) %s\nfx=%7.3f fy=%7.3f cx=%7.3f cy=%7.3f", calibration.getSize().getWidth(), calibration.getSize().getHeight(), calibration.getIdentity().toString(), this.fx, this.fy, this.cx, this.cy));
        } else if (this.fx == 0.0D && this.fy == 0.0D && this.cx == 0.0D && this.cy == 0.0D) {
            String warning = "User did not provide a camera calibration, nor was a built-in calibration found for this camera; 6DOF pose data will likely be inaccurate.";
//            Log.d("AprilTagProcessorImpl", warning);
//            RobotLog.addGlobalWarningMessage(warning);
            this.fx = 578.272D;
            this.fy = 578.272D;
            this.cx = (double)(width / 2);
            this.cy = (double)(height / 2);
        } else {
//            Log.d("AprilTagProcessorImpl", String.format("User provided their own camera calibration fx=%7.3f fy=%7.3f cx=%7.3f cy=%7.3f", this.fx, this.fy, this.cx, this.cy));
        }

        this.constructMatrix();
        this.canvasAnnotator = new AprilTagCanvasAnnotator(this.cameraMatrix);
    }

    public Object processFrame(Mat input, long captureTimeNanos) {
        Imgproc.cvtColor(input, this.grey, 11);
        synchronized(this.decimationSync) {
            if (this.needToSetDecimation) {
                AprilTagDetectorJNI.setApriltagDetectorDecimation(this.nativeApriltagPtr, this.decimation);
                this.needToSetDecimation = false;
            }
        }

        this.detections = this.runAprilTagDetectorForMultipleTagSizes(captureTimeNanos);
        synchronized(this.detectionsUpdateSync) {
            this.detectionsUpdate = this.detections;
        }

        return this.detections;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

    }

    ArrayList<AprilTagDetection> runAprilTagDetectorForMultipleTagSizes(long captureTimeNanos) {
        long ptrDetectionArray = AprilTagDetectorJNI.runApriltagDetector(this.nativeApriltagPtr, this.grey.dataAddr(), this.grey.width(), this.grey.height());
        if (ptrDetectionArray == 0L) {
            return new ArrayList();
        } else {
            long[] detectionPointers = ApriltagDetectionJNI.getDetectionPointers(ptrDetectionArray);
            ArrayList<AprilTagDetection> detections = new ArrayList(detectionPointers.length);
            long[] var7 = detectionPointers;
            int var8 = detectionPointers.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                long ptrDetection = var7[var9];
                AprilTagDetection detection = new AprilTagDetection();
                detection.frameAcquisitionNanoTime = captureTimeNanos;
                detection.id = ApriltagDetectionJNI.getId(ptrDetection);
                //System.out.println("DETECTION ID: " + detection.id);
                AprilTagMetadata metadata = this.tagLibrary.lookupTag(detection.id);
                detection.metadata = metadata;
                detection.hamming = ApriltagDetectionJNI.getHamming(ptrDetection);
                detection.decisionMargin = ApriltagDetectionJNI.getDecisionMargin(ptrDetection);
                double[] center = ApriltagDetectionJNI.getCenterpoint(ptrDetection);
                detection.center = new Point(center[0], center[1]);
                double[][] corners = ApriltagDetectionJNI.getCorners(ptrDetection);
                detection.corners = new Point[4];

                for(int p = 0; p < 4; ++p) {
                    detection.corners[p] = new Point(corners[p][0], corners[p][1]);
                }

                if (metadata == null) {
                    detection.rawPose = null;
                } else {
                    PoseSolver solver = this.poseSolver;
                    detection.rawPose = new AprilTagPoseRaw();
                    long startSolveTime = System.currentTimeMillis();
                    if (solver == PoseSolver.APRILTAG_BUILTIN) {
                        double[] pose = ApriltagDetectionJNI.getPoseEstimate(ptrDetection, this.outputUnitsLength.fromUnit(metadata.distanceUnit, metadata.tagsize), this.fx, this.fy, this.cx, this.cy);
                        detection.rawPose.x = pose[0];
                        detection.rawPose.y = pose[1];
                        detection.rawPose.z = pose[2];
                        float[] rotMtxVals = new float[9];

                        for(int i = 0; i < 9; ++i) {
                            rotMtxVals[i] = (float)pose[3 + i];
                        }

                        detection.rawPose.R = new GeneralMatrixF(3, 3, rotMtxVals);
                    } else {
                        AprilTagProcessorImpl.Pose opencvPose = poseFromTrapezoid(detection.corners, this.cameraMatrix, this.outputUnitsLength.fromUnit(metadata.distanceUnit, metadata.tagsize), solver.code);
                        detection.rawPose.x = opencvPose.tvec.get(0, 0)[0];
                        detection.rawPose.y = opencvPose.tvec.get(1, 0)[0];
                        detection.rawPose.z = opencvPose.tvec.get(2, 0)[0];
                        Mat R = new Mat(3, 3, 5);
                        Calib3d.Rodrigues(opencvPose.rvec, R);
                        float[] tmp2 = new float[9];
                        R.get(0, 0, tmp2);
                        detection.rawPose.R = new GeneralMatrixF(3, 3, tmp2);
                    }

                    long endSolveTime = System.currentTimeMillis();
                    this.solveTime.add((double)(endSolveTime - startSolveTime));
                }

                if (detection.rawPose != null) {
                    detection.ftcPose = new AprilTagPoseFtc();
                    detection.ftcPose.x = detection.rawPose.x;
                    detection.ftcPose.y = detection.rawPose.z;
                    detection.ftcPose.z = -detection.rawPose.y;
                    Orientation rot = Orientation.getOrientation(detection.rawPose.R, AxesReference.INTRINSIC, AxesOrder.YXZ, this.outputUnitsAngle);
                    detection.ftcPose.yaw = (double)(-rot.firstAngle);
                    detection.ftcPose.roll = (double)rot.thirdAngle;
                    detection.ftcPose.pitch = (double)rot.secondAngle;
                    detection.ftcPose.range = Math.hypot(detection.ftcPose.x, detection.ftcPose.y);
                    detection.ftcPose.bearing = this.outputUnitsAngle.fromUnit(AngleUnit.RADIANS, Math.atan2(-detection.ftcPose.x, detection.ftcPose.y));
                    detection.ftcPose.elevation = this.outputUnitsAngle.fromUnit(AngleUnit.RADIANS, Math.atan2(detection.ftcPose.z, detection.ftcPose.y));
                }

                detections.add(detection);
            }

            ApriltagDetectionJNI.freeDetectionList(ptrDetectionArray);
            return detections;
        }
    }

    public void onDrawFrame(Mat canvas, int onscreenWidth, int onscreenHeight) {
        synchronized(this.drawSync) {
            if ((this.drawAxes || this.drawCube || this.drawOutline || this.drawTagID)) {
//                this.canvasAnnotator.noteDrawParams(scaleBmpPxToCanvasPx, scaleCanvasDensity);
                ArrayList<AprilTagDetection> dets = getDetections();
                Iterator var9 = dets.iterator();

                while(var9.hasNext()) {
                    AprilTagDetection detection = (AprilTagDetection)var9.next();
                    if (this.drawTagID) {
//                        this.canvasAnnotator.drawTagID(detection, canvas);
                    }

                    if (detection.rawPose != null) {
                        AprilTagMetadata metadata = this.tagLibrary.lookupTag(detection.id);
                        double tagSize = this.outputUnitsLength.fromUnit(metadata.distanceUnit, metadata.tagsize);
                        if (this.drawOutline) {
//                            this.canvasAnnotator.drawOutlineMarker(detection, canvas, tagSize);
                        }

                        if (this.drawAxes) {
                            this.canvasAnnotator.drawAxisMarker(detection, canvas, tagSize);
                        }

                        if (this.drawCube) {
//                            this.canvasAnnotator.draw3dCubeMarker(detection, canvas, tagSize);
                        }
                    }
                }
            }

        }
    }

    public void setDecimation(float decimation) {
        synchronized(this.decimationSync) {
            this.decimation = decimation;
            this.needToSetDecimation = true;
        }
    }

    public void setPoseSolver(PoseSolver poseSolver) {
        this.poseSolver = poseSolver;
    }

    public int getPerTagAvgPoseSolveTime() {
        return (int)Math.round(this.solveTime.getMean());
    }

    public ArrayList<AprilTagDetection> getDetections() {
        return this.detections;
    }

    public ArrayList<AprilTagDetection> getFreshDetections() {
        synchronized(this.detectionsUpdateSync) {
            ArrayList<AprilTagDetection> ret = this.detectionsUpdate;
            this.detectionsUpdate = null;
            return ret;
        }
    }

    void constructMatrix() {
        this.cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        this.cameraMatrix.put(0, 0, new double[]{this.fx});
        this.cameraMatrix.put(0, 1, new double[]{0.0D});
        this.cameraMatrix.put(0, 2, new double[]{this.cx});
        this.cameraMatrix.put(1, 0, new double[]{0.0D});
        this.cameraMatrix.put(1, 1, new double[]{this.fy});
        this.cameraMatrix.put(1, 2, new double[]{this.cy});
        this.cameraMatrix.put(2, 0, new double[]{0.0D});
        this.cameraMatrix.put(2, 1, new double[]{0.0D});
        this.cameraMatrix.put(2, 2, new double[]{1.0D});
    }

    static AprilTagProcessorImpl.Pose aprilTagPoseToOpenCvPose(AprilTagPoseRaw aprilTagPose) {
        AprilTagProcessorImpl.Pose pose = new AprilTagProcessorImpl.Pose();
        pose.tvec.put(0, 0, new double[]{aprilTagPose.x});
        pose.tvec.put(1, 0, new double[]{aprilTagPose.y});
        pose.tvec.put(2, 0, new double[]{aprilTagPose.z});
        Mat R = new Mat(3, 3, 5);

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                R.put(i, j, new double[]{(double)aprilTagPose.R.get(i, j)});
            }
        }

        Calib3d.Rodrigues(R, pose.rvec);
        return pose;
    }

    static AprilTagProcessorImpl.Pose poseFromTrapezoid(Point[] points, Mat cameraMatrix, double tagsize, int solveMethod) {
        MatOfPoint2f points2d = new MatOfPoint2f(points);
        Point3[] arrayPoints3d = new Point3[]{new Point3(-tagsize / 2.0D, tagsize / 2.0D, 0.0D), new Point3(tagsize / 2.0D, tagsize / 2.0D, 0.0D), new Point3(tagsize / 2.0D, -tagsize / 2.0D, 0.0D), new Point3(-tagsize / 2.0D, -tagsize / 2.0D, 0.0D)};
        MatOfPoint3f points3d = new MatOfPoint3f(arrayPoints3d);
        AprilTagProcessorImpl.Pose pose = new AprilTagProcessorImpl.Pose();
        Calib3d.solvePnP(points3d, points2d, cameraMatrix, new MatOfDouble(), pose.rvec, pose.tvec, false, solveMethod);
        return pose;
    }

    static class Pose {
        Mat rvec;
        Mat tvec;

        public Pose() {
            this.rvec = new Mat(3, 1, 5);
            this.tvec = new Mat(3, 1, 5);
        }

        public Pose(Mat rvec, Mat tvec) {
            this.rvec = rvec;
            this.tvec = tvec;
        }
    }
}
