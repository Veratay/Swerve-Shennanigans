//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.hardware.camera;

import androidx.annotation.NonNull;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
//import androidx.annotation.Nullable;
//import com.qualcomm.robotcore.hardware.HardwareDevice;
//import com.qualcomm.robotcore.util.SerialNumber;

public interface WebcamName extends CameraName, HardwareDevice
//        ,HardwareDevice {

{
    @NonNull
//    SerialNumber getSerialNumber();

    String getUsbDeviceNameIfAttached();

    boolean isAttached();
}
