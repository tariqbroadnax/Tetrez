package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ScorePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private JLabel scoreLabel, scoreValLabel;
	
	private TetrisScene scene;
	
	public ScorePanel(TetrisScene scene)
	{
		this.scene = scene;
		
		scoreLabel = new JLabel("SCORE");
		
		scoreValLabel = new JLabel("0");

		scoreValLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		scoreLabel.setForeground(Color.WHITE);
		scoreValLabel.setForeground(Color.WHITE);

		setOpaque(false);
		
		setLayout(new BorderLayout());

		add(scoreLabel, BorderLayout.WEST);
		add(scoreValLabel, BorderLayout.CENTER);
	}
	
	public void update(double dt)
	{
		scoreValLabel.setText("" + scene.getScore());
	}
}
