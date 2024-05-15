package org.aperture.common.path.waypoints;

public class TWaypoint implements Waypoint {
    private final double t;
    private final double v;
    public TWaypoint(double t, double v) {
        this.t = t;
        this.v = v;
    }

    public double getT(double totalDist) {
        return t;
    }

    public double getV() {
        return v;
    }
}
