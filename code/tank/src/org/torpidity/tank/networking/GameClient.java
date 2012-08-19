package org.torpidity.tank.networking;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.torpidity.tank.data.Player;

/**
 * Similar to ChatClient, this class contains all the data of a connected
 * client.
 * 
 * @author Kevin Mershon
 */
public class GameClient {
	private Socket socket;
	private BufferedInputStream bIn;
	private DataInputStream inStream;
	private DataOutputStream outStream;
	
	private Player player;
	private long timeStamp1, timeStamp2;
	
	/**
	 * Create a new GameClient based on its connecting socket and the user id
	 */
	public GameClient(Socket socket) {
		this.socket = socket;
		
		try {
			socket.setPerformancePreferences(0, 2, 1);
			inStream = new DataInputStream(socket.getInputStream());
			bIn = new BufferedInputStream(inStream);
			outStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * Get the Player
	 * 
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Get the socket
	 * 
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Read in some data from the client
	 * 
	 * @return the data
	 */
	public int read() throws SocketException, IOException {
		int data = -1;
		
		while (bIn.available() >= 4) {
			timeStamp2 = System.currentTimeMillis();
			data = inStream.readInt();				
			if (player == null) {
				int uid = Player.getUIDFromClient(data);
				player = new Player(uid);
			}
		}
		timeStamp1 = System.currentTimeMillis();
		
		// Test for disconnection (2 seconds without data)
		if (timeStamp1 - timeStamp2 >= 2000)
			throw new SocketException();
		return data;
	}
	
	/**
	 * Write some data to the client
	 * 
	 * @param data
	 *            the data
	 */
	public void write(long data) {
		try {
			outStream.writeLong(data);
		} catch (SocketException e) {
			// user disconnected (do nothing)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
