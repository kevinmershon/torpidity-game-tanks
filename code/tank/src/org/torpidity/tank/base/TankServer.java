package org.torpidity.tank.base;

import java.util.ArrayList;
import java.util.Arrays;

import org.torpidity.tank.data.MySQL;
import org.torpidity.tank.gui.server.TankServerFrame;
import org.torpidity.tank.networking.ChatClient;
import org.torpidity.tank.networking.ServerChatHandler;
import org.torpidity.tank.networking.ServerGameHandler;

/**
 * TankServer contains the main method for the server side of the game. It is
 * responsible for setting up the database connections, the chat server, the
 * game server, and the server GUI.
 * 
 * @author Kevin Mershon
 */
public class TankServer {
	private static TankServerFrame frame;
	public static int MAX_USERS = 10;

	public static void main(String[] args) {
		System.out.println("Connecting to database.");
		MySQL.connect();
		System.out.println("Loading GUI.");
		frame = new TankServerFrame();
		System.out.println("Initializing network protocols.");
		ServerGameHandler.getInstance();
		ServerChatHandler.getInstance();
		System.out.println("Server online.");
		
		frame.render();
	}

	/**
	 * Add some upload/download data to the bandwidth meter
	 * 
	 * @param upload
	 *            uploaded bytes
	 * @param download
	 *            downloaded bytes
	 */
	public static void addToBandwidth(int upload, int download) {
		frame.addToBandwidth(upload, download);
	}

	/**
	 * Send some string to the log
	 * 
	 * @param str
	 *            the string
	 */
	public static void log(String str) {
		frame.log(str);
	}

	/**
	 * This method is called by the buttons pressed in the GUI. When the server
	 * starts up, this method is also called. The purpose of this is to enable
	 * the network connections so that clients can connect.
	 * 
	 * @param status
	 *            whether the server is accepting connections/data or not.
	 */
	public static void setStatus(boolean status) {
		ServerChatHandler.setStatus(status);
		ServerGameHandler.setStatus(status);
	}

	/**
	 * This method is basically just here to ensure that the database doesn't
	 * get screwed up by killing the server during a read/write. It is called by
	 * the WindowListener (windowClosing() method) embedded in TankServerFrame.
	 * We might want to add some fun little progress bars later?
	 */
	public static void shutDown() {
		System.out.println("Disconnecting players.");
		setStatus(false);
		System.out.println("Closing database connection.");
		MySQL.close();
		System.out.println("Shutting down.");
		System.exit(0);
	}

	/**
	 * Update the list of users in the server GUI
	 * 
	 * @param clients
	 *            the connected clients
	 */
	public static void updateClients(ArrayList<ChatClient> clients) {
		/*
		 * Remove client which are still temporarily null, if any
		 */
		String[] clientArray = new String[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			ChatClient client = clients.get(i);
			clientArray[i] = ""+client.getUsername();
		}

		Arrays.sort(clientArray);
		frame.setUsersList(clientArray);
	}

}