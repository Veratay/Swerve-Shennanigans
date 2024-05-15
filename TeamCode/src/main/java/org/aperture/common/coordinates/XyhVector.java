package org.aperture.common.coordinates;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;

public class XyhVector {

    //-----------------------
    // interface variables:
    //-----------------------
    public double x = 0.0;
    public double y = 0.0;
    public double h = 0.0;

    //----------------
    // constructors:
    //----------------
    public XyhVector() {
        x = 0.0;
        y = 0.0;
        h = 0.0;
    }
    public XyhVector(double x, double y, double h) {
        this.x = x;
        this.y = y;
        this.h = h;
    }
    public XyhVector(XyhVector xyhVector) {
        x = xyhVector.x;
        y = xyhVector.y;
        h = xyhVector.h;
    }

    //-----------
    // setters:
    //-----------
    public void set(double x, double y, double h) {
        this.x = x;
        this.y = y;
        this.h = h;
    }
    public void set(XyhVector newVector) {
        x = newVector.x;
        y = newVector.y;
        h = newVector.h;
    }

    //----------------
    // math functions:
    //----------------
    public void add(XyhVector v1, XyhVector v2) {
        x = v1.x + v2.x;
        y = v1.y + v2.y;
        h = v1.h + v2.h;
    }
    public void add(XyhVector v) {
        x += v.x;
        y += v.y;
        h += v.h;
    }
    public void add(double x, double y, double h) {
        this.x += x;
        this.y += y;
        this.h += h;
    }
    public void sub(XyhVector v1, XyhVector v2) {
        x = v1.x - v2.x;
        y = v1.y - v2.y;
        h = normalizeRadians(v1.h - v2.h);
    }
    public void abs(XyhVector v) {
        x = Math.abs(v.x);
        y = Math.abs(v.y);
        h = Math.abs(v.h);
    }
    public void sub(XyhVector v) {
        x -= v.x;
        y -= v.y;
        h -= v.h;
    }
    public void sub(double x, double y, double h) {
        this.x -= x;
        this.y -= y;
        this.h -= h;
    }
    public double maxAbs() {
        return Math.max( Math.max(Math.abs(x), Math.abs(y)), Math.abs(h));
    }
    public void limit(double maxVal) {
        x = Math.min(x, maxVal);
        y = Math.min(y, maxVal);
        h = Math.min(h, maxVal);
    }

    public void mul(XyhVector v1, XyhVector v2) {
        x = v1.x * v2.x;
        y = v1.y * v2.y;
        h = v1.h * v2.h;
    }
    public void mul(XyhVector v) {
        x *= v.x;
        y *= v.y;
        h *= v.h;
    }
    public void mul(XyhVector v, double scalar) {
        x = v.x * scalar;
        y = v.y * scalar;
        h = v.h * scalar;
    }
    public void mul(double scalar) {
        x *= scalar;
        y *= scalar;
        h *= scalar;
    }
    public void div(XyhVector v1, XyhVector v2) {
        x = v1.x / v2.x;
        y = v1.y / v2.y;
        h = v1.h / v2.h;
    }
    public void div(XyhVector v, double scalar) {
        x = v.x / scalar;
        y = v.y / scalar;
        h = v.h / scalar;
    }

    //------------------
    // path following:
    //------------------

    // Euclidian distance to another point:
    public double distanceTo(XyhVector v) {
        return Math.sqrt( (x-v.x)*(x-v.x) + (y-v.y)*(y-v.y) );
    }

    // Check if a point is within close proximity of the target point.
    public boolean proximity(XyhVector targetPos, XyhVector delta) {
        return     Math.abs(targetPos.x - x) < delta.x
                && Math.abs(targetPos.y - y) < delta.y
                && Math.abs(normalizeRadians(targetPos.h - h)) < delta.h;
    }

    //----------------------------------------------------------------------------------------------
    // Translate the speed on the field into robot coordinates through a rotation.
    //
    // In general, the robot can point in any direction on the field and the velocities relative to
    // the field in x and y direction need to be turned by the angle the robot is heading,
    // whereas the rotational speed stays the same.
    //----------------------------------------------------------------------------------------------
    public void rotate(XyhVector v, double angle) {
        double xx = v.x;
        x =  xx * Math.cos(angle) + v.y * Math.sin(angle);
        y = -xx * Math.sin(angle) + v.y * Math.cos(angle);
        h = v.h;
    }

    // TODO: we use this in the hardware class to normalize a velocity vector with THREE components
    public void normalize() {
        if(x==0 && y==0) return;
        double dist  = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        x/=dist;
        y/=dist;
    }

    // TODO: we use this in the hardware class to get the magnitude of a velocity vector with THREE components
    public double magnitude() {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    // TODO: we use this in the hardware class to clip a velocity vector with THREE components
    public void clip(double min, double max) {
        if( this.magnitude()>max) {
            this.normalize();
            x *= max;
            y *= max;
        }
        if( this.magnitude()<min) {
            this.normalize();
            x *= min;
            y *= min;
        }
    }

    //----------------------
    // string conversions:
    //----------------------
    public String toString() {
        return String.format("%8.3f,%8.3f,%8.3f,  ", x, y, h);
    }
    public String toStringDeg() {
        return String.format("%8.3f,%8.3f,%8.3f,  ", x, y, Math.toDegrees(h));
    }

    public void pathLinear(XyhVector p0, XyhVector p1, double t) {
        x = (1-t) * p0.x + t * p1.x;
        y = (1-t) * p0.y + t * p1.y;
        h = (1-t) * p0.h + t * p1.h;
    }
    public void pathLinear(XyhVector p0, XyhVector p1, XyhVector t) {
        x = (1-t.x) * p0.x + t.x * p1.x;
        y = (1-t.y) * p0.y + t.y * p1.y;
        h = (1-t.h) * p0.h + t.h * p1.h;
    }
}

