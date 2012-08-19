package org.torpidity.tank.chat;

import java.util.HashMap;

import org.torpidity.tank.data.Player;
import org.torpidity.tank.data.UserHandler;
import org.torpidity.tank.networking.ChatClient;
import org.torpidity.tank.networking.GameClient;
import org.torpidity.tank.networking.ServerChatHandler;
import org.torpidity.tank.networking.ServerGameHandler;

/**
 * ChatFilter manages emote parsing and nickname changes
 * 
 * @author Kevin Mershon
 */
public class ChatFilter {
	private static ChatFilter instance;
	
	private static HashMap<String, Integer> commands;
	private static HashMap<String, String> emotes;
	
	private static final String[] helpMenu = new String[] {
		" Help Menu -------------------",
		" --------- Controls ----------",
		" W, A, S, and D to move (or the arrow keys). Spacebar to fire.",
		" You can hold down the Shift key while moving to boost your speed.",
		" If you die, press the Home key to respawn.",
		" --------- Commands ----------",
		" /help - display this message",
		" /nick <username> - change nickname | set username to \"off\" to revert",
		" /me <text> - perform a \"me-mote\" (ie. /me eats a cookie)",
		" /quit - quit the game",
		" /whois <username> - get information about a player",
		"---------- Emotes ------------",
		" Type /word to perform a standard emote. (ie. /laugh)",
		" bitch, burp, crap, crazy, fart, highfive, joke, laugh, lol, moo,",
		" poke, sleep, snort, zing"
	};
	
	private static final String sep = ""+((char)202);

	private ChatFilter() {
		commands = new HashMap<String, Integer>();
		emotes = new HashMap<String, String>();
		
		commands.put("/help", 0);
		commands.put("/nick", 1);
		commands.put("/quit", 2);
		commands.put("/whois", 3);
		commands.put("/kill", 4);
		commands.put("/kick", 5);

		emotes.put("/bitch", " bitches and moans.");
		emotes.put("/burp", " lets out a burp. \"BRUUUAAAAAAAAAAAUUUUPPP!!\"");
		emotes.put("/crap", " craps out a one-pounder. Whew!");
		emotes.put("/crazy", " is crazy.");
		emotes.put("/fart", " rips a loud fart.");
		emotes.put("/highfive", " gives everyone a high-five!");
		emotes.put("/joke", " tells a joke.");
		emotes.put("/laugh", " makes a mocking gesture regarding you, plankton and the Vietnam Conflict.");
		emotes.put("/lol", " laughs aloud.");
		emotes.put("/moo", " moos.");
		emotes.put("/poke", " pokes you in the side.");
		emotes.put("/sleep", " falls asleep. \"Zzzzzzzz.....\"");
		emotes.put("/snort", " snorts obnoxiously.");
		emotes.put("/zing", " zings you.");
		
		// easter eggs - don't add to list
		emotes.put("/getmain", " throws a fit and shouts, \"I am SOO gonna get my main!!\"");
		emotes.put("/movezig", " moves zig.");
		emotes.put("/spin", " spins you right round, baby, right round, like a record baby, right round, right round...");
		emotes.put("/surrender", " is French.");
	}
	
	public static ChatFilter getInstance() {
		if (instance == null)
			instance = new ChatFilter();
		return instance;
	}

	/**
	 * Perform some command. Most commands are admin-only, and will bounce back
	 * a "You can't do that!" error to the user if they're not an admin.
	 * 
	 * @param client
	 *            the client
	 * @param uid
	 *            the uid
	 * @param param
	 *            the parameter
	 */
	private static String doCommand(int command, ChatClient client, String[] params) {
		int uid = client.getUID();

		switch (command) {
		case 0: {
			// Show help menu
			for (int i=0; i<helpMenu.length; i++) {
				ServerChatHandler.writeToClient(client, helpMenu[i]);
			}
			break;
		}
		case 1: {
			// Change nickname
			try {
				String oldNickname = client.getUsername();
				String nickname = UserHandler.setNickname(uid, params[1]);
				client.setUsername(nickname);
				if (params[1].equals("off"))
					return "SERVER : " + oldNickname + " is once again known as " + nickname;
				else
					return "SERVER : " + oldNickname + " is now known as " + nickname;
			} catch (ArrayIndexOutOfBoundsException e) {
				ServerChatHandler.writeToClient(client, "NICK : Parameter missing!");
				break;
			}
		}
		case 2: {
			// Leave the server
			ServerChatHandler.kickUser(client, "BYE");
			ServerGameHandler.getClient(client.getUID()).getPlayer().setDisconnecting(true);
			return "SERVER : " + client.getUsername() + " has left the server.";
		}
		case 3: {
			// Do a player 'whois'
			try {
				int playerID = UserHandler.getUID(params[1]);
				if (playerID == -1) {
					ServerChatHandler.writeToClient(client, "WHOIS : Player not found");
					break;
				}
				ChatClient chatClient = ServerChatHandler.getHashmap().get(playerID);
				if (chatClient != null) {
					String whoisData = "WHOIS : " + playerID + " - " + chatClient.getUsername();
					ServerChatHandler.writeToClient(client, whoisData);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				ServerChatHandler.writeToClient(client, "WHOIS : Parameter missing!");
			}
			break;
		}
		case 4: {
			// Force-kill a player
			if (uid != 1) {
				ServerChatHandler.writeToClient(client, "KILL : You can't do that!");
				break;
			}
			try {
				int playerID = new Integer(params[1]);
				ChatClient chatClient = ServerChatHandler.getHashmap().get(playerID);
				if (chatClient != null) {
					GameClient gameClient = ServerGameHandler.getClient(playerID);
					Player player = gameClient.getPlayer();
					player.changeHP(-player.getHP());
					return "SERVER : " + chatClient.getUsername() + " was killed by an admin.";
				}
			} catch (NullPointerException e) {
				ServerChatHandler.writeToClient(client, "KILL : Player not found");
			} catch (NumberFormatException e) {
				ServerChatHandler.writeToClient(client, "KILL : Requires numeric parameter!");
			} catch (ArrayIndexOutOfBoundsException e) {
				ServerChatHandler.writeToClient(client, "KILL : Parameter missing!");
			}
			break;
		}
		case 5: {
			// Kick a player
			if (uid != 1) {
				ServerChatHandler.writeToClient(client, "KICK : You can't do that!");
				break;
			}
			try {
				int playerID = new Integer(params[1]);
				ChatClient chatClient = ServerChatHandler.getHashmap().get(playerID);
				if (chatClient != null) {
					ServerChatHandler.kickUser(chatClient, "KICK");
					return "SERVER : " + chatClient.getUsername() + " was kicked from the server.";
				}
			} catch (NullPointerException e) {
				ServerChatHandler.writeToClient(client, "KICK : Player not found");
			} catch (NumberFormatException e) {
				ServerChatHandler.writeToClient(client, "KICK : Requires numeric paramter!");
			} catch (ArrayIndexOutOfBoundsException e) {
				ServerChatHandler.writeToClient(client, "KICK : Parameter missing!");
			}
			break;
		}
		}
		return null;
	}

	/**
	 * Parse some text data into a ChatMessge
	 * 
	 * Returns null if no text output should be displayed
	 * 
	 * @param client
	 *            the source client
	 * @param str
	 *            the data
	 * @return a ChatMessage
	 */
	public static ChatMessage parse(ChatClient client, String str) {
		String[] split = str.split(sep);
		long timestamp = new Long(split[0]);
		int uid = new Integer(split[1]);
		if (split.length == 2)
			return null;
		String msg = split[2];
		
		// Set the UID on the first received message (login)
		if (client.getUID() == 0 && msg.equals("CONNECT")) {
			client.setUID(uid);
			client.setUsername(UserHandler.getUsername(uid));
			ServerChatHandler.getHashmap().put(uid, client);
			return new ChatMessage(null, "SERVER : " + client.getUsername() + " has connected.");
		}

		/*
		 * Perform a command, pre-defined emote, or /me emote
		 */
		if (msg.charAt(0) == '/') {
			String[] params;
			String command = (params = msg.split(" "))[0];

			String emoteStr = null;
			if (commands.containsKey(command))
				emoteStr = doCommand(commands.get(command), client, params);
			else if (emotes.containsKey(command))
				emoteStr = client.getUsername() + emotes.get(command);
			else if (command.equals("/me") && params.length > 1)
				emoteStr = client.getUsername() + msg.substring(3);
			else {
				// invalid command
				ServerChatHandler.writeToClient(client, "SERVER : Invalid command!");
			}
			
			if (emoteStr == null) {
				return null;
			}

			return new ChatMessage(null, timestamp, emoteStr);
		}

		/*
		 * Normal message
		 */
		return new ChatMessage(client, timestamp, msg);
	}
}
