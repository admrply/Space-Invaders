import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


@SuppressWarnings("serial")
public class InvadersGame extends JFrame  {

	public static int width = 1024;
	public static int height = 768;
	static BufferedImage icon;
	
	public static void main(String[] args) {
		InvadersGame mp = new InvadersGame();
		mp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mp.setSize(width,height);
		mp.setResizable(false);
		mp.setTitle("Space Invaders");
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		//Creates a new transparent image
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		//Creates a new cursor with the newly created transparent image
//		mp.getContentPane().setCursor(blankCursor);
		//Sets the cursor to be this transparent image when over the JPanel
		
		mp.setVisible(true);
		mp.init();
		mp.setIconImage(icon);
		mp.run();

	}



	
	private GamePanel panel;


	//sets up the GUI and Game Components
	private void init() {
		Container pane = this.getContentPane();
		panel = new GamePanel();
		
		try {
			icon = ImageIO.read(this.getClass().getResource("/martian.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pane.add(panel, BorderLayout.CENTER);
		//adds our game panel as the listener for keyevents
		this.addKeyListener(panel);
		
		
	}
	
	
	private void run() {
		//get the available space to draw, ie the size of the game screen without the borders
		int panelWidth  = this.getContentPane().getWidth(); 
		int panelHeight = this.getContentPane().getHeight();
		
		
		
		//starts the game
		panel.startGame(panelWidth, panelHeight);
		
		
	}


	

}
