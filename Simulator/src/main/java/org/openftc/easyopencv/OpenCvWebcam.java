package org.openftc.easyopencv;

import org.firstinspires.ftc.vision.VisionPortalImpl;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class OpenCvWebcam implements OpenCvCamera {
    private OpenCvPipeline pipeline;
    private boolean init = false;
    public boolean ready = false;
    public int cols;
    public int rows;
    public void openCameraDeviceAsync(OpenCvCamera.AsyncCameraOpenListener listener) {
        listener.onOpened();
    }
    public void closeCameraDeviceAsync(OpenCvCamera.AsyncCameraCloseListener var1) {

    }

    public void setPipeline(OpenCvPipeline pipeline) {
        this.pipeline = pipeline;
        init = false;
    }

    public void pauseViewport() {}
    public void resumeViewport() {}

    public void startStreaming(int cols, int rows, OpenCvCameraRotation rotation,OpenCvWebcam.StreamFormat var4) {
        this.cols =cols;
        this.rows = rows;
        ready = true;
    }

    public void startStreaming(int cols, int rows, OpenCvCameraRotation rotation) {
        this.cols =cols;
        this.rows = rows;
        ready = true;
    }

    public void stopStreaming() {}

    public float getFps() {
        return 30;
    }

    public Mat sendMatToPipeline(Mat mat) {
        Core.flip(mat,mat,0);
        if(!init) {
            pipeline.init(mat);
            init = true;
        }

        Mat result = pipeline.processFrame(mat);
        if(processor!=null) {
            processor.onDrawFrame(result,result.width(),result.height());
        }
        Core.flip(result,result,0);
        return result;
    }

    VisionPortalImpl.ProcessingPipeline processor = null;
    public void setOnDrawCallback(VisionPortalImpl.ProcessingPipeline processor) {
        this.processor = processor;
    }

    public static enum StreamFormat {
        YUY2,
        MJPEG;

        private StreamFormat() {
        }
    }
}
