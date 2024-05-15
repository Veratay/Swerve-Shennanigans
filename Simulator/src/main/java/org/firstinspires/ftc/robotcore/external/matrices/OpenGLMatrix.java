//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

import android.opengl.Matrix;
import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.NonConst;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class OpenGLMatrix extends ColumnMajorMatrixF {
    float[] data;

    public OpenGLMatrix() {
        super(4, 4);
        this.data = new float[16];
        Matrix.setIdentityM(this.data, 0);
    }

    public OpenGLMatrix(float[] data) {
        super(4, 4);
        this.data = data;
        if (this.data.length != 16) {
            throw this.dimensionsError();
        }
    }

//    public OpenGLMatrix(Matrix44F matrix) {
//        this(matrix.getData());
//    }

    public OpenGLMatrix(MatrixF him) {
        this();
        if (him.numRows <= 4 && him.numCols <= 4) {
            for(int i = 0; i < Math.min(4, him.numRows); ++i) {
                for(int j = 0; j < Math.min(4, him.numCols); ++j) {
                    this.put(i, j, him.get(i, j));
                }
            }

        } else {
            throw him.dimensionsError();
        }
    }

    public MatrixF emptyMatrix(int numRows, int numCols) {
        return (MatrixF)(numRows == 4 && numCols == 4 ? new OpenGLMatrix() : new GeneralMatrixF(numRows, numCols));
    }

    public static OpenGLMatrix rotation(AngleUnit angleUnit, float angle, float dx, float dy, float dz) {
        float[] data = new float[16];
        Matrix.setRotateM(data, 0, angleUnit.toDegrees(angle), dx, dy, dz);
        return new OpenGLMatrix(data);
    }

    public static OpenGLMatrix rotation(AxesReference axesReference, AxesOrder axesOrder, AngleUnit angleUnit, float first, float second, float third) {
        OpenGLMatrix rotation = Orientation.getRotationMatrix(axesReference, axesOrder, angleUnit, first, second, third);
        return identityMatrix().multiplied(rotation);
    }

    public static OpenGLMatrix translation(float dx, float dy, float dz) {
        OpenGLMatrix result = new OpenGLMatrix();
        result.translate(dx, dy, dz);
        return result;
    }

    public static OpenGLMatrix identityMatrix() {
        return new OpenGLMatrix();
    }

    public float[] getData() {
        return this.data;
    }

    @NonConst
    public void scale(float scaleX, float scaleY, float scaleZ) {
        Matrix.scaleM(this.data, 0, scaleX, scaleY, scaleZ);
    }

    @NonConst
    public void scale(float scale) {
        this.scale(scale, scale, scale);
    }

    @NonConst
    public void translate(float dx, float dy, float dz) {
        Matrix.translateM(this.data, 0, dx, dy, dz);
    }

    @NonConst
    public void rotate(AngleUnit angleUnit, float angle, float dx, float dy, float dz) {
        Matrix.rotateM(this.data, 0, angleUnit.toDegrees(angle), dx, dy, dz);
    }

    @NonConst
    public void rotate(AxesReference axesReference, AxesOrder axesOrder, AngleUnit angleUnit, float first, float second, float third) {
        OpenGLMatrix rotation = Orientation.getRotationMatrix(axesReference, axesOrder, angleUnit, first, second, third);
        this.data = this.multiplied(rotation).getData();
    }

    @Const
    public OpenGLMatrix scaled(float scaleX, float scaleY, float scaleZ) {
        OpenGLMatrix result = new OpenGLMatrix();
        Matrix.scaleM(result.data, 0, this.data, 0, scaleX, scaleY, scaleZ);
        return result;
    }

    @Const
    public OpenGLMatrix scaled(float scale) {
        return this.scaled(scale, scale, scale);
    }

    @Const
    public OpenGLMatrix translated(float dx, float dy, float dz) {
        OpenGLMatrix result = new OpenGLMatrix();
        Matrix.translateM(result.data, 0, this.data, 0, dx, dy, dz);
        return result;
    }

    @Const
    public OpenGLMatrix rotated(AngleUnit angleUnit, float angle, float dx, float dy, float dz) {
        OpenGLMatrix result = new OpenGLMatrix();
        Matrix.rotateM(result.data, 0, this.data, 0, angleUnit.toDegrees(angle), dx, dy, dz);
        return result;
    }

    @Const
    public OpenGLMatrix rotated(AxesReference axesReference, AxesOrder axesOrder, AngleUnit angleUnit, float first, float second, float third) {
        OpenGLMatrix rotation = Orientation.getRotationMatrix(axesReference, axesOrder, angleUnit, first, second, third);
        return this.multiplied(rotation);
    }

    @Const
    public OpenGLMatrix inverted() {
        OpenGLMatrix result = new OpenGLMatrix();
        Matrix.invertM(result.data, 0, this.data, 0);
        return result;
    }

    @Const
    public OpenGLMatrix transposed() {
        return (OpenGLMatrix)super.transposed();
    }

    @Const
    public OpenGLMatrix multiplied(OpenGLMatrix him) {
        OpenGLMatrix result = new OpenGLMatrix();
        Matrix.multiplyMM(result.data, 0, this.data, 0, him.getData(), 0);
        return result;
    }

    @Const
    public MatrixF multiplied(MatrixF him) {
        return (MatrixF)(him instanceof OpenGLMatrix ? this.multiplied((OpenGLMatrix)him) : super.multiplied(him));
    }

    @NonConst
    public void multiply(OpenGLMatrix him) {
        this.data = this.multiplied(him).getData();
    }

    @NonConst
    public void multiply(MatrixF him) {
        if (him instanceof OpenGLMatrix) {
            this.multiply((OpenGLMatrix)him);
        } else {
            super.multiply(him);
        }

    }
}
