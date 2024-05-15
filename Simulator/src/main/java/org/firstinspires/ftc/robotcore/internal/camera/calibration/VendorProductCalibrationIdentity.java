//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.internal.camera.calibration;

import org.firstinspires.ftc.robotcore.internal.system.Misc;

public class VendorProductCalibrationIdentity implements CameraCalibrationIdentity {
    public final int vid;
    public final int pid;

    public String toString() {
        return Misc.formatInvariant("%s(vid=0x%04x,pid=0x%04x)", new Object[]{this.getClass().getSimpleName(), this.vid, this.pid});
    }

    public VendorProductCalibrationIdentity(int vid, int pid) {
        this.vid = vid;
        this.pid = pid;
    }

    public boolean isDegenerate() {
        return this.vid == 0 || this.pid == 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof VendorProductCalibrationIdentity)) {
            return super.equals(o);
        } else {
            VendorProductCalibrationIdentity them = (VendorProductCalibrationIdentity)o;
            return this.vid == them.vid && this.pid == them.pid;
        }
    }

    public int hashCode() {
        return Integer.valueOf(this.vid).hashCode() ^ Integer.valueOf(this.pid).hashCode() ^ 738187;
    }
}
