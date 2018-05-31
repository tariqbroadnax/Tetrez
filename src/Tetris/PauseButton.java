package Tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class PauseButton extends JButton implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private TetrisScene scene;
	
	public PauseButton(TetrisScene scene)
	{
		this.scene = scene;
		
		setIcon(new MyIcon());

		setFocusable(false);
		setBackground(Colors.NAVY_BLUE);
		
		setBorder(BorderFactory.createCompoundBorder(
				  BorderFactory.createLineBorder(Color.WHITE),
				  BorderFactory.createEmptyBorder(5, 30, 5, 30)));
	}

	public void update(double dt)
	{
		repaint();
	}
	
	private class MyIcon implements Icon
	{
		@Override
		public int getIconHeight()
		{
			return 18;
		}

		@Override
		public int getIconWidth()
		{
			return 18;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			g.setColor(Color.WHITE);

			if(scene.isPaused())
			{
				int h = getIconHeight(), w = getIconWidth();
				
				int[] xs = {0, w, 0},
					  ys = {0, h/2, h};
				
				g.fillPolygon(xs, ys, 3);
			}
			else
			{
				int h = getIconHeight(), w = getIconWidth()/3;
				
				g.fillRect(x, y, w, h);
				g.fillRect(x + 2 * w, y, w, h);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(scene.isPaused())
			scene.start();
		else
			scene.pause();
	}
}
