package aperture.simulator.math;

public class MagicTireFormula {
    public double B;
    public double C;
    public double D;
    public double E;

    public MagicTireFormula(double B, double C, double D, double E) {
        this.B = B;
        this.C = C;
        this.D = D;
        this.E = E;
    }

    public double calc(double slip) {
        return D*Math.sin(C*Math.atan(B*slip-E*(B*slip-Math.atan(B*slip))));
    }
}
