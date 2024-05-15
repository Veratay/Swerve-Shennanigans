package org.aperture.common.path;


import org.aperture.common.coordinates.XyhVector;

public interface Followable {
    XyhVector compute(double t); //calculates the curve at t
    XyhVector tangentVec(double t); //find tangent vector at t
    double closestPoint(XyhVector pos); //returns the t value of the closest point on the curve to the inputted pos point.

    double length(double t); //returns the arc length of the curve
    double lengthToT(double l); //finds the t value of the curve at a given length
    double totalLength(); //just a getter to a cached value, since this is used in a lot of calculations
}
