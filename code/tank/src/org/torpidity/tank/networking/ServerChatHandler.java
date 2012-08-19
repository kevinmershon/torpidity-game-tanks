package org.torpidity.tank.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.torpidity.tank.base.TankServer;
import org.torpidity.tank.chat.ChatFilter;
import org.torpidity.tank.chat.ChatMessage;
import org.torpidity.tank.data.MySQL;

/**
 * The ServerChatHandler listens for messages from connected clients and
 * appropriately relays them to other clients.
 * 
 * ServerChatHandler is a singleton class, since it cannot be made static due to
 * the fact that Threads cannot be static.
 * 
 * @author Kevin Mershon
 */
public class ServerChatHandler implements Runnable {
	private static ServerChatHandler instance;

	private static ServerSocket serverSocket;
	private static ArrayList<ChatClient> clients;
	private static HashMap<Integer, ChatClient> lookup;
	private static LinkedBlockingQueue<ChatMessage> messages;

	private static boolean isRunning = true;

	/**
	 * Instantiate ServerChatHandler and start up the socket thread
	 */
	private ServerChatHandler() {
		try {
			serverSocket = new ServerSocket(7281);
			serverSocket.setSoTimeout(1);
			clients = new ArrayList<ChatClient>(TankServer.MAX_USERS);
			lookup = new HashMap<Integer, ChatClient>(TankServer.MAX_USERS);
			messages = new LinkedBlockingQueue<ChatMessage>();
			MySQL.connect();
			ChatFilter.getInstance();

			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("Could not start chat server.");
			TankServer.shutDown();
		}
	}

	/**
	 * Get the lookup hashmap
	 * 
	 * @return the the hashmap
	 */
	public static HashMap<Integer, ChatClient> getHashmap() {
		return lookup;
	}
	
	/**
	 * Get the instance
	 * 
	 * @return the instance
	 */
	public static ServerChatHandler getInstance() {
		if (instance == null)
			instance = new ServerChatHandler();
		return instance;
	}

	/**
	 * Kick a user from the server
	 * 
	 * @param client
	 *            the client
	 * @param kickStr
	 *            the kick string to use ("BYE" or "KICK")
	 */
	public static void kickUser(ChatClient client, String kickStr) {
		client.write(new ChatMessage(null, kickStr));
		clients.remove(client);
		lookup.remove(client.getUID());
	}

	/**
	 * Loop through all connected clients for new data and handle accordingly.
	 */
	private static void readFromClients() {
		ChatClient client;
		String data = null;
		ChatMessage msg;
		for (int i = 0; i < clients.size(); i++) {
			client = clients.get(i);
			try {
				if ((data = client.read()) == null)
					continue;
				msg = ChatFilter.parse(client, data);
				if (msg != null)
					messages.offer(msg);
			} catch (SocketException e) {
				// user disconnected
				kickUser(client, "BYE");
				messages.offer(new ChatMessage(null, client.getUsername()
						+ " has left the server."));
			}
		}
		// This line must come after Chatfilter.parse()
		TankServer.updateClients(clients);
	}

	/**
	 * Wait for connecting sockets
	 */
	public void run() {
		while (isRunning) {
			readFromClients();
			writeToClients();

			try {
				Thread.sleep(50);
				// Test for new connections
				Socket connecting = serverSocket.accept();
				if (clients.size() < TankServer.MAX_USERS) {
					ChatClient client = new ChatClient(connecting);
					clients.add(client);
					writeToClient(client, "WELCOME");
				} else
					kickUser(new ChatClient(connecting), "FULL");
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
	public static void setStatus(boolean status) {
		isRunning = status;
		if (isRunning) {
			new Thread(instance).start();
		} else {
			ChatClient c;
			while (clients.size() > 0) {
				c = clients.get(0);
				kickUser(c, "KICK");
			}
			TankServer.updateClients(clients);
		}
	}

	/**
	 * Write a server message to a client
	 */
	public static void writeToClient(ChatClient client, String str) {
		ChatMessage msg = new ChatMessage(null, str);
		client.write(msg);
	}

	/**
	 * Write all the queued-up messages to the clients
	 */
	private static void writeToClients() {
		ChatClient client;
		ChatMessage msg;
		while ((msg = messages.poll()) != null) {
			// Write to clients first
			for (int i = 0; i < clients.size(); i++) {
				client = clients.get(i);
				client.write(msg);
			}
			// Add to server log
			if (msg.getClient() == null || msg.getClient().getUID() == 0)
				TankServer.log(msg.getMessage());
			else
				TankServer.log(msg.getClient().getUsername() + " says, \""
						+ msg.getMessage() + "\"");
		}

	}

}