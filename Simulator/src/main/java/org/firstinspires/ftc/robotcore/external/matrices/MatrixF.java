//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.matrices;

import android.annotation.SuppressLint;
import java.util.Arrays;
import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.NonConst;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public abstract class MatrixF {
    protected int numRows;
    protected int numCols;

    public MatrixF(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        if (numRows <= 0 || numCols <= 0) {
            throw this.dimensionsError();
        }
    }

    @Const
    public SliceMatrixF slice(int row, int col, int numRows, int numCols) {
        return new SliceMatrixF(this, row, col, numRows, numCols);
    }

    @Const
    public SliceMatrixF slice(int numRows, int numCols) {
        return this.slice(0, 0, numRows, numCols);
    }

    public static MatrixF identityMatrix(int dim) {
        return diagonalMatrix(dim, 1.0F);
    }

    public static MatrixF diagonalMatrix(int dim, float scale) {
        GeneralMatrixF result = new GeneralMatrixF(dim, dim);

        for(int i = 0; i < dim; ++i) {
            result.put(i, i, scale);
        }

        return result;
    }

    public static MatrixF diagonalMatrix(VectorF vector) {
        int dim = vector.length();
        GeneralMatrixF result = new GeneralMatrixF(dim, dim);

        for(int i = 0; i < dim; ++i) {
            result.put(i, i, vector.get(i));
        }

        return result;
    }

    @Const
    public abstract MatrixF emptyMatrix(int var1, int var2);

    @Const
    public int numRows() {
        return this.numRows;
    }

    @Const
    public int numCols() {
        return this.numCols;
    }

    @Const
    public abstract float get(int var1, int var2);

    @NonConst
    public abstract void put(int var1, int var2, float var3);

    @Const
    public VectorF getRow(int row) {
        VectorF result = VectorF.length(this.numCols);

        for(int j = 0; j < this.numCols; ++j) {
            result.put(j, this.get(row, j));
        }

        return result;
    }

    @Const
    public VectorF getColumn(int col) {
        VectorF result = VectorF.length(this.numRows);

        for(int i = 0; i < this.numRows; ++i) {
            result.put(i, this.get(i, col));
        }

        return result;
    }

    @Const
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");

        for(int i = 0; i < this.numRows; ++i) {
            if (i > 0) {
                result.append(",");
            }

            result.append("{");

            for(int j = 0; j < this.numCols; ++j) {
                if (j > 0) {
                    result.append(",");
                }

                result.append(String.format("%.3f", this.get(i, j)));
            }

            result.append("}");
        }

        result.append("}");
        return result.toString();
    }

    @Const
    public VectorF transform(VectorF him) {
        him = this.adaptHomogeneous(him);
        return this.multiplied(him).normalized3D();
    }

    @Const
    protected VectorF adaptHomogeneous(VectorF him) {
        if (this.numCols == 4) {
            if (him.length() == 3) {
                float[] newData = Arrays.copyOf(him.getData(), 4);
                newData[3] = 1.0F;
                return new VectorF(newData);
            }
        } else if (this.numCols == 3 && him.length() == 4) {
            return new VectorF(Arrays.copyOf(him.normalized3D().getData(), 3));
        }

        return him;
    }

//    public String formatAsTransform() {
//        return this.formatAsTransform(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
//    }
//
//    public String formatAsTransform(AxesReference axesReference, AxesOrder axesOrder, AngleUnit unit) {
//        VectorF translation = this.getTranslation();
//        Orientation orientation = Orientation.getOrientation(this, axesReference, axesOrder, unit);
//        return String.format("%s %s", orientation.toString(), translation.toString());
//    }

    @Const
    public MatrixF transposed() {
        MatrixF result = this.emptyMatrix(this.numCols, this.numRows);

        for(int i = 0; i < result.numRows; ++i) {
            for(int j = 0; j < result.numCols; ++j) {
                result.put(i, j, this.get(j, i));
            }
        }

        return result;
    }

    @NonConst
    public void multiply(MatrixF him) {
        if (this.numCols != him.numRows) {
            throw this.dimensionsError();
        } else if (him.numRows != him.numCols) {
            throw this.dimensionsError();
        } else {
            MatrixF temp = this.multiplied(him);

            for(int i = 0; i < this.numRows; ++i) {
                for(int j = 0; j < this.numCols; ++j) {
                    this.put(i, j, temp.get(i, j));
                }
            }

        }
    }

    @Const
    public MatrixF multiplied(MatrixF him) {
        if (this.numCols != him.numRows) {
            throw this.dimensionsError();
        } else {
            MatrixF result = this.emptyMatrix(this.numRows, him.numCols);

            for(int i = 0; i < result.numRows; ++i) {
                for(int j = 0; j < result.numCols; ++j) {
                    float sum = 0.0F;

                    for(int k = 0; k < this.numCols; ++k) {
                        sum += this.get(i, k) * him.get(k, j);
                    }

                    result.put(i, j, sum);
                }
            }

            return result;
        }
    }

    @Const
    public MatrixF multiplied(float scale) {
        MatrixF result = this.emptyMatrix(this.numCols, this.numRows);

        for(int i = 0; i < result.numRows; ++i) {
            for(int j = 0; j < result.numCols; ++j) {
                result.put(i, j, this.get(i, j) * scale);
            }
        }

        return result;
    }

    @NonConst
    public void multiply(float scale) {
        for(int i = 0; i < this.numRows; ++i) {
            for(int j = 0; j < this.numCols; ++j) {
                this.put(i, j, this.get(i, j) * scale);
            }
        }

    }

    @Const
    public VectorF multiplied(VectorF him) {
        return this.multiplied((MatrixF)(new ColumnMatrixF(him))).toVector();
    }

    @NonConst
    public void multiply(VectorF him) {
        VectorF result = this.multiplied((MatrixF)(new ColumnMatrixF(him))).toVector();

        for(int i = 0; i < result.length(); ++i) {
            this.put(i, 0, result.get(i));
        }

    }

    @Const
    public VectorF multiplied(float[] him) {
        return this.multiplied(new VectorF(him));
    }

    @NonConst
    public void multiply(float[] him) {
        VectorF result = this.multiplied(new VectorF(him));

        for(int i = 0; i < result.length(); ++i) {
            this.put(i, 0, result.get(i));
        }

    }

    @Const
    public VectorF toVector() {
        VectorF result;
        int j;
        if (this.numCols == 1) {
            result = VectorF.length(this.numRows);

            for(j = 0; j < this.numRows; ++j) {
                result.put(j, this.get(j, 0));
            }

            return result;
        } else if (this.numRows != 1) {
            throw this.dimensionsError();
        } else {
            result = VectorF.length(this.numCols);

            for(j = 0; j < this.numCols; ++j) {
                result.put(j, this.get(0, j));
            }

            return result;
        }
    }

    @Const
    public MatrixF added(MatrixF addend) {
        if (this.numRows == addend.numRows && this.numCols == addend.numCols) {
            MatrixF result = this.emptyMatrix(this.numRows, this.numCols);

            for(int i = 0; i < result.numRows; ++i) {
                for(int j = 0; j < result.numCols; ++j) {
                    result.put(i, j, this.get(i, j) + addend.get(i, j));
                }
            }

            return result;
        } else {
            throw this.dimensionsError();
        }
    }

    @NonConst
    public void add(MatrixF addend) {
        if (this.numRows == addend.numRows && this.numCols == addend.numCols) {
            for(int i = 0; i < this.numRows; ++i) {
                for(int j = 0; j < this.numCols; ++j) {
                    this.put(i, j, this.get(i, j) + addend.get(i, j));
                }
            }

        } else {
            throw this.dimensionsError();
        }
    }

    @Const
    public MatrixF subtracted(MatrixF subtrahend) {
        if (this.numRows == subtrahend.numRows && this.numCols == subtrahend.numCols) {
            MatrixF result = this.emptyMatrix(this.numRows, this.numCols);

            for(int i = 0; i < result.numRows; ++i) {
                for(int j = 0; j < result.numCols; ++j) {
                    result.put(i, j, this.get(i, j) - subtrahend.get(i, j));
                }
            }

            return result;
        } else {
            throw this.dimensionsError();
        }
    }

    @NonConst
    public void subtract(MatrixF subtrahend) {
        if (this.numRows == subtrahend.numRows && this.numCols == subtrahend.numCols) {
            for(int i = 0; i < this.numRows; ++i) {
                for(int j = 0; j < this.numCols; ++j) {
                    this.put(i, j, this.get(i, j) - subtrahend.get(i, j));
                }
            }

        } else {
            throw this.dimensionsError();
        }
    }

    @Const
    public MatrixF added(VectorF him) {
        return this.added((MatrixF)(new ColumnMatrixF(him)));
    }

    @Const
    public MatrixF added(float[] him) {
        return this.added(new VectorF(him));
    }

    @Const
    public MatrixF subtracted(VectorF him) {
        return this.subtracted((MatrixF)(new ColumnMatrixF(him)));
    }

    @Const
    public MatrixF subtracted(float[] him) {
        return this.subtracted(new VectorF(him));
    }

    @NonConst
    public void add(VectorF him) {
        this.add((MatrixF)(new ColumnMatrixF(him)));
    }

    @NonConst
    public void add(float[] him) {
        this.add(new VectorF(him));
    }

    @NonConst
    public void subtract(VectorF him) {
        this.subtract((MatrixF)(new ColumnMatrixF(him)));
    }

    @NonConst
    public void subtract(float[] him) {
        this.subtract(new VectorF(him));
    }

    @Const
    public VectorF getTranslation() {
        return this.getColumn(3).normalized3D();
    }

    protected RuntimeException dimensionsError() {
        return dimensionsError(this.numRows, this.numCols);
    }

    @SuppressLint({"DefaultLocale"})
    protected static RuntimeException dimensionsError(int numRows, int numCols) {
        return new IllegalArgumentException(String.format("matrix dimensions are incorrect: rows=%d cols=%d", numRows, numCols));
    }

    @Const
    public MatrixF inverted() {
        if (this.numRows != this.numCols) {
            throw this.dimensionsError();
        } else {
            MatrixF result;
            float m00;
            float m01;
            float m10;
            float m11;
            float m12;
            float m20;
            float m21;
            float m22;
            float denom;
            if (this.numRows == 4) {
                result = this.emptyMatrix(4, 4);
                m00 = this.get(0, 0);
                m01 = this.get(0, 1);
                m10 = this.get(0, 2);
                m11 = this.get(0, 3);
                denom = this.get(1, 0);
                m12 = this.get(1, 1);
                m20 = this.get(1, 2);
                m21 = this.get(1, 3);
                m22 = this.get(2, 0);
                denom = this.get(2, 1);
                m22 = this.get(2, 2);
                float m23 = this.get(2, 3);
                float m30 = this.get(3, 0);
                float m31 = this.get(3, 1);
                float m32 = this.get(3, 2);
                float m33 = this.get(3, 3);
                denom = m00 * m12 * m22 * m33 + m00 * m20 * m23 * m31 + m00 * m21 * denom * m32 + m01 * denom * m23 * m32 + m01 * m20 * m22 * m33 + m01 * m21 * m22 * m30 + m10 * denom * denom * m33 + m10 * m12 * m23 * m30 + m10 * m21 * m22 * m31 + m11 * denom * m22 * m31 + m11 * m12 * m22 * m32 + m11 * m20 * denom * m30 - m01 * denom * m22 * m33 - m00 * m20 * denom * m33 - m10 * m12 * m22 * m33 - m00 * m12 * m23 * m32 - m11 * denom * denom * m32 - m01 * m21 * m22 * m32 - m10 * denom * m23 * m31 - m00 * m21 * m22 * m31 - m11 * m20 * m22 * m31 - m01 * m20 * m23 * m30 - m11 * m12 * m22 * m30 - m10 * m21 * denom * m30;
                result.put(0, 0, (m12 * m22 * m33 + m20 * m23 * m31 + m21 * denom * m32 - m20 * denom * m33 - m12 * m23 * m32 - m21 * m22 * m31) / denom);
                result.put(0, 1, (m01 * m23 * m32 + m10 * denom * m33 + m11 * m22 * m31 - m01 * m22 * m33 - m11 * denom * m32 - m10 * m23 * m31) / denom);
                result.put(0, 2, (m01 * m20 * m33 + m10 * m21 * m31 + m11 * m12 * m32 - m10 * m12 * m33 - m01 * m21 * m32 - m11 * m20 * m31) / denom);
                result.put(0, 3, (m01 * m21 * m22 + m10 * m12 * m23 + m11 * m20 * denom - m01 * m20 * m23 - m11 * m12 * m22 - m10 * m21 * denom) / denom);
                result.put(1, 0, (denom * m23 * m32 + m20 * m22 * m33 + m21 * m22 * m30 - denom * m22 * m33 - m21 * m22 * m32 - m20 * m23 * m30) / denom);
                result.put(1, 1, (m00 * m22 * m33 + m10 * m23 * m30 + m11 * m22 * m32 - m10 * m22 * m33 - m00 * m23 * m32 - m11 * m22 * m30) / denom);
                result.put(1, 2, (m00 * m21 * m32 + m10 * denom * m33 + m11 * m20 * m30 - m00 * m20 * m33 - m11 * denom * m32 - m10 * m21 * m30) / denom);
                result.put(1, 3, (m00 * m20 * m23 + m10 * m21 * m22 + m11 * denom * m22 - m10 * denom * m23 - m00 * m21 * m22 - m11 * m20 * m22) / denom);
                result.put(2, 0, (denom * denom * m33 + m12 * m23 * m30 + m21 * m22 * m31 - m12 * m22 * m33 - denom * m23 * m31 - m21 * denom * m30) / denom);
                result.put(2, 1, (m00 * m23 * m31 + m01 * m22 * m33 + m11 * denom * m30 - m00 * denom * m33 - m11 * m22 * m31 - m01 * m23 * m30) / denom);
                result.put(2, 2, (m00 * m12 * m33 + m01 * m21 * m30 + m11 * denom * m31 - m01 * denom * m33 - m00 * m21 * m31 - m11 * m12 * m30) / denom);
                result.put(2, 3, (m00 * m21 * denom + m01 * denom * m23 + m11 * m12 * m22 - m00 * m12 * m23 - m11 * denom * denom - m01 * m21 * m22) / denom);
                result.put(3, 0, (denom * m22 * m31 + m12 * m22 * m32 + m20 * denom * m30 - denom * denom * m32 - m20 * m22 * m31 - m12 * m22 * m30) / denom);
                result.put(3, 1, (m00 * denom * m32 + m01 * m22 * m30 + m10 * m22 * m31 - m01 * m22 * m32 - m00 * m22 * m31 - m10 * denom * m30) / denom);
                result.put(3, 2, (m00 * m20 * m31 + m01 * denom * m32 + m10 * m12 * m30 - m00 * m12 * m32 - m10 * denom * m31 - m01 * m20 * m30) / denom);
                result.put(3, 3, (m00 * m12 * m22 + m01 * m20 * m22 + m10 * denom * denom - m01 * denom * m22 - m00 * m20 * denom - m10 * m12 * m22) / denom);
                return result;
            } else if (this.numRows == 3) {
                result = this.emptyMatrix(3, 3);
                m00 = this.get(0, 0);
                m01 = this.get(0, 1);
                m10 = this.get(0, 2);
                m11 = this.get(1, 0);
                denom = this.get(1, 1);
                m12 = this.get(1, 2);
                m20 = this.get(2, 0);
                m21 = this.get(2, 1);
                m22 = this.get(2, 2);
                denom = m00 * denom * m22 + m01 * m12 * m20 + m10 * m11 * m21 - m01 * m11 * m22 - m00 * m12 * m21 - m10 * denom * m20;
                result.put(0, 0, (denom * m22 - m12 * m21) / denom);
                result.put(0, 1, (m10 * m21 - m01 * m22) / denom);
                result.put(0, 2, (m01 * m12 - m10 * denom) / denom);
                result.put(1, 0, (m12 * m20 - m11 * m22) / denom);
                result.put(1, 1, (m00 * m22 - m10 * m20) / denom);
                result.put(1, 2, (m10 * m11 - m00 * m12) / denom);
                result.put(2, 0, (m11 * m21 - denom * m20) / denom);
                result.put(2, 1, (m01 * m20 - m00 * m21) / denom);
                result.put(2, 2, (m00 * denom - m01 * m11) / denom);
                return result;
            } else if (this.numRows == 2) {
                result = this.emptyMatrix(2, 2);
                m00 = this.get(0, 0);
                m01 = this.get(0, 1);
                m10 = this.get(1, 0);
                m11 = this.get(1, 1);
                denom = m00 * m11 - m01 * m10;
                result.put(0, 0, m11 / denom);
                result.put(0, 1, -m01 / denom);
                result.put(1, 0, -m10 / denom);
                result.put(1, 1, m00 / denom);
                return result;
            } else if (this.numRows == 1) {
                result = this.emptyMatrix(1, 1);
                result.put(0, 0, 1.0F / this.get(0, 0));
                return result;
            } else {
                throw this.dimensionsError();
            }
        }
    }
}
