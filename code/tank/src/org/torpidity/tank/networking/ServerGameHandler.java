package org.torpidity.tank.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.torpidity.tank.base.TankServer;
import org.torpidity.tank.data.MySQL;
import org.torpidity.tank.data.Player;
import org.torpidity.tank.object.Tank;

/**
 * ServerGameHandler processes all game activity, server-side
 */
public class ServerGameHandler implements Runnable {
	private static ServerGameHandler instance;

	private static ServerSocket serverSocket;
	private static ArrayList<GameClient> clients;
	private static HashMap<Integer, GameClient> lookup;

	private static boolean isRunning = true;

	/**
	 * Create a new ServerGameHandler
	 */
	private ServerGameHandler() {
		try {
			serverSocket = new ServerSocket(7282);
			serverSocket.setSoTimeout(1);
			serverSocket.setPerformancePreferences(0, 2, 1);
			clients = new ArrayList<GameClient>(TankServer.MAX_USERS);
			lookup = new HashMap<Integer, GameClient>(TankServer.MAX_USERS);
			MySQL.connect();

			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("Could not start game server.");
			TankServer.shutDown();
		}
	}
	
	/**
	 * Get a specific client based on its uid
	 * 
	 * @param uid the uid
	 * @return the client
	 */
	public static GameClient getClient(int uid) {
		return lookup.get(uid);
	}

	/**
	 * Get the instance
	 * 
	 * @return the instance
	 */
	public static ServerGameHandler getInstance() {
		if (instance == null)
			instance = new ServerGameHandler();
		return instance;
	}

	/**
	 * Kick a user from the server
	 * 
	 * @param client
	 *            the client
	 */
	private static void kickUser(GameClient client) {
		clients.remove(client);
		lookup.remove(client.getPlayer().getUID());
	}

	/**
	 * Loop through all connected clients for new data and handle accordingly.
	 */
	private static void readFromClients() {
		GameClient client;
		int data;
		for (int i = 0; i < clients.size(); i++) {
			client = clients.get(i);
			try {
				if ((data = client.read()) == -1)
					continue;
			
				if (lookup.get(client.getPlayer().getUID()) == null)
					lookup.put(client.getPlayer().getUID(), client);
				client.getPlayer().decode32(data);
				TankServer.addToBandwidth(0, 4);
			} catch (SocketException e) {
				// user disconnected
				client.getPlayer().setDisconnecting(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Update data for other clients, tick, and write updated data to clients
	 */
	public void run() {
		while (isRunning) {
			try {
				Thread.sleep(14);
				readFromClients();
				tick();
				writeToClients();
				
				// Test for new connections
				Socket connecting = serverSocket.accept();
				if (clients.size() < TankServer.MAX_USERS) {
					GameClient client = new GameClient(connecting);
					clients.add(client);
				}
				
			} catch (SocketTimeoutException e) {
				// no incoming connections
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Enable or disable the chat server
	 * 
	 * @param status
	 *            enabled?
	 */
	public static void setStatus(boolean enabled) {
		isRunning = enabled;
		if (isRunning) {
			new Thread(instance).start();
		}
		if (!isRunning) {
			clients.clear();
			lookup.clear();
		}
	}

	/**
	 * Advance the game state forward 1 tick.
	 * 
	 * XXX All game activity such as movement and collisions are processed here.
	 */
	private static void tick() {
		for (int i = 0; i < clients.size(); i++) {
			Player p1 = clients.get(i).getPlayer();
			Tank p1Tank = p1.getTank();
			if (p1.isAlive()) {
				p1Tank.movement();
				for (int j=0; j<clients.size(); j++) {
					Player p2 = clients.get(j).getPlayer();
					Tank p2Tank = p2.getTank();
					if (!p1.equals(p2) && p2.isAlive()) {
						if (p1Tank.hasCollidedWith(p2Tank)) {
							p1Tank.bounce();
							p2Tank.bounce();
						}
					}
				}
			} else if (p1.isSpawning()) {
				p1.spawn();
			}
		}
	}

	/**
	 * Write out game data to all clients
	 */
	private static void writeToClients() {
		GameClient client;
		long data;
		for (int i = 0; i < clients.size(); i++) {
			client = clients.get(i);
			data = client.getPlayer().encode64();
			for (int j = 0; j < clients.size(); j++) {
				clients.get(j).write(data);
				TankServer.addToBandwidth(8, 0);
			}

			if (client.getPlayer().isDisconnecting())
				kickUser(client);
		}
	}

}