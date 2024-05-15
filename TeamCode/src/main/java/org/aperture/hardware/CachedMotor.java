package org.aperture.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

public class CachedMotor<T extends DcMotor> {
    private final T motor;
    private double power = 0;
    private int zeroPos = 0;
    public int pos = 0;
    public int oldPos = 0;
    public double velocity = 0;

    public boolean trackVelocity = false;
    public boolean trackPos = true;
    private boolean changed = true;

    private final DcMotor.RunMode mode;

    private double minPower = -1;
    private double maxPower = 1;

    protected CachedMotor(T motor, DcMotor.RunMode mode) {
        this.motor = motor;
        this.mode = mode;
        motor.setMode(mode);
    }

    protected void write() {
        if(changed) motor.setPower(Range.clip(power,minPower,maxPower));
        changed = false;
    }

    protected void read() {
        oldPos = pos;
        if(trackPos) pos = motor.getCurrentPosition()-zeroPos;
        if(trackVelocity ) {
            if(motor instanceof DcMotorEx) {
                DcMotorEx motorEx = (DcMotorEx) motor;
                velocity = motorEx.getVelocity();
            } else {
                velocity = (double)(pos-oldPos)/RobotHardware.dt;
            }
        }
    }

    public void setPower(double power) {
        if(this.power!=power) changed = true;
        this.power = Range.clip(power,-1,1);
    }

    public double getPower() {
        return this.power;
    }

    public void resetEncoder() {
        zeroPos += pos;
    }
}
