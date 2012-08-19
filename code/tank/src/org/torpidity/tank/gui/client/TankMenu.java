package org.torpidity.tank.gui.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * TankMenu creates the menubar and submenus.
 * 
 * @author Kevin Mershon
 */
public class TankMenu {
	private Menu bar;
	private Menu inventory;
	private Menu weapons;
	
	/**
	 * Create a new TankMenu
	 * 
	 * @param shell the Shell
	 */
	public TankMenu(Shell shell) {
		bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);

		// Inventory
		MenuItem invItem = new MenuItem(bar, SWT.CASCADE);
		invItem.setText("&Inventory");
		inventory = new Menu(shell, SWT.DROP_DOWN);
		invItem.setMenu(inventory);
		
		// Weapons
		MenuItem weapItem = new MenuItem(bar, SWT.CASCADE);
		weapItem.setText("&Weapons");
		weapons = new Menu(shell, SWT.DROP_DOWN);
		weapItem.setMenu(weapons);
		
		doInventory();
		doWeapons();
	}
	
	/**
	 * Set up the inventory menu
	 */
	private void doInventory() {
		// Booster
		MenuItem invBooster = new MenuItem(inventory, SWT.PUSH);
		invBooster.setText("&Booster (Unlimited)");
		// Shield
		MenuItem invShield = new MenuItem(inventory, SWT.PUSH);
		invShield.setText("Weak &Shield (1)");
	}
	
	/**
	 * Set up the weapons menu
	 */
	private void doWeapons() {
		// Cannonball
		MenuItem weapCannonBall = new MenuItem(weapons, SWT.PUSH);
		weapCannonBall.setText("&Cannon Ball");
		// Landmine
		MenuItem weapLandMine = new MenuItem(weapons, SWT.PUSH);
		weapLandMine.setText("Land &Mine");
	}
}
