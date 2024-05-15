package org.aperture.common.path;

public class TrapezoidCurve {
    public final double accTime;
    public final double decTime;
    public final double maxSpeed;
    public final double minStartSpeed;
    public final double minEndSpeed;

    public TrapezoidCurve(double accTime, double decTime, double maxSpeed, double minStartSpeed, double minEndSpeed) {
        this.accTime = accTime;
        this.decTime = decTime;
        this.maxSpeed = maxSpeed;
        this.minStartSpeed = minStartSpeed;
        this.minEndSpeed = minEndSpeed;
    }

    public double compute(double t) {
        if(t<accTime) {
            return (t/accTime)*(maxSpeed-minStartSpeed) + minStartSpeed;
        } else if(t>(1-decTime)) {
            return maxSpeed-(maxSpeed-minEndSpeed)*((1-t)/decTime);
        } else {
            return maxSpeed;
        }
    }
}
