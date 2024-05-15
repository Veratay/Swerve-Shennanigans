package org.openftc.easyopencv;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class OpenCvCameraFactory {
    public static OpenCvWebcam leftCamera = new OpenCvWebcam();
    public static OpenCvWebcam rightCamera = new OpenCvWebcam();
    public enum ViewportSplitMethod {
        HORIZONTALLY,
        VERTICALLY
    }
    public static OpenCvCameraFactory instance = new OpenCvCameraFactory();
    public static OpenCvCameraFactory getInstance() {
        return instance;
    }

    public static void setInstance(OpenCvCameraFactory factory) {
        instance = factory;
    }

    public int[] splitLayoutForMultipleViewports(int containerId, int numViewports, ViewportSplitMethod viewportSplitMethod) {
        //left right
        return new int[] {0,1};
    }

    public OpenCvWebcam createWebcam(WebcamName webcamName, int viewportContainerIds) {
        if(viewportContainerIds==0) return leftCamera;
        return rightCamera;
    }

    public OpenCvWebcam createWebcam(WebcamName webcamName) {
        if(webcamName.getUsbDeviceNameIfAttached()=="cameraLeft") return leftCamera;
        return rightCamera;
    }
}
