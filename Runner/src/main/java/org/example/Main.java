package org.example;

//import aperture.robotics.autonomous.TestOpMode;
import aperture.simulator.Simulator;
import org.dolphinpod.DriveRobotSwerve;
import org.opencv.core.Core;

public class Main {
    static {
//        System.load("D:\\opencv\\opencv\\build\\java\\x64\\opencv_java470.dll");
        System.load("C:\\Users\\chemi\\Documents\\FTC\\opencv\\opencv\\build\\java\\x64\\opencv_java470.dll");
//        System.load("D:\\FTC\\Aperture_CS\\assets\\libs\\apriltagJNI.dll");
    }
    public static void main(String args[]) {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Simulator.init(new DriveRobotSwerve());
        Simulator.run();
    }
}

