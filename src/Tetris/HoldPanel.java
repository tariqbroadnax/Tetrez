package Tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

public class HoldPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private TetrisScene scene;
	
	private JLabel label;
	
	private HoldPane pane;
	
	public HoldPanel(TetrisScene scene)
	{
		this.scene = scene;
		
		label = new JLabel("HOLD");
		
		pane = new HoldPane();
		
		label.setForeground(Color.WHITE);
		
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.setAlignmentX(Component.CENTER_ALIGNMENT);

		setOpaque(false);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(label);
		add(pane);
	}
	
	public void update(double dt)
	{
		pane.update(dt);
	}
	
	private class HoldPane extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public HoldPane()
		{
			setPreferredSize(new Dimension(80, 80));
			setMaximumSize(new Dimension(80, 80));
			
			Border lineBorder = BorderFactory.createLineBorder(Color.WHITE, 2),
				   emptyBorder = BorderFactory.createEmptyBorder(20, 5, 20, 5);
			
			setBorder(new CompoundBorder(lineBorder, emptyBorder));
			
			setBackground(new Color(0f,0f,0f,0f));
		}
		
		public void update(double dt)
		{
			Tetromino holdPiece = scene.getHoldPiece();
			
			if(scene.isHoldPieceAvailable() && holdPiece != null)
				setBackground(holdPiece.getColor());
			else
				setBackground(new Color(0f,0f,0f,0f));
			
			repaint();
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			
			Insets insets = getInsets();
			
			Rectangle frame = new Rectangle(
					insets.left, insets.top, 
					getWidth() - insets.left - insets.right,
					getHeight() - insets.top - insets.bottom);
		
			Tetromino holdPiece = scene.getHoldPiece();

			if(!scene.isHoldPieceAvailable() || holdPiece == null)
				return;

			int cols = holdPiece.cols(),
				rows = holdPiece.rows();
			
			double colWidth = frame.width / 4.0,
				   rowHeight = frame.height / 2.0;
			
			double centerx = frame.getCenterX(),
				   centery = frame.getCenterY();

			holdPiece.paint(g2d, centerx - colWidth * cols / 2.0,
								 centery - rowHeight * rows / 2.0,
								 colWidth * cols, rowHeight * rows);
		}
	}
}
