//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.openftc.easyopencv;

import org.opencv.core.Mat;

public abstract class TimestampedOpenCvPipeline extends OpenCvPipeline {
    private long timestamp;

    public TimestampedOpenCvPipeline() {
    }

    public final Mat processFrame(Mat input) {
        return this.processFrame(input, this.timestamp);
    }

    public abstract Mat processFrame(Mat var1, long var2);

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
