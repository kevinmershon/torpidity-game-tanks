package org.torpidity.tank.networking;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.torpidity.tank.data.MySQL;
import org.torpidity.tank.data.UserHandler;
import org.torpidity.tank.gui.client.ChatPanel;
import org.torpidity.tank.gui.client.ErrorDialog;
import org.torpidity.tank.gui.client.TankClientFrame;

/**
 * ClientChatHandler handles the encoding and decoding of messages on the client
 * side.
 * 
 * @author Kevin Mershon
 */
public class ClientChatHandler implements Runnable {
	private ChatPanel chatPanel;
	
	private static final String sep = ""+((char)202);

	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	private int uid;
	
	private boolean isRunning = true;
		
	/**
	 * Create a new ClientChatHandler
	 * 
	 * @param uid the uid
	 */
	public ClientChatHandler(int uid, ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
		MySQL.connect();
		this.uid = uid;
		try {
			socket = new Socket("69.181.238.214", 7281);
			socket.setSoTimeout(1);

			writer = new PrintWriter(new OutputStreamWriter(
					new BufferedOutputStream(socket.getOutputStream())), true);
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			
			new Thread(this).start();
		} catch (IOException e) {
			System.out.println("Could not connect to chat server.");
		}
	}

	/**
	 * Parse data input into messages
	 * 
	 * @param input input to parse
	 */
	private String parse(String input) throws ArrayIndexOutOfBoundsException {
		String[] split = input.split(sep);
		//long timestamp = new Long(split[0]);
		int uid = new Integer(split[1]);
		String message = new String(split[2]);
		
		if (message.equals("WELCOME")) {
			write("CONNECT");
			return null;
		} else if (uid == 0) {
			if (message.equals("BYE")) {
				System.exit(0);
				return null;
			} else if (message.equals("FULL")) {
				TankClientFrame.doError(new ErrorDialog("Disconnected", "Server is full!", true));
				return null;
			} else if (message.equals("KICK")) {
				TankClientFrame.doError(new ErrorDialog("Disconnected", "You were kicked from the server!", true));
				return null;
			}
			return message;
		} else {
			if (uid != this.uid)
				return UserHandler.getUsername(uid) + " says, \"" + message
						+ "\"";
			else
				return "You say, \"" + message + "\"";
		}
	}
	
	public void run() {
		while (isRunning) {
			String str;
			while ((str = read()) != null) {
				chatPanel.log(str);
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
	
	/**
	 * Read the InputStream for messages
	 * 
	 * @return messages
	 */
	public String read() {
		String input = null;
		try {
			input = parse(reader.readLine());
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			TankClientFrame.doError(new ErrorDialog("Server Disconnect", "Disconnected from server!", true));
			isRunning = false;
		} catch (SocketTimeoutException e) {
			// do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	/**
	 * Format a message for the OutputStream
	 * 
	 * @param str the message
	 */
	public void write(String str) {
		writer.println(System.currentTimeMillis() + sep + uid + sep + str);
	}
}
