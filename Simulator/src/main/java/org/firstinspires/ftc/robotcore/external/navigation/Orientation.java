//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.navigation;

import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

public class Orientation {
    public AxesReference axesReference;
    public AxesOrder axesOrder;
    public AngleUnit angleUnit;
    public float firstAngle;
    public float secondAngle;
    public float thirdAngle;
    public long acquisitionTime;

    public Orientation() {
        this(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS, 0.0F, 0.0F, 0.0F, 0L);
    }

    public Orientation(AxesReference axesReference, AxesOrder axesOrder, AngleUnit angleUnit, float firstAngle, float secondAngle, float thirdAngle, long acquisitionTime) {
        this.axesReference = axesReference;
        this.axesOrder = axesOrder;
        this.angleUnit = angleUnit;
        this.firstAngle = firstAngle;
        this.secondAngle = secondAngle;
        this.thirdAngle = thirdAngle;
        this.acquisitionTime = acquisitionTime;
    }

    public Orientation toAngleUnit(AngleUnit angleUnit) {
        return angleUnit != this.angleUnit ? new Orientation(this.axesReference, this.axesOrder, angleUnit, angleUnit.fromUnit(this.angleUnit, this.firstAngle), angleUnit.fromUnit(this.angleUnit, this.secondAngle), angleUnit.fromUnit(this.angleUnit, this.thirdAngle), this.acquisitionTime) : this;
    }

    public Orientation toAxesReference(AxesReference axesReference) {
        if (this.axesReference != axesReference) {
            return new Orientation(this.axesReference.reverse(), this.axesOrder.reverse(), this.angleUnit, this.thirdAngle, this.secondAngle, this.firstAngle, this.acquisitionTime);
        } else {
            return this;
        }
    }

    public Orientation toAxesOrder(AxesOrder axesOrder) {
        return this.axesOrder != axesOrder ? getOrientation(this.getRotationMatrix(), this.axesReference, axesOrder, this.angleUnit) : this;
    }

    public String toString() {
        return this.angleUnit == AngleUnit.DEGREES ? String.format("{%s %s %.0f %.0f %.0f}", this.axesReference.toString(), this.axesOrder.toString(), this.firstAngle, this.secondAngle, this.thirdAngle) : String.format("{%s %s %.3f %.3f %.3f}", this.axesReference.toString(), this.axesOrder.toString(), this.firstAngle, this.secondAngle, this.thirdAngle);
    }

    public OpenGLMatrix getRotationMatrix() {
        return getRotationMatrix(this.axesReference, this.axesOrder, this.angleUnit, this.firstAngle, this.secondAngle, this.thirdAngle);
    }

    public static OpenGLMatrix getRotationMatrix(AxesReference axesReference, AxesOrder axesOrder, AngleUnit unit, float firstAngle, float secondAngle, float thirdAngle) {
        if (axesReference == AxesReference.INTRINSIC) {
            return getRotationMatrix(axesReference.reverse(), axesOrder.reverse(), unit, thirdAngle, secondAngle, firstAngle);
        } else {
            firstAngle = unit.toRadians(firstAngle);
            secondAngle = unit.toRadians(secondAngle);
            thirdAngle = unit.toRadians(thirdAngle);
            float m00;
            float m01;
            float m02;
            float m10;
            float m11;
            float m12;
            float m20;
            float m21;
            float m22;
            switch(axesOrder) {
                case XZX:
                default:
                    m00 = (float)Math.cos((double)secondAngle);
                    m01 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)secondAngle)));
                    m02 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m10 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m20 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m21 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    break;
                case XYX:
                    m00 = (float)Math.cos((double)secondAngle);
                    m01 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m02 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle));
                    m10 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(-(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m20 = (float)(-(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle)));
                    m21 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    break;
                case YXY:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m02 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m10 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m11 = (float)Math.cos((double)secondAngle);
                    m12 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)secondAngle)));
                    m20 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m21 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    break;
                case YZY:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(-(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle)));
                    m02 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m10 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle));
                    m11 = (float)Math.cos((double)secondAngle);
                    m12 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m20 = (float)(-(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m21 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    break;
                case ZYZ:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m02 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m10 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m20 = (float)(-(Math.cos((double)firstAngle) * Math.sin((double)secondAngle)));
                    m21 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m22 = (float)Math.cos((double)secondAngle);
                    break;
                case ZXZ:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.cos((double)secondAngle) * Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(-(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m02 = (float)(Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m10 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(-(Math.cos((double)thirdAngle) * Math.sin((double)secondAngle)));
                    m20 = (float)(Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m21 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle));
                    m22 = (float)Math.cos((double)secondAngle);
                    break;
                case XZY:
                    m00 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
                    m01 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) - Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m02 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m10 = (float)Math.sin((double)secondAngle);
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    m12 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)firstAngle)));
                    m20 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)));
                    m21 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    break;
                case XYZ:
                    m00 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
                    m01 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle) - Math.cos((double)firstAngle) * Math.sin((double)thirdAngle));
                    m02 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m10 = (float)(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) + Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m20 = (float)(-Math.sin((double)secondAngle));
                    m21 = (float)(Math.cos((double)secondAngle) * Math.sin((double)firstAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    break;
                case YXZ:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)));
                    m02 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m10 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m11 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
                    m12 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) - Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m20 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)firstAngle)));
                    m21 = (float)Math.sin((double)secondAngle);
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    break;
                case YZX:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    m01 = (float)(-Math.sin((double)secondAngle));
                    m02 = (float)(Math.cos((double)secondAngle) * Math.sin((double)firstAngle));
                    m10 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m11 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
                    m12 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle) - Math.cos((double)firstAngle) * Math.sin((double)thirdAngle));
                    m20 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m21 = (float)(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m22 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) + Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    break;
                case ZYX:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    m01 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)firstAngle)));
                    m02 = (float)Math.sin((double)secondAngle);
                    m10 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) + Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) - Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m12 = (float)(-(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle)));
                    m20 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) - Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m21 = (float)(Math.cos((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle));
                    m22 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
                    break;
                case ZXY:
                    m00 = (float)(Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) + Math.sin((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle));
                    m01 = (float)(Math.cos((double)firstAngle) * Math.sin((double)secondAngle) * Math.sin((double)thirdAngle) - Math.cos((double)thirdAngle) * Math.sin((double)firstAngle));
                    m02 = (float)(Math.cos((double)secondAngle) * Math.sin((double)thirdAngle));
                    m10 = (float)(Math.cos((double)secondAngle) * Math.sin((double)firstAngle));
                    m11 = (float)(Math.cos((double)firstAngle) * Math.cos((double)secondAngle));
                    m12 = (float)(-Math.sin((double)secondAngle));
                    m20 = (float)(Math.cos((double)thirdAngle) * Math.sin((double)firstAngle) * Math.sin((double)secondAngle) - Math.cos((double)firstAngle) * Math.sin((double)thirdAngle));
                    m21 = (float)(Math.sin((double)firstAngle) * Math.sin((double)thirdAngle) + Math.cos((double)firstAngle) * Math.cos((double)thirdAngle) * Math.sin((double)secondAngle));
                    m22 = (float)(Math.cos((double)secondAngle) * Math.cos((double)thirdAngle));
            }

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
    }

    public static Orientation getOrientation(MatrixF rot, AxesReference axesReference, AxesOrder axesOrder, AngleUnit unit) {
        Orientation one = getOrientation(rot, axesReference, axesOrder, unit, Orientation.AngleSet.THEONE);
        Orientation theOther = getOrientation(rot, axesReference, axesOrder, unit, Orientation.AngleSet.THEOTHER);
        VectorF vOne = new VectorF(one.firstAngle, one.secondAngle, one.thirdAngle);
        VectorF vOther = new VectorF(theOther.firstAngle, theOther.secondAngle, theOther.thirdAngle);
        return vOne.magnitude() <= vOther.magnitude() ? one : theOther;
    }

    public static Orientation getOrientation(MatrixF rot, AxesReference axesReference, AxesOrder axesOrder, AngleUnit unit, Orientation.AngleSet angleSet) {
        if (axesReference == AxesReference.INTRINSIC) {
            return getOrientation(rot, axesReference.reverse(), axesOrder.reverse(), unit, angleSet).toAxesReference(axesReference);
        } else {
            float firstAngle;
            float secondAngle;
            float thirdAngle;
            float test;
            switch(axesOrder) {
                case XZX:
                default:
                    test = rot.get(0, 0);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(2, 1), (double)rot.get(1, 1)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(1, 2), (double)rot.get(2, 2)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(0, 0)) : -Math.acos((double)rot.get(0, 0)));
                        firstAngle = (float)Math.atan2((double)rot.get(0, 2) / Math.sin((double)secondAngle), (double)(-rot.get(0, 1)) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(2, 0) / Math.sin((double)secondAngle), (double)rot.get(1, 0) / Math.sin((double)secondAngle));
                    }
                    break;
                case XYX:
                    test = rot.get(0, 0);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(2, 1), (double)rot.get(1, 1)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)(-rot.get(1, 2)), (double)rot.get(1, 1)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(0, 0)) : -Math.acos((double)rot.get(0, 0)));
                        firstAngle = (float)Math.atan2((double)rot.get(0, 1) / Math.sin((double)secondAngle), (double)rot.get(0, 2) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(1, 0) / Math.sin((double)secondAngle), (double)(-rot.get(2, 0)) / Math.sin((double)secondAngle));
                    }
                    break;
                case YXY:
                    test = rot.get(1, 1);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(0, 2), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(0, 2), (double)rot.get(0, 0)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(1, 1)) : -Math.acos((double)rot.get(1, 1)));
                        firstAngle = (float)Math.atan2((double)rot.get(1, 0) / Math.sin((double)secondAngle), (double)(-rot.get(1, 2)) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(0, 1) / Math.sin((double)secondAngle), (double)rot.get(2, 1) / Math.sin((double)secondAngle));
                    }
                    break;
                case YZY:
                    test = rot.get(1, 1);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(0, 2), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)(-rot.get(0, 2)), (double)rot.get(2, 2)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(1, 1)) : -Math.acos((double)rot.get(1, 1)));
                        firstAngle = (float)Math.atan2((double)rot.get(1, 2) / Math.sin((double)secondAngle), (double)rot.get(1, 0) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(2, 1) / Math.sin((double)secondAngle), (double)(-rot.get(0, 1)) / Math.sin((double)secondAngle));
                    }
                    break;
                case ZYZ:
                    test = rot.get(2, 2);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(1, 0), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(0, 1), (double)rot.get(1, 1)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(2, 2)) : -Math.acos((double)rot.get(2, 2)));
                        firstAngle = (float)Math.atan2((double)rot.get(2, 1) / Math.sin((double)secondAngle), (double)(-rot.get(2, 0)) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(1, 2) / Math.sin((double)secondAngle), (double)rot.get(0, 2) / Math.sin((double)secondAngle));
                    }
                    break;
                case ZXZ:
                    test = rot.get(2, 2);
                    if (test == 1.0F) {
                        secondAngle = 0.0F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(1, 0), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = 3.1415927F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)(-rot.get(0, 1)), (double)rot.get(0, 0)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.acos((double)rot.get(2, 2)) : -Math.acos((double)rot.get(2, 2)));
                        firstAngle = (float)Math.atan2((double)rot.get(2, 0) / Math.sin((double)secondAngle), (double)rot.get(2, 1) / Math.sin((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(0, 2) / Math.sin((double)secondAngle), (double)(-rot.get(1, 2)) / Math.sin((double)secondAngle));
                    }
                    break;
                case XZY:
                    test = rot.get(1, 0);
                    if (test == 1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(0, 2), (double)rot.get(2, 2)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(2, 1), (double)rot.get(0, 1)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.asin((double)rot.get(1, 0)) : 3.141592653589793D - Math.asin((double)rot.get(1, 0)));
                        firstAngle = (float)Math.atan2((double)(-rot.get(1, 2)) / Math.cos((double)secondAngle), (double)rot.get(1, 1) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)(-rot.get(2, 0)) / Math.cos((double)secondAngle), (double)rot.get(0, 0) / Math.cos((double)secondAngle));
                    }
                    break;
                case XYZ:
                    test = rot.get(2, 0);
                    if (test == -1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(0, 1), (double)rot.get(0, 2)));
                    } else if (test == 1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)(-rot.get(0, 1)), (double)rot.get(1, 1)) - (double)firstAngle);
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? -Math.asin((double)rot.get(2, 0)) : 3.141592653589793D + Math.asin((double)rot.get(2, 0)));
                        firstAngle = (float)Math.atan2((double)rot.get(2, 1) / Math.cos((double)secondAngle), (double)rot.get(2, 2) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(1, 0) / Math.cos((double)secondAngle), (double)rot.get(0, 0) / Math.cos((double)secondAngle));
                    }
                    break;
                case YXZ:
                    test = rot.get(2, 1);
                    if (test == 1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(0, 2), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(0, 2), (double)rot.get(0, 0)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.asin((double)rot.get(2, 1)) : 3.141592653589793D - Math.asin((double)rot.get(2, 1)));
                        firstAngle = (float)Math.atan2((double)(-rot.get(2, 0)) / Math.cos((double)secondAngle), (double)rot.get(2, 2) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)(-rot.get(0, 1)) / Math.cos((double)secondAngle), (double)rot.get(1, 1) / Math.cos((double)secondAngle));
                    }
                    break;
                case YZX:
                    test = rot.get(0, 1);
                    if (test == -1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(1, 2), (double)rot.get(1, 0)));
                    } else if (test == 1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)(-rot.get(1, 2)), (double)rot.get(2, 2)) - (double)firstAngle);
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? -Math.asin((double)rot.get(0, 1)) : 3.141592653589793D + Math.asin((double)rot.get(0, 1)));
                        firstAngle = (float)Math.atan2((double)rot.get(0, 2) / Math.cos((double)secondAngle), (double)rot.get(0, 0) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(2, 1) / Math.cos((double)secondAngle), (double)rot.get(1, 1) / Math.cos((double)secondAngle));
                    }
                    break;
                case ZYX:
                    test = rot.get(0, 2);
                    if (test == 1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)rot.get(1, 0), (double)rot.get(1, 1)) - (double)firstAngle);
                    } else if (test == -1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(1, 0), (double)rot.get(1, 1)));
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? Math.asin((double)rot.get(0, 2)) : 3.141592653589793D - Math.asin((double)rot.get(0, 2)));
                        firstAngle = (float)Math.atan2((double)(-rot.get(0, 1)) / Math.cos((double)secondAngle), (double)rot.get(0, 0) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)(-rot.get(1, 2)) / Math.cos((double)secondAngle), (double)rot.get(2, 2) / Math.cos((double)secondAngle));
                    }
                    break;
                case ZXY:
                    test = rot.get(1, 2);
                    if (test == -1.0F) {
                        secondAngle = 1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)((double)firstAngle - Math.atan2((double)rot.get(2, 0), (double)rot.get(0, 0)));
                    } else if (test == 1.0F) {
                        secondAngle = -1.5707964F;
                        firstAngle = 0.0F;
                        thirdAngle = (float)(Math.atan2((double)(-rot.get(0, 1)), (double)rot.get(0, 0)) - (double)firstAngle);
                    } else {
                        secondAngle = (float)(angleSet == Orientation.AngleSet.THEONE ? -Math.asin((double)rot.get(1, 2)) : 3.141592653589793D + Math.asin((double)rot.get(1, 2)));
                        firstAngle = (float)Math.atan2((double)rot.get(1, 0) / Math.cos((double)secondAngle), (double)rot.get(1, 1) / Math.cos((double)secondAngle));
                        thirdAngle = (float)Math.atan2((double)rot.get(0, 2) / Math.cos((double)secondAngle), (double)rot.get(2, 2) / Math.cos((double)secondAngle));
                    }
            }

            return new Orientation(axesReference, axesOrder, unit, unit.fromRadians(firstAngle), unit.fromRadians(secondAngle), unit.fromRadians(thirdAngle), 0L);
        }
    }

    public static enum AngleSet {
        THEONE,
        THEOTHER;

        private AngleSet() {
        }
    }
}
