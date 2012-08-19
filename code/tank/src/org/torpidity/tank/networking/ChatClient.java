package org.torpidity.tank.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.torpidity.tank.base.TankServer;
import org.torpidity.tank.chat.ChatMessage;

/**
 * ChatClient contains all the data of a connected client
 * 
 * @author Kevin Mershon
 */
public class ChatClient {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	// Player data
	private int uid;
	private String username;

	/**
	 * Create a new ChatClient
	 * 
	 * @param socket a socket
	 */
	public ChatClient(Socket socket) {
		this.socket = socket;
		try {
			socket.setSoTimeout(1);
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket
					.getOutputStream()), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * Get the UID
	 * 
	 * @return the UID
	 */
	public int getUID() {
		return uid;
	}
	
	/**
	 * Get the username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Set the uid
	 * 
	 * @param uid the uid
	 */
	public void setUID(int uid) {
		this.uid = uid;
	}
	
	/**
	 * Set the username
	 * 
	 * @param username the username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Read this client for messages and parse the data
	 *  
	 * @return the messsage
	 */
	public String read() throws SocketException {
		String input = null;
		try {
			input = reader.readLine();
			TankServer.addToBandwidth(0, input.getBytes().length);
		} catch (SocketTimeoutException e) {
			// do nothing
		} catch (SocketException e) {
			// user disconnected
			throw e;
		} catch (IOException e) {
			// doesn't happen
			e.printStackTrace();
		}
		return input;
	}
	
	/**
	 * Write to this client
	 */
	public void write(ChatMessage msg) {
		TankServer.addToBandwidth(msg.toString().getBytes().length, 0);
		writer.println(msg);
	}
	
	/**
	 * Get the username
	 */
	public String toString() {
		return username;
	}
}
