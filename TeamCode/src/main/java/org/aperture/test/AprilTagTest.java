//package org.aperture.test;
//
//import android.graphics.Canvas;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.aperture.LogConfig;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.VisionProcessor;
//import org.firstinspires.ftc.vision.apriltag.*;
//import org.opencv.calib3d.Calib3d;
//import org.opencv.core.*;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.imgproc.Moments;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//
//@TeleOp(name = "AprilTagTest", group = "Test")
//public class AprilTagTest extends LinearOpMode {
//
//    /**
//     * The variable to store our instance of the AprilTag processor.
//     */
//    private AprilTagProcessor aprilTag;
//
//    class PerspectiveWarp implements VisionProcessor {
//
//        Scalar lower = new Scalar(-1,0,0);
//        Scalar upper = new Scalar(49,50,50);
//        Mat threshMat = new Mat();
//        @Override
//        public void init(int var1, int var2, CameraCalibration var3) {}
//
//        Point[][] endPoints = new Point[][] {
//                new Point[] {new Point(184.0444793701172,44.692821502685554), new Point(186.7691497802734,97.09703063964845), new Point(230.44651794433594,103.71796417236328), new Point(227.8836669921875,54.23505401611327),}, new Point[] {new Point(190.5931854248047,202.87588500976565), new Point(192.33920288085935,253.89860534667966), new Point(235.2235107421875,251.76721191406253), new Point(233.59458923339844,203.68218994140628),}, new Point[] {new Point(194.37623596191412,358.3108520507813), new Point(195.0235595703125,409.77575683593744), new Point(237.17257690429682,399.8139343261718), new Point(237.1688995361328,350.95910644531256),},
//        };
//
//        Point[] pixelCenterPoints = new Point[] {
//                new Point(657,670),
//                new Point(677,657),
//                new Point(409,659),
//                new Point(699,577),new Point(635,577),new Point(571,577),new Point(507,577),new Point(443,577),new Point(379,577),new Point(667,523),new Point(603,523),new Point(539,523),new Point(475,523),new Point(411,523),new Point(346,524),new Point(699,470),new Point(635,470),new Point(571,470),new Point(507,470),new Point(443,470),new Point(379,470),new Point(667,417),new Point(603,417),new Point(539,417),new Point(475,417),new Point(411,417),new Point(347,417),new Point(699,363),new Point(379,363),new Point(635,363),new Point(571,363),new Point(507,363),new Point(443,363),new Point(667,310),new Point(603,310),new Point(539,310),new Point(475,310),new Point(411,310),new Point(346,310),new Point(700,256),new Point(635,256),new Point(571,256),new Point(507,256),new Point(443,256),new Point(378,256),new Point(668,203),new Point(603,203),new Point(539,203),new Point(475,203),new Point(410,203),new Point(346,203),new Point(700,149),new Point(635,149),new Point(571,149),new Point(507,149),new Point(443,149),new Point(378,149),new Point(668,96),new Point(603,96),new Point(539,96),new Point(475,96),new Point(410,96),new Point(346,96),new Point(700,42),new Point(635,42),new Point(571,42),new Point(507,42),new Point(443,42),new Point(378,42),
//        };
//        @Override
//        public Mat processFrame(Mat input, long var2) {
//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//
//            List<Point> points = new ArrayList<>();
//            List<Point> points2 = new ArrayList<>();
//            if (LogConfig.VISION_POINTS) {
//                List<Point>[] printPoints = new List[] { new ArrayList<>(), new ArrayList<>(), new ArrayList<>() };
//                for (AprilTagDetection detection : currentDetections) {
//                    switch (detection.id) {
//                        case 1:
//                            printPoints[0].addAll(Arrays.asList(detection.corners));
//                            break;
//                        case 2:
//                            printPoints[1].addAll(Arrays.asList(detection.corners));
//                            break;
//                        case 3:
//                            printPoints[2].addAll(Arrays.asList(detection.corners));
//                            break;
//                        default: {
//                            return null;
//                        }
//                    }
//                }
//                System.out.println("VISION: points=" + Arrays.stream(printPoints).map(item0 -> "new Point[] {" + item0.stream().map(item -> "new Point(" + item.x + "," + item.y + "),").collect(Collectors.joining(" ")) + "},").collect(Collectors.joining(" "))) ;
//            }
//
//            for (AprilTagDetection detection : currentDetections) {
//                points.addAll(Arrays.asList(detection.corners));
//                switch (detection.id) {
//                    case 1:
//                        points2.addAll(Arrays.asList(endPoints[0]));
//                        break;
//                    case 2:
//                        points2.addAll(Arrays.asList(endPoints[1]));
//                        break;
//                    case 3:
//                        points2.addAll(Arrays.asList(endPoints[2]));
//                        break;
//                    default: {
//                        return null;
//                    }
//                }
//            }
//
//            if(points.isEmpty()) {
//                return null;
//            }
//
//            MatOfPoint2f p0 = new MatOfPoint2f();
//            MatOfPoint2f p2 = new MatOfPoint2f();
//            p0.fromList(points);
//            p2.fromList(points2);
//
//            Mat H = Calib3d.findHomography(p0,p2);
//
//            Imgproc.warpPerspective(input,input,H,input.size());
//
//            Core.inRange(input,lower,upper,threshMat);
//
//            List<MatOfPoint> contours = new ArrayList<>();
//            Mat hierarchy = new Mat();
//            Imgproc.findContours(threshMat,contours,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
//
//            points.clear();
//            points2.clear();
//            for(MatOfPoint contour : contours) {
//                Moments moments = Imgproc.moments(contour);
//                int cX = (int)(moments.m10/moments.m00);
//                int cY = (int)(moments.m01/moments.m00);
//                int area = (int)moments.m00;
//
//                if(area > 200 && area < 600)  {
//                    Imgproc.circle(input,new Point(cX,cY), (int) (moments.m00/100.0),new Scalar(0,255,0));
//
//                    Point closestPoint = null;
//                    for(Point point : pixelCenterPoints) {
//                        double dist = Math.sqrt(Math.pow(cX-point.x,2))+Math.pow(cY-point.y,2);
//                        if (dist < 30f && (closestPoint == null || Math.sqrt(Math.pow(cX-closestPoint.x,2))+Math.pow(cY-closestPoint.y,2) > dist)) {
//                            closestPoint = point;
//                        }
//                    }
//
//                    if(closestPoint!=null) {
//                        points.add(new Point(cX,cY));
//                        points2.add(closestPoint);
//                    }
//                }
//            }
//
//            if(points.size() > 3) {
//                p0.release();
//                p2.release();
//                p0 = new MatOfPoint2f();
//                p2 = new MatOfPoint2f();
//                p0.fromList(points);
//                p2.fromList(points2);
//
//                Mat H2 = Calib3d.findHomography(p0,p2);
//
//                System.out.println("H2: " + H2.dump());
//                if(!H2.empty()) {
////                    Imgproc.warpPerspective(input,input,H2,input.size());
//                }
//
//                H2.release();
//            }
//
//            hierarchy.release();
//            H.release();
//            p0.release();
//            p2.release();
//            return null;
//        }
//
//        @Override
//        public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
//
//        }
//    }
//
//    /**
//     * The variable to store our instance of the vision portal.
//     */
//    private VisionPortal visionPortal;
//
//    @Override
//    public void runOpMode() {
//
//        initAprilTag();
//
//        // Wait for the DS start button to be touched.
//        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
//        telemetry.addData(">", "Touch Play to start OpMode");
//        telemetry.update();
//        waitForStart();
//
//        if (opModeIsActive()) {
//            while (opModeIsActive()) {
//
//                telemetryAprilTag();
//
//                // Push telemetry to the Driver Station.
//                telemetry.update();
//
//                // Save CPU resources; can resume streaming when needed.
//                if (gamepad1.dpad_down) {
//                    visionPortal.stopStreaming();
//                } else if (gamepad1.dpad_up) {
//                    visionPortal.resumeStreaming();
//                }
//
//                // Share the CPU.
//                sleep(20);
//            }
//        }
//
//        // Save more CPU resources when camera is no longer needed.
//        visionPortal.close();
//
//    }   // end method runOpMode()
//
//    /**
//     * Initialize the AprilTag processor.
//     */
//
//    AprilTagLibrary aprilTagLibrary;
//    private void initAprilTag() {
//
//        aprilTagLibrary = AprilTagGameDatabase.getCenterStageTagLibrary();
//
//        // Create the AprilTag processor.
//        aprilTag = new AprilTagProcessor.Builder()
//                .setDrawAxes(true)
////                .setDrawCubeProjection(true)
////                .setDrawTagOutline(true)
//                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
//                .setTagLibrary(AprilTagGameDatabase.getCenterStageTagLibrary())
//                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
//
//
//                // == CAMERA CALIBRATION ==
//                // If you do not manually specify calibration parameters, the SDK will attempt
//                // to load a predefined calibration for your camera.
//                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
//
//                // ... these parameters are fx, fy, cx, cy.
//
//                .build();
//
//        // Create the vision portal by using a builder.
//        VisionPortal.Builder builder = new VisionPortal.Builder();
//
//        aprilTag.setDecimation(1f);
//
//        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam"));
//
//        // Choose a camera resolution. Not all cameras support all resolutions.
//        builder.setCameraResolution(new android.util.Size(640, 480));
//
//        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
//        //builder.enableCameraMonitoring(true);
//
//        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
//        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
//
//        // Choose whether or not LiveView stops if no processors are enabled.
//        // If set "true", monitor shows solid orange screen if no processors enabled.
//        // If set "false", monitor shows camera viealso the scaling of the apriltags seems to be offw without annotations.
//        //builder.setAutoStopLiveView(false);
//
//        // Set and enable the processor.
//        builder.addProcessor(aprilTag);
//        builder.addProcessor(new PerspectiveWarp());
//
//        // Build the Vision Portal, using the above settings.
//        visionPortal = builder.build();
//
//        // Disable or re-enable the aprilTag processor at any time.
//        //visionPortal.setProcessorEnabled(aprilTag, true);
//
//    }
//
//
//    /**
//     * Add telemetry about AprilTag detections.
//     */
//    private void telemetryAprilTag() {
//        if(LogConfig.VISION_APRILTAG) {
//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//            telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//            // Step through the list of detections and display info for each one.
//            for (AprilTagDetection detection : currentDetections) {
////            System.out.println(detection.toString());
//                if (detection.metadata != null) {
//                    telemetry.addLine(String.format(Locale.US, "\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                    telemetry.addLine(String.format(Locale.US, "XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
//                    telemetry.addLine(String.format(Locale.US, "PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
//                    telemetry.addLine(String.format(Locale.US, "RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
//                    telemetry.addLine("Corners" + Arrays.toString(detection.corners));
////                System.out.println("Corners:" + detection.id + " " + Arrays.toString(detection.corners));
//                } else {
//                    telemetry.addLine(String.format(Locale.US, "\n==== (ID %d) Unknown", detection.id));
//                    telemetry.addLine(String.format(Locale.US, "Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//                }
//            }
//
//            // Add "key" information to telemetry
//            telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//            telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
//            telemetry.addLine("RBE = Range, Bearing & Elevation");
//        }
//
//
//    }
//
//}
