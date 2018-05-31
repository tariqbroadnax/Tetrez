package Tetris;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DemoScreen extends JPanel implements ActionListener, MouseListener, KeyListener, ComponentListener
{
	private static final long serialVersionUID = 1L;

	private Timer timer;
	
	private JLayeredPane layers;
	
	private JPanel glassPane;

	private ScenePane scenePane;

	private LinkedList<KeyEvent> timeline;
	
	private double elapsed;
	
	private long lastUpdate, start;
	
	private UI ui;

	public DemoScreen(UI ui)
	{
		this(new TetrisScene());
	
		this.ui = ui;
	}

	public DemoScreen(TetrisScene scene, String path)
	{
		this(scene);
		
		importFile(path);
	}		
	
	public DemoScreen(TetrisScene scene)
	{
		timeline = new LinkedList<KeyEvent>();

		timer = new Timer(33, this);
		
		layers = new JLayeredPane();
		
		scenePane = new ScenePane(scene);
		glassPane = new JPanel();
		
		glassPane.setOpaque(false);
		
		start = 0;
		
		layers.add(scenePane);
		layers.add(glassPane);
		
		layers.setLayer(scenePane, 0);
		layers.setLayer(glassPane, 1);
		
		setLayout(new BorderLayout());
		
		add(layers, BorderLayout.CENTER);
		
		addComponentListener(this);
		
		glassPane.addKeyListener(this);
		glassPane.addMouseListener(this);
		
		glassPane.requestFocusInWindow();
		
		addRandomInput();
	}

	private void importFile(String path)
	{
		try(ObjectInputStream in = new ObjectInputStream(
				   new FileInputStream(path)))
		{
			start = in.readLong();
			
			timeline.clear();
			
			while(in.available() > 0)
			{
				int id = in.readInt();
				long when = in.readLong();
				int modifiers = in.readInt();
				int keyCode = in.readInt();
				char keyChar = in.readChar();
				
				KeyEvent e = new KeyEvent(null, id, when, modifiers, keyCode, keyChar);
				
				timeline.add(e);
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void addRandomInput()
	{
		for(int i = 0; i < 20; i++)
		{
			int rand = (int)(Math.random() * 3);
			
			long when = (long)(i * 1 * 1e9),
				 when2 = (long)((i + 0.5) * 1 * 1e9);
			
			int code = rand == 0 ? KeyEvent.VK_LEFT :
					   rand == 1 ? KeyEvent.VK_RIGHT :
						   		   KeyEvent.VK_SPACE;
			
			char keyChar = rand == 3 ? ' ' : '?';
			
			timeline.add(new KeyEvent(this, KeyEvent.KEY_PRESSED, when, 0, code, keyChar));
			timeline.add(new KeyEvent(this, KeyEvent.KEY_RELEASED, when2, 0, code, keyChar));
		}
	}
	
	public void start()
	{
		scenePane.start();
		
		timer.start();

		lastUpdate = System.nanoTime();
		
		elapsed = 0;
	}
	
	public void addNotify()
	{
		super.addNotify();
		
		glassPane.requestFocusInWindow();
	}
	
	public void removeNotify()
	{
		super.removeNotify();
		
		timer.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		long now = System.nanoTime();

		double dt = (now - lastUpdate) / 1.0e9;

		lastUpdate = now;
		
		elapsed += dt;
		
		if(timeline.isEmpty())
			return;
		
		double when = (timeline.peek().getWhen() - start) / 1.0e9;
		
		while(when < elapsed)
		{
			KeyEvent evt = timeline.pop();
			
			scenePane.processKeyEvent(evt);

			if(timeline.isEmpty())
				when = elapsed;
			else
				when = (timeline.peek().getWhen() - start) / 1.0e9;
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(ui != null)
			ui.showTitleScreen();
	}	

	@Override
	public void keyReleased(KeyEvent e){}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(ui != null)
			ui.showTitleScreen();
	}

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	@Override
	public void componentHidden(ComponentEvent e){}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentResized(ComponentEvent e)
	{
		Dimension size = getSize();
		
		scenePane.setSize(size);
		glassPane.setSize(size);	
		
		scenePane.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		Dimension size = getSize();
		
		scenePane.setSize(size);
		glassPane.setSize(size);

		scenePane.revalidate();
	}
}
