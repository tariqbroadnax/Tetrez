package Tetris;

public class Tetris3D 
{	
	private UI ui;
	
	private TetrisScene scene;
	
	public Tetris3D()
	{
		scene = new TetrisScene();
	
		ui = new UI(this);
	}
	
	public void start()
	{
		ui.start();
	}
	
	public void stop()
	{
		ui.stop();
	}
	
	public TetrisScene getScene() {
		return scene;
	}
}
