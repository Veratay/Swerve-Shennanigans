package com.qualcomm.robotcore.hardware;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.*;

public class HardwareMap {
    public PhantomHardwareMapping<HardwareDevice> devices;
    HashMap<String, HardwareDevice> map;
    public HardwareMap(PhantomHardwareMapping<HardwareDevice> devices) {
        this.devices = devices;
        map = devices.getAll();
    }

    public <T> T get(Class<T> classIn, String deviceName) {
        for (String name : map.keySet()) {
            if (Objects.equals(deviceName, name) && classIn.isInstance(map.get(name))) {
                return classIn.cast(map.get(name));
            }
        }
        return null;
    }

    public Object get(String deviceName) {
        for (String name : map.keySet()) {
            if (Objects.equals(deviceName, name)) {
                return map.get(name);
            }
        }
        return null;
    }

    public HardwareMap appContext = this;
    public HardwareMap getResources() {return this;}
    public HardwareMap getPackageName() {return this;}
    public int getIdentifier(String str1, String str2, HardwareMap hardwareMap) { return 0; }
}


