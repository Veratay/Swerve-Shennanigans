package com.qualcomm.hardware.bosch;

import com.qualcomm.robotcore.hardware.IMU;
import aperture.simulator.Simulator;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class BNO055IMU extends IMU {
    double bias = 0;
    public Orientation getAngularOrientation(AxesReference reference, AxesOrder order, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit angleUnit) {
        return Simulator.getAngularOrientation(bias);
    }

    public Orientation getRobotYawPitchRollAngles() {
        return Simulator.getAngularOrientation(bias);
    }

    @Override
    public void resetYaw() {
        bias = Simulator.getAngularOrientation(0).firstAngle;
    }
}
