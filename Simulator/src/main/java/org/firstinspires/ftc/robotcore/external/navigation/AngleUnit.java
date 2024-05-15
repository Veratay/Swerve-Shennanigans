//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

public enum AngleUnit {
    DEGREES(0),
    RADIANS(1);

    public final byte bVal;
    protected static final double TwoPi = 6.283185307179586D;
    public static final float Pif = 3.1415927F;

    private AngleUnit(int i) {
        this.bVal = (byte)i;
    }

    public double fromDegrees(double degrees) {
        switch(this) {
            case RADIANS:
            default:
                return this.normalize(degrees / 180.0D * 3.141592653589793D);
            case DEGREES:
                return this.normalize(degrees);
        }
    }

    public float fromDegrees(float degrees) {
        switch(this) {
            case RADIANS:
            default:
                return this.normalize(degrees / 180.0F * 3.1415927F);
            case DEGREES:
                return this.normalize(degrees);
        }
    }

    public double fromRadians(double radians) {
        switch(this) {
            case RADIANS:
            default:
                return this.normalize(radians);
            case DEGREES:
                return this.normalize(radians / 3.141592653589793D * 180.0D);
        }
    }

    public float fromRadians(float radians) {
        switch(this) {
            case RADIANS:
            default:
                return this.normalize(radians);
            case DEGREES:
                return this.normalize(radians / 3.1415927F * 180.0F);
        }
    }

    public double fromUnit(AngleUnit them, double theirs) {
        switch(them) {
            case RADIANS:
            default:
                return this.fromRadians(theirs);
            case DEGREES:
                return this.fromDegrees(theirs);
        }
    }

    public float fromUnit(AngleUnit them, float theirs) {
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

    public double normalize(double mine) {
        switch(this) {
            case RADIANS:
            default:
                return normalizeRadians(mine);
            case DEGREES:
                return normalizeDegrees(mine);
        }
    }

    public float normalize(float mine) {
        switch(this) {
            case RADIANS:
            default:
                return normalizeRadians(mine);
            case DEGREES:
                return normalizeDegrees(mine);
        }
    }

    public static double normalizeDegrees(double degrees) {
        while(degrees >= 180.0D) {
            degrees -= 360.0D;
        }

        while(degrees < -180.0D) {
            degrees += 360.0D;
        }

        return degrees;
    }

    public static float normalizeDegrees(float degrees) {
        return (float)normalizeDegrees((double)degrees);
    }

    public static double normalizeRadians(double radians) {
        while(radians >= 3.141592653589793D) {
            radians -= 6.283185307179586D;
        }

        while(radians < -3.141592653589793D) {
            radians += 6.283185307179586D;
        }

        return radians;
    }

    public static float normalizeRadians(float radians) {
        return (float)normalizeRadians((double)radians);
    }

    public UnnormalizedAngleUnit getUnnormalized() {
        switch(this) {
            case RADIANS:
            default:
                return UnnormalizedAngleUnit.RADIANS;
            case DEGREES:
                return UnnormalizedAngleUnit.DEGREES;
        }
    }
}
