package aperture.simulator.math;

//TODO: maybe update to RK45 with adaptive step size.
public class RungeKutta4<I> {

    public interface TimeInvariantDiffEqSystem<I> {
        //must not mutate state passed in.
        double[] calc(double[] state, I input);
    }

    public final double[] state;
    private final TimeInvariantDiffEqSystem<I> diffEqSystem;

    public RungeKutta4(double[] initial, TimeInvariantDiffEqSystem<I> diffEqSystem) {
        this.state = initial;
        this.diffEqSystem = diffEqSystem;
    }
    public void step(I input, double dt) {
        //clones are needed because java arrays are pass-by-reference.
        double[] f1 = diffEqSystem.calc(state,input);
        double[] stateF1 = state.clone();
        smallStep(stateF1,f1,dt/2.0);
        double[] f2 = diffEqSystem.calc(stateF1,input);
        double[] stateF2 = state.clone();
        smallStep(stateF2,f2,dt/2.0);
        double[] f3 = diffEqSystem.calc(stateF2,input);
        double[] stateF3 = state.clone();
        smallStep(stateF3,f3,dt);
        double[] f4 = diffEqSystem.calc(stateF3,input);

        for(int i=0; i<state.length; i++) {
            state[i] += (dt/6.0)*(f1[i] + 2.0*f2[i] + 2.0*f3[i] + f4[i]);
        }
    }

    private void smallStep(double[] initial, double[] dx, double dt) {
        for(int i=0; i<initial.length; i++) {
            initial[i] += dx[i]*dt;
        }
    }
}
