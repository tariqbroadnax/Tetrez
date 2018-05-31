package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CreditsScreen extends JPanel implements ActionListener, KeyListener, ComponentListener
{
	private static final double SCROLL_SPEED = 12;
	
	private static final long serialVersionUID = 1L;
	
	private static final double ENTER_PERIOD = 3, EXIT_PERIOD = 1; 

	private MyEditorPane editorPane;
	
	private Timer timer;
	
	private long lastUpdate;
	
	private double elapsed, dy;

	private boolean turbo;
	
	private enum State {
		ENTERING, SCROLLING, EXITING;
	}
	
	private State state;
	
	private UI ui;
	
	public CreditsScreen(UI ui)
	{
		this.ui = ui;
		
		editorPane = new MyEditorPane();
		
		timer = new Timer(33, this);
		
		setLayout(new BorderLayout());
	
		add(editorPane, BorderLayout.CENTER);
		
		addKeyListener(this);
		addComponentListener(this);
	}
	
	public void start()
	{
		state = State.ENTERING;
		
		elapsed = 0;
		
		lastUpdate = System.nanoTime();
		
		requestFocusInWindow();

		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		long now = System.nanoTime();
		
		double dt = (now - lastUpdate) / 1.0e9;

		elapsed += dt;
		
		lastUpdate = now;
		
		if(state == State.ENTERING)
		{
			if(elapsed > ENTER_PERIOD)
			{
				state = State.SCROLLING;
				elapsed = 0;
			}
		}
		else if(state == State.SCROLLING)
		{
			dy -= (turbo ? 4 : 1) * SCROLL_SPEED * dt;

			if(editorPane.getPreferredSize().height + dy < 0)
			{
				state = State.EXITING;
				
				elapsed = 0;
			}
			
			repaint();
		}
		else
		{
			if(elapsed > EXIT_PERIOD)
			{
				timer.stop();
				
				if(ui != null)
					ui.showMainMenu();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_SPACE)
			turbo = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_SPACE)
			turbo = false;
	}
	
	private class MyEditorPane extends JTextPane
	{
		private static final long serialVersionUID = 1L;

		public MyEditorPane()
		{
			setContentType("text/html");

			try
			{
				Scanner input = new Scanner(new File("credits.txt"));
			
				String text = "";
				
				while(input.hasNextLine())
					text += input.nextLine();
				
				setText(text);
				
				input.close();
				
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			
			StyledDocument doc = getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
			setBackground(Color.black);
		}
		
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.BLACK);
			
			g.fillRect(0, 0, getWidth(), getHeight());
			
			Graphics2D g2d = (Graphics2D) g.create();
			
			g2d.translate(0, dy);
			
			super.paintComponent(g2d);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0){}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		if(!timer.isRunning())
			dy = getHeight() / 4;		
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
	}
}
