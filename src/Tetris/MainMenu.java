package Tetris;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JPanel implements KeyListener, MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	
	private UI ui;
	
	private JList<String> list;
	
	public MainMenu()
	{
		this(null);
	}
	
	public MainMenu(UI ui)
	{
		this.ui = ui;
	
		list = new JList<String>();

		MySwingUtilities.installFont("PressStart2P.ttf");
		
		list.setCellRenderer(new Renderer());
		list.setModel(new ListModel());
		list.setBackground(Color.black);
		list.setFixedCellHeight(50);
		list.addKeyListener(this);
		list.setBorder(new CompoundBorder(
				BorderFactory.createLineBorder(Color.DARK_GRAY, 5),
				new EmptyBorder(5, 15, 5, 15)));
		
		list.addMouseListener(this);
		list.addMouseMotionListener(this);
		
		setBackground(Color.black);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		add(list, gbc);
	}
	
	public void addNotify()
	{
		super.addNotify();
		
		list.requestFocusInWindow();
		
		list.setSelectedIndex(0);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
	
	private class Renderer implements ListCellRenderer<String>
	{
		private JLabel label;
		
		public Renderer()
		{
			label = new MyLabel();
			
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		
		@Override
		public Component getListCellRendererComponent(
				JList<? extends String> list,
				String val, int index,
				boolean selected, boolean focused)
		{
			if(selected)
				label.setForeground(Color.WHITE);
			else
				label.setForeground(Color.WHITE.darker());
			
			label.setText(val);

			return label;
		}
	}
	
	private class MyLabel extends JLabel
	{
		private static final long serialVersionUID = 1L;

		public MyLabel()
		{
			setOpaque(true);
			setBackground(new Color(0f,0f,0f,0f));
			
			setFont(new Font("Press Start 2P", Font.PLAIN, 20));
		}
		
		public void paintComponent(Graphics g)
		{
			g.setColor(Color.DARK_GRAY);
			
			Color foreground = getForeground();
			
			setForeground(Color.gray);
			
			paintComponent(g, -1, -1);
			paintComponent(g, 1, -1);
			paintComponent(g, -1, 1);
			paintComponent(g, 1, 1);
			
			setForeground(foreground);
			
			super.paintComponent(g);
		}
		
		public void paintComponent(Graphics g, int x, int y)
		{
			Graphics2D g2d = (Graphics2D) g.create();

			g2d.translate(x, y);

			super.paintComponent(g2d);
		}
	}

	private class ListModel extends AbstractListModel<String>
	{
		private static final long serialVersionUID = 1L;

		@Override
		public String getElementAt(int index)
		{
			if(index == 0)
				return "Play";
			else if(index == 2)
				return "High Scores";
			else if(index == 1)
				return "Options";
			else if(index == 3)
				return "Credits";
			else
				return "Exit";
		}

		@Override
		public int getSize()
		{
			return 5;
		}
	}

	private void activate(int index)
	{
		if(index == 0)
			ui.showSceneScreen();
		else if(index == 1)
			ui.showOptionsMenu();
		else if(index == 2)
			ui.showHighScoreScreen();
		else if(index == 3)
			ui.showCreditsScreen();
		else if(index == 4)
			ui.showTitleScreen();
		
		requestFocusInWindow(); // block input
	}

	private boolean ignoreMouseMotion = false;
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		
		int index = list.getSelectedIndex();
		
		if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE )
		{
			if(ui == null)
				return;
			
			activate(index);
		}
		else if(code == KeyEvent.VK_ESCAPE)
		{
			if(ui == null)
				return;
			
			ui.showTitleScreen();
			
			requestFocusInWindow(); // block input
		}
		else if(code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)
		{
			EventQueue.invokeLater(() -> 
			{
				int newIndex = list.getSelectedIndex();
			
				if(index == newIndex)
				{
					if(index == 0)
						newIndex = list.getModel().getSize() - 1;
					else 
						newIndex = 0;
					
					list.setSelectedIndex(newIndex);
				}
				
				Rectangle bounds = list.getCellBounds(newIndex , newIndex);
				
				Point pt = bounds.getLocation();
				
				Point screenLoc = list.getLocationOnScreen();
				
				pt.x += screenLoc.x; pt.y += screenLoc.y;
				
				try
				{				
					ignoreMouseMotion = true;
					
					Robot robot = new Robot();

					robot.mouseMove(pt.x, pt.y);				
				} 
				catch (AWTException e1)
				{
					e1.printStackTrace();
				}
			});
		}
			
	}

	@Override
	public void keyReleased(KeyEvent e){}

	@Override
	public void keyTyped(KeyEvent e){}

	@Override
	public void mouseDragged(MouseEvent e){}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if(ignoreMouseMotion)
		{
			ignoreMouseMotion = false;
			return;
		}
		
		Point pt = e.getPoint();
		
		int index = list.locationToIndex(pt);
		
		if(index > -1)
		{
			list.setSelectedIndex(index);
		}	
	}

	@Override
	public void mouseClicked(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Point pt = e.getPoint();
	
		int index = list.locationToIndex(pt);
		
		if(index > -1 && ui != null)
		{
			activate(index);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e){}
}
