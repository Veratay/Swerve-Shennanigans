//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.internal.camera.calibration;

import androidx.annotation.NonNull;
import java.util.Arrays;
import org.firstinspires.ftc.robotcore.internal.system.Misc;

public class CameraIntrinsics {
    public float focalLengthX;
    public float focalLengthY;
    public float principalPointX;
    public float principalPointY;
    @NonNull
    public final float[] distortionCoefficients;

    public CameraIntrinsics(float focalLengthX, float focalLengthY, float principalPointX, float principalPointY, float[] distortionCoefficients) throws RuntimeException {
        if (distortionCoefficients != null && distortionCoefficients.length == 8) {
            this.focalLengthX = focalLengthX;
            this.focalLengthY = focalLengthY;
            this.principalPointX = principalPointX;
            this.principalPointY = principalPointY;
            this.distortionCoefficients = Arrays.copyOf(distortionCoefficients, distortionCoefficients.length);
        } else {
            throw Misc.illegalArgumentException("distortionCoefficients must have length 8");
        }
    }

    public float[] toArray() {
        float[] result = new float[12];
        result[0] = this.focalLengthX;
        result[1] = this.focalLengthY;
        result[2] = this.principalPointX;
        result[3] = this.principalPointY;
        System.arraycopy(this.distortionCoefficients, 0, result, 4, 8);
        return result;
    }

    public boolean isDegenerate() {
        return this.focalLengthX == 0.0F && this.focalLengthY == 0.0F && this.principalPointX == 0.0F && this.principalPointY == 0.0F && this.distortionCoefficients[0] == 0.0F && this.distortionCoefficients[1] == 0.0F && this.distortionCoefficients[2] == 0.0F && this.distortionCoefficients[3] == 0.0F && this.distortionCoefficients[4] == 0.0F && this.distortionCoefficients[5] == 0.0F && this.distortionCoefficients[6] == 0.0F && this.distortionCoefficients[7] == 0.0F;
    }
}
