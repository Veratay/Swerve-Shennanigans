package org.aperture.common.path.waypoints;

public class DistWaypoint implements Waypoint {
    private final double dist;
    private final double v;
    public DistWaypoint(double dist, double v) {
        this.dist = dist;
        this.v = v;
    }

    public double getT(double totalDist) {
        if(dist < 0 || 1.0/dist == Double.NEGATIVE_INFINITY) {
            return (totalDist+dist)/totalDist;
        } else {
            return dist/totalDist;
        }
    }

    public double getV() {
        return v;
    }
}
