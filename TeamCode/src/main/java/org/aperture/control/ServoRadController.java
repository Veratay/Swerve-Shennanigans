package org.aperture.control;

import org.aperture.hardware.CachedServo;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;
//allows to set servo positions in radians
public class ServoRadController {

    private final double zeroPos;
    private final double onePos;

    public final CachedServo servo;

    public ServoRadController(CachedServo servo, double zeroPos, double onePos) {
        this.zeroPos = zeroPos;
        this.onePos = onePos;
        this.servo = servo;
    }

    public void setPos(double rad) {
        double x = normalizeRadians(rad-zeroPos)/normalizeRadians(onePos-zeroPos);
        x = Math.max(0,Math.min(1,x));
        servo.setPos(x);
    }
}
