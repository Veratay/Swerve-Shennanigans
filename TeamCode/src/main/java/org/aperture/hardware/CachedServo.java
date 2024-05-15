package org.aperture.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class CachedServo {
    private final Servo servo;
    private double pos = 0;
    private boolean changed = false;

    protected CachedServo(Servo servo) {
        this.servo = servo;
    }

    public void write() {
        if(changed) servo.setPosition(pos);
        changed = false;
    }

    public void setPos(double pos) {
        if(pos!=this.pos) changed = true;
        this.pos = pos;
    }
}
