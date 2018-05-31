package Tetris;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;

public class Block
{	
	private Color color;
	
	private static BufferedImage img;
	
	static 
	{
		try
		{
			File file = new File("tetrominoes.png");
			
			img = ImageIO.read(file);
			
			TexturePool.request("tetrominoes.png");
		} 
		catch (GLException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Block()
	{
		color = Color.RED;
	}
	
	public Block(Color color)
	{
		this.color = color;
	}
	
	public void paint(Graphics2D g, double x, double y, double width, double height)
	{
		int i = 0;
		
		if(color == Color.CYAN)
			i = 0;
		else if(color == Color.YELLOW)
			i = 1;
		else if(color == Color.MAGENTA)
			i = 2;
		else if(color == Color.GREEN)
			i = 3;
		else if(color == Color.RED)
			i = 4;
		else if(color == Color.BLUE)
			i = 5;
		else
			i = 6;
		
		int imgWidth = img.getWidth(),
			imgHeight = img.getHeight();
		
		g = (Graphics2D) g.create();
		
		g.translate(x, y);
		g.scale(width, height);
		g.drawImage(img, 0, 0, 1, 1, i * imgWidth / 7, 0, 
													  (i + 1) * imgWidth / 7, imgHeight, null);
	}
	
	public void render(GL2 gl, float x, float y, float z, float w, float h, float a)
	{
		Texture texture = TexturePool.get("tetrominoes.png");
		
		double i = 0;
		
		if(color == Color.CYAN)
			i = 0;
		else if(color == Color.YELLOW)
			i = 1;
		else if(color == Color.MAGENTA)
			i = 2;
		else if(color == Color.GREEN)
			i = 3;
		else if(color == Color.RED)
			i = 4;
		else if(color == Color.BLUE)
			i = 5;
		else
			i = 6;
		
//		gl.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
//					 color.getAlpha() / 255f);



		texture.bind(gl);
		texture.enable(gl);
		gl.glColor4f(1, 1, 1, a);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2d(i/7, 0);
		gl.glVertex3f(x, y, z);
		gl.glTexCoord2d((i + 1)/7, 0);
		gl.glVertex3f(x + w, y, z);
		gl.glTexCoord2d((i + 1)/7, 1);
		gl.glVertex3f(x + w, y + h, z);
		gl.glTexCoord2d(i/7, 1);
		gl.glVertex3f(x, y + h, z);
		
		gl.glEnd();
		
		texture.disable(gl);
	}

	public static Block random()
	{
		int i = (int)(Math.random() * 7);

		if(i == 0)
			return new Block(Color.CYAN);
		else if(i == 1)
			return new Block(Color.YELLOW);
		else if(i == 2)
			return new Block(Color.MAGENTA);
		else if(i == 3)
			return new Block(Color.GREEN);
		else if(i == 4)
			return new Block(Color.RED);
		else if(i == 5)
			return new Block(Color.BLUE);
		else if(i == 6)
			return new Block(Color.ORANGE);
		else 
			return new Block(Color.BLACK);
	}
}
