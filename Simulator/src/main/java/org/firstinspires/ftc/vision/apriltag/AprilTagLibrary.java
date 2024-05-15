//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.firstinspires.ftc.vision.apriltag;

import java.util.ArrayList;
import java.util.Iterator;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;

public class AprilTagLibrary {
    private final AprilTagMetadata[] data;

    private AprilTagLibrary(AprilTagMetadata[] data) {
        this.data = data;
    }

    public AprilTagMetadata[] getAllTags() {
        return this.data;
    }

    public AprilTagMetadata lookupTag(int id) {
        AprilTagMetadata[] var2 = this.data;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            AprilTagMetadata tagMetadata = var2[var4];
            if (tagMetadata.id == id) {
                return tagMetadata;
            }
        }

        return null;
    }

    public static class Builder {
        private ArrayList<AprilTagMetadata> data = new ArrayList();
        private boolean allowOverwrite = false;

        public Builder() {
        }

        public AprilTagLibrary.Builder setAllowOverwrite(boolean allowOverwrite) {
            this.allowOverwrite = allowOverwrite;
            return this;
        }

        public AprilTagLibrary.Builder addTag(AprilTagMetadata aprilTagMetadata) {
            Iterator var2 = this.data.iterator();

            while(var2.hasNext()) {
                AprilTagMetadata m = (AprilTagMetadata)var2.next();
                if (m.id == aprilTagMetadata.id) {
                    if (!this.allowOverwrite) {
                        throw new RuntimeException("You attempted to add a tag to the library when it already contains a tag with that ID. You can call .setAllowOverwrite(true) to allow overwriting the existing entry");
                    }

                    this.data.remove(m);
                    break;
                }
            }

            this.data.add(aprilTagMetadata);
            return this;
        }

        public AprilTagLibrary.Builder addTag(int id, String name, double size, VectorF fieldPosition, DistanceUnit distanceUnit, Quaternion fieldOrientation) {
            return this.addTag(new AprilTagMetadata(id, name, size, fieldPosition, distanceUnit, fieldOrientation));
        }

        public AprilTagLibrary.Builder addTag(int id, String name, double size, DistanceUnit distanceUnit) {
            return this.addTag(new AprilTagMetadata(id, name, size, new VectorF(0.0F, 0.0F, 0.0F), distanceUnit, Quaternion.identityQuaternion()));
        }

        public AprilTagLibrary.Builder addTags(AprilTagLibrary library) {
            AprilTagMetadata[] var2 = library.getAllTags();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                AprilTagMetadata m = var2[var4];
                this.addTag(m);
            }

            return this;
        }

        public AprilTagLibrary build() {
            return new AprilTagLibrary((AprilTagMetadata[])this.data.toArray(new AprilTagMetadata[0]));
        }
    }
}
