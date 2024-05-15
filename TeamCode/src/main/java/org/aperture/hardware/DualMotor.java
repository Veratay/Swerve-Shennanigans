package org.aperture.hardware;

public class DualMotor {
    private final CachedMotor<?>motor1;
    private final CachedMotor<?>motor2;

    DualMotor(CachedMotor<?> motor1, CachedMotor<?> motor2) {
        this.motor1 = motor1;
        this.motor2 = motor2;
    }

    protected void read() { motor1.read(); }

    protected void write() {
        motor1.write();
        motor2.write();
    }

    public void setPower(double power) {
        motor1.setPower(power);
        motor2.setPower(power);
    }

    public double getPower() {
        return motor1.getPower();
    }

    public int getPos() {
        return motor1.pos;
    }

    public void resetEncoders() {
        motor1.resetEncoder();
        motor2.resetEncoder();
    }
}