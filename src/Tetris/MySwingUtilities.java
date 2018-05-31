package Tetris;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class MySwingUtilities
{
	public static void installFont(String path)
	{
		try 
		{
		     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(path)));
		} 
		catch (IOException|FontFormatException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			System.exit(1);
		}
	}
	
	public static void drawString(Graphics g, String text, int x, int y) 
	{
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
}
