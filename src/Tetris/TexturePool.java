package Tetris;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TexturePool
{
	private static HashMap<String, Texture> map = new HashMap<String, Texture>();
	
	private static GLContext context;

	public static void setGLContext(GLContext context)
	{
		TexturePool.context = context;
		
		for(String path : map.keySet())
			request(path);
	}
	
	public static void request(String path)
	{
		if(context == null)
		{
			map.put(path, null);
		}
		else
		{
			File file = new File(path);
			
			try
			{
				Texture texture = TextureIO.newTexture(file, true);
			
				texture.bind(context.getGL());
				
				map.put(path, texture);
			} 
			catch (GLException | IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static Texture get(String path)
	{
		return map.get(path);
	}
}
