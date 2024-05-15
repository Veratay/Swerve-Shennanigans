package android.graphics;

public class Rect {
    int left;
    int right;
    int top;
    int bottom;

    public Rect(double left, double top, double right, double bottom) {
        this.left = (int) left;
        this.top = (int) top;
        this.right = (int) right;
        this.bottom = (int) bottom;
    }
}
