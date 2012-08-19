package org.torpidity.tank.gui.client;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * ErrorDialog provides simple utility to non-GUI threads for throwing an error
 * dialog for some reason.
 * 
 * @author Kevin Mershon
 */
public class ErrorDialog implements Runnable {
	private Shell shell;
	
	private String title;
	private String message;
	private boolean shutDown;

	/**
	 * Create a new ErrorDialog
	 * 
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param shutDown
	 *            shutdown afterward?
	 */
	public ErrorDialog(String title, String message, boolean shutDown) {
		this.title = title;
		this.message = message;
		this.shutDown = shutDown;
	}

	/**
	 * Display the error dialog
	 */
	public void run() {
		MessageBox error = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
		error.setMessage(message);
		error.setText(title);
		error.open();
		if (shutDown)
			System.exit(1);
	}
	
	/**
	 * Set the Shell for displaying
	 * 
	 * @param shell the shell
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}
}
