package org.torpidity.tank.gui.client;

import java.awt.Frame;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.torpidity.jogl.Control;
import org.torpidity.jogl.TestScreen;
import org.torpidity.tank.control.PlayerControls;
import org.torpidity.tank.data.MySQL;
import org.torpidity.tank.gui.ViewPanel;

/**
 * TankClientFrame creates the client UI and handles the UI thread
 * 
 * @author Kevin Mershon
 */
public class TankClientFrame {
	private static Display display;
	private static Shell shell;

	@SuppressWarnings("unused")
	private TankMenu menu;
	private PlayerControls controls;
	private ChatPanel chatPanel;

	/**
	 * Create the client frame
	 */
	public TankClientFrame(int uid) {
		int width = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getWidth();
		int height = (int) (Toolkit.getDefaultToolkit()).getScreenSize()
				.getHeight();
		display = Display.getDefault();
		shell = new Shell(display, SWT.MIN);
		shell.setText("Torpidity Tank Game");
		shell.setBounds((width - 800) / 2, (height - 600) / 2, 800, 600);
		
		menu = new TankMenu(shell);
		MySQL.connect();
		
		// Layout setup
		int margin = 0;
		FormLayout layout = new FormLayout();
		layout.marginLeft = margin;
		layout.marginRight = margin;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		shell.setLayout(layout);
		
		// View Panel as embedded legacy Swing code
		Composite comp = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(comp);
		ViewPanel view = null;
		frame.add(view = new ViewPanel(uid));
		view.setFocusable(true);
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0);
		viewData.right = new FormAttachment(100);
		viewData.top = new FormAttachment(0);
		viewData.bottom = new FormAttachment(80);
		comp.setLayoutData(viewData);

		controls = new PlayerControls(comp, view.getLocalPlayer());
		comp.addKeyListener(controls.getKeyAdapter());
		comp.addMouseListener(controls.getMouseAdapter());
		 
		/*
		Composite comp = new Composite(shell, SWT.NONE);
		FormData compData = new FormData();
		compData.left = new FormAttachment(0);
		compData.top = new FormAttachment(0);
		compData.right = new FormAttachment(100);
		compData.bottom = new FormAttachment(80);
		comp.setLayoutData(compData);
		comp.setLayout(new FillLayout());
		TestScreen screen = new TestScreen(display, comp);
		screen.init();
		new Control(screen);
		*/
		
		// Chat Panel
		chatPanel = new ChatPanel(shell, uid);
		FormData chatPanelData = new FormData();
		chatPanelData.left = new FormAttachment(0);
		chatPanelData.top = new FormAttachment(80);
		chatPanelData.right = new FormAttachment(100);
		chatPanelData.bottom = new FormAttachment(100);
		chatPanel.setLayoutData(chatPanelData);
		
		shell.open();
		shell.setFocus();
		comp.setFocus();
		
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
		System.exit(0);
	}

	/**
	 * Add some error to the queue to be displayed
	 * 
	 * @param error
	 *            the error
	 */
	public static void doError(ErrorDialog error) {
		error.setShell(shell);
		display.asyncExec(error);
	}
}
