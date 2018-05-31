package Tetris;
import java.awt.Color;

class I extends Tetromino
{
	public I(int row, int col) {
		super(row, col, Color.CYAN);
	}

	@Override
	public int[][] indices() 
	{
		int[][] indices = new int[4][2];
		
		indices[0][0] = 0; indices[0][1] = 0;
		
		if(orientation == 0)
		{
			indices[1][0] = -1; indices[1][1] = 0;
			indices[2][0] = 1; indices[2][1] = 0;
			indices[3][0] = 2; indices[3][1] = 0;
		}
		else if(orientation == 3)
		{
			indices[1][0] = 0; indices[1][1] = -1;
			indices[2][0] = 0; indices[2][1] = 1;
			indices[3][0] = 0; indices[3][1] = 2;
		}
		else if(orientation == 2)
		{
			indices[1][0] = -2; indices[1][1] = 0;
			indices[2][0] = -1; indices[2][1] = 0;
			indices[3][0] = 1; indices[3][1] = 0;
		}
		else
		{
			indices[1][0] = 0; indices[1][1] = -2;
			indices[2][0] = 0; indices[2][1] = -1;
			indices[3][0] = 0; indices[3][1] = 1;
		}
		
		return indices;
	}
}