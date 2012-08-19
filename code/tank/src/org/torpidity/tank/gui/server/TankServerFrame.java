package org.torpidity.tank.gui.server;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.torpidity.tank.base.TankServer;

/**
 * TankServerFrame displays the list of clients, the chat server activity, and
 * provides the host with the ability to start and stop the server, and adjust
 * the maximum user threshhold as needed.
 * 
 * @author Kevin Mershon
 */
public class TankServerFrame implements Runnable {
	private Display display;
	private Shell shell;
	private Button startButton;
	private Button stopButton;
	private Text log;
	private List playerList;
	private Label bandwidth;
	private Label statusBar;
	
	private int bytesWritten = 0;
	private int bytesRead = 0;
	
	// Values for setting via other threads
	private String[] players = null;
	private LinkedBlockingQueue<String> messages;
	private boolean status = true;

	/**
	 * Instantiate the UI
	 */
	public TankServerFrame() {
		messages = new LinkedBlockingQueue<String>();
		
		int width = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getWidth();
		int height = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getHeight();

		// Prepare the shell (window)
		display = new Display();
		shell = new Shell(display, SWT.MIN);
		shell.setText("Torpidity Tank Game - Server");
		shell.setBounds(width - 700, height - 335, 700, 300);
		
		// Layout setup
		int margin = 3;
		FormLayout layout = new FormLayout();
		layout.marginLeft = margin;
		layout.marginRight = margin;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		shell.setLayout(layout);
		
		// Start button
		startButton = new Button(shell, SWT.PUSH);
		startButton.setText("Start Server");
		startButton.setEnabled(false);
		FormData startButtonData = new FormData();
		startButtonData.left = new FormAttachment(0);
		startButtonData.top = new FormAttachment(0);
		startButton.setLayoutData(startButtonData);
		
		// Stop button
		stopButton = new Button(shell, SWT.PUSH);
		stopButton.setText("Stop Server");
		FormData stopButtonData = new FormData();
		stopButtonData.left = new FormAttachment(startButton, margin);
		stopButtonData.top = new FormAttachment(0);
		stopButton.setLayoutData(stopButtonData);
		
		// Chat Log
		log = new Text(shell, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		log.setBackground(new Color(display, 255, 255, 255));
		FormData logData = new FormData();
		logData.left = new FormAttachment(stopButton, margin);
		logData.top = new FormAttachment(0);
		logData.right = new FormAttachment(100);
		logData.bottom = new FormAttachment(92);
		log.setLayoutData(logData);
		
		// Bandwidth meters
		bandwidth = new Label(shell, SWT.BORDER);
		FormData bandwidthData = new FormData();
		bandwidthData.left = new FormAttachment(0);
		bandwidthData.right = new FormAttachment(log, -margin);
		bandwidthData.top = new FormAttachment(log, margin);
		bandwidthData.bottom = new FormAttachment(100);
		bandwidth.setLayoutData(bandwidthData);		

		// Status bar
		statusBar = new Label(shell, SWT.BORDER);
		FormData statusBarData = new FormData();
		statusBarData.left = new FormAttachment(bandwidth, margin);
		statusBarData.right = new FormAttachment(100);
		statusBarData.top = new FormAttachment(log, margin);
		statusBarData.bottom = new FormAttachment(100);
		statusBar.setLayoutData(statusBarData);
		
		// Player list
		playerList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		FormData playerListData = new FormData();
		playerListData.left = new FormAttachment(0);
		playerListData.right = new FormAttachment(log, -margin);
		playerListData.top = new FormAttachment(startButton, margin);
		playerListData.bottom = new FormAttachment(statusBar, -margin);
		playerList.setLayoutData(playerListData);

		// Event handlers
		SelectionAdapter buttonsAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				if (source == startButton)
					setStatus(true);
				else if (source == stopButton)
					setStatus(false);
			}
		};
		startButton.addSelectionListener(buttonsAdapter);
		stopButton.addSelectionListener(buttonsAdapter);
	}
	
	/**
	 * Add some byte counts to the bandwidth meter
	 * 
	 * @param up the upload count
	 * @param down the download count
	 */
	public void addToBandwidth(int up, int down) {
		bytesRead += down;
		bytesWritten += up;
		display.asyncExec(this);
	}
	
	/**
	 * Add some string to the log
	 * 
	 * @param str the string
	 */
	public void log(String str) {
		messages.offer(str + "\n");
		display.asyncExec(this);
	}
	
	/**
	 * Render updates
	 */
	public void render() {
		shell.open();
		try {
			// Display until closed
			while (!shell.isDisposed())
				if (!display.readAndDispatch())
					display.sleep();
			display.dispose();
		} catch (SWTException e) {
			// do nothing
		}
		TankServer.shutDown();
	}
		
	/**
	 * Check for updates and perform them
	 */
	public void run() {
		// Bandwidth
		bandwidth.setText("Bytes: " + bytesWritten + "/" + bytesRead);
		
		// Player list
		if (players != null) {
			if (!Arrays.equals(players, playerList.getItems()))
				playerList.setItems(players);
		}

		// Chat log
		String msg;
		while ((msg = messages.poll()) != null) {
			log.append(msg);
		}
		
		// Status bar
		if (status)
			statusBar.setText("Server started");
		else
			statusBar.setText("Server stopped");
		stopButton.setEnabled(status);
		startButton.setEnabled(!status);
	}
	
	/**
	 * Set the status to true to turn the server on. Set it to false to turn it
	 * off. Buttons and the status bar are updated accordingly.
	 * 
	 * @param status
	 *            the status
	 */
	private void setStatus(boolean status) {
		this.status = status;
		TankServer.setStatus(status);
		display.asyncExec(this);
	}
	
	/**
	 * Set the players list
	 * 
	 * @param players the players
	 */
	public void setUsersList(String[] players) {
		this.players = players;
		display.asyncExec(this);
	}
}
