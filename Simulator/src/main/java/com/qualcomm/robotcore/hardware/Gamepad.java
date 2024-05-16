package com.qualcomm.robotcore.hardware;

import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad {
    public  float left_stick_x = 0f;

    /**
     * left analog stick vertical axis
     */
    public  float left_stick_y = 0f;

    /**
     * right analog stick horizontal axis
     */
    public  float right_stick_x = 0f;

    /**
     * right analog stick vertical axis
     */
    public  float right_stick_y = 0f;

    /**
     * dpad up
     */
    public  boolean dpad_up = false;

    /**
     * dpad down
     */
    public  boolean dpad_down = false;

    /**
     * dpad left
     */
    public  boolean dpad_left = false;

    /**
     * dpad right
     */
    public  boolean dpad_right = false;

    /**
     * button a
     */
    public  boolean a = false;

    /**
     * button b
     */
    public  boolean b = false;

    /**
     * button x
     */
    public  boolean x = false;

    /**
     * button y
     */
    public  boolean y = false;

    /**
     * button guide - often the large button in the middle of the controller. The OS may
     * capture this button before it is sent to the app; in which case you'll never
     * receive it.
     */
    public  boolean guide = false;

    /**
     * button start
     */
    public  boolean start = false;

    /**
     * button back
     */
    public  boolean back = false;

    /**
     * button left bumper
     */
    public  boolean left_bumper = false;

    /**
     * button right bumper
     */
    public  boolean right_bumper = false;

    /**
     * left stick button
     */
    public  boolean left_stick_button = false;

    /**
     * right stick button
     */
    public  boolean right_stick_button = false;

    /**
     * left trigger
     */
    public  float left_trigger = 0f;

    /**
     * right trigger
     */
    public  float right_trigger = 0f;

    public float home = 0f;

    final int num;

    public Gamepad(int gamepadNum) {
        if (gamepadNum==1) {
            num = GLFW_JOYSTICK_1;
        } else {
            num = GLFW_JOYSTICK_2;
        }
        ByteBuffer stateBuffer = MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF);
        state = new GLFWGamepadState(stateBuffer);
    }

    final GLFWGamepadState state;
    public void update(long window) {
        left_stick_x = (float) (glfwGetKey(window, GLFW_KEY_RIGHT)==GLFW_PRESS ? 1.0 : glfwGetKey(window, GLFW_KEY_LEFT)==GLFW_PRESS ? -1.0 : 0.0);
        left_stick_y = (float) (glfwGetKey(window, GLFW_KEY_DOWN)==GLFW_PRESS ? 1.0 : glfwGetKey(window, GLFW_KEY_UP)==GLFW_PRESS ? -1.0 : 0.0);
        right_stick_x = (float) (glfwGetKey(window, GLFW_KEY_L)==GLFW_PRESS ? 1.0 : glfwGetKey(window, GLFW_KEY_J)==GLFW_PRESS ? -1.0 : 0.0);
        right_stick_y = (float) (glfwGetKey(window, GLFW_KEY_K)==GLFW_PRESS ? 1.0 : glfwGetKey(window, GLFW_KEY_I)==GLFW_PRESS ? -1.0 : 0.0);

        if(glfwGetGamepadState(num,state)) {
            a = state.buttons(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS;
            b = state.buttons(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS;
            x = state.buttons(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS;
            y = state.buttons(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS;
            dpad_down = state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW_PRESS;
            dpad_up = state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW_PRESS;
            dpad_left = state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW_PRESS;
            dpad_right = state.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW_PRESS;
            left_bumper = state.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS;
            right_bumper = state.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS;
            right_stick_button = state.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB) == GLFW_PRESS;
            left_stick_button = state.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS;

            left_stick_x = state.axes(GLFW_GAMEPAD_AXIS_LEFT_X);
            left_stick_y = state.axes(GLFW_GAMEPAD_AXIS_LEFT_Y);
            right_stick_x = state.axes(GLFW_GAMEPAD_AXIS_RIGHT_X);
            right_stick_y = state.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y);
            left_trigger = (float)(state.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER)+1.0)/2f;
            right_trigger = (float)(state.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)+1.0)/2f;
        }

//        if (axes != null && buttons != null) {
//            System.out.println( "left trigger" + axes.get(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER));
//            left_stick_x = axes.get(GLFW_GAMEPAD_AXIS_LEFT_X);
//            left_stick_y = axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y);
//            right_stick_x = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_X);
//            right_stick_y = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_Y);
//            left_trigger = axes.get(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
//            right_trigger = axes.get(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
//
//            a = buttons.get(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS;
//            b = buttons.get(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS;
//            x = buttons.get(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS;
//            y = buttons.get(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS;
//
////            dpad_down = buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW_PRESS;
////            dpad_up = buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW_PRESS;
////            dpad_left = buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW_PRESS;
////            dpad_right = buttons.get(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW_PRESS;
//
//            left_bumper = buttons.get(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS;
//            right_bumper = buttons.get(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS;
//        }
    }
}
