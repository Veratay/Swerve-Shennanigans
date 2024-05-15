//package org.aperture.common.vision;
//
//import android.graphics.Canvas;
//import android.util.Size;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.VisionProcessor;
//import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessorImpl;
//import org.opencv.core.Mat;
//
//import java.lang.reflect.Field;
//
////thin wrapper for VisionProcessor specific to our use cases.
//public class Vision {
//    private final VisionPortal portal;
//
//    private final VisionProcessor apriltag;
//
//    public int width;
//    public int height;
//
//    //This has to be calculated in multiple places, so a cached version is here. (Calculated in the apriltag stage if active)
//    private Mat cachedGrey = null;
//
//    private boolean apriltagActive = true;
//
//    public Vision(CameraName camera, int width, int height) throws NoSuchFieldException {
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//        builder.setCamera(camera);
//        builder.setCameraResolution(new Size(width,height));
//
//        this.width = width;
//        this.height = height;
//
//        this.apriltag = initAprilTag();
//
//        Field greyMatField =  AprilTagProcessorImpl.class.getDeclaredField("grey");
//        greyMatField.setAccessible(true);
//
//        VisionCallbackProcessor greyCacher = new VisionCallbackProcessor(() -> {
//            if(apriltagActive)
//                try { cachedGrey = (Mat) greyMatField.get(apriltag); }
//                catch (IllegalAccessException e) { cachedGrey = null; }
//            else { cachedGrey = null; }
//        });
//
//        builder.addProcessors(
//                this.apriltag,
//
//                new PixelDetector(this)
//        );
//
//        this.portal = builder.build();
//    }
//
//    public void setApriltagEnabled(boolean enabled) {
//        if(apriltag instanceof GreyReceiver) {
//            ((GreyReceiver)apriltag).apriltagEnabled = enabled;
//        } else {
//            portal.setProcessorEnabled(apriltag,enabled);
//        }
//    }
//
//    static class VisionCallbackProcessor implements VisionProcessor {
//        interface Callback {
//            void fn();
//        }
//
//        Callback fn;
//        VisionCallbackProcessor(Callback fn) {
//            this.fn = fn;
//        }
//
//        @Override
//        public void init(int width, int height, CameraCalibration calibration) {}
//
//        @Override
//        public Object processFrame(Mat frame, long captureTimeNanos) {
//            fn.fn();
//            return null;
//        }
//
//        @Override
//        public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {}
//    }
//
//    class GreyReceiver implements VisionProcessor {
//        final AprilTagProcessorImpl processor;
//        final Field greyMatField;
//
//        boolean apriltagEnabled = true;
//
//        public GreyReceiver(AprilTagProcessorImpl processor) throws NoSuchFieldException {
//            this.processor = processor;
//            greyMatField =  AprilTagProcessorImpl.class.getDeclaredField("grey");
//            greyMatField.setAccessible(true);
//        }
//
//        @Override
//        public void init(int width, int height, CameraCalibration calibration) {
//            processor.init(width,height,calibration);
//        }
//
//        @Override
//        public Object processFrame(Mat frame, long captureTimeNanos) {
//            if(apriltagEnabled) {
//                Object res = processor.processFrame(frame,captureTimeNanos);
//                try {
//                    Vision.this.cachedGrey = (Mat) greyMatField.get(processor);
//                } catch (IllegalAccessException e) {
//                    Vision.this.cachedGrey = null;
//                    e.printStackTrace();
//                }
//                return res;
//            } else {
//                Vision.this.cachedGrey = null;
//                return null;
//            }
//        }
//
//        @Override
//        public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
//            processor.onDrawFrame(canvas, onscreenWidth, onscreenHeight, scaleBmpPxToCanvasPx, scaleCanvasDensity, userContext);
//        }
//    }
//
//    private VisionProcessor initAprilTag() {
//        AprilTagProcessor apriltag = new AprilTagProcessor.Builder()
//                .setDrawAxes(true)
//                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
//                .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
//                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
//                .build();
//        apriltag.setDecimation(1f);
//
//        return apriltag;
//    }
//}
