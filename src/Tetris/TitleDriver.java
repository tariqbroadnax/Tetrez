package Tetris;

import javax.swing.JFrame;

public class TitleDriver
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		
		TitleScreen screen = new TitleScreen();
		
		screen.setDisplayDebugInformation(true);
		
		frame.setContentPane(screen);
		
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
