//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

import java.util.Locale;

public enum DistanceUnit {
    METER(0),
    CM(1),
    MM(2),
    INCH(3);

    public final byte bVal;
    public static final double infinity = 1.7976931348623157E308D;
    public static final double mmPerInch = 25.4D;
    public static final double mPerInch = 0.0254D;

    private DistanceUnit(int i) {
        this.bVal = (byte)i;
    }

    public double fromMeters(double meters) {
        if (meters == 1.7976931348623157E308D) {
            return 1.7976931348623157E308D;
        } else {
            switch(this) {
                case METER:
                default:
                    return meters;
                case CM:
                    return meters * 100.0D;
                case MM:
                    return meters * 1000.0D;
                case INCH:
                    return meters / 0.0254D;
            }
        }
    }

    public double fromInches(double inches) {
        if (inches == 1.7976931348623157E308D) {
            return 1.7976931348623157E308D;
        } else {
            switch(this) {
                case METER:
                default:
                    return inches * 0.0254D;
                case CM:
                    return inches * 0.0254D * 100.0D;
                case MM:
                    return inches * 0.0254D * 1000.0D;
                case INCH:
                    return inches;
            }
        }
    }

    public double fromCm(double cm) {
        if (cm == 1.7976931348623157E308D) {
            return 1.7976931348623157E308D;
        } else {
            switch(this) {
                case METER:
                default:
                    return cm / 100.0D;
                case CM:
                    return cm;
                case MM:
                    return cm * 10.0D;
                case INCH:
                    return this.fromMeters(METER.fromCm(cm));
            }
        }
    }

    public double fromMm(double mm) {
        if (mm == 1.7976931348623157E308D) {
            return 1.7976931348623157E308D;
        } else {
            switch(this) {
                case METER:
                default:
                    return mm / 1000.0D;
                case CM:
                    return mm / 10.0D;
                case MM:
                    return mm;
                case INCH:
                    return this.fromMeters(METER.fromMm(mm));
            }
        }
    }

    public double fromUnit(DistanceUnit him, double his) {
        switch(him) {
            case METER:
            default:
                return this.fromMeters(his);
            case CM:
                return this.fromCm(his);
            case MM:
                return this.fromMm(his);
            case INCH:
                return this.fromInches(his);
        }
    }

    public double toMeters(double inOurUnits) {
        switch(this) {
            case METER:
            default:
                return METER.fromMeters(inOurUnits);
            case CM:
                return METER.fromCm(inOurUnits);
            case MM:
                return METER.fromMm(inOurUnits);
            case INCH:
                return METER.fromInches(inOurUnits);
        }
    }

    public double toInches(double inOurUnits) {
        switch(this) {
            case METER:
            default:
                return INCH.fromMeters(inOurUnits);
            case CM:
                return INCH.fromCm(inOurUnits);
            case MM:
                return INCH.fromMm(inOurUnits);
            case INCH:
                return INCH.fromInches(inOurUnits);
        }
    }

    public double toCm(double inOurUnits) {
        switch(this) {
            case METER:
            default:
                return CM.fromMeters(inOurUnits);
            case CM:
                return CM.fromCm(inOurUnits);
            case MM:
                return CM.fromMm(inOurUnits);
            case INCH:
                return CM.fromInches(inOurUnits);
        }
    }

    public double toMm(double inOurUnits) {
        switch(this) {
            case METER:
            default:
                return MM.fromMeters(inOurUnits);
            case CM:
                return MM.fromCm(inOurUnits);
            case MM:
                return MM.fromMm(inOurUnits);
            case INCH:
                return MM.fromInches(inOurUnits);
        }
    }

    public String toString(double inOurUnits) {
        switch(this) {
            case METER:
            default:
                return String.format(Locale.getDefault(), "%.3fm", inOurUnits);
            case CM:
                return String.format(Locale.getDefault(), "%.1fcm", inOurUnits);
            case MM:
                return String.format(Locale.getDefault(), "%.0fmm", inOurUnits);
            case INCH:
                return String.format(Locale.getDefault(), "%.2fin", inOurUnits);
        }
    }

    public String toString() {
        switch(this) {
            case METER:
            default:
                return "m";
            case CM:
                return "cm";
            case MM:
                return "mm";
            case INCH:
                return "in";
        }
    }
}
