package org.torpidity.tank.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.torpidity.tank.data.Player;
import org.torpidity.tank.networking.ClientGameHandler;

/**
 * The ViewPanel acts as the rendering screen. When instantiated the player's
 * data is loaded and controls are initialized.
 * 
 * @author Kevin Mershon
 */
@SuppressWarnings("serial")
public class ViewPanel extends JPanel implements Runnable {
	private ClientGameHandler gameServer;
	private ArrayList<Player> players;
	
	private Player localPlayer;

	private final Color background = new Color(118, 84, 50);
	public static final int WIDTH = 800;
	public static final int HEIGHT = 505;

	/**
	 * Create a new ViewPanel with the given player UID
	 */
	public ViewPanel(int uid) {
		super(new BorderLayout());
		localPlayer = new Player(uid);
		gameServer = new ClientGameHandler(localPlayer);
				
		// Start rendering
		new Thread(this).start();
	}

	/**
	 * Draw the player's healthbar
	 * 
	 * @param g2 the graphics context
	 */
	private void drawPlayerHealthBar(Graphics2D g2) {
		Player player = gameServer.getLocalPlayer();
		if (player == null)
			return;
		
		int hp_I = player.getHP();
		int maxHP_I = player.getMaxHP();
		double hp_D = (double) hp_I;
		double maxHP_D = (double) maxHP_I;

		g2.setColor(Color.white);
		g2.drawString("HP: (" + hp_I + " / " + maxHP_I + ")", 680, 35);
		g2.setColor(Color.black);
		g2.fill(new Rectangle2D.Double(670, 3, 116, 16));
		if (hp_D >= maxHP_D / 2)
			g2.setColor(new Color((int) (225 * ((maxHP_D - hp_D) / (maxHP_D / 2))),
					225, 0));
		if (hp_D < maxHP_D / 2 && hp_D != 0)
			g2.setColor(new Color(225, (int) (225 * (hp_D / (maxHP_D / 2))), 0));
		g2.fill(new Rectangle2D.Double(671, 4, (114 * (hp_D / maxHP_D)), 14));
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}
	
	/**
	 * Draw a frame
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setBackground(background);
		g2.clearRect(0, 0, WIDTH, HEIGHT);
		
		if (players != null) {		
			for (int i=0; i<players.size(); i++) {
				Player player = players.get(i);
				if (player.isAlive()) {
					player.getTank().draw(g2);
					if (!player.equals(localPlayer))
						player.getTank().drawHealthBar(g2);
				}
			}
		}
		drawPlayerHealthBar(g2);
	}
	
	/**
	 * Get the most recent client data from the game server and then draw a new
	 * frame.
	 * 
	 * Sleeping for 15 ms works out to approximately 66fps.
	 */
	public void run() {
		while (true) {
			players = gameServer.getPlayers();
			gameServer.write(localPlayer.encode32());
			repaint();
			
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}
	
}