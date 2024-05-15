package org.aperture;

@SuppressWarnings("all")
public class LogConfig {
    private static boolean ALL = true;
    public static boolean ODOMETRY = true && ALL;
    public static boolean VISION = true && ALL;
        public static boolean VISION_POINTS = true && VISION;
        public static boolean VISION_APRILTAG = true && VISION;
    public static boolean BEZIER_MOVE = true && ALL;
    public static boolean LOOPTIME = true && ALL;
    public static boolean COMMAND = true && ALL;
        public static boolean COMMAND_ACTIVE = true && COMMAND;
        public static boolean PROMISE = true && ALL;
    public static boolean INPUT = true && ALL;

    public static String ODOMETRY_TAG = "ODOMETRY: ";
    public static String VISION_TAG = "VISION: ";
    public static String MOVE_TAG = "MOVE: ";
    public static String LOOPTIME_TAG = "LOOPTIME: ";
    public static String COMMAND_TAG = "APERTURE COMMAND: ";
    public static String PROMISE_TAG = "APERTURE PROMISE: ";
    public static String INPUT_TAG = "APERTURE INPUT: ";
}