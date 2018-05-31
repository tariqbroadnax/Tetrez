package Tetris;

import java.awt.Color;

public class Colors
{
	public final static Color NAVY_BLUE = hex2Rgb("#003171"),
							  PICTON_BLUE = hex2Rgb("#22A7F0");
	
	public static Color hex2Rgb(String colorStr) 
	{
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
	
	public static Color getWithAlpha(Color color, float a)
	{
		return new Color(color.getRed()/255f, color.getGreen()/255f,
						 color.getBlue()/255f, a);	
	}
}
