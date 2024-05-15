//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.robotcore.external.android.util;

public final class Size {
    private final int mWidth;
    private final int mHeight;

    public Size(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Size)) {
            return false;
        } else {
            Size other = (Size)obj;
            return this.mWidth == other.mWidth && this.mHeight == other.mHeight;
        }
    }

    public String toString() {
        return this.mWidth + "x" + this.mHeight;
    }

    private static NumberFormatException invalidSize(String s) {
        throw new NumberFormatException("Invalid Size: \"" + s + "\"");
    }

    public static Size parseSize(String string) throws NumberFormatException {
        if (null == string) {
            throw new IllegalArgumentException("string must not be null");
        } else {
            int sep_ix = string.indexOf(42);
            if (sep_ix < 0) {
                sep_ix = string.indexOf(120);
            }

            if (sep_ix < 0) {
                throw invalidSize(string);
            } else {
                try {
                    return new Size(Integer.parseInt(string.substring(0, sep_ix)), Integer.parseInt(string.substring(sep_ix + 1)));
                } catch (NumberFormatException var3) {
                    throw invalidSize(string);
                }
            }
        }
    }

    public int hashCode() {
        return this.mHeight ^ (this.mWidth << 16 | this.mWidth >>> 16);
    }
}
