//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision;

import android.util.Size;
import java.util.ArrayList;
import java.util.List;
//import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.CameraControl;
//import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.openftc.easyopencv.OpenCvCameraFactory.ViewportSplitMethod;

public abstract class VisionPortal {

    public VisionPortal() {
    }

//    public static int[] makeMultiPortalView(int numPortals, VisionPortal.MultiPortalLayout mpl) {
//        return OpenCvCameraFactory.getInstance().splitLayoutForMultipleViewports(DEFAULT_VIEW_CONTAINER_ID, numPortals, mpl.viewportSplitMethod);
//    }

//    public static VisionPortal easyCreateWithDefaults(BuiltinCameraDirection cameraDirection, VisionProcessor... processors) {
//        return (new VisionPortal.Builder()).setCamera(cameraDirection).addProcessors(processors).build();
//    }

    public static VisionPortal easyCreateWithDefaults(CameraName cameraName, VisionProcessor... processors) {
        return (new VisionPortal.Builder()).setCamera(cameraName).addProcessors(processors).build();
    }

    public abstract void setProcessorEnabled(VisionProcessor var1, boolean var2);

    public abstract boolean getProcessorEnabled(VisionProcessor var1);

    public abstract VisionPortal.CameraState getCameraState();

    public abstract void saveNextFrameRaw(String var1);

    public abstract void stopStreaming();

    public abstract void resumeStreaming();

    public abstract void stopLiveView();

    public abstract void resumeLiveView();

    public abstract float getFps();

//    public abstract <T extends CameraControl> T getCameraControl(Class<T> var1);

//    public abstract void setActiveCamera(WebcamName var1);

//    public abstract WebcamName getActiveCamera();

    public abstract void close();

    public static enum CameraState {
        OPENING_CAMERA_DEVICE,
        CAMERA_DEVICE_READY,
        STARTING_STREAM,
        STREAMING,
        STOPPING_STREAM,
        CLOSING_CAMERA_DEVICE,
        CAMERA_DEVICE_CLOSED,
        ERROR;

        private CameraState() {
        }
    }

    public static class Builder {
        private static final ArrayList<VisionProcessor> attachedProcessors = new ArrayList();
        private CameraName camera;
        private int cameraMonitorViewId;
        private boolean autoStopLiveView;
        private Size cameraResolution;
        private VisionPortal.StreamFormat streamFormat;
        private VisionPortal.StreamFormat STREAM_FORMAT_DEFAULT;
        private final List<VisionProcessor> processors;

        public Builder() {
            this.cameraMonitorViewId = 0;
            this.autoStopLiveView = true;
            this.cameraResolution = new Size(640, 480);
            this.streamFormat = null;
            this.STREAM_FORMAT_DEFAULT = VisionPortal.StreamFormat.YUY2;
            this.processors = new ArrayList();
        }

        public VisionPortal.Builder setCamera(CameraName camera) {
            this.camera = camera;
            return this;
        }

//        public VisionPortal.Builder setCamera(BuiltinCameraDirection cameraDirection) {
//            this.camera = Simulator.nameFromCameraDirection(cameraDirection);
//            return this;
//        }

        public VisionPortal.Builder setStreamFormat(VisionPortal.StreamFormat streamFormat) {
            this.streamFormat = streamFormat;
            return this;
        }

        public VisionPortal.Builder enableCameraMonitoring(boolean enableLiveView) {
            int viewId;
            if (enableLiveView) {
                viewId = 0;
            } else {
                viewId = 0;
            }

            return this.setCameraMonitorViewId(viewId);
        }

        public VisionPortal.Builder setAutoStopLiveView(boolean autoPause) {
            this.autoStopLiveView = autoPause;
            return this;
        }

        public VisionPortal.Builder setCameraMonitorViewId(int cameraMonitorViewId) {
            this.cameraMonitorViewId = cameraMonitorViewId;
            return this;
        }

        public VisionPortal.Builder setCameraResolution(Size cameraResolution) {
            this.cameraResolution = cameraResolution;
            return this;
        }

        public VisionPortal.Builder addProcessor(VisionProcessor processor) {
            synchronized(attachedProcessors) {
                if (attachedProcessors.contains(processor)) {
                    throw new RuntimeException("This VisionProcessor has already been attached to a VisionPortal, either a different one or perhaps even this same portal.");
                }

                attachedProcessors.add(processor);
            }

            this.processors.add(processor);
            return this;
        }

        public VisionPortal.Builder addProcessors(VisionProcessor... processors) {
            VisionProcessor[] var2 = processors;
            int var3 = processors.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                VisionProcessor p = var2[var4];
                this.addProcessor(p);
            }

            return this;
        }

        public VisionPortal build() {
            if (this.camera == null) {
                throw new RuntimeException("You can't build a vision portal without setting a camera!");
            } else {
                if (this.streamFormat != null) {
                    if (!this.camera.isWebcam() && !this.camera.isSwitchable()) {
                        throw new IllegalStateException("setStreamFormat() may only be used with a webcam");
                    }
                } else {
                    this.streamFormat = this.STREAM_FORMAT_DEFAULT;
                }

                return new VisionPortalImpl(this.camera, this.cameraMonitorViewId, this.autoStopLiveView, this.cameraResolution, this.streamFormat, (VisionProcessor[])this.processors.toArray(new VisionProcessor[this.processors.size()]));
            }
        }
    }

    public static enum MultiPortalLayout {
        VERTICAL(ViewportSplitMethod.VERTICALLY),
        HORIZONTAL(ViewportSplitMethod.HORIZONTALLY);

        private final ViewportSplitMethod viewportSplitMethod;

        private MultiPortalLayout(ViewportSplitMethod viewportSplitMethod) {
            this.viewportSplitMethod = viewportSplitMethod;
        }
    }

    public static enum StreamFormat {
        YUY2(org.openftc.easyopencv.OpenCvWebcam.StreamFormat.YUY2),
        MJPEG(org.openftc.easyopencv.OpenCvWebcam.StreamFormat.MJPEG);

        final org.openftc.easyopencv.OpenCvWebcam.StreamFormat eocvStreamFormat;

        private StreamFormat(org.openftc.easyopencv.OpenCvWebcam.StreamFormat eocvStreamFormat) {
            this.eocvStreamFormat = eocvStreamFormat;
        }
    }
}
