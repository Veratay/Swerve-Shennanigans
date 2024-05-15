//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision;


import android.util.Size;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibrationIdentity;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import org.openftc.easyopencv.OpenCvWebcam;
import org.openftc.easyopencv.TimestampedOpenCvPipeline;
import org.openftc.easyopencv.OpenCvCamera.AsyncCameraOpenListener;


public class VisionPortalImpl extends VisionPortal {
    protected OpenCvCamera camera;
    protected volatile CameraState cameraState;
    protected VisionProcessor[] processors;
    protected volatile boolean[] processorsEnabled;
    protected volatile CameraCalibration calibration;
    protected final boolean autoPauseCameraMonitor;
    protected final Object userStateMtx;
    protected final Size cameraResolution;
    protected final StreamFormat webcamStreamFormat;
    protected static final OpenCvCameraRotation CAMERA_ROTATION;
    protected String captureNextFrame;
    protected final Object captureFrameMtx;

    public VisionPortalImpl(CameraName camera, int cameraMonitorViewId, boolean autoPauseCameraMonitor, Size cameraResolution, StreamFormat webcamStreamFormat, VisionProcessor[] processors) {
        this.cameraState = CameraState.CAMERA_DEVICE_CLOSED;
        this.userStateMtx = new Object();
        this.captureFrameMtx = new Object();
        this.processors = processors;
        this.cameraResolution = cameraResolution;
        this.webcamStreamFormat = webcamStreamFormat;
        this.processorsEnabled = new boolean[processors.length];

        for(int i = 0; i < processors.length; ++i) {
            this.processorsEnabled[i] = true;
        }

        this.autoPauseCameraMonitor = autoPauseCameraMonitor;
        this.createCamera(camera, cameraMonitorViewId);
        this.startCamera();
    }

    protected void startCamera() {
        if (this.camera == null) {
            throw new IllegalStateException("This should never happen");
        } else if (this.cameraResolution == null) {
            throw new IllegalArgumentException("parameters.cameraResolution == null");
        } else {
//            this.camera.setViewportRenderer(ViewportRenderer.NATIVE_VIEW);
//            if (!(this.camera instanceof OpenCvWebcam)) {
//                this.camera.setViewportRenderingPolicy(ViewportRenderingPolicy.OPTIMIZE_VIEW);
//            }

            this.cameraState = CameraState.OPENING_CAMERA_DEVICE;
            this.camera.openCameraDeviceAsync(new AsyncCameraOpenListener() {
                public void onOpened() {
                    VisionPortalImpl.this.cameraState = CameraState.CAMERA_DEVICE_READY;
                    VisionPortalImpl.this.cameraState = CameraState.STARTING_STREAM;
                    if (VisionPortalImpl.this.camera instanceof OpenCvWebcam) {
                        ((OpenCvWebcam)VisionPortalImpl.this.camera).startStreaming(VisionPortalImpl.this.cameraResolution.getWidth(), VisionPortalImpl.this.cameraResolution.getHeight(), VisionPortalImpl.CAMERA_ROTATION, VisionPortalImpl.this.webcamStreamFormat.eocvStreamFormat);
                    } else {
                        VisionPortalImpl.this.camera.startStreaming(VisionPortalImpl.this.cameraResolution.getWidth(), VisionPortalImpl.this.cameraResolution.getHeight(), VisionPortalImpl.CAMERA_ROTATION);
                    }

                    if (VisionPortalImpl.this.camera instanceof OpenCvWebcam) {
//                        CameraCalibrationIdentity identity = ((OpenCvWebcam)VisionPortalImpl.this.camera).getCalibrationIdentity();
                        CameraCalibrationIdentity identity = null;
                        if (identity != null) {
//                            VisionPortalImpl.this.calibration = CameraCalibrationHelper.getInstance().getCalibration(identity, VisionPortalImpl.this.cameraResolution.getWidth(), VisionPortalImpl.this.cameraResolution.getHeight());
                        }
                    }

                    ProcessingPipeline processingPipeline = VisionPortalImpl.this.new ProcessingPipeline();
                    VisionPortalImpl.this.camera.setPipeline(processingPipeline);
                    VisionPortalImpl.this.camera.setOnDrawCallback(processingPipeline);
                    VisionPortalImpl.this.cameraState = CameraState.STREAMING;

                }

                public void onError(int errorCode) {
                    VisionPortalImpl.this.cameraState = CameraState.ERROR;
//                    RobotLog.ee("VisionPortalImpl", "Camera opening failed.");
                }
            });
        }
    }

    protected void createCamera(CameraName cameraName, int cameraMonitorViewId) {
        if (cameraName == null) {
            throw new IllegalArgumentException("parameters.camera == null");
        } else {
            if (cameraName.isWebcam()) {
                if (cameraMonitorViewId != 0) {
                    this.camera = OpenCvCameraFactory.getInstance().createWebcam((WebcamName)cameraName, cameraMonitorViewId);
                } else {
                    this.camera = OpenCvCameraFactory.getInstance().createWebcam((WebcamName)cameraName);
                }
            }

        }
    }

    public void setProcessorEnabled(VisionProcessor processor, boolean enabled) {
        int numProcessorsEnabled = 0;
        boolean ok = false;

        for(int i = 0; i < this.processors.length; ++i) {
            if (processor == this.processors[i]) {
                this.processorsEnabled[i] = enabled;
                ok = true;
            }

            if (this.processorsEnabled[i]) {
                ++numProcessorsEnabled;
            }
        }

        if (ok) {
            if (this.autoPauseCameraMonitor) {
                if (numProcessorsEnabled == 0) {
                    this.camera.pauseViewport();
                } else {
                    this.camera.resumeViewport();
                }
            }

        } else {
            throw new IllegalArgumentException("Processor not attached to this helper!");
        }
    }

    public boolean getProcessorEnabled(VisionProcessor processor) {
        for(int i = 0; i < this.processors.length; ++i) {
            if (processor == this.processors[i]) {
                return this.processorsEnabled[i];
            }
        }

        throw new IllegalArgumentException("Processor not attached to this helper!");
    }

    public CameraState getCameraState() {
        return this.cameraState;
    }

//    public void setActiveCamera(WebcamName webcamName) {
//        if (this.camera instanceof OpenCvSwitchableWebcam) {
//            ((OpenCvSwitchableWebcam)this.camera).setActiveCamera(webcamName);
//        } else {
//            throw new UnsupportedOperationException("setActiveCamera is only supported for switchable webcams");
//        }
//    }
//
//    public WebcamName getActiveCamera() {
//        if (this.camera instanceof OpenCvSwitchableWebcam) {
//            return ((OpenCvSwitchableWebcam)this.camera).getActiveCamera();
//        } else {
//            throw new UnsupportedOperationException("getActiveCamera is only supported for switchable webcams");
//        }
//    }

//    public <T extends CameraControl> T getCameraControl(Class<T> controlType) {
//        if (this.cameraState == CameraState.STREAMING) {
//            if (this.camera instanceof OpenCvWebcam) {
//                return ((OpenCvWebcam)this.camera).getControl(controlType);
//            } else {
//                throw new UnsupportedOperationException("Getting controls is only supported for webcams");
//            }
//        } else {
//            throw new IllegalStateException("You cannot use camera controls until the camera is streaming");
//        }
//    }

    public void saveNextFrameRaw(String filepath) {
        synchronized(this.captureFrameMtx) {
            this.captureNextFrame = filepath;
        }
    }

    public void stopStreaming() {
        synchronized(this.userStateMtx) {
            if (this.cameraState != CameraState.STREAMING && this.cameraState != CameraState.STARTING_STREAM) {
                if (this.cameraState != CameraState.STOPPING_STREAM && this.cameraState != CameraState.CAMERA_DEVICE_READY && this.cameraState != CameraState.CLOSING_CAMERA_DEVICE) {
                    throw new RuntimeException("Illegal CameraState when calling stopStreaming()");
                }
            } else {
                this.cameraState = CameraState.STOPPING_STREAM;
                (new Thread(() -> {
                    synchronized(this.userStateMtx) {
                        this.camera.stopStreaming();
                        this.cameraState = CameraState.CAMERA_DEVICE_READY;
                    }
                })).start();
            }

        }
    }

    public void resumeStreaming() {
        synchronized(this.userStateMtx) {
            if (this.cameraState != CameraState.CAMERA_DEVICE_READY && this.cameraState != CameraState.STOPPING_STREAM) {
                if (this.cameraState != CameraState.STREAMING && this.cameraState != CameraState.STARTING_STREAM && this.cameraState != CameraState.OPENING_CAMERA_DEVICE) {
                    throw new RuntimeException("Illegal CameraState when calling stopStreaming()");
                }
            } else {
                this.cameraState = CameraState.STARTING_STREAM;
                (new Thread(() -> {
                    synchronized(this.userStateMtx) {
                        if (this.camera instanceof OpenCvWebcam) {
                            ((OpenCvWebcam)this.camera).startStreaming(this.cameraResolution.getWidth(), this.cameraResolution.getHeight(), CAMERA_ROTATION, this.webcamStreamFormat.eocvStreamFormat);
                        } else {
                            this.camera.startStreaming(this.cameraResolution.getWidth(), this.cameraResolution.getHeight(), CAMERA_ROTATION);
                        }

                        this.cameraState = CameraState.STREAMING;
                    }
                })).start();
            }

        }
    }

    public void stopLiveView() {
        OpenCvCamera cameraSafe = this.camera;
        if (cameraSafe != null) {
            this.camera.pauseViewport();
        }

    }

    public void resumeLiveView() {
        OpenCvCamera cameraSafe = this.camera;
        if (cameraSafe != null) {
            this.camera.resumeViewport();
        }

    }

    public float getFps() {
        OpenCvCamera cameraSafe = this.camera;
        return cameraSafe != null ? cameraSafe.getFps() : 0.0F;
    }

    public void close() {
        synchronized(this.userStateMtx) {
            this.cameraState = CameraState.CLOSING_CAMERA_DEVICE;
            if (this.camera != null) {
                this.camera.closeCameraDeviceAsync(() -> {
                    this.cameraState = CameraState.CAMERA_DEVICE_CLOSED;
                });
            }

            this.camera = null;
        }
    }

    static {
        CAMERA_ROTATION = OpenCvCameraRotation.SENSOR_NATIVE;
    }

    public class ProcessingPipeline extends TimestampedOpenCvPipeline {
        ProcessingPipeline() {
        }

        public void init(Mat firstFrame) {
            VisionProcessor[] var2 = VisionPortalImpl.this.processors;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                VisionProcessor processor = var2[var4];
                processor.init(firstFrame.width(), firstFrame.height(), VisionPortalImpl.this.calibration);
            }

        }

        public Mat processFrame(Mat input, long captureTimeNanos) {
            synchronized(VisionPortalImpl.this.captureFrameMtx) {
                if (VisionPortalImpl.this.captureNextFrame != null) {
                    this.saveMatToDiskFullPath(input, "/sdcard/VisionPortal-" + VisionPortalImpl.this.captureNextFrame + ".png");
                }

                VisionPortalImpl.this.captureNextFrame = null;
            }

            Object[] processorDrawCtxes = new Object[VisionPortalImpl.this.processors.length];

            for(int i = 0; i < VisionPortalImpl.this.processors.length; ++i) {
                if (VisionPortalImpl.this.processorsEnabled[i]) {
                    processorDrawCtxes[i] = VisionPortalImpl.this.processors[i].processFrame(input, captureTimeNanos);
                }
            }

            this.requestViewportDrawHook(processorDrawCtxes);
            return input;
        }

        public void onDrawFrame(Mat canvas, int onscreenWidth, int onscreenHeight) {


            for(int i = 0; i < VisionPortalImpl.this.processors.length; ++i) {
                if (VisionPortalImpl.this.processorsEnabled[i]) {
//                    VisionPortalImpl.this.processors[i].onDrawFrame(canvas, onscreenWidth, onscreenHeight);
                }
            }

        }
    }
}
