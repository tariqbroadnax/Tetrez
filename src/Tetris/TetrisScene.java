package Tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;

public class TetrisScene
{
	public final static int ROWS = 20, COLS = 10;

	public final static float ROW_HEIGHT = 1 / 10f,
							  COL_WIDTH = ROW_HEIGHT;
	
	public final static double INIT_MOVE_DELAY = 0.5;
	
	public final static double UNFILL_PERIOD = 0.5,
							   SHAKE_PERIOD = 1.0,
							   COUNT_DOWN_PERIOD = 3.0,
							   COVER_PERIOD = 1.5;
	
	private Block[][][] blocks;

	private Tetromino curr;
	
	private DropMode dropMode;
	
	private enum DropMode {NORMAL, FAST};
	
	private Mode mode;
	
	private enum Mode {WAITING, COUNTING_DOWN, DROPPING, ROTATING, UNFILLING, COVERING, GAME_OVER, UNCOVERING};
	
	private float dx, dy;
	
	private float rotation, targetRotation;

	private double actualMoveDelay;
	
	private double elapsed, shakeElapsed = 1.0,
				   time = 0.0;
	
	private int lines, level, score;
	
	private int filledRowStart, filledRowEnd;

	private boolean holdAvailable;

	private Tetromino hold;

	private LinkedList<Tetromino> nextPieces;
	
	private boolean paused = false;
	
	public TetrisScene()
	{
		blocks = new Block[4][ROWS][COLS];

		nextPieces = new LinkedList<Tetromino>();
		
		curr = Tetromino.random();
		
		for(int i = 0; i < 3; i++)
			nextPieces.add(Tetromino.random());
		
		dropMode = DropMode.NORMAL;
		
		mode = Mode.WAITING;
				
		actualMoveDelay = INIT_MOVE_DELAY;
		
		filledRowStart = filledRowEnd = -1;
	
		holdAvailable = true;
	}
	

	public void start()
	{
		if(mode == Mode.WAITING)
			mode = Mode.COUNTING_DOWN;
		else if(mode == Mode.GAME_OVER)
			mode = Mode.UNCOVERING;
	}

	public void update(double dt)
	{	
		elapsed += dt;
		
		//System.out.println(elapsed + " " + mode);

		if(mode == Mode.COUNTING_DOWN)
		{			
			if(elapsed > COUNT_DOWN_PERIOD)
			{
				elapsed = 0;
				
				mode = Mode.DROPPING;
			}
		}
		else if(mode == Mode.DROPPING)
		{
			time += dt;
			
			dropUpdate();
		}
		else if(mode == Mode.ROTATING)
		{
			time += dt;
			
			updateRotation(dt);
		}
		else if(mode == Mode.UNFILLING)
		{
			time += dt;
			
			unfillUpdate();		
		}
		else if(mode == Mode.COVERING)
		{
			coverUpdate();
		}
		else if(mode == Mode.UNCOVERING)
		{
			uncoverUpdate();
		}
		
		updateShake(dt);
		
		if(curr != null)
			curr.update(dt);
	}
	
	private boolean rotating()
	{
		return rotation != targetRotation;
	}

	private void coverUpdate()
	{
		int halfCols = (int) Math.ceil(COLS / 2.0);
		
		int halfCoveredCols = elapsed < COVER_PERIOD ? 
				(int)(halfCols * elapsed / COVER_PERIOD) :
				halfCols;
		
		int orientation = getOrientation();
				
		for(int c = 0; c < halfCoveredCols; c++)
		{
			int c2 = COLS - 1 - c;
			
			for(int r = 0; r < ROWS; r++)
			{
				if(blocks[orientation][r][c] == null)
					blocks[orientation][r][c] = Block.random();
			}
			
			if(c == c2)
				continue;
			
			for(int r = 0; r < ROWS; r++)
			{
				if(blocks[orientation][r][c2] == null)
					blocks[orientation][r][c2] = Block.random();
			}
		}
		
		if(elapsed > COVER_PERIOD)
		{
			elapsed = 0;
			
			mode = Mode.GAME_OVER;
		}
	}
	
	private void uncoverUpdate()
	{
		int halfCols = (int) Math.floor(COLS / 2.0);
		
		int halfUncoveredCols = elapsed < COVER_PERIOD ? 
				(int)(halfCols * elapsed / COVER_PERIOD) :
				halfCols;
		
		int orientation = getOrientation();
				
		for(int i = 0; i < halfUncoveredCols; i++)
		{
			int c = halfCols - i - 1,
				c2 = halfCols + i;
			
			for(int r = 0; r < ROWS; r++)
				blocks[orientation][r][c] = null;	
			
			if(c == c2)
				continue;
			
			for(int r = 0; r < ROWS; r++)
				blocks[orientation][r][c2] = null;
		}
		
		if(elapsed > COVER_PERIOD)
		{
			elapsed = 0;
			
			mode = Mode.COUNTING_DOWN;
		}
	}
	
	private void dropUpdate()
	{
		double moveDelay = getMoveDelay();
		
		while(elapsed > moveDelay)
		{						
			elapsed -= moveDelay;
						
			curr.moveDown();
			
			int orientation = getOrientation();
			
			if(curr.collides(orientation, this))
			{
				curr.moveUp();
				curr.split(orientation, this);
				
				filledRowStart = filledRowEnd = -1;
				
				for(int row = 0; row < ROWS; row++)
				{
					if(filledRow(orientation, row))
					{
						filledRowEnd = row;
						
						if(filledRowStart == -1)
							filledRowStart = row;
					}
				}
				
				if(filledRowStart != -1)
				{
					curr = null;

					mode = Mode.UNFILLING;
				}
				else
					nextPiece();
			}
			else
				fadeTetromino();
		}
	}
	
	private void unfillUpdate()
	{
		int c2 = COLS / 2;
	
		int orientation = getOrientation();
				
		for(int row = filledRowStart; row <= filledRowEnd; row++)
		{
			for(int col = 0; col < COLS; col++)
			{
				if(elapsed > (c2 - Math.abs(col - c2)) * UNFILL_PERIOD / c2)
					blocks[orientation][row][col] = null;
			}
		}
		
		if(elapsed > UNFILL_PERIOD)
		{
			elapsed -= UNFILL_PERIOD;
			
			for(int i = 0; i < filledRowEnd - filledRowStart + 1; i++)
				dropRow(orientation, filledRowStart);
			
			filledRowStart = filledRowEnd = -1;
			
			mode = Mode.DROPPING;
			
			nextPiece();
			
			dropUpdate();
		}
	}
	
	private double getMoveDelay() 
	{
		return dropMode == DropMode.NORMAL ? 
			   actualMoveDelay : actualMoveDelay / 4;
	}
	
	// if tetromino will stop after one more drop,
	// then make it start fading out
	private void fadeTetromino()
	{
		int orientation = getOrientation();
		double moveDelay = getMoveDelay();

		curr.moveDown();
		
		if(curr.collides(orientation, this))
		{
			double remainder = moveDelay - elapsed;
			
			if(remainder >= 0)
				curr.fade(remainder);
		}
		else
			curr.fade(Tetromino.NO_FADE);
		
		curr.moveUp();
	}
	
	public void dropTetromino()
	{
		if(paused || mode != Mode.DROPPING)
			return;
		
		elapsed = 0;
		
		int orientation = getOrientation();
		
		while(!curr.collides(orientation, this))
			curr.moveDown();
		
		curr.moveUp();
		
		curr.split(orientation, this);
		
		filledRowStart = filledRowEnd = -1;
		
		for(int row = 0; row < ROWS; row++)
		{
			if(filledRow(orientation, row))
			{
				filledRowEnd = row;
				
				if(filledRowStart == -1)
					filledRowStart = row;
			}
		}
		
		if(filledRowStart != -1)
		{
			curr = null;

			mode = Mode.UNFILLING;
		}
		else
			nextPiece();
	}
	
	private void updateShake(double dt)
	{
		shakeElapsed += dt;
		
		if(shakeElapsed < SHAKE_PERIOD)
			dx = (float) (0.01 * Math.sin(2 * Math.PI * shakeElapsed * 4 / SHAKE_PERIOD));			
		else 
			dx = 0;
	}
	
	private void updateRotation(double dt)
	{
		if (rotation < targetRotation)
		{
			rotation += dt * 180;

			if (rotation > targetRotation)
			{
				rotation = targetRotation;
				
				// since tetromino might stop soon in new orientation, 
				// we have to check if it should be fading
				fadeTetromino();
				
				mode = Mode.DROPPING;
			}
		}
		else if(rotation > targetRotation)
		{
			rotation -= dt * 180;

			if (rotation < targetRotation) 
			{
				rotation = targetRotation;
				
				fadeTetromino();	
				
				mode = Mode.DROPPING;
			}
		}
	}
	
	public void setBlock(int orientation, int row, int col, Block block)
	{
		blocks[orientation][row][col] = block;
		
		if(col == 0)
		{
			int prevOrientation = getPrevOrientation(orientation);
			
			blocks[prevOrientation][row][COLS - 1] = block;
		}
		else if(col == COLS - 1)
		{
			int nextOrientation = getNextOrientation(orientation);

			blocks[nextOrientation][row][0] = block;
		}
	}
	
	public Block getBlock(int orientation, int row, int col) {
		return blocks[orientation][row][col];
	}
	
	public void renderCountDown(GLAutoDrawable drawable)
	{
		int width = drawable.getSurfaceWidth(),
			height = drawable.getSurfaceHeight();
		
		Font font = new Font("Press Start 2P", Font.PLAIN, 30);
		
		TextRenderer tr = new TextRenderer(font, false);
	
		tr.beginRendering(width, height);
		
		int count = (int)(COUNT_DOWN_PERIOD - elapsed);
		
		String str = count + "";
		
		Rectangle2D bounds = tr.getBounds(str);
		
		double x = width / 2 - bounds.getWidth() / 2,
			   y = height / 2 - bounds.getHeight() / 2; 
		
		tr.setColor(Color.white);
		tr.draw(str, (int) x, (int) y);
		tr.endRendering();
	}
	
	public void render(GLAutoDrawable drawable)
	{					
		GL2	gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);

		float startx = -COL_WIDTH * COLS / 2.0f,
			  starty = -ROW_HEIGHT * ROWS / 2.0f;
		
		float z = 0.5f;

		gl.glPushMatrix();

		gl.glRotatef(rotation, 0, 1, 0);

		for(int orientation = 0; orientation < 4; orientation++)
		{		
			gl.glPushMatrix();

			gl.glTranslatef(dx, dy, 0);

			renderGrid(gl, startx, starty, z, orientation);

			gl.glPopMatrix();

			gl.glRotatef(90, 0, 1, 0);
		}		
		
		gl.glPopMatrix();
		
		
		// when grid rotates, edges will appear in front of curr tetrominoe if depth not disable

		if(curr != null)
		{
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
			
			curr.render(gl, startx, starty, z, COL_WIDTH, ROW_HEIGHT);
			curr.renderOutline(gl, startx, starty, z, COL_WIDTH, ROW_HEIGHT);
			
			renderDropLocation(gl, startx, starty, z, COL_WIDTH, ROW_HEIGHT);
		}
		
		if(mode == Mode.COUNTING_DOWN) 
		{
			renderCountDown(drawable);
		}
	}

	private void renderGrid(GL2 gl, float x, float y, float z, int orientation)
	{
		renderGridWall(gl, x, y, z, orientation);
		renderCells(gl, x, y, z, orientation);
	}
	
	private void renderGridWall(GL2 gl, float x, float y, float z, int orientation)
	{
//		Color color = null;
//		
//		if(orientation == 0)
//			color = Colors.hex2Rgb("#DC3023");
//		else if(orientation == 1)
//			color = Colors.hex2Rgb("#59ABE3");
//		else if(orientation == 2)
//			color = Colors.hex2Rgb("#26A65B");
//		else if(orientation == 3)
//			color = Colors.hex2Rgb("#FFB94E");
//		
//		gl.glColor3f(color.getRed() / 255f, color.getGreen() / 255f,
//					 color.getBlue() / 255f);
//		
		gl.glBegin(GL2.GL_POLYGON);
		
		gl.glColor3f(0f, 0f, 0f);

		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x + COLS * COL_WIDTH, y, z);
		gl.glVertex3f(x + COLS * COL_WIDTH, y + ROWS * ROW_HEIGHT, z);
		gl.glVertex3f(x, y + ROWS * ROW_HEIGHT, z);
		
		gl.glEnd();
	}
	
	private void renderCells(GL2 gl, float x, float y, float z, int orientation)
	{
		for(int row = 0; row < ROWS; row++)
		{
			for(int col = 0; col < COLS; col++)
			{
				float blockx = x + col * COL_WIDTH,
					  blocky = y + row * ROW_HEIGHT;
				
				if(blocks[orientation][row][col] != null)
					blocks[orientation][row][col].render(gl, blockx, blocky, z, COL_WIDTH, ROW_HEIGHT, 1);
			
				renderCell(gl, blockx, blocky, z, COL_WIDTH, ROW_HEIGHT);
			}
		}
	}

	private void renderCell(GL2 gl, float x, float y, float z, float w, float h)
	{
		gl.glBegin(GL2.GL_LINE_LOOP);
				
		gl.glColor3f(0.5f, 0.5f, 0.5f);

		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x + w, y, z);
		gl.glVertex3f(x + w, y + h, z);
		gl.glVertex3f(x, y + h, z);
		
		gl.glEnd();
	}
	
	private void renderDropLocation(GL2 gl, float x, float y, float z, float w, float h)
	{
		int initRow = curr.row, initCol = curr.col;
		
		int orientation = getOrientation();
		
		while(!curr.collides(orientation, this))
			curr.moveDown();
		curr.moveUp();
		
		curr.renderOutline2(gl, x, y, z, w, h);
		
		curr.row = initRow; curr.col = initCol;
	}
	
	private boolean filledRow(int orientation, int row)
	{
		for(int col = 0; col < COLS; col++)
			if(blocks[orientation][row][col] == null)
				return false;
		return true;
	}
	
	private void dropRow(int orientation, int row)
	{
		lines++;
		
		score += 100;
		
		if(lines % 10 == 0)
		{
			level++;
			
			actualMoveDelay *= 2.0/3;
		}
		
		for(int r = row; r < ROWS - 1; r++)
			for(int col = 0; col < COLS; col++)
				setBlock(orientation, r, col, getBlock(orientation, r + 1, col));
		
		for(int col = 0; col < COLS; col++)
			setBlock(orientation, ROWS - 1, col, null);
	}
	
	private int getPrevOrientation(int orientation)
	{
		int prevOrientation = orientation - 1;
		
		if(prevOrientation < 0) prevOrientation += 4;
		
		return prevOrientation;
	}
	
	private int getNextOrientation(int orientation)
	{
		int nextOrientation = orientation + 1;
		
		nextOrientation %= 4;
		
		return nextOrientation;
	}
	
	private int getOrientation() 
	{
		int r = (int) -rotation;
		
		while(r < 0)
			r += 360;
		
		r %= 360;
				
		return r / 90;
	}

	private void rotate(float dr) 
	{
		targetRotation = rotation + dr;		
		
		mode = Mode.ROTATING;		
	}
	
	public boolean canRotateLeft()
	{
		int prevOrientation = getPrevOrientation(getOrientation());
		
		return !curr.collides(prevOrientation, this);
	}
	
	public boolean canRotateRight()
	{
		int nextOrientation = getNextOrientation(getOrientation());
		
		return !curr.collides(nextOrientation, this);
	}
	
	public void rotateLeft() 
	{
		if(paused || mode != Mode.DROPPING && 
					 mode != Mode.ROTATING)
		return;
		
		if(canRotateLeft())
		{
			rotate(90);
		
			// stop fading if rotating
			curr.fade(Tetromino.NO_FADE);
		}
		else
			shake();
	}
	
	public void rotateRight() 
	{
		if(paused || mode != Mode.DROPPING && 
					 mode != Mode.ROTATING)
			return;
		
		if(canRotateRight())
		{
			rotate(-90);
			
			// stop fading if rotating
			curr.fade(Tetromino.NO_FADE);
		}
		else
			shake();
	}
	
	public void moveTetrominoLeft() 
	{
		if(paused || mode != Mode.DROPPING)
			return;
		
		curr.moveLeft();
		
		int orientation = getOrientation();
		
		if(curr.collides(orientation, this))
			curr.moveRight();
		else
		{
			// since tetromino might not stop soon after moving,
			// we have to check if it should be fading
			fadeTetromino();
		}
	}
	
	public void moveTetrominoRight()
	{
		if(paused || mode != Mode.DROPPING)
			return;
		
		curr.moveRight();
		
		int orientation = getOrientation();
		
		if(curr.collides(orientation, this))
			curr.moveLeft();
		else
			fadeTetromino();
	}
	
	public void rotateTetromino()
	{
		if(paused || mode != Mode.DROPPING &&
					 mode != Mode.ROTATING)
			return;
		
		curr.rotateRight();
		
		int orientation = getOrientation();
		
		if(curr.collides(orientation, this))
			curr.rotateLeft();
		else
		{
			// since tetromino might not stop soon after rotating,
			// we have to check if it should be fading
			fadeTetromino();
		}
	}
	
	public void shake() {
		shakeElapsed = 0;
	}
	
	public boolean isRotating() {
		return rotation != targetRotation;
	}
	
	public void setFastDrop() 
	{
		if(dropMode != DropMode.FAST)
		{
			dropMode = DropMode.FAST;
		
			// prevents sudden drop
			// if fps gets rly low, this could lead to unexpected behaviors
			elapsed = 0;
		}
	}
	
	public void setNormalDrop() {
		dropMode = DropMode.NORMAL;
	}

	// FIXME fix this
	public Tetromino getNextPiece(int index) {
		return nextPieces.get(index);
	}

	public Tetromino getHoldPiece() {
		return hold;
	}

	public boolean isHoldPieceAvailable() {
		return holdAvailable;
	}

	public void hold()
	{
		if(paused || mode != Mode.ROTATING &&
					 mode != Mode.DROPPING)
			return;
		
		if(holdAvailable)
		{
			if(hold == null)
			{
				hold = curr;
				
				nextPiece();
			}
			else
			{
				Tetromino temp = hold;
				
				hold = curr;
				curr = temp;
			}
			
			hold.row = 10; hold.col = 4;
			
			holdAvailable = false;
		}
	}

	private void nextPiece()
	{
		curr = nextPieces.pop();
		
		nextPieces.add(Tetromino.random());
		
		holdAvailable = true;
		
		if(curr.collides(getOrientation(), this))
		{
			mode = Mode.COVERING;
			
			elapsed = 0;
		}
	}

	public int getScore()
	{
		return score;
	}

	public boolean isPaused()
	{
		return paused;
	}

	public void pause()
	{
		if(paused)
			return;
		
		if(mode != Mode.WAITING &&
		   mode != Mode.GAME_OVER &&
		   mode != Mode.COVERING)
		{
			paused = true;
		}
	}
	
	public void unpause()
	{
		if(!paused)
			return;
		
		if(mode != Mode.UNCOVERING)
		{
			mode = Mode.COUNTING_DOWN;
		}
		
		paused = false;
	}

	public int getLines() {
		return lines;
	}

	public int getLevel() {
		return level;
	}
	
	public double getTime() {
		return time;
	}
}
