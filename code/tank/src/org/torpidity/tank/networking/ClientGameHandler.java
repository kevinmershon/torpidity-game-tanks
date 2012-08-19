package org.torpidity.tank.networking;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import org.torpidity.tank.data.Player;

/**
 * ClientGameHandler maintains a local copy of all the other clients and their
 * data, and is also the proxy for sending player state changes to the server.
 * 
 * @author Kevin Mershon
 */
public class ClientGameHandler implements Runnable {
	private static final int MAX_USERS = 10;
	
	private Socket socket;
	private BufferedInputStream bIn;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	
	private Player localPlayer;
	private ArrayList<Player> players;
	private HashMap<Integer, Player> lookup;
	
	/**
	 * Create a new ClientGameHandler
	 */
	public ClientGameHandler(Player localPlayer) {
		this.localPlayer = localPlayer;
		try {
			socket = new Socket("69.181.238.214", 7282);
			inStream = new DataInputStream(socket.getInputStream());
			bIn = new BufferedInputStream(inStream);
			outStream = new DataOutputStream(socket.getOutputStream());
			
			players = new ArrayList<Player>(MAX_USERS);
			lookup = new HashMap<Integer, Player>(MAX_USERS);
			
			new Thread(this).start();
		} catch (SocketException e) {
			System.out.println("Could not connect to server!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a Player based on UID
	 *  
	 * @param uid the uid
	 * @return the Player
	 */
	public Player getLocalPlayer() {
		return lookup.get(localPlayer.getUID());
	}
	
	/**
	 * Get the ArrayList of Players
	 * 
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	/**
	 * Read in any incoming data and update the remote client tank data
	 * accordingly
	 */
	public void read() {
		try {
			while(bIn.available() >= 8) {
				long data = inStream.readLong();
				int uid = Player.getUIDFromClient(data);
				
				Player player = lookup.get(uid);
				// Test for new connections
				if (player == null) {
					player = new Player(uid);
					players.add(player);
					lookup.put(uid, player);
				}
				
				player.decode64(data);
								
				// Player is disconnecting
				if (player.isDisconnecting()) {
					players.remove(player);
					lookup.remove(uid);
				}
			}
		} catch (SocketException e) {
			// disconnected from server
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Constantly read new data from the server
	 */
	public void run() {
		while (true) {
			read();
			
			try {
				Thread.sleep(15);
			} catch (Exception e) {
				// do nothing
			}
		}
	}
	
	/**
	 * Write the local client data to the server.
	 */
	public void write(int data) {
		try {
			outStream.writeInt(data);
		} catch (SocketException e) {
			// disconnected from server
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Predict the next position of a tank based on its state values.
	 * 
	 * @param tank the tank
	 
	private void recalc(Tank tank) {
		
		timePassed[0][TankPos] = timePassed[1][TankPos];
		timePassed[1][TankPos] = Calendar.getInstance().getTimeInMillis();
		double[] x = new double[2];
		double[] y = new double[2];
		double[] r = new double[2];
		double[] t = new double[2];
		for (int i = 0; i < 2; i++) {
			x[i] = clientTanks[i][TankPos].getX();
			y[i] = clientTanks[i][TankPos].getY();
			r[i] = clientTanks[i][TankPos].getRotation();
			t[i] = timePassed[i][TankPos] - timePassed[0][TankPos];
		}

		// Takes care of "average" problem when angle jumps from 2pi to 0
		if (Math.abs(r[1] - r[0]) > 490) {
			r[1] = 0;
			r[0] = 0;
		}

		ax[TankPos] = (x[1] - x[0]) / ((t[1] - t[0]) * 7 / 6);
		bx[TankPos] = x[0];
		ay[TankPos] = (y[1] - y[0]) / ((t[1] - t[0]) * 7 / 6);
		by[TankPos] = y[0];
		ar[TankPos] = (r[1] - r[0]) / ((t[1] - t[0]) * 9 / 6);
		br[TankPos] = r[0];
		
	}*/

	/*
	 * Predict the next position of a tank based on its state values.
	 * 
	 * @param tank the tank
	 
	private void predict(Tank tank) {
		/*
		double timeSince = Calendar.getInstance().getTimeInMillis()
				- timePassed[0][TankPos];
		double px = (ax[TankPos] * timeSince + bx[TankPos]);
		double py = (ay[TankPos] * timeSince + by[TankPos]);
		double pr = (ar[TankPos] * timeSince + br[TankPos]);

		
		tank.positionTank((int) px, (int) py, (int) pr);
		
	}*/

}