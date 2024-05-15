package aperture.simulator.hardware;

import com.qualcomm.robotcore.hardware.Servo;

public class PhantomServo extends Servo {
    public volatile double pos = 0;
    public void setPosition(double pos) {
        this.pos = pos;
    }
}
