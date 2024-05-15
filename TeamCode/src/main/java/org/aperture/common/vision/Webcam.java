//package org.aperture.common.vision;
//
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.ImageFormat;
//import android.graphics.Rect;
//import android.os.Handler;
//
//import androidx.annotation.NonNull;
//
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.qualcomm.robotcore.util.RobotLog;
//
//import org.firstinspires.ftc.robotcore.external.ClassFactory;
//import org.firstinspires.ftc.robotcore.external.android.util.Size;
//import org.firstinspires.ftc.robotcore.external.function.Consumer;
//import org.firstinspires.ftc.robotcore.external.function.Continuation;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraManager;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
//import org.firstinspires.ftc.robotcore.internal.network.CallbackLooper;
//import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
//import org.firstinspires.ftc.robotcore.internal.system.ContinuationSynchronizer;
//import org.firstinspires.ftc.robotcore.internal.system.Deadline;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Locale;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//public class Webcam {
//
//    private final float MIN_SATURATION = 0.5f;
//
//    //----------------------------------------------------------------------------------------------
//    // State
//    //----------------------------------------------------------------------------------------------
//    private static final String TAG = "WEBCAM: ";
//
//    /** How long we are to wait to be granted permission to use the camera before giving up. Here,
//     * we wait indefinitely */
//    private static final int secondsPermissionTimeout = Integer.MAX_VALUE;
//
//    /** State regarding our interaction with the camera */
//    private CameraManager cameraManager;
//    private WebcamName cameraName;
//    private Camera camera;
//    private CameraCaptureSession cameraCaptureSession;
//
//    /** The queue into which all frames from the camera are placed as they become available.
//     * Frames which are not processed by the OpMode are automatically discarded. */
//    private EvictingBlockingQueue<Bitmap> frameQueue;
//
//    /** State regarding where and how to save frames when the 'A' button is pressed. */
//    private int captureCounter = 0;
//    private File captureDirectory = AppUtil.ROBOT_DATA_DIR;
//
//    /** A utility object that indicates where the asynchronous callbacks from the camera
//     * infrastructure are to run. In this OpMode, that's all hidden from you (but see {@link #startCamera}
//     * if you're curious): no knowledge of multi-threading is needed here. */
//    private Handler callbackHandler;
//
//
//    private Bitmap currentBitmap = null;
//
//
//    public Webcam(HardwareMap hardwareMap, String name) {
//        callbackHandler = CallbackLooper.getDefault().getHandler();
//
//        cameraManager = ClassFactory.getInstance().getCameraManager();
//        cameraName = hardwareMap.get(WebcamName.class, name);
//
//        initializeFrameQueue(2);
//        AppUtil.getInstance().ensureDirectoryExists(captureDirectory);
//
//        openCamera();
//        if (camera == null) return;
//
//        startCamera();
//        if (cameraCaptureSession == null) return;
//    }
//
//    public void takePicture() throws InterruptedException {
//        if (currentBitmap != null) {
//            currentBitmap.recycle();
//        }
//
//        currentBitmap = frameQueue.take();
//        if (currentBitmap != null) {
//            System.out.println("WEBCAM picture was taken!");
//        } else {
//            System.out.println("WEBCAM: picture was NOT taken!");
//        }
//    }
//
//    public float avgHue(Rect rect) {
//        float[] hsv = new float[3];
//        float hueSum = 0;
//
//        for (int y = rect.top; y < rect.bottom; y++) {
//            for (int x = rect.left; x < rect.right; x++) {
//                int color = currentBitmap.getPixel(x, y);
//                Color.colorToHSV(color, hsv);
//                hueSum += hsv[0];
//            }
//        }
//        float avgHue = hueSum / (rect.width() * rect.height());
//
//        return avgHue;
//    }
//
//    public float avgSaturation(Rect rect) {
//        float[] hsv = new float[3];
//        float saturationSum = 0;
//
//        for (int y = rect.top; y < rect.bottom; y++) {
//            for (int x = rect.left; x < rect.right; x++) {
//                int color = currentBitmap.getPixel(x, y);
//                Color.colorToHSV(color, hsv);
//                saturationSum += hsv[1];
//            }
//        }
//        float avgSaturation = saturationSum / (rect.width() * rect.height());
//
//        return avgSaturation;
//    }
//
//
//    // code that examines the bitmap
//    public int countPixels(float hue, float tolerance, Rect rect) {
//        float[] hsv = new float[3];
//        int cnt = 0;
//
//        float hueSum = 0;
//        float saturationSum = 0;
//
//        for (int y = rect.top; y < rect.bottom; y++) {
//            for (int x = rect.left; x < rect.right; x++) {
//                int color = currentBitmap.getPixel(x, y);
//                Color.colorToHSV(color, hsv);
//                hueSum += hsv[0];
//                saturationSum += hsv[1];
//
//                if(hsv[0] >= hue - tolerance && hsv[0] < hue + tolerance && hsv[1] >= MIN_SATURATION ) {
//                    cnt += 1;
//                }
//            }
//        }
//        float avgHue = hueSum / (rect.width() * rect.height());
//        float avgSaturation = saturationSum / (rect.width() * rect.height());
//        System.out.println(TAG + "hue: " + avgHue + ", sat: " + avgSaturation);
//
//        return cnt;
//    }
//
//    public void drawRect(Rect rect, int color) {
//        // draw top and bottom lines horizontally
//        int left  = Math.min(rect.left, rect.right);
//        int right = Math.max(rect.left, rect.right);
//        for (int x = left; x <= right; x++) {
//            currentBitmap.setPixel(x, rect.top,    color);
//            currentBitmap.setPixel(x, rect.bottom, color);
//        }
//
//        // draw left and right lines vertically
//        int top    = Math.min(rect.top, rect.bottom);
//        int bottom = Math.max(rect.top, rect.bottom);
//        for (int y = rect.top; y <= rect.bottom; y++) {
//            currentBitmap.setPixel(rect.left,  y, color);
//            currentBitmap.setPixel(rect.right, y, color);
//        }
//    }
//
//
//    //----------------------------------------------------------------------------------------------
//    // Camera operations
//    //----------------------------------------------------------------------------------------------
//
//    private void initializeFrameQueue(int capacity) {
//        /** The frame queue will automatically throw away bitmap frames if they are not processed
//         * quickly by the OpMode. This avoids a buildup of frames in memory */
//        frameQueue = new EvictingBlockingQueue<Bitmap>(new ArrayBlockingQueue<Bitmap>(capacity));
//        frameQueue.setEvictAction(new Consumer<Bitmap>() {
//            @Override public void accept(Bitmap frame) {
//                // RobotLog.ii(TAG, "frame recycled w/o processing");
//                frame.recycle(); // not strictly necessary, but helpful
//            }
//        });
//    }
//
//    private void openCamera() {
//        if (camera != null) return; // be idempotent
//
//        Deadline deadline = new Deadline(secondsPermissionTimeout, TimeUnit.SECONDS);
//        camera = cameraManager.requestPermissionAndOpenCamera(deadline, cameraName, null);
//        if (camera == null) {
//            error("camera not found or permission to use not granted: %s", cameraName);
//        }
//    }
//
//    private void startCamera() {
//        if (cameraCaptureSession != null) return; // be idempotent
//
//        /** YUY2 is supported by all Webcams, per the USB Webcam standard: See "USB Device Class Definition
//         * for Video Devices: Uncompressed Payload, Table 2-1". Further, often this is the *only*
//         * image format supported by a camera */
//        final int imageFormat = ImageFormat.YUY2;
//
//        /** Verify that the image is supported, and fetch size and desired frame rate if so */
//        CameraCharacteristics cameraCharacteristics = cameraName.getCameraCharacteristics();
//        if (!contains(cameraCharacteristics.getAndroidFormats(), imageFormat)) {
//            error("image format not supported");
//            return;
//        }
//        final Size size = cameraCharacteristics.getDefaultSize(imageFormat);
//        final int fps = cameraCharacteristics.getMaxFramesPerSecond(imageFormat, size);
//
//        /** Some of the logic below runs asynchronously on other threads. Use of the synchronizer
//         * here allows us to wait in this method until all that asynchrony completes before returning. */
//        final ContinuationSynchronizer<CameraCaptureSession> synchronizer = new ContinuationSynchronizer<>();
//        try {
//            /** Create a session in which requests to capture frames can be made */
//            camera.createCaptureSession(Continuation.create(callbackHandler, new CameraCaptureSession.StateCallbackDefault() {
//                @Override public void onConfigured(@NonNull CameraCaptureSession session) {
//                    try {
//                        /** The session is ready to go. Start requesting frames */
//                        final CameraCaptureRequest captureRequest = camera.createCaptureRequest(imageFormat, size, fps);
//                        session.startCapture(captureRequest,
//                                new CameraCaptureSession.CaptureCallback() {
//                                    @Override public void onNewFrame(@NonNull CameraCaptureSession session, @NonNull CameraCaptureRequest request, @NonNull CameraFrame cameraFrame) {
//                                        /** A new frame is available. The frame data has <em>not</em> been copied for us, and we can only access it
//                                         * for the duration of the callback. So we copy here manually. */
//                                        Bitmap bmp = captureRequest.createEmptyBitmap();
//                                        cameraFrame.copyToBitmap(bmp);
//                                        frameQueue.offer(bmp);
//                                    }
//                                },
//                                Continuation.create(callbackHandler, new CameraCaptureSession.StatusCallback() {
//                                    @Override public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, CameraCaptureSequenceId cameraCaptureSequenceId, long lastFrameNumber) {
//                                        RobotLog.ii(TAG, "capture sequence %s reports completed: lastFrame=%d", cameraCaptureSequenceId, lastFrameNumber);
//                                    }
//                                })
//                        );
//                        synchronizer.finish(session);
//                    } catch (CameraException |RuntimeException e) {
//                        RobotLog.ee(TAG, e, "exception starting capture");
//                        error("exception starting capture");
//                        session.close();
//                        synchronizer.finish(null);
//                    }
//                }
//            }));
//        } catch (CameraException|RuntimeException e) {
//            RobotLog.ee(TAG, e, "exception starting camera");
//            error("exception starting camera");
//            synchronizer.finish(null);
//        }
//
//        /** Wait for all the asynchrony to complete */
//        try {
//            synchronizer.await();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        /** Retrieve the created session. This will be null on error. */
//        cameraCaptureSession = synchronizer.getValue();
//    }
//
//    private void stopCamera() {
//        if (cameraCaptureSession != null) {
//            cameraCaptureSession.stopCapture();
//            cameraCaptureSession.close();
//            cameraCaptureSession = null;
//        }
//    }
//
//    private void closeCamera() {
//        stopCamera();
//        if (camera != null) {
//            camera.close();
//            camera = null;
//        }
//    }
//
//    //----------------------------------------------------------------------------------------------
//    // Utilities
//    //----------------------------------------------------------------------------------------------
//
//    private void error(String msg) {
//        System.out.println(msg);
//    }
//    private void error(String format, Object...args) {
//        System.out.printf(format, args);
//    }
//
//    private boolean contains(int[] array, int value) {
//        for (int i : array) {
//            if (i == value) return true;
//        }
//        return false;
//    }
//
//    private void saveBitmap(Bitmap bitmap) {
//        File file = new File(captureDirectory, String.format(Locale.getDefault(), "webcam-frame-%d.jpg", captureCounter++));
//        try {
//            try (FileOutputStream outputStream = new FileOutputStream(file)) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                System.out.println("captured " + file.getName());
//            }
//        } catch (IOException e) {
//            RobotLog.ee(TAG, e, "exception in saveBitmap()");
//            error("exception saving %s", file.getName());
//        }
//    }
//
//    public void saveBitmap() {
//        File file = new File(captureDirectory, String.format(Locale.getDefault(), "webcam-frame-%d.jpg", captureCounter++));
//        try {
//            try (FileOutputStream outputStream = new FileOutputStream(file)) {
//                currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                System.out.println("captured " + file.getName());
//            }
//        } catch (IOException e) {
//            RobotLog.ee(TAG, e, "exception in saveBitmap()");
//            error("exception saving %s", file.getName());
//        }
//    }
//}