package Tetris;

import javax.swing.JFrame;

public class MainMenuDriver
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		
		MainMenu screen = new MainMenu();
		
		frame.setContentPane(screen);
		
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
