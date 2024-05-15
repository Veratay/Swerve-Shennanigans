package org.aperture.common.path.waypoints;

public class WaypointPath {
    private final Waypoint[] waypoints;
    private double defaultStart;
    public WaypointPath(Waypoint[] waypoints) {
        this.waypoints = waypoints;
    }

    public void setDefaultStart(double v) {
        defaultStart = v;
    }

    public double computeAtT(double t, double totalDist) {

        //code for no interpolation and just going to the closest waypoint under t.
        Waypoint closestUnder = new TWaypoint(0.0,defaultStart);
        for(Waypoint waypoint : waypoints) {
            double curT = waypoint.getT(totalDist);
            double closestT = closestUnder.getT(totalDist);

            if(curT <= t && t-curT <= t-closestT) {
                closestUnder = waypoint;
            }
        }
        return closestUnder.getV();
    }

    public double computeAtDist(double d, double totalDist) {
        return computeAtT(d/totalDist,totalDist);
    }
}
