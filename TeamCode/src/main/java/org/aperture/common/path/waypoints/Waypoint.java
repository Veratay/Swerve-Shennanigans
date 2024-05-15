package org.aperture.common.path.waypoints;

public interface Waypoint {
    double getT(double totalDist);
    double getV();
}
