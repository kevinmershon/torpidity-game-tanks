package org.torpidity.tank.base;

import org.torpidity.tank.gui.client.Login;
import org.torpidity.tank.gui.client.TankClientFrame;

/**
 * The TankGame class simply checks for a uid login value, and then starts up
 * the UI
 * 
 * @author Kevin Mershon
 */
public class TankGame {
	public static void main(String[] args) {

		int uid;
		if (args.length > 0 && args[0] != null) {
			uid = Integer.parseInt(args[0]);
			new TankClientFrame(uid);
		} else {
			new Login();
		}
	}
}