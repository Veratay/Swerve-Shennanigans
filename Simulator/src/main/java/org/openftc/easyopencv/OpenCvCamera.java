//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openftc.easyopencv;


import org.firstinspires.ftc.vision.VisionPortalImpl;

public interface OpenCvCamera  {
    public void setOnDrawCallback(VisionPortalImpl.ProcessingPipeline processor);
    int CAMERA_OPEN_ERROR_FAILURE_TO_OPEN_CAMERA_DEVICE = -1;
    int CAMERA_OPEN_ERROR_POSTMORTEM_OPMODE = -2;

//    /** @deprecated */
//    @Deprecated
//    int openCameraDevice();

    void openCameraDeviceAsync(OpenCvCamera.AsyncCameraOpenListener var1);

//    void closeCameraDevice();

    void closeCameraDeviceAsync(OpenCvCamera.AsyncCameraCloseListener var1);

//    void showFpsMeterOnViewport(boolean var1);

    void pauseViewport();

    void resumeViewport();

//    void setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy var1);
//
//    void setViewportRenderer(OpenCvCamera.ViewportRenderer var1);

//    void startStreaming(int var1, int var2);

    void startStreaming(int var1, int var2, OpenCvCameraRotation var3);

    void stopStreaming();

    void setPipeline(OpenCvPipeline var1);

//    int getFrameCount();

    float getFps();

//    int getPipelineTimeMs();
//
//    int getOverheadTimeMs();
//
//    int getTotalFrameTimeMs();
//
//    int getCurrentPipelineMaxFps();

//    void startRecordingPipeline(PipelineRecordingParameters var1);

//    void stopRecordingPipeline();

    public static enum ViewportRenderer {
        SOFTWARE,
        /** @deprecated */
        @Deprecated
        GPU_ACCELERATED,
        NATIVE_VIEW;

        private ViewportRenderer() {
        }
    }

    public static enum ViewportRenderingPolicy {
        MAXIMIZE_EFFICIENCY,
        OPTIMIZE_VIEW;

        private ViewportRenderingPolicy() {
        }
    }

    public interface AsyncCameraCloseListener {
        void onClose();
    }

    public interface AsyncCameraOpenListener {
        void onOpened();

        void onError(int var1);
    }
}
