package Tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LevelPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private TetrisScene scene;
	
	private JLabel label, linesLabel;
	
	private JPanel panel;
	
	public LevelPanel(TetrisScene scene)
	{
		this.scene = scene;
		
		label = new JLabel("LEVEL");
		
		linesLabel = new JLabel("0");
		
		panel = new JPanel();

		linesLabel.setFont(new Font("Consolas", Font.BOLD, 40));
		linesLabel.setForeground(Color.WHITE);
		
		label.setForeground(Color.WHITE);
		
		panel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

		panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
		panel.add(linesLabel, gbc);
		
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
	
		panel.setPreferredSize(new Dimension(80, 80));
		panel.setMaximumSize(new Dimension(80, 80));
		
		setOpaque(false);
		panel.setBackground(Color.LIGHT_GRAY);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(label);
		add(panel);
	}
	
	public void update(double dt)
	{
		label.setText("" + scene.getLevel());
	}
}
