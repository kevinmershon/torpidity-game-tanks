package org.torpidity.tank.gui.client;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.torpidity.tank.networking.ClientChatHandler;

public class ChatPanel extends Composite implements Runnable {
	private ClientChatHandler chatServer;
	
	private Text log;
	private Text entry;
	private Button sendButton;
	
	private LinkedBlockingQueue<String> messages;
	
	public ChatPanel(Shell shell, int uid) {
		super(shell, SWT.NO_FOCUS);		
		messages = new LinkedBlockingQueue<String>();
		
		// Layout setup
		int margin = 3;
		FormLayout layout = new FormLayout();
		layout.marginLeft = margin;
		layout.marginRight = margin;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		this.setLayout(layout);
		
		// Send button
		sendButton = new Button(this, SWT.PUSH);
		sendButton.setText("Send");
		FormData sendData = new FormData();
		sendData.right = new FormAttachment(100);
		sendData.bottom = new FormAttachment(100);
		sendButton.setLayoutData(sendData);
				
		// Chat log
		log = new Text(this, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		log.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		log.append("Torpidity Tank Game v.2.0 - Kevin Mershon\n----------------------------------------------------\n");
		FormData logData = new FormData();
		logData.left = new FormAttachment(0);
		logData.top = new FormAttachment(0);
		logData.right = new FormAttachment(100);
		logData.bottom = new FormAttachment(sendButton, -margin);
		log.setLayoutData(logData);
		
		// Entry field
		entry = new Text(this, SWT.BORDER);
		FormData entryData = new FormData();
		entryData.left = new FormAttachment(0);
		entryData.right = new FormAttachment(sendButton, -margin);
		entryData.top = new FormAttachment(log, margin);
		entryData.bottom = new FormAttachment(100);
		entry.setLayoutData(entryData);
		
		// Event handlers
		entry.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN)
					sendMessage();
			}
		});
		sendButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sendMessage();
				entry.setFocus();
			}
		});
		entry.setFocus();
		
		chatServer = new ClientChatHandler(uid, this);
	}
	
	/**
	 * Add some string to the log
	 * 
	 * @param str the string
	 */
	public void log(String str) {
		messages.offer(str + "\n");
		Display.getDefault().asyncExec(this);
	}
	
	/**
	 * Update the chat log
	 */
	public void run() {
		String message;
		while ((message = messages.poll()) != null) {
			log.append(message);
		}
	}
	
	/**
	 * Send the contents of the entry to the server
	 */
	private void sendMessage() {
		String message = entry.getText();
		entry.setText("");
		if (message != "") {
			chatServer.write(message);
		}
	}
}
