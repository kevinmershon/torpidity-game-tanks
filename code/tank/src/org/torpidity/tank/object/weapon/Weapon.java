package org.torpidity.tank.object.weapon;

import org.torpidity.tank.base.SoundHandler;
import org.torpidity.tank.object.GameObject;

public abstract class Weapon extends GameObject {
	// Static final maximums
	public static final double X_MAX = 1600;
	public static final double Y_MAX = 1010;
	public static final double ROT_MAX = 360;

	// Variables
	protected boolean hasHit = true;
	protected boolean isFired = false;
	protected String soundFile;
	protected int refireRate;
	protected int refireTime;

	/**
	 * Has this weapon hit something?
	 * 
	 * @return true if it has, false otherwise
	 */
	public boolean hasHit() {
		return hasHit;
	}

	/**
	 * Fire this weapon. Parameters are passed to setLocation() method.
	 * 
	 * @param x
	 *            the x-position
	 * @param y
	 *            the y-position
	 * @param rotation
	 *            the rotation
	 */
	public void fire(double x, double y, double rotation) {
		if (refireTime > 0)
			return;

		setPosition(x, y, rotation);
		refireTime = refireRate;
		hasHit = false;
		isFired = true;
		try {
			if (soundFile != null) {
				new Thread() {
					public void run() {
						SoundHandler.playSound(soundFile);
					}
				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}