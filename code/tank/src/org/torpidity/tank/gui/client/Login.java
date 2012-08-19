package org.torpidity.tank.gui.client;

import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.torpidity.tank.data.MySQL;

/**
 * Login provides a login screen to the game, which includes a short news blurb
 * related to the game.
 * 
 * @author Kevin Mershon
 */
public class Login {
	private Display display;
	private Shell shell;

	private Text news;
	private Text username;
	private Text password;
	private Button loginButton;

	private boolean success = false;
	
	/**
	 * Create a new Login
	 */
	public Login() {
		int width = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getWidth();
		int height = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getHeight();
		MySQL.connect();
		display = Display.getDefault();
		shell = new Shell(display, SWT.MIN);
		shell.setText("Torpidity Tank Game - Log in");
		shell.setBounds((width - 300) / 2, (height - 300) / 2, 300, 300);

		// Layout setup
		int margin = 3;
		FormLayout layout = new FormLayout();
		layout.marginLeft = margin;
		layout.marginRight = margin;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		shell.setLayout(layout);

		// News
		news = new Text(shell, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL
				| SWT.READ_ONLY);
		news.setBackground(new Color(display, 255, 255, 255));
		FormData newsData = new FormData();
		newsData.top = new FormAttachment(0);
		newsData.left = new FormAttachment(0);
		newsData.right = new FormAttachment(100);
		newsData.bottom = new FormAttachment(70);
		news.setLayoutData(newsData);

		// Username label
		Label usernameLabel = new Label(shell, SWT.READ_ONLY);
		usernameLabel.setText("Username:");
		FormData ulData = new FormData();
		ulData.top = new FormAttachment(news, margin);
		ulData.left = new FormAttachment(15);
		usernameLabel.setLayoutData(ulData);
		// Username field
		username = new Text(shell, SWT.BORDER);
		FormData unData = new FormData();
		unData.top = new FormAttachment(news, margin);
		unData.left = new FormAttachment(45);
		unData.right = new FormAttachment(85);
		username.setLayoutData(unData);

		// Password label
		Label passwordLabel = new Label(shell, SWT.READ_ONLY);
		passwordLabel.setText("Password:");
		FormData plData = new FormData();
		plData.top = new FormAttachment(username, margin);
		plData.left = new FormAttachment(15);
		passwordLabel.setLayoutData(plData);
		// Password field
		password = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		FormData pnData = new FormData();
		pnData.top = new FormAttachment(username, margin);
		pnData.left = new FormAttachment(45);
		pnData.right = new FormAttachment(85);
		password.setLayoutData(pnData);

		// loginButton
		loginButton = new Button(shell, SWT.PUSH);
		loginButton.setText("Login");
		FormData loginData = new FormData();
		loginData.left = new FormAttachment(35);
		loginData.right = new FormAttachment(65);
		loginData.bottom = new FormAttachment(100);
		loginButton.setLayoutData(loginData);
		
		loadNews();
		
		loginButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				connect();
			}
		});
		
		shell.open();
		shell.setFocus();

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		if (!success)
			display.dispose();
	}
	
	/**
	 * Connect, validate login, and then join the game
	 */
	public void connect() {
		int uid;
		try {
			if (!username.getText().equals("")
					&& !password.getText().equals("")) {
				int query = MySQL
						.query("SELECT `uid` FROM `tankgame_users` WHERE `username`='"
								+ username.getText()
								+ "' AND `password`=MD5('"
								+ new String(password.getText()) + "')");
				if (query == -1)
					throw new SQLException("Query failed.");
				ResultSet r = MySQL.result();
				if (r.first()) {
					uid = r.getInt("uid");
					success = true;
					shell.dispose();
					new TankClientFrame(uid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (NullPointerException e) {
			ErrorDialog error = new ErrorDialog("Login", "Invalid username or password!", false);
			error.setShell(shell);
			error.run();
		}
	}

	/**
	 * Load the news from the database
	 */
	private void loadNews() {
		try {
			MySQL.query("SELECT `news` FROM `tankgame_news` ORDER BY `id` DESC LIMIT 1");
			ResultSet r = MySQL.result();
			String newsStr = r.getString("news");
			news.setText(newsStr);
		} catch (SQLException e) {
			return;
		}
	}
}
