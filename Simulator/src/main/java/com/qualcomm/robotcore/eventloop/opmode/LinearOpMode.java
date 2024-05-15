package com.qualcomm.robotcore.eventloop.opmode;

import aperture.simulator.Renderer;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import aperture.simulator.Simulator;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class LinearOpMode {
    Thread opModeThread = null;
    abstract public void runOpMode() throws InterruptedException;

    public Thread startOpModeInThread() {
            opModeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runOpMode();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            return opModeThread;
    }

    public void initGamepads() {
        gamepad1 = new Gamepad(1);
        gamepad2 = new Gamepad(2);
    }
    public void waitForStart() {}

    private boolean isActive = true;
    public boolean opModeIsActive() { return isActive; }
    public boolean opModeInInit() { return false; }
    public void stopOpMode() {
        isActive = false;
    }

    public Telemetry telemetry = new Telemetry();

    public Gamepad gamepad1 = null;

    public Gamepad gamepad2 = null;

    public HardwareMap hardwareMap = Simulator.getHardwareMap();

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void update() {
        gamepad1.update(Renderer.window);
        gamepad2.update(Renderer.window);
    }
}

