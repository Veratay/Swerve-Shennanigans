package aperture.simulator;

import org.joml.Matrix4f;

public class Projection {

    private final float FOV;
    private static final float Z_FAR = 1000.f;
    private static final float Z_NEAR = 0.01f;

    private Matrix4f projMatrix;

    public Projection(int width, int height, float FOV) {
        projMatrix = new Matrix4f();
        this.FOV = FOV;
        updateProjMatrix(width, height);
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public void updateProjMatrix(int width, int height) {
        projMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
    }

    public void setProjMatrix(Matrix4f proj) {
        this.projMatrix = proj;
    }
}
