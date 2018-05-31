package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class UI
{
	private Tetris3D tetris;

	private JFrame frame;
	
	private TitleScreen titleScreen;
	
	private MainMenu mainMenu;
	
	private ScenePane sceneScreen;
	
	private DemoScreen demoScreen;
	
	private FadePanel panel;

	private CreditsScreen creditsScreen;
	
	public UI(Tetris3D tetris)
	{
		this.tetris = tetris;
		
		titleScreen = new TitleScreen(this);
		
		mainMenu = new MainMenu(this);
		
		sceneScreen = new ScenePane(tetris.getScene());

		creditsScreen = new CreditsScreen(this);
		
		demoScreen = new DemoScreen(this);
		
		panel = new FadePanel();

		frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		
		frame.setContentPane(panel);

		demoScreen.setSize(784, 561);
	
		installFont("NEW.ttf");
		installFont("PressStart2P.ttf");	
	}

	private void installFont(String path)
	{
		try 
		{
		     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path)));
		} 
		catch (IOException|FontFormatException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(1);
		}
	}
	
	public void start() 
	{
		frame.setVisible(true);
		
//		sceneScreen.start(); // FIXME REMOVEME
	}

	public void stop() {
		frame.setVisible(false);
	}

	public void showSceneScreen()
	{
		panel.show(sceneScreen, () -> sceneScreen.start());
	}

	public void showTitleScreen() 
	{
		panel.show(titleScreen, null);
	}
	
	public void showDemoScreen()
	{
		panel.show(demoScreen, () -> demoScreen.start());
	}

	public void showOptionsMenu()
	{
	}

	public void showCreditsScreen()
	{
		panel.show(creditsScreen, () -> creditsScreen.start());
	}

	public void showMainMenu()
	{
		panel.show(mainMenu, null);
	}
	
	private class FadePanel extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;

		private static final int FADING_IN = 0,
								 FADING_OUT = 1,
								 NORMAL = 2;
		
		private static final double FADE_PERIOD = 0.2;
		
		private int state;
		
		public Timer timer;
		
		private JComponent next;
		
		private long lastUpdate;
		
		private double elapsed;
		
		private Runnable runnable;
		
		public FadePanel()
		{
			state = NORMAL;
			
			timer = new Timer(16, this);
		
			setLayout(new BorderLayout());
			
			add(titleScreen, BorderLayout.CENTER);
		}
		
		public void show(JComponent next, Runnable runnable)
		{
			this.next = next;
			this.runnable = runnable;
			
			state = FADING_OUT;
		
			elapsed = 0;
		
			lastUpdate = System.nanoTime();
		
			timer.start();
		}
		
		public void paint(Graphics g)
		{
			super.paint(g);
			
			if(state == FADING_IN)
			{
				float a = 1 - (float) (elapsed / FADE_PERIOD);
				
				g.setColor(new Color(0f, 0f, 0f, a));
				
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			else if(state == FADING_OUT)
			{
				float a = (float) (elapsed / FADE_PERIOD);

				g.setColor(new Color(0f, 0f, 0f, a));
				
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			long now = System.nanoTime();
			
			double dt = (now - lastUpdate) / 1.0e9;
			
			lastUpdate = now;
			
			// hack to fix this problem
			if(elapsed == 0 && dt > 0.4)
				return;
			
			elapsed += dt;
			
			if(state == FADING_OUT)
			{
				if(elapsed > FADE_PERIOD)
				{
					removeAll();
					
					add(next, BorderLayout.CENTER);
					revalidate();
					
					state = FADING_IN;
					
					elapsed = 0;
				}
			}
			else if(state == FADING_IN)
			{
				if(elapsed > FADE_PERIOD)
				{
					elapsed = 0;
					
					state = NORMAL;
					
					timer.stop();
					
					if(runnable != null)
						runnable.run();
				}
			}
			
			repaint();
		}
	}

	public void showHighScoreScreen()
	{
		// TODO Auto-generated method stub
		
	}
}
