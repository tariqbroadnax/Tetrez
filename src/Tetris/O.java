package Tetris;
import java.awt.Color;

class O extends Tetromino
{
	public O(int row, int col) {
		super(row, col, Color.YELLOW);
	}

	@Override
	public int[][] indices() 
	{
		int[][] indices = new int[4][2];
		
		indices[0][0] = 0; indices[0][1] = 0;
		indices[1][0] = 1; indices[1][1] = 0;
		indices[2][0] = 0; indices[2][1] = 1;
		indices[3][0] = 1; indices[3][1] = 1;
		
		return indices;
	}
}