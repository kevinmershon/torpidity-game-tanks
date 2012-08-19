package org.torpidity.tank.sprites;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * TankImage is a generic Java2D sprite which has 3-color basis, a center, a
 * description, and a BufferedImage in which the sprite is stored.
 * 
 * @author Kevin Mershon
 */
public abstract class TankImage extends Sprite {
	protected BufferedImage image;
	protected Area boundingArea;
	protected Point center;

	protected Color myColor;
	protected Color myColor2;
	protected Color myColor3;

	protected String description;

	/**
	 * Create a new TankImage
	 * 
	 * @param color
	 *            the color array
	 * @param description
	 *            a description of the TankImage
	 */
	public TankImage(Color[] colors, String description) {
		myColor = colors[0];
		myColor2 = colors[1];
		myColor3 = colors[2];
		
		this.description = description;
		this.boundingArea = new Area();
	}

	/**
	 * Get the BoundingArea for this TankImage
	 * 
	 * @return the BoundingArea for this TankImage
	 */
	public Area getBoundingArea() {
		return boundingArea;
	}
	
	/**
	 * Get the String representation of this TankImage
	 */
	public String toString() {
		return description;
	}
}