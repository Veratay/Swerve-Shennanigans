//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision.apriltag;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;

public class AprilTagMetadata {
    public final int id;
    public final double tagsize;
    public final String name;
    public final DistanceUnit distanceUnit;
    public final VectorF fieldPosition;
    public final Quaternion fieldOrientation;

    public AprilTagMetadata(int id, String name, double tagsize, VectorF fieldPosition, DistanceUnit distanceUnit, Quaternion fieldOrientation) {
        this.id = id;
        this.name = name;
        this.tagsize = tagsize;
        this.fieldOrientation = fieldOrientation;
        this.fieldPosition = fieldPosition;
        this.distanceUnit = distanceUnit;
    }

    public AprilTagMetadata(int id, String name, double tagsize, DistanceUnit distanceUnit) {
        this.id = id;
        this.name = name;
        this.tagsize = tagsize;
        this.fieldOrientation = Quaternion.identityQuaternion();
        this.fieldPosition = new VectorF(0.0F, 0.0F, 0.0F);
        this.distanceUnit = distanceUnit;
    }
}
