package org.aperture.control.depo;

public class ArmPose {
    public final double shoulderRad;
    public final double elbowRad;
    public final double slideT;

    protected ArmPose(double shoulderRad, double elbowRad, double slideT) {
        this.shoulderRad = shoulderRad;
        this.elbowRad = elbowRad;
        this.slideT = slideT;
    }
}
