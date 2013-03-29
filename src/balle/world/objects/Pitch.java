package balle.world.objects;

import balle.world.Coord;
import balle.world.Line;

public class Pitch extends StaticFieldObject {
	
	// Pitch dimensions
	private final double pitchWidth, pitchLength;

	// Coordinates
    private final double minX, maxX, minY, maxY;
    
    // Corners 
    private final Coord cornerLeftTop, cornerRightTop, cornerLeftBottom, cornerRightBottom;
    
    // Sides
    private final Line leftSide, rightSide, topSide, bottomSide;
    

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }
    
    public Pitch() {
    	// Test with smaller pitch.
    	this(0, 2.30, 0, 1.00);
    }

    public Pitch(double minX, double maxX, double minY, double maxY) {
    	// Set coordinates
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        
    	// Pitch dimensions
		this.pitchWidth = Math.abs(maxY - minY);
		this.pitchLength = Math.abs(maxX - minX);
		
		// Set pitch corners
		cornerLeftTop = new Coord(maxX, maxY);
		cornerRightTop = new Coord(maxX, minY);
		cornerLeftBottom = new Coord(minX, maxY);
		cornerRightBottom = new Coord(minX, minY);
		
		// Set pitch sides
		leftSide = new Line(cornerLeftBottom, cornerLeftTop);
		rightSide = new Line(cornerRightBottom, cornerRightTop);
		topSide = new Line(cornerRightTop, cornerLeftTop);
		bottomSide = new Line(cornerRightBottom, cornerLeftBottom);
    }

    @Override
    public Coord getPosition() {
        return new Coord((minX + maxX) / 2, (minY + maxY / 2));
    }

    @Override
    public boolean containsCoord(Coord point) {
        if (point.getX() > maxX)
            return false;
        if (point.getX() < minX)
            return false;
        if (point.getY() > maxY)
            return false;
        if (point.getY() < minY)
            return false;
        return true;
    }
    
	public Line[] getWalls() {
		return new Line[] {leftSide, rightSide, topSide, bottomSide};
	}

	public Line getLeftWall() {
		return leftSide;
	}
	
	public Line getRightWall() {
		return rightSide;
	}
	
	public Line getTopWall() {
		return topSide;
	}
	
	public Line getBottomWall() {
		return bottomSide;
	}


	@Override
	public boolean intersects(Line line) {
		return containsCoord(line.getA()) != containsCoord(line.getB());
	}

	/**
	 * Get half of the pitch this point is on.
	 * 
	 * @param position
	 *            Point in the pitch.
	 * @return True if on left, false otherwise.
	 */
	public Object getHalf(Coord position) {
		return position.getX() < getPosition().getX();
	}

}
