package org.aperture.common.coordinates;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 copy() {
        return new Vector3(this.x,this.y,this.z);
    }

    public void add(Vector3 v) {
        x+=v.x;
        y+=v.y;
        z+=v.z;
    }

    public void sub(Vector3 v) {
        x-=v.x;
        y-=v.y;
        z-=v.z;
    }

    public double dot(Vector3 v) {
        return x*v.x + y*v.y + z*v.z;
    }

    public void mul(double s) {
        x*=s;
        y*=s;
        z*=s;
    }

    public void div(double s) {
        x/=s;
        y/=s;
        z/=s;
    }

    public double magnitude() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public void normalize() {
        div(magnitude());
    }

    public void rotateAroundVector(Vector3 a, double theta) {
        a.normalize();

        double c = Math.cos(theta);
        double s = Math.sin(theta);

        double ic = 1-c;

        double newX = x*(c + a.x*a.x*ic) +     y*(a.x*a.y*ic - a.z*s) + z*(a.x*a.z*ic + a.y*s);
        double newY = x*(a.x*a.y*ic + a.z*s) + y*(c + a.y*a.y*ic) +     z*(a.y*a.z*ic - a.x*s);
        double newZ = x*(a.x*a.z*ic - a.y*s) + y*(a.y*a.z*ic + a.x*s) + z*(c + a.z*a.z*ic)    ;

        x = newX;
        y = newY;
        z = newZ;
    }

    public void intersectionWithPlane(Vector3 direction, Vector3 planeNormal, Vector3 pointOnPlane) {
        direction.normalize();
        planeNormal.normalize();

        pointOnPlane.sub(this);

        double d = pointOnPlane.dot(planeNormal)/direction.dot(planeNormal);

        direction.mul(d);
        this.add(direction);
    }

    public double distanceTo(Vector3 v) {
        return Math.sqrt((x-v.x)*(x-v.x) + (y-v.y)*(y-v.y) + (z-v.z)*(z-v.z));
    }

    public void interpolate(Vector3 v, double t) {
        x = x+(v.x-x)*t;
        y = y+(v.y-y)*t;
        z = z+(v.z-z)*t;
    }
}
