package com.qualcomm.robotcore.hardware;

public abstract class DigitalChannel implements HardwareDevice {
    public enum Mode {
        INPUT
    }

    public void setMode(Mode mode) {};

    public abstract boolean getState();
}
