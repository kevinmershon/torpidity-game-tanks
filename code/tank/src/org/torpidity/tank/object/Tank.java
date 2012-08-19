package org.torpidity.tank.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.torpidity.tank.data.Player;
import org.torpidity.tank.gui.ViewPanel;
import org.torpidity.tank.object.weapon.Weapon;
import org.torpidity.tank.object.weapon.CannonBall;
import org.torpidity.tank.sprites.TankM1A2;

/**
 * This class is the data and graphical manifestation of the player's Tank.
 * 
 * @author Kevin Mershon
 */
public class Tank extends GameObject {
	public static final double X_MAX = 1600;
	public static final double Y_MAX = 1000;
	public static final double ROT_MAX = 360;

	// User values
	private Player player;
	private ArrayList<Weapon> weapons;
	private TankM1A2 img;

	/**
	 * Create a new tank
	 * 
	 * @param colors
	 *            the colors
	 */
	public Tank(Player player) {
		this.player = player;
		weapons = new ArrayList<Weapon>(10);
		img = new TankM1A2(player.getColors());

		// All tanks have a cannonball
		weapons.add(new CannonBall());

		spawn();
	}
	
	/**
	 * Bounce this tank off another
	 * 
	 * @param other the other Tank
	 */
	public void bounce() {
		double boosterVal = (player.isBooster() ? 3 : 1);
		double radians = Math.toRadians(-rotation);
		
		xPos += 3 * boosterVal * .5+player.isMoving() * Math.cos(radians);
		yPos -= 3 * boosterVal * .5+player.isMoving() * Math.sin(radians);
	}
	
	/**
	 * Draw this Tank
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public void draw(Graphics2D g2) {
		drawWeapons(g2);

		double X = xPos / X_MAX * ViewPanel.WIDTH;
		double Y = yPos / Y_MAX * ViewPanel.HEIGHT;
		double R = Math.toRadians(rotation);

		img.draw(g2, X, Y, R);
	}

	/**
	 * Draw the player's health bar in the upper right of the screen
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public void drawHealthBar(Graphics2D g2) {
		int health = player.getHP();
		int max_health = player.getMaxHP();
		double hp = player.getHP();
		double max_hp = player.getMaxHP();

		double X = xPos / X_MAX * 800;
		double Y = yPos / Y_MAX * 505;

		g2.setColor(Color.black);
		g2.fill(new Rectangle2D.Double(X - 30, Y + 50, 60, 6));
		if (health >= max_health / 2)
			g2.setColor(new Color((int) (225 * ((max_hp - hp) / (max_hp / 2))),
					225, 0));
		if (health < max_health / 2 && health != 0)
			g2.setColor(new Color(225, (int) (225 * (hp / (max_hp / 2))), 0));
		g2
				.fill(new Rectangle2D.Double(X - 29, Y + 51,
						(58 * (hp / max_hp)), 4));
	}

	/**
	 * Draw all the weapons currently displaying for this Tank
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public void drawWeapons(Graphics2D g2) {
		for (int i = 0; i < weapons.size(); i++) {
			Weapon weapon = weapons.get(i);
			if (weapon instanceof CannonBall)
				((CannonBall) weapon).draw(g2);
		}
	}

	/**
	 * Get the BoundingRectangle for this Tank
	 */
	public BoundingArea getBoundingArea() {
		double X = xPos / X_MAX * ViewPanel.WIDTH;
		double Y = yPos / Y_MAX * ViewPanel.HEIGHT;
		double R = Math.toRadians(rotation);
		
		return new BoundingArea(X, Y, R, img.getBoundingArea());
	}
	
	/**
	 * Move the Tank one tick
	 */
	public void movement() {
		double boosterVal = (player.isBooster() ? 3 : 1);
		
		// Turning
		if (player.isTurning() < 0)
			rotation -= 1.5;
		else if (player.isTurning() > 0)
			rotation += 1.5;
		if (rotation < 0)
			rotation += Tank.ROT_MAX;
		else if (rotation > Tank.ROT_MAX)
			rotation -= Tank.ROT_MAX;
		
		// Moving
		if (player.isMoving() != 0) {
			double radians = Math.toRadians(rotation);
			xPos += 3 * boosterVal * player.isMoving() * Math.cos(radians);
			yPos -= 3 * boosterVal * player.isMoving() * Math.sin(radians);
		}

		/**
		 * Don't let the tank leave the game area (for now)
		 */
		if (xPos < 30)
			xPos = 30;
		else if (xPos > 1550)
			xPos = 1550;
		if (yPos < 30)
			yPos = 30;
		else if (yPos > 825)
			yPos = 825;
	}
	
	/**
	 * Spawn the Tank
	 */
	public void spawn() {
		setPosition(X_MAX / 2, Y_MAX / 2, ROT_MAX / 4);
	}

}