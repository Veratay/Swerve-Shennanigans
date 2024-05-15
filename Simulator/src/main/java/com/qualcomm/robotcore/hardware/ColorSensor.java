package com.qualcomm.robotcore.hardware;

public abstract class ColorSensor implements HardwareDevice {
    public abstract int red();
    public abstract int blue();
    public abstract int green();
    public abstract int alpha();
}
