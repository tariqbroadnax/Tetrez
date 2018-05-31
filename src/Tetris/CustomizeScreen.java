package Tetris;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CustomizeScreen extends JPanel
{
	private JLabel levelLabel,
				   heightLabel;
	
	private NumberField levelField,
						heightField;
	
	private JButton backButton,
					startButton;
	
	public CustomizeScreen()
	{
		levelLabel = new JLabel("LEVEL");
		heightLabel = new JLabel("HEIGHT");
		
		levelField = new NumberField(1, 5, 10, 15);
		heightField = new NumberField(0, 1, 2, 3, 4, 5);
	
		backButton = new JButton("BACK");
		startButton = new JButton("START");
				
		JPanel levelPanel = new JPanel(),
			   heightPanel = new JPanel();
		
		levelPanel.setLayout(new BorderLayout());
		heightPanel.setLayout(new BorderLayout());
		
		levelPanel.add(levelLabel, BorderLayout.CENTER);
		levelPanel.add(levelField, BorderLayout.EAST);
		
		heightPanel.add(heightLabel, BorderLayout.CENTER);
		heightPanel.add(heightField, BorderLayout.EAST);
	
		JPanel custPanel = new JPanel();
		
		custPanel.setLayout(new BoxLayout(custPanel, BoxLayout.Y_AXIS));
	
		levelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		heightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		custPanel.add(levelPanel);
		custPanel.add(heightPanel);
		
		JPanel vcenterPanel = new JPanel();
		
		vcenterPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		vcenterPanel.add(custPanel, gbc);
		
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
	
		buttonPanel.add(backButton);
		buttonPanel.add(startButton);
		
		setLayout(new BorderLayout());
		
		add(vcenterPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private class NumberField extends JComponent
	{
		private int[] vals;
		
		public NumberField(int...vals)
		{
			this.vals = vals;
		}
	}
	
}
