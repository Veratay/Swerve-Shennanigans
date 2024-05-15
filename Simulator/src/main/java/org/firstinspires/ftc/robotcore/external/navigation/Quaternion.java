//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

public class Quaternion {
    public float w;
    public float x;
    public float y;
    public float z;
    public long acquisitionTime;

    public static Quaternion identityQuaternion() {
        return new Quaternion(1.0F, 0.0F, 0.0F, 0.0F, 0L);
    }

    public Quaternion() {
        this(0.0F, 0.0F, 0.0F, 0.0F, 0L);
    }

    public Quaternion(float w, float x, float y, float z, long acquisitionTime) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.acquisitionTime = acquisitionTime;
    }

    public static Quaternion fromMatrix(MatrixF m, long acquisitionTime) {
        float tr = m.get(0, 0) + m.get(1, 1) + m.get(2, 2);
        float w;
        float x;
        float y;
        float z;
        float s;
        if (tr > 0.0F) {
            s = (float)(Math.sqrt((double)tr + 1.0D) * 2.0D);
            w = (float)(0.25D * (double)s);
            x = (m.get(2, 1) - m.get(1, 2)) / s;
            y = (m.get(0, 2) - m.get(2, 0)) / s;
            z = (m.get(1, 0) - m.get(0, 1)) / s;
        } else if (m.get(0, 0) > m.get(1, 1) & m.get(0, 0) > m.get(2, 2)) {
            s = (float)(Math.sqrt(1.0D + (double)m.get(0, 0) - (double)m.get(1, 1) - (double)m.get(2, 2)) * 2.0D);
            w = (m.get(2, 1) - m.get(1, 2)) / s;
            x = (float)(0.25D * (double)s);
            y = (m.get(0, 1) + m.get(1, 0)) / s;
            z = (m.get(0, 2) + m.get(2, 0)) / s;
        } else if (m.get(1, 1) > m.get(2, 2)) {
            s = (float)(Math.sqrt(1.0D + (double)m.get(1, 1) - (double)m.get(0, 0) - (double)m.get(2, 2)) * 2.0D);
            w = (m.get(0, 2) - m.get(2, 0)) / s;
            x = (m.get(0, 1) + m.get(1, 0)) / s;
            y = (float)(0.25D * (double)s);
            z = (m.get(1, 2) + m.get(2, 1)) / s;
        } else {
            s = (float)(Math.sqrt(1.0D + (double)m.get(2, 2) - (double)m.get(0, 0) - (double)m.get(1, 1)) * 2.0D);
            w = (m.get(1, 0) - m.get(0, 1)) / s;
            x = (m.get(0, 2) + m.get(2, 0)) / s;
            y = (m.get(1, 2) + m.get(2, 1)) / s;
            z = (float)(0.25D * (double)s);
        }

        return (new Quaternion(w, x, y, z, acquisitionTime)).normalized();
    }

    public float magnitude() {
        return (float)Math.sqrt((double)(this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public Quaternion normalized() {
        float mag = this.magnitude();
        return new Quaternion(this.w / mag, this.x / mag, this.y / mag, this.z / mag, this.acquisitionTime);
    }

    public Quaternion conjugate() {
        return new Quaternion(this.w, -this.x, -this.y, -this.z, this.acquisitionTime);
    }

    /** @deprecated */
    @Deprecated
    public Quaternion congugate() {
        return this.conjugate();
    }

    public Quaternion inverse() {
        return this.normalized().conjugate();
    }

    public Quaternion multiply(Quaternion q, long acquisitionTime) {
        return new Quaternion(this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z, this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y, this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x, this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w, acquisitionTime);
    }

    public VectorF applyToVector(VectorF vector) {
        float vx = vector.get(0);
        float vy = vector.get(1);
        float vz = vector.get(2);
        float qs = this.w;
        float q1 = this.x;
        float q2 = this.y;
        float q3 = this.z;
        float i1 = -1.0F * q1;
        float i2 = -1.0F * q2;
        float i3 = -1.0F * q3;
        float S1V2x = qs * vx;
        float S1V2y = qs * vy;
        float S1V2z = qs * vz;
        float V1xV21 = q2 * vz - q3 * vy;
        float V1xV22 = -1.0F * (q1 * vz - q3 * vx);
        float V1xV23 = q1 * vy - q2 * vx;
        float qVs = -1.0F * (q1 * vx + q2 * vy + q3 * vz);
        float qV1 = S1V2x + V1xV21;
        float qV2 = S1V2y + V1xV22;
        float qV3 = S1V2z + V1xV23;
        float TS1V2x = qVs * i1;
        float TS1V2y = qVs * i2;
        float TS1V2z = qVs * i3;
        float TS2V1x = qs * qV1;
        float TS2V1y = qs * qV2;
        float TS2V1z = qs * qV3;
        float TV1XV21 = qV2 * i3 - qV3 * i2;
        float qVq1 = TS1V2x + TS2V1x + TV1XV21;
        float TV1XV22 = -1.0F * (qV1 * i3 - qV3 * i1);
        float qVq2 = TS1V2y + TS2V1y + TV1XV22;
        float TV1XV23 = qV1 * i2 - qV2 * i1;
        float qVq3 = TS1V2z + TS2V1z + TV1XV23;
        return new VectorF(qVq1, qVq2, qVq3);
    }

    public MatrixF toMatrix() {
        float xx = this.x * this.x;
        float xy = this.x * this.y;
        float xz = this.x * this.z;
        float xw = this.x * this.w;
        float yy = this.y * this.y;
        float yz = this.y * this.z;
        float yw = this.y * this.w;
        float zz = this.z * this.z;
        float zw = this.z * this.w;
        float m00 = (float)(1.0D - 2.0D * (double)(yy + zz));
        float m01 = (float)(2.0D * (double)(xy - zw));
        float m02 = (float)(2.0D * (double)(xz + yw));
        float m10 = (float)(2.0D * (double)(xy + zw));
        float m11 = (float)(1.0D - 2.0D * (double)(xx + zz));
        float m12 = (float)(2.0D * (double)(yz - xw));
        float m20 = (float)(2.0D * (double)(xz - yw));
        float m21 = (float)(2.0D * (double)(yz + xw));
        float m22 = (float)(1.0D - 2.0D * (double)(xx + yy));
        OpenGLMatrix result = new OpenGLMatrix();
        result.put(0, 0, m00);
        result.put(0, 1, m01);
        result.put(0, 2, m02);
        result.put(1, 0, m10);
        result.put(1, 1, m11);
        result.put(1, 2, m12);
        result.put(2, 0, m20);
        result.put(2, 1, m21);
        result.put(2, 2, m22);
        return result;
    }

    public Orientation toOrientation(AxesReference axesReference, AxesOrder axesOrder, AngleUnit angleUnit) {
        Orientation result = Orientation.getOrientation(this.toMatrix(), axesReference, axesOrder, angleUnit);
        result.acquisitionTime = this.acquisitionTime;
        return result;
    }

    public String toString() {
        return String.format(Locale.US, "{w=%.3f, x=%.3f, y=%.3f, z=%.3f}", this.w, this.x, this.y, this.z);
    }
}
