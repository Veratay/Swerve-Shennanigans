package org.aperture.control;

import org.aperture.hardware.RobotHardware;

public class PIDController {
    public double P;
    public double I;
    public double D;
    public double decay;

    double errSum = 0;
    public double oldErr = 0;

    public PIDController(double p, double i, double d) { this(p,i,d,0.95); }
    public PIDController(double p, double i, double d, double decay) {
        this.P = p;
        this.I = i;
        this.D = d;
        this.decay =decay;
    }

    public double run(double err) {
        double dt = RobotHardware.dt;
        double result = P*err + I*errSum + ((err-oldErr)/dt)*D;
        oldErr = err;
        errSum = errSum*decay + err*dt;
        return result;
    }
}
