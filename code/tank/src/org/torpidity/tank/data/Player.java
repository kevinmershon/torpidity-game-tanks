package org.torpidity.tank.data;

import java.awt.Color;
import java.awt.Graphics2D;
import org.torpidity.tank.object.Tank;

/**
 * The Player class represents the data manifestation of a player
 * 
 * @author Kevin Mershon
 */
public class Player {
	// Database values
	private int uid;
	private String username;
	private int maxHP;
	private Color[] colors;

	// Game values
	private Tank tank;
	private int hp;

	// State change values
	private boolean isConnecting;
	private boolean isDisconnecting;
	private int isMoving;
	private boolean booster;
	private int isTurning;
	private int weaponSelection;
	private boolean isFiring;
	private boolean isSpawning;

	/**
	 * Create a new player with the given UID
	 * 
	 * @param uid
	 *            the uid
	 */
	public Player(int uid) {
		this.uid = uid;
		isConnecting = true;
		tank = new Tank(this);
		maxHP = UserHandler.getMaxHP(uid);
		hp = maxHP;
		weaponSelection = 1;
	}

	/**
	 * Affect the player's HP in some way
	 * 
	 * @param change
	 *            the value to be added to HP
	 */
	public void changeHP(int change) {
		hp += change;
		if (hp < 0)
			hp = 0;
		if (hp > maxHP)
			hp = maxHP;
	}

	/**
	 * Draw this player's graphical components (usually just the tank)
	 * 
	 * @param g2
	 *            the graphics context
	 */
	public void draw(Graphics2D g2) {
		tank.draw(g2);
	}

	/**
	 * Decode a 32-bit Integer into player data
	 * 
	 * @param data
	 *            the encoded data
	 */
	public void decode32(int data) {
		// UID
		int uid = (data >> 22) & 0xFF;
		if (uid != this.uid)
			return;

		// Codes
		int code = (data >> 30) & 0x3;
		if (code == 0) {
			isConnecting = false;
			isDisconnecting = false;

			// Movement
			int movement = (data >> 20) & 0x3;
			if (movement == 2)
				isMoving = 1;
			else if (movement == 1)
				isMoving = -1;
			else
				isMoving = 0;
			int theBooster = (data >> 19) & 0x1;
			booster = (theBooster == 1);
			// Turning
			int turning = (data >> 17) & 0x3;
			if (turning == 2)
				isTurning = 1;
			else if (turning == 1)
				isTurning = -1;
			else
				isTurning = 0;
			// Weapon
			weaponSelection = (data >> 12) & 0x1F;
			// Firing
			int firing = (data >> 11) & 0x1;
			isFiring = (firing == 1);
			// Spawning
			int spawning = (data >> 10) & 0x1;
			isSpawning = (spawning == 1);
		} else if (code == 1) {
			isConnecting = true;
			maxHP = (data >> 11) & 0x7FF;
		} else {
			isDisconnecting = true;
		}
	}

	/**
	 * Decode a 64-bit Long into player data
	 * 
	 * @param data
	 *            the encoded data
	 * @param isLocal
	 *            the player is the local player
	 */
	public void decode64(long data) {
		// UID
		int uid = (int) ((data >> 54) & 0xFF);
		if (uid != this.uid)
			return;

		// Codes
		int code = (int) ((data >> 62) & 0x3);
		if (code == 0) {
			isConnecting = false;
			isDisconnecting = false;

			// HP
			hp = (int) ((data >> 43) & 0x7FF);
			// Position
			int xPos = (int) ((data >> 32) & 0x7FF);
			int yPos = (int) ((data >> 21) & 0x7FF);
			int angle = (int) ((data >> 12) & 0x1FF);
			tank.setPosition(xPos, yPos, angle);
			// Movement
			int movement = (int) ((data >> 10) & 0x3);
			if (movement == 2)
				isMoving = 1;
			else if (movement == 1)
				isMoving = -1;
			else
				isMoving = 0;
			int theBooster = (int) ((data >> 9) & 0x1);
			booster = (theBooster == 1);
			// Turning
			int turning = (int) ((data >> 7) & 0x3);
			if (turning == 2)
				isTurning = 1;
			else if (turning == 1)
				isTurning = -1;
			else
				isTurning = 0;
			// Weapon
			weaponSelection = (int) ((data >> 2) & 0x1F);
			// Firing
			int firing = (int) ((data >> 1) & 0x1);
			isFiring = (firing == 1);
			// Spawning
			int spawning = (int) (data & 0x1);
			isSpawning = (spawning == 1);
		} else if (code == 1) {
			isConnecting = true;
			maxHP = (int) ((data >> 43) & 0x7FF);
			double xPos = (double) ((data >> 32) & 0x7FF);
			double yPos = (double) ((data >> 21) & 0x7FF);
			double angle = (double) ((data >> 12) & 0x1FF);
			tank.setPosition(xPos, yPos, angle);
		} else {
			isDisconnecting = true;
		}
	}

	/**
	 * Encode this player's data into a 32-bit Integer
	 * 
	 * @return the encoded data
	 */
	public int encode32() {
		// UID
		int data = (uid << 22);
		if (isConnecting) {
			// Connecting code
			data |= (1 << 30);
			// Max HP
			data |= (maxHP << 11);
			isConnecting = false;
		} else if (isDisconnecting) {
			// Disconnecting code
			data |= (1 << 31);
		} else {
			// Movement
			if (isMoving == 1)
				data |= (1 << 21);
			else if (isMoving == -1)
				data |= (1 << 20);
			if (booster)
				data |= (1 << 19);
			// Turning
			if (isTurning == 1)
				data |= (1 << 18);
			else if (isTurning == -1)
				data |= (1 << 17);
			// Weapon
			data |= (weaponSelection << 12);
			// Firing
			if (isFiring)
				data |= (1 << 11);
			// Spawning
			if (isSpawning)
				data |= (1 << 10);
		}
		return data;
	}

	/**
	 * Encode this player's data into a 64-bit Long
	 * 
	 * @return the encoded data
	 */
	public long encode64() {
		// UID
		long data = ((long) uid << 54);
		if (isConnecting) {
			// Connecting code
			data |= (1L << 62);
			// Max HP
			data |= ((long) maxHP << 43);
		} else if (isDisconnecting) {
			// Disconnecting code
			data |= (1L << 63);
		} else {
			// HP
			data |= ((long) hp << 43);
			// Position
			data |= (((long) tank.getX()) << 32);
			data |= (((long) tank.getY()) << 21);
			data |= (((long) tank.getRotation()) << 12);
			// Movement
			if (isMoving == 1)
				data |= (2L << 10);
			else if (isMoving == -1)
				data |= (1L << 10);
			if (booster)
				data |= (1L << 9);
			// Turning
			if (isTurning == 1)
				data |= (2L << 7);
			else if (isTurning == -1)
				data |= (1L << 7);
			// Weapon
			data |= ((long) weaponSelection << 2);
			// Firing
			if (isFiring)
				data |= (1L << 1);
			// Spawning
			if (isSpawning)
				data |= 1L;
		}
		return data;
	}

	/**
	 * Two Players are equal if their UID is the same
	 * 
	 * @param other
	 *            the other player
	 * @return true if the UIDs are the same, false otherwise
	 */
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Player))
			return false;

		Player pOther = (Player) other;
		if (pOther.uid == this.uid)
			return true;
		return false;
	}

	/**
	 * Get the player colors
	 * 
	 * Accesses database on first call
	 * 
	 * @return the colors
	 */
	public Color[] getColors() {
		if (colors == null)
			colors = UserHandler.getColors(uid);
		return colors;
	}

	/**
	 * Get the player's HP
	 * 
	 * @return the player's HP
	 */
	public int getHP() {
		return hp;
	}

	/**
	 * Get the player's max HP
	 * 
	 * @return the player's max HP
	 */
	public int getMaxHP() {
		return maxHP;
	}

	/**
	 * Get the player's Tank
	 * 
	 * @return the player's Tank
	 */
	public Tank getTank() {
		return tank;
	}

	/**
	 * get the player's UID
	 * 
	 * @return the player's uid
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Get the Player UID from some encoded data received from a client
	 * 
	 * @param data
	 *            the data
	 * @return the uid
	 */
	public static int getUIDFromClient(int data) {
		return (data >> 22) & 0xFF;
	}

	/**
	 * Get the Player UID from some encoded data received from the server
	 * 
	 * @param data
	 *            the data
	 * @return the uid
	 */
	public static int getUIDFromClient(long data) {
		return (int) ((data >> 54) & 0xFF);
	}

	/**
	 * Get the player's username
	 * 
	 * Accesses database on first call
	 * 
	 * @return the username
	 */
	public String getUsername() {
		if (username == null) {
			username = UserHandler.getUsername(uid);
		}
		return username;
	}

	/**
	 * Check whether this player is alive
	 * 
	 * @return true if alive, false otherwise
	 */
	public boolean isAlive() {
		return hp > 0;
	}

	/**
	 * Check whether the player is using their booster
	 * 
	 * @return true if they are, false otherwise
	 */
	public boolean isBooster() {
		return booster;
	}

	/**
	 * Check whether this player is connecting
	 * 
	 * @return true if connecting, false otherwise
	 */
	public boolean isConnecting() {
		return isConnecting;
	}

	/**
	 * Check whether this player is disconnecting
	 * 
	 * @return true if disconnecting, false otherwise
	 */
	public boolean isDisconnecting() {
		return isDisconnecting;
	}

	/**
	 * Check whether this player is moving
	 * 
	 * @return 1 if they're moving forward, -1 if backward, 0 if no movement
	 */
	public int isMoving() {
		return isMoving;
	}
	
	/**
	 * Check whether this player is spawning
	 * 
	 * @return true if they're spawning, false otherwise
	 */
	public boolean isSpawning() {
		return isSpawning;
	}
	
	/**
	 * Check whether this player is turning
	 * 
	 * @return 1 if they're turning left, -1 if right, 0 if no turning
	 */
	public int isTurning() {
		return isTurning;
	}

	/**
	 * Set the isDisconnecting value
	 * 
	 * @param disconnecting
	 *            the value to be set
	 */
	public void setDisconnecting(boolean disconnecting) {
		isDisconnecting = disconnecting;
	}
	
	/**
	 * Spawn this player
	 */
	public void spawn() {
		hp = maxHP;
		tank.spawn();
	}

	/**
	 * Get this Player as a String
	 */
	public String toString() {
		return "UID : " + uid + "\n" + "HP : " + hp + "\n" + "MaxHP : " + maxHP
				+ "\n" + "Connect : " + isConnecting + "\n" + "Disconnect : "
				+ isDisconnecting + "\n" + "Moving : " + isMoving + "\n"
				+ "Booster : " + booster + "\n" + "Turning : " + isTurning
				+ "\n" + "Weapon : " + weaponSelection + "\n" + "Firing : "
				+ isFiring + "\n" + "Spawning : " + isSpawning + "\n"
				+ "Tank X : " + tank.getX() + "\n" + "TANK Y : " + tank.getY()
				+ "\n" + "TANK R : " + tank.getRotation() + "\n";
	}

	public void stateChangeBooster(boolean booster) {
		if (!isAlive())
			return;
		this.booster = booster;
	}

	public void stateChangeConnect(boolean isConnecting) {
		this.isConnecting = isConnecting;
	}

	public void stateChangeFire(boolean isFiring) {
		if (!isAlive())
			return;
		this.isFiring = isFiring;
	}

	public void stateChangeMove(int isMoving) {
		if (!isAlive())
			return;
		this.isMoving = isMoving;
	}

	public void stateChangeSpawn(boolean isSpawning) {
			this.isSpawning = isSpawning;
	}

	public void stateChangeTurn(int isTurning) {
		if (!isAlive())
			return;
		this.isTurning = isTurning;
	}

	public void stateChangeWeapon(int weaponSelection) {
		this.weaponSelection = weaponSelection;
	}
}
