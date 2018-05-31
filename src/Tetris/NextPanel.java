package Tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class NextPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private TetrisScene scene;
	
	private JLabel label;
	
	private NextPane pane0, pane1, pane2;
	
	public NextPanel(TetrisScene scene)
	{
		this.scene = scene;
	
		label = new JLabel("Next");
		
		label.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
		
		pane0 = new NextPane(0, true);
		pane1 = new NextPane(1, false);
		pane2 = new NextPane(2, false);
		
		label.setForeground(Color.WHITE);
		
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setOpaque(false);
		
		add(label);
		add(pane0);
		add(pane1);
		add(pane2);
	}
	
	public void update(double dt)
	{
		pane0.update(dt);
		pane1.update(dt);
		pane2.update(dt);
	}
	
	public void toggle()
	{
		setEnabled(!isEnabled());
	}
	
	private class NextPane extends JPanel
	{
		private static final long serialVersionUID = 1L;
	
		private int index;
		
		public NextPane(int index, boolean opaque)
		{			
			this.index = index;
			
			setOpaque(opaque);
			
			update(0);			
			
			setPreferredSize(new Dimension(80, 80));
			setMaximumSize(new Dimension(80, 80));
			
			Border lineBorder = BorderFactory.createLineBorder(Color.black, 2),
				   emptyBorder = BorderFactory.createEmptyBorder(20, 5, 20, 5);
			
			setBorder(new CompoundBorder(lineBorder, emptyBorder));
		}
		
		public void update(double dt)
		{
			Tetromino nextPiece = scene.getNextPiece(index);
			
			Color color = nextPiece.getColor();
			
			setBackground(color);
			
			repaint();
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			if(!NextPanel.this.isEnabled())
				return;
			
			Graphics2D g2d = (Graphics2D) g;
			
			Insets insets = getInsets();
			
			Rectangle frame = new Rectangle(
					insets.left, insets.top, 
					getWidth() - insets.left - insets.right,
					getHeight() - insets.top - insets.bottom);
		
			Tetromino nextPiece = scene.getNextPiece(index);

			int cols = nextPiece.cols(),
				rows = nextPiece.rows();
			
			double colWidth = frame.width / 4.0,
				   rowHeight = frame.height / 2.0;
			
			double centerx = frame.getCenterX(),
				   centery = frame.getCenterY();

			nextPiece.paint(g2d, centerx - colWidth * cols / 2.0,
								 centery - rowHeight * rows / 2.0,
								 colWidth * cols, rowHeight * rows);
		}
	}
}
