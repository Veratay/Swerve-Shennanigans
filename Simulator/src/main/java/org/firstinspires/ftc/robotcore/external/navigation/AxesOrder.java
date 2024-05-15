//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

public enum AxesOrder {
    XZX(new int[]{0, 2, 0}),
    XYX(new int[]{0, 1, 0}),
    YXY(new int[]{1, 0, 1}),
    YZY(new int[]{1, 2, 1}),
    ZYZ(new int[]{2, 1, 2}),
    ZXZ(new int[]{2, 0, 2}),
    XZY(new int[]{0, 2, 1}),
    XYZ(new int[]{0, 1, 2}),
    YXZ(new int[]{1, 0, 2}),
    YZX(new int[]{1, 2, 0}),
    ZYX(new int[]{2, 1, 0}),
    ZXY(new int[]{2, 0, 1});

    private final int[] indices;

    private AxesOrder(int[] indices) {
        this.indices = indices;
    }

    public int[] indices() {
        return this.indices;
    }

    public Axis[] axes() {
        Axis[] result = new Axis[]{Axis.fromIndex(this.indices[0]), Axis.fromIndex(this.indices[1]), Axis.fromIndex(this.indices[2])};
        return result;
    }

    public AxesOrder reverse() {
        switch(this) {
            case XZX:
            default:
                return XZX;
            case XYX:
                return XYX;
            case YXY:
                return YXY;
            case YZY:
                return YZY;
            case ZYZ:
                return ZYZ;
            case ZXZ:
                return ZXZ;
            case XZY:
                return YZX;
            case XYZ:
                return ZYX;
            case YXZ:
                return ZXY;
            case YZX:
                return XZY;
            case ZYX:
                return XYZ;
            case ZXY:
                return YXZ;
        }
    }
}
