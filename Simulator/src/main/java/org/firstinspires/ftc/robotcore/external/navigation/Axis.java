//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

public enum Axis {
    X(0),
    Y(1),
    Z(2),
    UNKNOWN(-1);

    public int index;

    private Axis(int index) {
        this.index = index;
    }

    public static Axis fromIndex(int index) {
        switch(index) {
            case 0:
                return X;
            case 1:
                return Y;
            case 2:
                return Z;
            default:
                return UNKNOWN;
        }
    }
}
