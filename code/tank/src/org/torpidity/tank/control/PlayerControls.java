package org.torpidity.tank.control;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.torpidity.tank.data.Player;

/**
 * This class handles all the Player interactions.
 * 
 * @author Kevin Mershon
 */
public class PlayerControls {
	private KeyAdapter key;
	private MouseAdapter mouse;

	private Composite comp;
	private Player player;

	private class _keyboard extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.keyCode == SWT.ARROW_UP || e.character == 'w'
					|| e.character == 'W') {
				player.stateChangeMove(1);
			}
			if (e.keyCode == SWT.ARROW_DOWN || e.character == 's'
					|| e.character == 'S') {
				player.stateChangeMove(-1);
			}

			if (e.keyCode == SWT.ARROW_LEFT || e.character == 'a'
					|| e.character == 'A') {
				player.stateChangeTurn(1);
			}
			if (e.keyCode == SWT.ARROW_RIGHT || e.character == 'd'
					|| e.character == 'D') {
				player.stateChangeTurn(-1);
			}
			if (e.keyCode == SWT.SHIFT) {
				player.stateChangeBooster(true);
			}
			if (e.character == ' ') {
				player.stateChangeFire(true);
			}
			if (e.keyCode == SWT.HOME) {
				player.stateChangeSpawn(true);
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.keyCode == SWT.ARROW_UP || e.character == 'w'
					|| e.keyCode == SWT.ARROW_DOWN || e.character == 's'
					|| e.character == 'W' || e.character == 'S') {
				player.stateChangeMove(0);
			}
			if (e.keyCode == SWT.ARROW_LEFT || e.character == 'a'
					|| e.keyCode == SWT.ARROW_RIGHT || e.character == 'd'
					|| e.character == 'A' || e.character == 'D') {
				player.stateChangeTurn(0);
			}
			if (e.keyCode == SWT.SHIFT) {
				player.stateChangeBooster(false);
			}
			if (e.character == ' ') {
				player.stateChangeFire(false);
			}
			if (e.keyCode == SWT.HOME) {
				player.stateChangeSpawn(false);
			}
		}
	}

	private class _mouse extends MouseAdapter {
		public void mouseDown(MouseEvent e) {
			comp.setFocus();
		}
	}
	
	/**
	 * Create a new PlayerControls instance with both KeyAdapter and
	 * MouseAdapter components.
	 * 
	 * @param view
	 *            the ViewPanel
	 * @param player
	 *            the Player
	 */
	public PlayerControls(Composite comp, Player player) {
		this.comp = comp;
		this.key = new _keyboard();
		this.mouse = new _mouse();
		this.player = player;
	}

	/**
	 * Get the KeyAdapter component
	 * 
	 * @return the KeyAdapter component
	 */
	public KeyAdapter getKeyAdapter() {
		return key;
	}

	/**
	 * Get the MouseAdapter component
	 * 
	 * @return the MouseAdapter component
	 */
	public MouseAdapter getMouseAdapter() {
		return mouse;
	}
}
