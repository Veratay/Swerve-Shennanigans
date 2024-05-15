//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

import android.annotation.SuppressLint;
import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.NonConst;

public class VectorF {
    protected float[] data;

    public static VectorF length(int length) {
        return new VectorF(new float[length]);
    }

    public VectorF(float[] data) {
        this.data = data;
    }

    public VectorF(float x) {
        this.data = new float[1];
        this.data[0] = x;
    }

    public VectorF(float x, float y) {
        this.data = new float[2];
        this.data[0] = x;
        this.data[1] = y;
    }

    public VectorF(float x, float y, float z) {
        this.data = new float[3];
        this.data[0] = x;
        this.data[1] = y;
        this.data[2] = z;
    }

    public VectorF(float x, float y, float z, float w) {
        this.data = new float[4];
        this.data[0] = x;
        this.data[1] = y;
        this.data[2] = z;
        this.data[3] = w;
    }

    @Const
    public float[] getData() {
        return this.data;
    }

    @Const
    public int length() {
        return this.data.length;
    }

    @Const
    public float get(int index) {
        return this.data[index];
    }

    @NonConst
    public void put(int index, float value) {
        this.data[index] = value;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");

        for(int i = 0; i < this.length(); ++i) {
            if (i > 0) {
                result.append(" ");
            }

            result.append(String.format("%.2f", this.data[i]));
        }

        result.append("}");
        return result.toString();
    }

    @Const
    public VectorF normalized3D() {
        if (this.length() == 3) {
            return this;
        } else if (this.length() == 4) {
            return new VectorF(this.data[0] / this.data[3], this.data[1] / this.data[3], this.data[2] / this.data[3]);
        } else {
            throw this.dimensionsError();
        }
    }

    @Const
    public float magnitude() {
        return (float)Math.sqrt((double)this.dotProduct(this));
    }

    @Const
    public float dotProduct(VectorF him) {
        if (this.length() != him.length()) {
            throw this.dimensionsError();
        } else {
            float sum = 0.0F;

            for(int i = 0; i < this.length(); ++i) {
                sum += this.get(i) * him.get(i);
            }

            return sum;
        }
    }

    @Const
    public MatrixF multiplied(MatrixF him) {
        return (new RowMatrixF(this)).multiplied(him);
    }

    @Const
    public MatrixF added(MatrixF addend) {
        return (new RowMatrixF(this)).added(addend);
    }

    @Const
    public VectorF added(VectorF addend) {
        if (this.length() != addend.length()) {
            throw this.dimensionsError();
        } else {
            VectorF result = length(this.length());

            for(int i = 0; i < this.length(); ++i) {
                result.put(i, this.get(i) + addend.get(i));
            }

            return result;
        }
    }

    @NonConst
    public void add(VectorF addend) {
        if (this.length() != addend.length()) {
            throw this.dimensionsError();
        } else {
            for(int i = 0; i < this.length(); ++i) {
                this.put(i, this.get(i) + addend.get(i));
            }

        }
    }

    @Const
    public MatrixF subtracted(MatrixF subtrahend) {
        return (new RowMatrixF(this)).subtracted(subtrahend);
    }

    @Const
    public VectorF subtracted(VectorF subtrahend) {
        if (this.length() != subtrahend.length()) {
            throw this.dimensionsError();
        } else {
            VectorF result = length(this.length());

            for(int i = 0; i < this.length(); ++i) {
                result.put(i, this.get(i) - subtrahend.get(i));
            }

            return result;
        }
    }

    @NonConst
    public void subtract(VectorF subtrahend) {
        if (this.length() != subtrahend.length()) {
            throw this.dimensionsError();
        } else {
            for(int i = 0; i < this.length(); ++i) {
                this.put(i, this.get(i) - subtrahend.get(i));
            }

        }
    }

    @Const
    public VectorF multiplied(float scale) {
        VectorF result = length(this.length());

        for(int i = 0; i < this.length(); ++i) {
            result.put(i, this.get(i) * scale);
        }

        return result;
    }

    @NonConst
    public void multiply(float scale) {
        for(int i = 0; i < this.length(); ++i) {
            this.put(i, this.get(i) * scale);
        }

    }

    protected RuntimeException dimensionsError() {
        return dimensionsError(this.length());
    }

    @SuppressLint({"DefaultLocale"})
    protected static RuntimeException dimensionsError(int length) {
        return new IllegalArgumentException(String.format("vector dimensions are incorrect: length=%d", length));
    }
}
