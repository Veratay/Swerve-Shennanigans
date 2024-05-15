package org.aperture.hardware;

public class DualServo {
    private final CachedServo servo1;
    private final CachedServo servo2;
    private double currentPosition;

    DualServo(CachedServo servo1, CachedServo servo2) {
        this.servo1 = servo1;
        this.servo2 = servo2;
    }

    public void setPos(double position) {
        servo1.setPos(position);
        servo2.setPos(1.0 - position);
        this.currentPosition = position;
    }

    public double getPos() {
        return currentPosition;
    }

    public void write() {
        servo1.write();
        servo2.write();
    }

}
