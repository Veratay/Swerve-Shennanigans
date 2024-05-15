package com.qualcomm.hardware.rev;

import com.qualcomm.robotcore.hardware.IMU;

public class RevHubOrientationOnRobot implements IMU.ImuOrientationOnRobot {

    public RevHubOrientationOnRobot(LogoFacingDirection logo,UsbFacingDirection usb) {

    }
    public enum LogoFacingDirection {
        RIGHT,LEFT,DOWN,UP
    }

    public enum UsbFacingDirection {
        RIGHT,LEFT,DOWN,UP
    }
}
