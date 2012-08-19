package org.torpidity.tank.data;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserHandler is used to retrieve and update player information
 * 
 * @author Kevin Mershon
 */
public class UserHandler {
	
	/**
	 * Get the tank color for this uid
	 * 
	 * @param uid the uid
	 * @return the tank color (as an array of 3 Colors)
	 */
	public static Color[] getColors(int uid) {
		Color[] colors = new Color[3];
		int r, g, b;
		
		MySQL.query("SELECT * FROM `tankgame_colors` WHERE `cid`=(SELECT `color` FROM `tankgame_users` WHERE `uid`='" + uid + "')");
		ResultSet results = MySQL.result();
		try {
			r = results.getInt("color_1r");
			b = results.getInt("color_1b");
			g = results.getInt("color_1g");
			colors[0] = new Color(r, b, g);
			r = results.getInt("color_2r");
			b = results.getInt("color_2b");
			g = results.getInt("color_2g");
			colors[1] = new Color(r, b, g);
			r = results.getInt("color_3r");
			b = results.getInt("color_3b");
			g = results.getInt("color_3g");
			colors[2] = new Color(r, b, g);
		} catch (NullPointerException e) {
			// weird error
			return getColors(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return colors;
	}
	
	/**
	 * Get the max HP for this uid
	 * 
	 * @param uid the uid
	 * @return the max HP
	 */
	public static int getMaxHP(int uid) {
		MySQL.query("SELECT `max_hp` FROM `tankgame_users` WHERE `uid`='" + uid + "'");
		ResultSet r = MySQL.result();
		try {
			return r.getInt("max_hp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Get the UID associated with a particular username
	 * 
	 * @param username the username
	 * @return the uid
	 */
	public static int getUID(String username) {
		int uid = -1;
		MySQL.query("SELECT `uid` FROM `tankgame_users` WHERE `username`='" + username + "'");
		ResultSet r = MySQL.result();
		try {
			uid = r.getInt("uid");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// do nothing
		}
		return uid;
	}

	/**
	 * Get the username associated with a particular UID
	 * 
	 * @param uid the uid
	 * @return the username
	 */
	public static String getUsername(int uid) {
		String username = "";
		String nickname = "";
		MySQL.query("SELECT `username` FROM `tankgame_users` WHERE `uid`='" + uid + "'");
		ResultSet r = MySQL.result();
		try {
			username = r.getString("username");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		MySQL.query("SELECT `nickname` FROM `tankgame_users` WHERE `uid`='" + uid + "'");
		ResultSet r2 = MySQL.result();
		try {
			nickname = r2.getString("nickname");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (username == null)
			return getUsername(uid);
		
		if (!nickname.equals(""))
			return nickname + " [" + username + "]";
		else
			return username;
	}

	/**
	 * Set the nickname for a player
	 * 
	 * Setting the nickname to "off" will delete the nickname
	 * 
	 * @param uid the uid
	 * @param nickname the nickname
	 * @return the username
	 */
	public static String setNickname(int uid, String nickname) {
		if (!nickname.equals("off")) {
			MySQL.update("UPDATE `tankgame_users` SET `nickname`='" + nickname
					+ "' WHERE `uid`='" + uid + "'");
			return getUsername(uid);
		}
		else {
			MySQL.update("UPDATE `tankgame_users` SET `nickname`='' WHERE `uid`='" + uid + "'");
			return getUsername(uid);
		}
	}
}