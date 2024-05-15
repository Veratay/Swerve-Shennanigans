package aperture.simulator.math;

public class DcMotorDynamics implements RungeKutta4.TimeInvariantDiffEqSystem<Double[]> {

    private final double k_t;
    private final double R;
    private final double L;
    private final double g_r;
    private final double c_f;
    private final double J;
    public DcMotorDynamics(double torqueConstant, double resistance, double inductance, double gearRatio, double load, double coloumbFriction) {
        k_t = torqueConstant;
        R = resistance;
        L = inductance;
        g_r = gearRatio;
        J = load;
        c_f = coloumbFriction;
    }


    @Override
    public double[] calc(double[] state, Double[] input) {
        return new double[] {
                -k_t*state[1]*g_r/L - state[0]*R/L +input[0]/L,
                -Math.signum(state[1])*c_f/J + state[0]*k_t*g_r/J + input[1]
        };
    }
}
