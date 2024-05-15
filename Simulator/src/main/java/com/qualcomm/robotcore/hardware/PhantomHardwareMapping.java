package com.qualcomm.robotcore.hardware;

import java.util.HashMap;

public class PhantomHardwareMapping<T extends HardwareDevice> {
    final HashMap<String, T> devices;

    public PhantomHardwareMapping(HashMap<String,T> devices) {
        this.devices = devices;
    }

    public T get(String name) {
        return devices.get(name);
    }

    HashMap<String,T> getAll() {
        return devices;
    }
}
