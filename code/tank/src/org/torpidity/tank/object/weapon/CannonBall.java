package org.torpidity.tank.object.weapon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import org.torpidity.tank.gui.ViewPanel;
import org.torpidity.tank.object.BoundingArea;

/**
 * CannonBall is a cannon ball.
 * 
 * @author Kevin Mershon
 */
public class CannonBall extends Weapon {

	/**
	 * Create a new CannonBall
	 */
	public CannonBall() {
		soundFile = "cannon1.wav";
		refireRate = 100;
		refireTime = 0;
	}

	/**
	 * Draw this CannonBall
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public void draw(Graphics2D g2) {
		refireTime -= 1;
		if (!hasHit) {
			double X = ((double) xPos) / ((double) X_MAX) * ViewPanel.WIDTH;
			double Y = ((double) yPos) / ((double) Y_MAX) * ViewPanel.HEIGHT;
			double R = Math.toRadians(rotation);

			AffineTransform orig = g2.getTransform();
			AffineTransform rotated = (AffineTransform) (orig.clone());
			rotated.rotate(R, X, Y);
			g2.setTransform(rotated);
			g2.setColor(Color.black);
			g2.fill(new Ellipse2D.Double(X - 3, Y - 3, 7, 7));
			g2.setTransform(orig);
		}
	}
	
	/**
	 * Get the BoundingArea for this CannonBall
	 */
	public BoundingArea getBoundingArea() {
		return null;
	}

	/**
	 * Move this CannonBall one tick
	 */
	public void movement() {
		if (isFired) {
			double R = Math.toRadians(rotation);
			xPos += 65 * Math.cos(R);
			yPos -= 65 * Math.sin(R);
		}
	}
	
}