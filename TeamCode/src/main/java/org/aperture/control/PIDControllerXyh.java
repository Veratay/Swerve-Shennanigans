package org.aperture.control;

import org.aperture.common.coordinates.XyhVector;

public class PIDControllerXyh {
    PIDController xController;
    PIDController yController;
    PIDController hController;

    public PIDControllerXyh(XyhVector P,XyhVector I, XyhVector D) {
        xController = new PIDController(P.x, I.x, D.x);
        yController = new PIDController(P.y, I.y, D.y);
        hController = new PIDController(P.h, I.h, D.h);
    }

    public XyhVector run(XyhVector err) {
        double x= xController.run(err.x);
        double y= yController.run(err.y);
        double h= hController.run(err.h);

        return new XyhVector(x,y,h);
    }
}
