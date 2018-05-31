package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class ScenePane extends JPanel implements GLEventListener, KeyListener
{
	private static final long serialVersionUID = 1L;

	public static final GLU	GLU = new GLU();
	public static final GLUT GLUT = new GLUT();
	
	private static final int TARGET_FPS = 60;

	private TetrisScene scene;

	private FPSAnimator animator;

	private TopBar topBar;
	
	private LeftBar leftBar;
	
	private NextPanel nextPanel;
	
	private MyCanvas canvas;

	private long lastUpdate;

	public ScenePane(TetrisScene scene)
	{
		this.scene = scene;

		topBar = new TopBar(scene);
		leftBar = new LeftBar(scene);
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new MyCanvas(capabilities);

		animator = new FPSAnimator(canvas, TARGET_FPS);
		
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.setFocusable(true);
		canvas.requestFocusInWindow();
		canvas.setPreferredSize(new Dimension(240, 440));
	
		nextPanel = new NextPanel(scene);
				
		setLayout(new GridBagLayout());

		setBackground(Color.BLACK);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.insets.set(5, 5, 5, 5);
		
		gbc.gridx = 1; gbc.gridy = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0; gbc.weighty = 0;
		add(leftBar, gbc);
		
		gbc.gridx = 2; gbc.gridy = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0; gbc.weighty = 0;
		add(canvas, gbc);
		
		gbc.gridx = 3; gbc.gridy = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0; gbc.weighty = 0;
		add(nextPanel, gbc);

		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1; gbc.weighty = 1;
		add(createTransparentJPanel(), gbc);
		
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1; gbc.weighty = 1;
		add(createTransparentJPanel(), gbc);
		
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1; gbc.weighty = 1;
		add(createTransparentJPanel(), gbc);
		
		gbc.gridx = 4; gbc.gridy = 1;
		gbc.gridwidth = 3; gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1; gbc.weighty = 1;
		add(createTransparentJPanel(), gbc);
		
		canvas.setBorder(BorderFactory.createLineBorder(Color.white));
	}
	
	private JPanel createTransparentJPanel()
	{
		JPanel panel = new JPanel();
		
		panel.setOpaque(false);
		
		return panel;
	}

	public void processKeyEvent(KeyEvent e)
	{
		canvas.processKeyEvent(e);
	}
	
	public void start()
	{
		animator.start();
		
		lastUpdate = System.nanoTime();
		
		scene.start();
	}
	
	public void addNotify()
	{
		super.addNotify();
	
		requestFocusInWindow();
	}
	
	public void removeNotify()
	{
		super.removeNotify();
		
		animator.stop();
	}
	
	@Override
	public void init(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		TexturePool.setGLContext(gl.getContext());
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		if(animator.isAnimating())
			update();		

		render(drawable);		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
	{
		GL2	gl = drawable.getGL().getGL2();

		TexturePool.setGLContext(gl.getContext());
	}
	
	private void update()
	{
		long now = System.nanoTime();
				
		double dt = (now - lastUpdate) / 1.0e9;
		
		scene.update(dt);
		
		topBar.update(dt);
		leftBar.update(dt);
		nextPanel.update(dt);
		
		lastUpdate = now;
	}
	

	private void render(GLAutoDrawable drawable)
	{
		GL2	gl = drawable.getGL().getGL2();

		resetProjection(gl);

		clearScreen(gl);
			
		scene.render(drawable);
	}
	
	private void clearScreen(GL2 gl)
	{
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}
	
	private void resetProjection(GL2 gl)
	{
		GLU glu = new GLU();
		
		gl.glMatrixMode(GL2.GL_PROJECTION);			
		gl.glLoadIdentity();				
		glu.gluOrtho2D(-0.6, 0.6, -1.1, 1.1);
	}
	
	private class MyCanvas extends GLJPanel
	{
		private static final long serialVersionUID = 1L;

		public MyCanvas(GLCapabilities capabilities)
		{
			super(capabilities);
		}

		public void processKeyEvent(KeyEvent e)
		{
			super.processKeyEvent(e);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_DOWN)
		{
			//scene.moveTetrominoDown();
			
			scene.setFastDrop();
		}
		
		if(scene.isRotating())
			return;
		
		if(code == KeyEvent.VK_LEFT)
			scene.moveTetrominoLeft();
		else if(code == KeyEvent.VK_RIGHT)
			scene.moveTetrominoRight();
		else if(code == KeyEvent.VK_UP)
			scene.rotateTetromino();
		else if(code == KeyEvent.VK_SPACE)
			scene.dropTetromino();
		else if(code == KeyEvent.VK_A)
			scene.rotateLeft();
		else if(code == KeyEvent.VK_D)
			scene.rotateRight();
		else if(code == KeyEvent.VK_H)
			scene.hold();
		else if(code == KeyEvent.VK_SHIFT)
			nextPanel.toggle();
		else if(code == KeyEvent.VK_ESCAPE)
			System.exit(1);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();

		if(code == KeyEvent.VK_DOWN)
			scene.setNormalDrop();
	}

	@Override
	public void keyTyped(KeyEvent e){}
}
