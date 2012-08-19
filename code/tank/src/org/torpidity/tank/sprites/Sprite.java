package org.torpidity.tank.sprites;

import java.awt.Graphics2D;

/**
 * Sprites can be drawn
 * 
 * @author Kevin Mershon
 */
public abstract class Sprite {

	public abstract void draw(Graphics2D g2, double xPos, double yPos,
			double rotation);

}
