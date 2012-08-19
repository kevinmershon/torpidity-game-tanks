package org.torpidity.tank.chat;

import org.torpidity.tank.networking.ChatClient;


/**
 * ChatMessage contains all the data about a specific message to be sent through
 * the chatserver: a reference to the Chatclient, a timestamp, and the message
 * itself.
 * 
 * A null client indicates a message from the server. 
 * 
 * @author Kevin Mershon
 */
public class ChatMessage {
	private ChatClient client;
	private long timestamp;
	private String message;
	
	private static final String sep = ""+((char)202);

	/**
	 * Create a new ChatMessage stamped with the current time
	 * 
	 * @param client
	 *            the client
	 * @param message
	 *            the message
	 */
	public ChatMessage(ChatClient client, String message) {
		this(client, System.currentTimeMillis(), message);
	}

	/**
	 * Create a new ChatMessage
	 * 
	 * @param socket
	 *            the client
	 * @param timestamp
	 *            the timestamp
	 * @param message
	 *            the message
	 */
	public ChatMessage(ChatClient client, long timestamp, String message) {
		this.client = client;
		this.timestamp = timestamp;
		this.message = message;
	}

	/**
	 * Get the client
	 * 
	 * @return the client
	 */
	public ChatClient getClient() {
		return client;
	}

	/**
	 * Get the message
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get the timestamp
	 * 
	 * @return the timestamp
	 */
	public long getTimeStamp() {
		return timestamp;
	}

	/**
	 * Overridden toString method
	 */
	public String toString() {
		int uid = (client != null ? client.getUID() : 0);
		return timestamp + sep + uid + sep + message;
	}
}
