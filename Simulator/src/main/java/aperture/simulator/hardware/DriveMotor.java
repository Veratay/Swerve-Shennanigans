package aperture.simulator.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;

public class DriveMotor implements DcMotor {

    double power = 0;
    double targetPos = 0;
    double currentPos = 0;
    RunMode runMode = RunMode.RUN_WITHOUT_ENCODER;
    Direction direction = Direction.FORWARD;
    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {

    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return ZeroPowerBehavior.UNKNOWN;
    }

    @Override
    public void setTargetPosition(int position) {
        targetPos = position;
    }

    @Override
    public int getTargetPosition() {
        return (int)targetPos;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return (int)currentPos;
    }

    @Override
    public void setMode(RunMode mode) {
        runMode = mode;
    }

    @Override
    public RunMode getMode() {
        return runMode;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public double getPower() {
        return this.power;
    }

    void run() {

    }
}
