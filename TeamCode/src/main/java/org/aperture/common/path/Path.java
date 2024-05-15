package org.aperture.common.path;

import org.aperture.common.coordinates.XyhVector;
import org.aperture.common.path.waypoints.WaypointPath;

import java.util.Arrays;

public class Path implements Followable {
    private Followable[] curves;
    private double[] lengths;
    private double[] sumLengths;
    private double[] startTimes;

    private WaypointPath headingPath;
    public void setHeadingPath(WaypointPath path) {
        headingPath = path;
    }

    public Path(XyhVector[] vectors) { this(new XyhVector[][] {vectors}, null); }
    public Path(XyhVector[] vectors, WaypointPath headingPath) { this(new XyhVector[][]{vectors},headingPath);}
    public Path(XyhVector[][] vectors) {this(vectors,null); }
    public Path(XyhVector[][] vectors, WaypointPath headingPath) {
        this.headingPath = headingPath;

        curves = new Followable[vectors.length];
        lengths = new double[vectors.length];
        sumLengths = new double[vectors.length+1];
        startTimes = new double[vectors.length];

        for(int i=0; i < vectors.length; i++) {
            if(vectors[i].length==2) {
                XyhVector mid = new XyhVector();
                mid.pathLinear(vectors[i][0], vectors[i][1], 0.5);
                curves[i] = new Quadratic(new XyhVector[] {vectors[i][0],mid,vectors[i][1]});
            } else {
                curves[i] = new Quadratic(vectors[i]);
            }
            lengths[i] = curves[i].length(1);
            sumLengths[i] = i == 0 ? 0 : sumLengths[i-1] + lengths[i-1];
        }
        sumLengths[vectors.length] = curves[curves.length-1].length(1) + sumLengths[vectors.length-1];

        /* startTimes is the t value of the path where the curve starts
         * the time across the entire path is distributed proportionally to the length of each curve in the path.
         *
         * sumLengths is the length of the entire path at the start of each curve.
         *
         * ex- length = [ 15, 5,            10,         10          ] total = 40
         *  sumLength = [ 0,  15,           20,         30          ] total = 40
         * startTimes = [ 0,  0.375(15/40), 0.5(20/40), 0.75(30/40) ]
         * */
        for(int i=0; i<vectors.length; i++) {
            startTimes[i] = sumLengths[i]/sumLengths[vectors.length];
        }
        System.out.println("PATH: vectors=" + Arrays.deepToString(vectors));
        System.out.println("PATH: lengths=" + Arrays.toString(lengths));
        System.out.println("PATH: sumLengths=" + Arrays.toString(sumLengths));
        System.out.println("PATH: startTimes=" + Arrays.toString(startTimes));
    }

    //Here, because we have start times, we can find the index of the curve that we are currently on according to t
    //then, we find the "range" that curve takes up in the Path's t distribution, which is again calculated using startTimes.
    //after that, we scale the amount of t that is actually being used in the curCure by the range

    /* EXAMPLE
     * startTimes = [ 0,  0.375(15/40), 0.5(20/40), 0.75(30/40) ]
     *
     * so, for an input t of 0.4, the following would happen
     * i = 1(because 0.375 is the largest startTime lower than 0.4)
     * tRange = 0.125 (0.5-0.375)
     *
     * after this, we can get the t that isint "used up" in previous curves
     * tLeft = 0.025 (0.4-0.375)
     *
     * scales the t value of the curve from the range that it takes up in the path to the range it
     * should take up in the curve (0 to 1 instead of 0 to 0.125)
     * t that goes into curve - 0.2 (0.025/0.125)
     * */

    public XyhVector compute(double t) {
        int i = findIndexOfLastUnderThresh(startTimes,t);
        double tRange = i==curves.length-1 ? 1-startTimes[i] : startTimes[i+1]-startTimes[i];
        XyhVector result = curves[i].compute((t-startTimes[i])/tRange);
        if(headingPath != null) {
            result.h = headingPath.computeAtT(t,totalLength());
        }
        return result;
    }
    public XyhVector tangentVec(double t) {
        int i = findIndexOfLastUnderThresh(startTimes,t);
        double tRange = i==curves.length-1 ? 1-startTimes[i] : startTimes[i+1]-startTimes[i];
        return curves[i].tangentVec((t-startTimes[i])/tRange);
    }

    //this works a bit differently. it just runs the function for all the curves and returns the best result.
    public double closestPoint(XyhVector pos) {
        double closestT = 0;
        int closestI = 0;
        double closestDist = Double.POSITIVE_INFINITY;
        for(int i=0; i<curves.length; i++) {
            double t = curves[i].closestPoint(pos);
            XyhVector p = curves[i].compute(t);
            double dist = p.distanceTo(pos);
            if(dist < closestDist) {
                closestDist = dist;
                closestT = t;
                closestI = i;
            }
        }
        double tRange = closestI==curves.length-1 ? 1-startTimes[closestI] : startTimes[closestI+1]-startTimes[closestI];
        return closestT*tRange + startTimes[closestI];
    }

    //arc length up to t=t. this works by adding up the length of all the curves behind curCurve
    //(stored in sumLengths), and as the arc length of the current curve up to t.
    public double length(double t) {
        int i = findIndexOfLastUnderThresh(startTimes,t);
        double tRange = i==curves.length-1 ? 1-startTimes[i] : startTimes[i+1]-startTimes[i];
        System.out.println();
        return curves[i].length((t-startTimes[i])/tRange) + sumLengths[i];
    }

    public double totalLength() {
        return sumLengths[curves.length];
    }

    //converts arcLength into the t value of the curve (arc length parameterization of the curve)
    public double lengthToT(double l) {
        int i = findIndexOfLastUnderThresh(sumLengths,l);
        i = Math.min(i,curves.length-1);
        double tRange = i==curves.length-1 ? 1-startTimes[i] : startTimes[i+1]-startTimes[i];
        return curves[i].lengthToT(l-sumLengths[i])*tRange + startTimes[i];
    }

    private int findIndexOfLastUnderThresh(double[] arr, double target) {
        int result = 0;
        for(int i=0; i<arr.length; i++) {
            if(arr[i]<target) {
                result = i;
            } else {
                return result;
            }
        }
        return result;
    }
}