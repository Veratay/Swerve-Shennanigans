package org.aperture.common.path;

import org.aperture.common.coordinates.XyhVector;

import java.util.Arrays;

class Quadratic implements Followable {
    private final XyhVector[] points;
    private double[] length_LUT;

    private double hDiff;

    public Quadratic(XyhVector[] points) {
        this.points = points;
        calcConsts();
        System.out.println("PATH: lut="+ Arrays.toString(length_LUT));
    }

    //computes the curve at t=t
    public XyhVector compute(double t) {
        return new XyhVector(
                Math.pow(1-t,2)*points[0].x + 2*(1-t)*t*points[1].x + Math.pow(t,2)*points[2].x,
                Math.pow(1-t,2)*points[0].y + 2*(1-t)*t*points[1].y + Math.pow(t,2)*points[2].y,
                points[0].h + t*hDiff
        );
    }

    //returns the arc length of a bezier curve between t=0 and t=t
    public double length(double t) {
        double bottom = length_LUT[(int)(t*LUT_RESOLUTION)];
        double top = length_LUT[Math.min((int)(t*LUT_RESOLUTION)+1,LUT_RESOLUTION)];
        double tRem = (t*LUT_RESOLUTION)-((int)(t*LUT_RESOLUTION));
        return (bottom + tRem*(top-bottom));
    }

    private Double totalLength;
    public double totalLength() {
        if(totalLength == null) totalLength = length(1);
        return totalLength;
    }

    public double lengthToT(double l) {
        int index = 0;
        for(int i=0; i<length_LUT.length; i++) {
            if(length_LUT[i] < l) {
                index = i;
            } else {
                break;
            }
        }
        double bottom = length_LUT[index];
        double top = length_LUT[Math.min(index+1,length_LUT.length-1)];
        double lRem = top==bottom ? 0 : (l-bottom)/(top-bottom);
        return (index + lRem)/LUT_RESOLUTION;
    }

    //derivative of a bezier curve
    public XyhVector tangentVec(double t) {
        return new XyhVector(
                2*(1-t)*(points[1].x-points[0].x)+2*t*(points[2].x-points[1].x),
                2*(1-t)*(points[1].y-points[0].y)+2*t*(points[2].y-points[1].y),
                hDiff
        );
    }

    private double distToCurve(double t, double d4, double d3, double d2, double d1, double d0) {
        return d4*Math.pow(t,4) + 4*d3*Math.pow(t,3) + 2*d2*Math.pow(t,2) + 4*d1*t + d0;
    }

    public double closestPoint(XyhVector pos) {
        double d4 = Math.pow(points[0].x - 2*points[1].x + points[2].x,2) + Math.pow(points[0].y - 2*points[1].y + points[2].y,2);
        double d3 = (points[0].x - 2*points[1].x + points[2].x)*(points[1].x-points[0].x) + (points[0].y - 2*points[1].y + points[2].y)*(points[1].y-points[0].y);
        double d2 = (points[0].x - 2*points[1].x + points[2].x)*(points[0].x-pos.x)+(points[0].y - 2*points[1].y + points[2].y)*(points[0].y-pos.y)+2*Math.pow(points[1].x-points[0].x,2)+2*Math.pow(points[1].y-points[0].y,2);
        double d1 = (points[1].x - points[0].x)*(points[0].x-pos.x)+(points[1].y - points[0].y)*(points[0].y-pos.y);
        double d0 = Math.pow(points[0].x-pos.x,2)+Math.pow(points[0].y-pos.y,2);

        double a = d4;
        double b = 3*d3;
        double c = d2;
        double d = d1;

        double[] roots = solveCubic(a,b,c,d);

        double minDist = distToCurve(0,d4,d3,d2,d1,d0);
        double minT = 0;
        for(int i = 0; i<=roots.length; i++) {
            double t = (i == roots.length ? 1 : roots[i]);
            if(t>=0 && t<=1) {
                double dist = distToCurve(t,d4,d3,d2,d1,d0);
                if(dist<minDist) {
                    minDist = dist;
                    minT = t;
                }
            }
        }
        return minT;
    }

    private double cuberoot(double x) {
        double y = Math.pow(Math.abs(x), 1.0/3);
        return x < 0 ? -y : y;
    }

    private double[] solveCubic(double a, double b, double c, double d) {
        if (Math.abs(a) < 1e-8) { // Quadratic case, ax^2+bx+c=0
            a = b; b = c; c = d;
            if (Math.abs(a) < 1e-8) { // Linear case, ax+b=0
                a = b; b = c;
                if (Math.abs(a) < 1e-8) // Degenerate case
                    return new double[] {};
                return new double[] {-b/a};
            }

            double D = b*b - 4*a*c;
            if (Math.abs(D) < 1e-8)
                return new double[] {-b/(2*a)};
            else if (D > 0)
                return new double[] {(-b+Math.sqrt(D))/(2*a), (-b-Math.sqrt(D))/(2*a)};
            return new double[] {};
        }

        // Convert to depressed cubic t^3+pt+q = 0 (subst x = t - b/3a)
        double p = (3*a*c - b*b)/(3*a*a);
        double q = (2*b*b*b - 9*a*b*c + 27*a*a*d)/(27*a*a*a);
        double[] roots;

        if (Math.abs(p) < 1e-8) { // p = 0 -> t^3 = -q -> t = -q^1/3
            roots = new double[] {cuberoot(-q)};
        } else if (Math.abs(q) < 1e-8) { // q = 0 -> t^3 + pt = 0 -> t(t^2+p)=0
            if(p < 0) {
                roots = new double[] {Math.sqrt(-p), -Math.sqrt(-p)};
            } else {
                roots = new double[] {};
            }
        } else {
            double D = q*q/4 + p*p*p/27;
            if (Math.abs(D) < 1e-8) {       // D = 0 -> two roots
                roots =  new double[] {-1.5*q/p, 3*q/p};
            } else if (D > 0) {             // Only one real root
                double u = cuberoot(-q/2 - Math.sqrt(D));
                roots = new double[] {u - p/(3*u)};
            } else {                        // D < 0, three roots, but needs to use complex numbers/trigonometric solution
                double u = 2*Math.sqrt(-p/3);
                double t = Math.acos(3*q/p/u)/3;  // D < 0 implies p < 0 and acos argument in [-1..1]
                double k = 2*Math.PI/3;
                roots = new double[] {u*Math.cos(t), u*Math.cos(t-k), u*Math.cos(t-2*k)};
            }
        }

        // Convert back from depressed cubic
        for (int i = 0; i < roots.length; i++)
            roots[i] -= b/(3*a);

        return roots;
    }

    private final int LUT_RESOLUTION = 100;
    private void calcConsts() {
        length_LUT = new double[LUT_RESOLUTION+1];
        XyhVector lastPos = new XyhVector(points[0]);
        double lastLength = 0;
        for(int i=0; i<=LUT_RESOLUTION; i++) {
            XyhVector newPos = compute(i/(double)LUT_RESOLUTION);
            double curDist = newPos.distanceTo(lastPos);
            length_LUT[i] = curDist + lastLength;
            lastLength = length_LUT[i];
            lastPos = newPos;
        }

        double hDiff = points[2].h - points[0].h;
        while (hDiff < -Math.PI) hDiff += 2 * Math.PI;
        while (hDiff > Math.PI) hDiff -= 2 * Math.PI;
        this.hDiff = hDiff;
    }
}