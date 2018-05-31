package Tetris;
import java.awt.Color;
import java.awt.Graphics2D;

import com.jogamp.opengl.GL2;

public abstract class Tetromino
{
	public static final int MAX_ROWS = 4,
							MAX_COLS = 4;
	
	public static final double NO_FADE = -1;
	
	protected int row, col;
	
	private Color color;
	
	protected int orientation;
	
	private double fadeElapsed, fadeSpan;
	
	public static Tetromino random()
	{
		int i = (int)(Math.random() * 7);
		
//		i = 0;
		
		if(i == 0)
			return new I(18, 4);
		else if(i == 1)
			return new O(18, 4);
		else if(i == 2)
			return new T(18, 5);
		else if(i == 3)
			return new S(18, 5);
		else if(i == 4)
			return new Z(18, 5);
		else if(i == 5)
			return new J(18, 5);
		else
			return new L(18, 5);
	}
	
	public Tetromino(int row, int col, Color color)
	{
		this.row = row;
		this.col = col;
		
		this.color = color;
		
		fadeSpan = NO_FADE;
	}
	
	public abstract int[][] indices();
	
	public void update(double dt)
	{
		fadeElapsed += dt;
	}
	
	public void render(GL2 gl, float x, float y, float z, float w, float h)
	{
		float a = fadeSpan == NO_FADE ? 1 :
				  fadeElapsed > fadeSpan ? 0 : (float) (fadeElapsed / fadeSpan);
				
		int[][] indices = indices();
		
		Block block = new Block(color);
				
		for(int i = 0; i < indices.length; i++)
		{
			int blockRow = row + indices[i][1],
				blockCol = col + indices[i][0];
			
			float blockx = x + blockCol * w,
				  blocky = y + blockRow * h;
							
			block.render(gl, blockx, blocky, z, w, h, a);
		}
	}
	
	private void _renderOutline(GL2 gl, float x, float y, float z, float w, float h)
	{
		int[][] indices = indices();
		
		for(int i = 0; i < indices.length; i++)
		{
			int blockRow = row + indices[i][1],
				blockCol = col + indices[i][0];
			
			float blockx = x + blockCol * w,
				  blocky = y + blockRow * h;
			
			gl.glBegin(GL2.GL_LINE_LOOP);
			
			gl.glVertex3f(blockx, blocky, z);
			gl.glVertex3f(blockx + w, blocky, z);
			gl.glVertex3f(blockx + w, blocky + h, z);
			gl.glVertex3f(blockx, blocky + h, z);
			
			gl.glEnd();
		}
	}

	public void renderOutline(GL2 gl, float x, float y, float z, float w, float h)
	{
		gl.glColor3f(0.5f, 0.5f, 0.5f);

		_renderOutline(gl, x, y, z, w, h);
	}

	public void renderOutline2(GL2 gl, float x, float y, float z, float w, float h)
	{
		gl.glColor3f(1f, 1f, 1f);

		_renderOutline(gl, x, y, z, w, h);
	}
	
	public void split(int orientation, TetrisScene scene)
	{
		int[][] indices = indices();

		Block block = new Block(color);
				
		for(int i = 0; i < indices.length; i++)
		{
			int r = row + indices[i][1],
				c = col + indices[i][0];
			
			scene.setBlock(orientation, r, c, block);
		}
	}
	
	public void rotateLeft()
	{
		orientation--;
		if(orientation < 0)
			orientation = 3;
		// Audio.play("sounds/block-rotate.wav", false);
	}
	
	public void rotateRight()
	{
		orientation++;
		orientation %= 4;
		//Audio.play("sounds/block-rotate.wav", false);
	}
	
	public void moveUp() { row++; }
	
	public void moveDown() { row--; }
	
	public void moveLeft() { col--; }
	
	public void moveRight() { col++; }
	
	public boolean collides(int orientation, TetrisScene scene)
	{
		int[][] indices = indices();
						
		int rows = TetrisScene.ROWS, cols = TetrisScene.COLS;
		
		for(int i = 0; i < indices.length; i++)
		{
			int pieceRow = row + indices[i][1],
				pieceCol = col + indices[i][0];
							
			if(pieceRow >= rows)
				continue;
			else if(pieceCol < 0 || pieceCol >= cols ||
					pieceRow < 0 || scene.getBlock(orientation, pieceRow, pieceCol) != null)
				return true;
		}
		
		return false;
	}
	
	public void fade(double fadeSpan) 
	{
		this.fadeSpan = fadeSpan;
		
		fadeElapsed = 0;
	}
	
	public int rows()
	{
		int[][] indices = indices();
		
		int minRow = Integer.MAX_VALUE,
			maxRow = Integer.MIN_VALUE;
		
		for(int[] i : indices)
		{
			int row = i[1];
			
			if(row < minRow)
				minRow = row;
			if(row > maxRow)
				maxRow = row;
		}
		
		return maxRow - minRow + 1;
	}

	public int cols()
	{
		int[][] indices = indices();
		
		int minCol = Integer.MAX_VALUE,
			maxCol = Integer.MIN_VALUE;
		
		for(int[] i : indices)
		{
			int col = i[0];
			
			if(col < minCol)
				minCol = col;
			if(col > maxCol)
				maxCol = col;
		}
		
		return maxCol - minCol + 1;
	}

	public void paint(Graphics2D g, double x, double y, double width, double height)
	{
		int[][] indices = indices();

		int minRow = Integer.MAX_VALUE,
			maxRow = Integer.MIN_VALUE;
		int minCol = Integer.MAX_VALUE,
			maxCol = Integer.MIN_VALUE;
		
		for(int[] i : indices)
		{
			int row = i[1];
			int col = i[0];

			if(row < minRow)
				minRow = row;
			if(row > maxRow)
				maxRow = row;
			
			if(col < minCol)
				minCol = col;
			if(col > maxCol)
				maxCol = col;
		}
		
		int rows = maxRow - minRow + 1,
			cols = maxCol - minCol + 1;
		
		double rowHeight = height / rows,
			   colWidth = width / cols;
		
		Block block = new Block(color);

		for(int[] i : indices)
		{
			int row = i[1];
			int col = i[0];

			block.paint(g, x + (col - minCol) * colWidth, y + (row - minRow) * rowHeight, colWidth, rowHeight);
		}
	}

	public Color getColor()
	{
		return color;
	}
}