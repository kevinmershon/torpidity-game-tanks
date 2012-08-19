package org.torpidity.tank.object;

import java.awt.Graphics2D;

/**
 * GameObject is used to define certain aspects which all objects in the game
 * must have. Specifically objects must be drawable, and they must have an (x,y)
 * position and a rotation angle. Lastly this position can be set with the
 * setLocation() method.
 * 
 * @author Kevin Mershon
 */
public abstract class GameObject {
	protected double xPos;
	protected double yPos;
	protected double rotation;
	
	/**
	 * Draw this GameObject
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public abstract void draw(Graphics2D g2);
	
	/**
	 * Get the BoundingRectangle for this GameObject
	 * 
	 * @return the bounding rectangle
	 */
	public abstract BoundingArea getBoundingArea();
	
	/**
	 * Get the rotation
	 * 
	 * @return the rotation
	 */
	public double getRotation() {
		return rotation;
	}

	/**
	 * Get the x-position
	 * 
	 * @return the x-position
	 */
	public double getX() {
		return xPos;
	}

	/**
	 * Get the y-position
	 * 
	 * @return the y-position
	 */
	public double getY() {
		return yPos;
	}
	
	/**
	 * Test whether this GameObject has collided with another
	 * 
	 * @param other the other GameObject
	 * @return true if they have collided, false otherwise
	 */
	public boolean hasCollidedWith(GameObject other) {
		BoundingArea ba1 = getBoundingArea();
		BoundingArea ba2 = other.getBoundingArea();

		return ba1.intersects(ba2);
	}
	
	/**
	 * Move this GameObject one tick
	 */
	public abstract void movement();

	/**
	 * Position the object at (x,y) facing angle rotation
	 * 
	 * @param x
	 *            the x-position
	 * @param y
	 *            the y-position
	 * @param rotation
	 *            the rotation
	 */
	public void setPosition(double x, double y, double rotation) {
		this.xPos = x;
		this.yPos = y;
		this.rotation = rotation;
	}

}
