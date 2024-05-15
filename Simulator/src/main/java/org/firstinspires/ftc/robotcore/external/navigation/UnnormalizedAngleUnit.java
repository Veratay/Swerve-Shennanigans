//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

public enum UnnormalizedAngleUnit {
    DEGREES(0),
    RADIANS(1);

    public final byte bVal;

    private UnnormalizedAngleUnit(int i) {
        this.bVal = (byte)i;
    }

    public double fromDegrees(double degrees) {
        switch(this) {
            case RADIANS:
            default:
                return degrees / 180.0D * 3.141592653589793D;
            case DEGREES:
                return degrees;
        }
    }

    public float fromDegrees(float degrees) {
        switch(this) {
            case RADIANS:
            default:
                return degrees / 180.0F * 3.1415927F;
            case DEGREES:
                return degrees;
        }
    }

    public double fromRadians(double radians) {
        switch(this) {
            case RADIANS:
            default:
                return radians;
            case DEGREES:
                return radians / 3.141592653589793D * 180.0D;
        }
    }

    public float fromRadians(float radians) {
        switch(this) {
            case RADIANS:
            default:
                return radians;
            case DEGREES:
                return radians / 3.1415927F * 180.0F;
        }
    }

    public double fromUnit(UnnormalizedAngleUnit them, double theirs) {
        switch(them) {
            case RADIANS:
            default:
                return this.fromRadians(theirs);
            case DEGREES:
                return this.fromDegrees(theirs);
        }
    }

    public float fromUnit(UnnormalizedAngleUnit them, float theirs) {
        switch(them) {
            case RADIANS:
            default:
                return this.fromRadians(theirs);
            case DEGREES:
                return this.fromDegrees(theirs);
        }
    }

    public double toDegrees(double inOurUnits) {
        switch(this) {
            case RADIANS:
            default:
                return DEGREES.fromRadians(inOurUnits);
            case DEGREES:
                return DEGREES.fromDegrees(inOurUnits);
        }
    }

    public float toDegrees(float inOurUnits) {
        switch(this) {
            case RADIANS:
            default:
                return DEGREES.fromRadians(inOurUnits);
            case DEGREES:
                return DEGREES.fromDegrees(inOurUnits);
        }
    }

    public double toRadians(double inOurUnits) {
        switch(this) {
            case RADIANS:
            default:
                return RADIANS.fromRadians(inOurUnits);
            case DEGREES:
                return RADIANS.fromDegrees(inOurUnits);
        }
    }

    public float toRadians(float inOurUnits) {
        switch(this) {
            case RADIANS:
            default:
                return RADIANS.fromRadians(inOurUnits);
            case DEGREES:
                return RADIANS.fromDegrees(inOurUnits);
        }
    }

    public AngleUnit getNormalized() {
        switch(this) {
            case RADIANS:
            default:
                return AngleUnit.RADIANS;
            case DEGREES:
                return AngleUnit.DEGREES;
        }
    }
}
