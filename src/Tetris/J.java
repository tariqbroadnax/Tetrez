package Tetris;
import java.awt.Color;

class J extends Tetromino
{
	public J(int row, int col) {
		super(row, col, Color.BLUE);
	}

	@Override
	public int[][] indices() 
	{
		int[][] indices = new int[4][2];
		
		indices[0][0] = 0; indices[0][1] = 0;
		
		if(orientation == 0)
		{
			indices[1][0] = -1; indices[1][1] = 0;
			indices[2][0] = -1; indices[2][1] = 1;
			indices[3][0] = 1; indices[3][1] = 0;
		}
		else if(orientation == 1)
		{
			indices[1][0] = 0; indices[1][1] = -1;
			indices[2][0] = 0; indices[2][1] = 1;
			indices[3][0] = 1; indices[3][1] = 1;
		}
		else if(orientation == 2)
		{
			indices[1][0] = -1; indices[1][1] = 0;
			indices[2][0] = 1; indices[2][1] = 0;
			indices[3][0] = 1; indices[3][1] = -1;
		}
		else
		{
			indices[1][0] = 0; indices[1][1] = -1;
			indices[2][0] = 0; indices[2][1] = 1;
			indices[3][0] = -1; indices[3][1] = -1;
		}
		
		return indices;
	}
}