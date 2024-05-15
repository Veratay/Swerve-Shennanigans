//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.internal.camera.calibration;

import androidx.annotation.NonNull;
import org.firstinspires.ftc.robotcore.external.android.util.Size;
import org.firstinspires.ftc.robotcore.internal.system.Misc;

public class CameraCalibration extends CameraIntrinsics implements Cloneable {
    protected CameraCalibrationIdentity identity;
    protected Size size;
    protected boolean remove;
    protected final boolean isFake;

    public String toString() {
        return Misc.formatInvariant("CameraCalibration(%s %dx%d f=%.3f,%.3f)", new Object[]{this.identity, this.size.getWidth(), this.size.getHeight(), this.focalLengthX, this.focalLengthY});
    }

    public CameraCalibrationIdentity getIdentity() {
        return this.identity;
    }

    public Size getSize() {
        return this.size;
    }

    public boolean getRemove() {
        return this.remove;
    }

    public boolean isFake() {
        return this.isFake;
    }

    public CameraCalibration(@NonNull CameraCalibrationIdentity identity, Size size, float focalLengthX, float focalLengthY, float principalPointX, float principalPointY, float[] distortionCoefficients, boolean remove, boolean isFake) throws RuntimeException {
        super(focalLengthX, focalLengthY, principalPointX, principalPointY, distortionCoefficients);
        this.identity = identity;
        this.size = size;
        this.remove = remove;
        this.isFake = isFake;
    }

    public CameraCalibration(@NonNull CameraCalibrationIdentity identity, int[] size, float[] focalLength, float[] principalPoint, float[] distortionCoefficients, boolean remove, boolean isFake) throws RuntimeException {
        this(identity, new Size(size[0], size[1]), focalLength[0], focalLength[1], principalPoint[0], principalPoint[1], distortionCoefficients, remove, isFake);
        if (size.length != 2) {
            throw Misc.illegalArgumentException("frame size must be 2");
        } else if (principalPoint.length != 2) {
            throw Misc.illegalArgumentException("principal point size must be 2");
        } else if (focalLength.length != 2) {
            throw Misc.illegalArgumentException("focal length size must be 2");
        } else if (distortionCoefficients.length != 8) {
            throw Misc.illegalArgumentException("distortion coefficients size must be 8");
        }
    }

    public static CameraCalibration forUnavailable(CameraCalibrationIdentity calibrationIdentity, Size size) {
        if (calibrationIdentity == null) {
            calibrationIdentity = new VendorProductCalibrationIdentity(0, 0);
        }

        return new CameraCalibration((CameraCalibrationIdentity)calibrationIdentity, size, 0.0F, 0.0F, 0.0F, 0.0F, new float[8], false, true);
    }

    protected CameraCalibration memberwiseClone() {
        try {
            return (CameraCalibration)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new RuntimeException();
        }
    }

    public CameraCalibration scaledTo(Size newSize) {
//        Assert.assertTrue(Misc.approximatelyEquals(getAspectRatio(newSize), getAspectRatio(this.size)));
        double factor = (double)newSize.getWidth() / (double)this.size.getWidth();
        CameraCalibration result = this.memberwiseClone();
        result.size = newSize;
        result.focalLengthX = (float)((double)result.focalLengthX * factor);
        result.focalLengthY = (float)((double)result.focalLengthY * factor);
        result.principalPointX = (float)((double)result.principalPointX * factor);
        result.principalPointY = (float)((double)result.principalPointY * factor);
        return result;
    }

    public double getAspectRatio() {
        return getAspectRatio(this.size);
    }

    public double getDiagonal() {
        return getDiagonal(this.size);
    }

    public static double getDiagonal(Size size) {
        return Math.sqrt((double)(size.getWidth() * size.getWidth() + size.getHeight() * size.getHeight()));
    }

    protected static double getAspectRatio(Size size) {
        return (double)size.getWidth() / (double)size.getHeight();
    }
}
