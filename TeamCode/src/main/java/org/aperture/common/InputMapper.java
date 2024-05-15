package org.aperture.common;

/**
 * @author FTC Team #5064 Aperture Science
 * 2023-2024 Season, Centerstage
 *
 * Version 1.0
 */

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class InputMapper {
    public enum Actions {
        HANG,
        PLANE,
        LET_GO_OR_PICKUP,
        INTAKE,
        OUTTAKE,
        RETRACT_INTAKE,
        INTAKE_FULL_EXTEND,
        INTAKE_MED_EXTEND,
        INTAKE_SHORT_EXTEND,
        TRANSFER,
        DEPO_FULL_DOWN,
        DEPO_DOWN,
        DEPO_UP,
        DEPO_FULL_UP,
        DOOR_TOGGLE,
        DEPO_MANUAL,
        INTAKE_MANUAL,
        WRIST_CLOCKWISE,
        WRIST_COUNTERCLOCKWISE
    }

    private Gamepad gamepad1;
    private Gamepad gamepad2;
    private final double triggerThreshold;
    private final Map<Actions, Integer> intMap;
    private final boolean[] currPressed = new boolean[Actions.values().length];
    private final boolean[] prevPressed = new boolean[Actions.values().length];
    private final double[] values = new double[Actions.values().length];

    public InputMapper(Gamepad gamepad1, Gamepad gamepad2, double triggerThreshold) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.triggerThreshold = triggerThreshold;
        intMap = new HashMap<>();
        for (int i = 0; i < Actions.values().length; i++) {
            intMap.put(Actions.values()[i], i);
        }
    }

    private final Map<Actions, Callable<Double>> actionMap = new HashMap<Actions, Callable<Double>>() {{
        put(Actions.HANG, () -> gamepad1.back || gamepad2.back ? 1.0 : 0.0);
        put(Actions.PLANE, () -> gamepad1.start || gamepad2.start ? 1.0 : 0.0);

        put(Actions.INTAKE, () -> (double)gamepad1.right_trigger);
        put(Actions.OUTTAKE, () -> (double)gamepad1.left_trigger);
        put(Actions.LET_GO_OR_PICKUP, () -> gamepad1.left_bumper ? 1.0 : 0.0);
        put(Actions.DOOR_TOGGLE, ()->gamepad1.right_bumper ? 1.0 : 0.0);

        put(Actions.DEPO_FULL_DOWN,()->(double)gamepad2.left_trigger);
//        put(Actions.DEPO_FULL_UP,()->gamepad1.dpad_right || gamepad2.dpad_right ? 1.0 : 0.0);
        put(Actions.DEPO_DOWN,()->gamepad1.dpad_down || gamepad2.dpad_down ? 1.0 : 0.0);
        put(Actions.DEPO_UP,()->gamepad1.dpad_up || gamepad2.dpad_up ? 1.0 : 0.0);

        put(Actions.INTAKE_SHORT_EXTEND,()->gamepad1.a || gamepad2.a ? 1.0 : 0.0);
        put(Actions.INTAKE_MED_EXTEND,()->gamepad1.b || gamepad2.b ? 1.0 : 0.0);
        put(Actions.INTAKE_FULL_EXTEND,()->gamepad1.y || gamepad2.y ? 1.0 : 0.0);
        put(Actions.RETRACT_INTAKE,()->(double)gamepad2.right_trigger);
        put(Actions.TRANSFER,()->gamepad1.x || gamepad2.x ? 1.0 : 0.0);

        put(Actions.INTAKE_MANUAL,()->-(double)gamepad2.left_stick_y);
        put(Actions.DEPO_MANUAL,()->-(double)gamepad2.right_stick_y);

        put(Actions.WRIST_CLOCKWISE,()->gamepad2.right_bumper ? 1.0 : 0.0);
        put(Actions.WRIST_COUNTERCLOCKWISE,()->gamepad2.left_bumper ? 1.0 : 0.0);

    }};

    public void read() {
        System.arraycopy(currPressed, 0, prevPressed, 0, Actions.values().length);

        try {
            for (int i = 0; i < Actions.values().length; i++) {
                Actions action = Actions.values()[i];
                Callable<Double> func = actionMap.get(action);

                double value = func.call();
                values[i] = value;
                currPressed[i] = value > triggerThreshold;
            }
        } catch (Exception ignored) {}
    }

    public boolean isPressed(Actions action) {
        return currPressed[intMap.get(action)];
    }

    public boolean justReleased(Actions action) {
        return prevPressed[intMap.get(action)] && !currPressed[intMap.get(action)];
    }

    public boolean justPressed(Actions action) {
        return currPressed[intMap.get(action)] && !prevPressed[intMap.get(action)];
    }

    public double get(Actions action) {
        return values[intMap.get(action)];
    }

    public boolean areJoysticksOverThreshGP1(double thresh) {
        double leftDist = Math.sqrt(Math.pow(gamepad1.left_stick_x,2)+Math.pow(gamepad1.left_stick_y,2));
        return leftDist > thresh || Math.abs(gamepad1.right_stick_x) > thresh;
    }

    public boolean areJoysticksOverThreshGP2(double thresh) {
        double leftDist = Math.sqrt(Math.pow(gamepad2.left_stick_x,2)+Math.pow(gamepad2.left_stick_y,2));
        return leftDist > thresh || Math.abs(gamepad2.right_stick_x) > thresh;
    }
}