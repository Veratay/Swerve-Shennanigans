package org.aperture.hardware;

import com.qualcomm.robotcore.hardware.TouchSensor;

public class CachedTouchSensor {
    private final TouchSensor touchSensor;

    public boolean pressed = false;
    public CachedTouchSensor(TouchSensor touchSensor) {
        this.touchSensor = touchSensor;
    }

    public void read() { pressed = touchSensor.isPressed(); }
    public boolean isPressed() { return this.pressed; }
}
