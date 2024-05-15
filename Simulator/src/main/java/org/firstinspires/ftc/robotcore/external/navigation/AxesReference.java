//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

public enum AxesReference {
    EXTRINSIC,
    INTRINSIC;

    private AxesReference() {
    }

    public AxesReference reverse() {
        switch(this) {
            case EXTRINSIC:
            default:
                return INTRINSIC;
            case INTRINSIC:
                return EXTRINSIC;
        }
    }
}
