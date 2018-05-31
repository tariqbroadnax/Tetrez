package Tetris;

import javax.swing.JFrame;

public class DemoDriver
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		
		DemoScreen screen = new DemoScreen(new TetrisScene());

		frame.setContentPane(screen);
		
		frame.setSize(600, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
