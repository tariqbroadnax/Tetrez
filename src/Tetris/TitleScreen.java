package Tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScreen extends JPanel implements ActionListener, KeyListener, MouseListener, FocusListener
{
	private static final long serialVersionUID = 1L;

	private static final double FADE_PERIOD = 2,
								EXIT_DELAY = 1,
								MAX_NO_ACTIVITY_PERIOD = 10;
	
	private enum State {
		NORMAL, EXITING_MENU, EXITING_DEMO
	}
	
	private State state = State.NORMAL;
	
	private JLabel titleLabel,
				   startLabel;
	
	private Timer timer;
	
	private Emitter emitter;
	
	private UI ui;

	private long lastUpdate;
	
	private double elapsed;
	
	private boolean displayDebugInfo = false;
	
	public TitleScreen()
	{
		this(null);
	}
	
	public TitleScreen(UI ui)
	{
		this.ui = ui;
		
		titleLabel = new TitleLabel();
		startLabel = new JLabel("Press Enter or Spacebar to Start");

		timer = new Timer(Constants.FPS_60_DELAY_MS_INT, this);
		
		emitter = new Emitter();
				
        MySwingUtilities.installFont("NEW.ttf");
        MySwingUtilities.installFont("PressStart2P.ttf");
        
        titleLabel.setFont(new Font("NEW TETRIS", Font.BOLD, 80));
        startLabel.setFont(new Font("Press Start 2P", Font.BOLD, 10));		
		
        startLabel.setForeground(Color.white);
		
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		setBackground(Color.black);

		addComponents();
		
        addKeyListener(this);
        addFocusListener(this);
	}
	
	private void addComponents()
	{
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(titleLabel);
		panel.add(startLabel);
		
		setLayout(new GridBagLayout());
        
		GridBagConstraints gbc = new GridBagConstraints();
        
        add(panel, gbc);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		emitter.paint(g);
		
		if(displayDebugInfo)
		{
			g.setColor(Color.WHITE);
			
			String str = "particles: " + emitter.getParticleCount() + 
						 "\nstate: " + state;
					
			MySwingUtilities.drawString(g, str, 0, 0);
		}
	}
	
	public void addNotify()
	{
		super.addNotify();

		state = State.NORMAL;
		
		lastUpdate = System.nanoTime();
	
		elapsed = 0;

		requestFocusInWindow();
	
		emitter.reset();

		timer.start();
	}
	
	public void removeNotify()
	{
		super.removeNotify();
		
		timer.stop();
	}

	private void update(double dt)
	{
		elapsed += dt;

		float alpha = 1.0f;
				
		if(state == State.EXITING_MENU)
		{
			alpha = (float)Math.abs((4 * (elapsed % (FADE_PERIOD / 4))  / FADE_PERIOD - 0.5));
		
			if(elapsed > EXIT_DELAY)
			{
				if(ui == null)
				{
					state = State.NORMAL;
				
					elapsed = 0;
				}
				else
				{
					if(state == State.EXITING_MENU)
						ui.showMainMenu();
					else
						ui.showDemoScreen();
					
					alpha = 1;
					
					timer.stop();
				}
			}
		}
		else
		{
			alpha = (float)Math.abs(( (elapsed % FADE_PERIOD)  / FADE_PERIOD - 0.5));
			
			if(elapsed > MAX_NO_ACTIVITY_PERIOD && ui != null)
				state = State.EXITING_DEMO;
		}
		
		Color color = new Color(1f, 1f, 1f, alpha);
		
		startLabel.setForeground(color);

		emitter.update(dt);
		
		repaint();	
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		long now = System.nanoTime();
		
		double dt = (now - lastUpdate) / 1.0e9;
		
		lastUpdate = now;

		update(dt);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_SPACE && state != State.EXITING_MENU)
		{
			state = State.EXITING_MENU;
		
			elapsed = 0;
		}
		else if(code == KeyEvent.VK_F11)
		{
			displayDebugInfo = !displayDebugInfo;
		}
	}

	@Override
	public void keyReleased(KeyEvent e){}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(state != State.EXITING_MENU)
		{
			state = State.EXITING_MENU;
		
			elapsed = 0;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	public void setDisplayDebugInformation(boolean displayDebugInfo)
	{
		this.displayDebugInfo = displayDebugInfo;
		
		repaint();
	}

	private class TitleLabel extends JLabel
	{
		private static final long serialVersionUID = 1L;

		public TitleLabel()
		{
			super("<html><font color=red>T</font>"
							+ "<font color=orange>e</font>"
							+ "<font color=yellow>t</font>"
							+ "<font color=green>r</font>"
							+ "<font color=blue>e</font>"
							+ "<font color=purple>z</font></html>");
		}
		
		public void paintComponent(Graphics g)
		{
			FontMetrics metrics = g.getFontMetrics();
			
			int height = metrics.getAscent() + metrics.getLeading();
			
			g.setColor(Color.black);
			
			for(int i = 1; i < 10; i++)
				g.drawString("Tetrez", i, height + i);
			
			super.paintComponent(g);
		}
	}
	
	private class TetroParticle
	{
		public final static int BLOCK_WIDTH =  20,
								BLOCK_HEIGHT = 20;
		
		private Tetromino piece;
		
		private double x, y;
		
		private double velx, vely;
		
		public TetroParticle(double x, double y, double velx, double vely)
		{
			this.x = x;
			this.y = y;
			
			this.velx = velx;
			this.vely = vely;
			
			piece = Tetromino.random();
		}
		
		public void update(double dt)
		{
			x += velx * dt;
			y += vely * dt;
		}
		
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g.create(); 
			
			piece.paint(g2d, x, y, BLOCK_WIDTH * piece.cols(), BLOCK_HEIGHT * piece.rows());
		}
		
		public double gety() {
			return y;
		}
	}
	
	private class Emitter
	{
		private static final double MIN_VEL = 200, MAX_VEL = 400;
		
		private static final double SPAWN_DELAY = 0.5;
		
		private static final double SPAWN_COUNT = 10;
		
		private List<TetroParticle> particles;
		
		private double elapsed;
		
		public Emitter()
		{
			particles = new ArrayList<TetroParticle>();
		
			elapsed = SPAWN_DELAY;
		}
		
		public void reset()
		{
			elapsed = SPAWN_DELAY;
			
			particles.clear();
		}
		
		public void update(double dt)
		{
			double height = getHeight() + 20;
			
			particles.removeIf(p -> p.gety() > height);
		
			for(TetroParticle particle : particles)
				particle.update(dt);
		
			elapsed += dt;
			
			if(elapsed > SPAWN_DELAY)
			{
				elapsed = 0;
				
				spawn();
			}
		}
		
		public void paint(Graphics g)
		{
			for(TetroParticle particle : particles)
				particle.paint(g);
		}
		
		private void spawn()
		{
			double width = 2000;
			
			for(int i = 0; i < SPAWN_COUNT; i++)
			{
				double x = Math.random() * width;
				
				double vely = MIN_VEL + Math.random() * MAX_VEL;

				// spawn above screen
				TetroParticle particle = new TetroParticle(x, -Tetromino.MAX_ROWS * TetroParticle.BLOCK_HEIGHT, 0, vely);
				
				particles.add(particle);
			}
		}
		
		public int getParticleCount() {
			return particles.size();
		}
	}

	@Override
	public void focusGained(FocusEvent e)
	{
        // do not accept mouse input unless window already has focus
		addMouseListener(this);
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		removeMouseListener(this);
	}
}
