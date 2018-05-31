package Tetris;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class TopBar extends JPanel
{
	private static final long serialVersionUID = 1L;

	private ScorePanel scorePanel;
	
	private PauseButton pauseButton;
	
	public TopBar(TetrisScene scene)
	{
		scorePanel = new ScorePanel(scene);
		
		pauseButton = new PauseButton(scene);
		
		setLayout(new BorderLayout());
		
		add(scorePanel, BorderLayout.CENTER);
		add(pauseButton, BorderLayout.EAST);
		
		setOpaque(false);
	}
	
	public void update(double dt)
	{
		scorePanel.update(dt);
		pauseButton.update(dt);
	}
}
