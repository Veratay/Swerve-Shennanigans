package com.qualcomm.robotcore.hardware;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public abstract class DistanceSensor implements HardwareDevice {
    public abstract double getDistance(DistanceUnit unit);
}
