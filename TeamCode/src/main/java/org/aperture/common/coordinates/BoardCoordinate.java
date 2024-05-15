package org.aperture.common.coordinates;

public class BoardCoordinate {
    //coordinate system:
    //(0,0) is the bottom leftmost pixel
    int row;
    int column;

    //all of this (should) be computed at compile time, leaving the math to make it explicit.
    //values taken from GM2
    public final static double boardTheta = Math.toRadians(60);
    private final static double pixelRadiusSmall = inchToCm(1.5);
    private final static double pixelRadius = (1.0/Math.cos(Math.toRadians(30))) * pixelRadiusSmall;
    private final static double pixelSide = 2.0*Math.tan(Math.toRadians(30)) * pixelRadiusSmall;
    private final static double cmPerRow = pixelRadius + pixelSide/2.0;
    private final static double cmPerColumn = pixelRadiusSmall*2.0;
    private final static double lowestPixelCenter = (1.0/Math.sin(boardTheta))*inchToCm(7.25);
    private final static double tileSize = inchToCm(24);
    private final static double boardY = 6*tileSize-inchToCm(11.25); //not exactly right
    private final static double boardX = 1.5*tileSize-pixelRadiusSmall*5.0;

    public BoardCoordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Vector3 toVector3(boolean red) {
        //frame:
        //at origin with the board on the xz plane
        Vector3 pos = new Vector3(
                cmPerColumn*((double)column-(double)(column%2)*0.5*pixelRadiusSmall),
                0,
                lowestPixelCenter+cmPerRow*row
        );

        //tilts board
        pos.rotateAroundVector(new Vector3(1,0,0), boardTheta);

        //transforms from origin to board position
        //(boardX, boardY) is the coordinate on the ground of a line that goes straight down on the board
        //that intersects with the center of the BoardCoordinate (0,0)
        pos.y += boardY;
        if(red) pos.x += tileSize*3 + boardX; else pos.x += boardX;

        return pos;
    }

    static double inchToCm(double inch) {
        return inch*2.54;
    }
}
