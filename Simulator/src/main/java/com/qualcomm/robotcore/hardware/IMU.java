package com.qualcomm.robotcore.hardware;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public abstract class IMU implements HardwareDevice {
    double bias = 0;
    public static class Parameters {
        // NOTE: Whenever this class is updated, you must also update the copy() method, and any
        // subclasses.
        public ImuOrientationOnRobot imuOrientationOnRobot;

        /**
         * @param imuOrientationOnRobot The orientation of the IMU relative to the robot. If the IMU
         *                              is in a REV Control or Expansion Hub, create an instance of
         *                              {@code com.qualcomm.hardware.rev.RevHubOrientationOnRobot}
         *                              (from the Hardware module).
         */
        public Parameters(ImuOrientationOnRobot imuOrientationOnRobot) {
            this.imuOrientationOnRobot = imuOrientationOnRobot;
        }

        public Parameters copy() {
            return new Parameters(imuOrientationOnRobot);
        }
    }

    public interface ImuOrientationOnRobot {}

    public void initialize(Parameters parameters) {}

    public abstract void resetYaw();

    public abstract Orientation getRobotYawPitchRollAngles();
}
