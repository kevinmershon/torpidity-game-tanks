package org.torpidity.tank.object;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * This class is used for 2D rectangluar collision detection
 * 
 * @author Kevin Mershon
 */
public class BoundingArea {
	private Area area;

	/**
	 * Create a new BoundingRectangle
	 * 
	 * @param xPos
	 *            the x-position
	 * @param yPos
	 *            the y-position
	 * @param angle
	 *            the angle
	 * @param thearea
	 *            the Area
	 */
	public BoundingArea(double xPos, double yPos, double angle, Area theArea) {
		this.area = theArea;

		AffineTransform transform = new AffineTransform();
		transform.rotate(-angle);
		area = area.createTransformedArea(transform);

		transform = new AffineTransform();
		transform.translate(xPos, yPos);
		area = area.createTransformedArea(transform);
	}

	/**
	 * Get the Area for this BoundingArea
	 * 
	 * @return the Area
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * Check whether this BoundingRectangle intersects another
	 * 
	 * @param other
	 *            the other
	 * @return true if they intersect, false otherwise
	 */
	public boolean intersects(BoundingArea other) {
		Area intersection = new Area();
		intersection.add(area);
		intersection.intersect(other.area);

		return !intersection.isEmpty();
	}
}
