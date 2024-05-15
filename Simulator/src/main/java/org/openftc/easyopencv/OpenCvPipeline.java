package org.openftc.easyopencv;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class OpenCvPipeline {
    public void init(Mat mat) {};
    public Mat processFrame(Mat mat) {return mat;}
    public void saveMatToDiskFullPath(Mat mat, String path) {
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2BGR);
        path = "D:/FTC/Winston_sim" + path;
        Imgcodecs.imwrite(path,mat);
    };

    public void requestViewportDrawHook(Object userContext) {

    }
}
