package org.aperture.control;

public class DualServoRadController {
    private final ServoRadController servo1;
    private final ServoRadController servo2;

    public DualServoRadController(ServoRadController servo1, ServoRadController servo2) {
        this.servo1 = servo1;
        this.servo2 = servo2;
    }

    public void setPos(double rad) {
        servo1.setPos(rad);
        servo2.setPos(rad);
    }
}
