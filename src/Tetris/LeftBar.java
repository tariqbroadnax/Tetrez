package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class LeftBar extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTextPane area;
	
	private TetrisScene scene;
	
	public LeftBar(TetrisScene scene)
	{
		this.scene = scene;
		
		area = new JTextPane();
	
		setOpaque(false);
		
		area.setOpaque(false);
		area.setFont(new Font("Press Start 2P", Font.BOLD, 12));
		area.setEditable(false);
		area.setForeground(Color.WHITE);

		setLayout(new BorderLayout());
		
		add(area, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(90, 1));
	
		int time = (int) scene.getTime();
		
		int min = time / 60;
		int secs = time % 60;
		
		area.setText(
				  "Score\n"
				+ scene.getScore() + "\n"
				+ "Lines\n"
				+ scene.getLines() + "\n"
				+ "Level\n"
				+ scene.getLevel() + "\n"
				+ "Time\n"
				+ String.format("%d:%02d", min, secs));
		
		area.selectAll();
		
		MutableAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(set, 1);
		
		area.setParagraphAttributes(set, false);
		area.setHighlighter(null);
	}

	public void update(double dt)
	{
		int time = (int) scene.getTime();
		
		int min = time / 60;
		int secs = time % 60;
		
		area.setText(
				  "Score\n"
				+ scene.getScore() + "\n"
				+ "Lines\n"
				+ scene.getLines() + "\n"
				+ "Level\n"
				+ scene.getLevel() + "\n"
				+ "Time\n"
				+ String.format("%d:%02d", min, secs));
	}
}
