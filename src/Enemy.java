import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Enemy implements Serializable{
	private int parentWidth;
	@SuppressWarnings("unused")
	private int parentHeight;
	
	private boolean active = true;
	private int width;
	private int height;
	protected Point position;
	protected transient BufferedImage img;
	protected int enemyspeed = GamePanel.level;
	private boolean directionRight = true;
	
	protected int lives = 1;
	protected int value;
	
	
	public Enemy(int parentWidth, int parentHeight, int x, int y)
	{
		
		//this code loads the image for the player. The try catches are used to catch and file io error
		setImg();
		
		//the height and width of the player are set via the image size
		if((this instanceof Meteor)&&!GamePanel.konamiActive){
			this.height = img.getHeight();
			this.width = img.getWidth();
			//Makes cats big
		}
		else{
			this.height = img.getHeight()/2;
			this.width = img.getWidth()/2;
			//Scales all enemies
		}
		
		//this is the height and width of the game screen, passed through from or main application
		this.parentHeight = parentHeight;
		this.parentWidth = parentWidth;
		
		//we set the player to initially appear in the middle (Horizontally) 
		//and at the bottom (vertically) of the game screen 
		this.position = new Point(x,y);
		
		//finally we create the bullet list for the player
		
	}
	
	public void setImg() {
		try{
			img = ImageIO.read(getClass().getResource("/mercurian.jpg"));
			System.out.println("***Enemy reread***");
		} catch (IOException e){
			System.out.println("***CAN'T READ ENEMY***");
			e.printStackTrace();
		}
		
	}
	
	
	//this method draws the player class to the screen
	public void draw(Graphics g) {
		
		//draws the player image in its current position
			g.drawImage(img, position.x, position.y, width, height, null);
		
		//this code uses an iterator to run through the bullets, if a bullet has bee maked as inactive it is removed,
		//otherwise the bullets move and draw methods are called to update it on the screen
		
	}
	
	//the players move method.
	public void move(){
		if(GamePanel.paused){}
		else{
			if(directionRight){
				this.position.x = this.position.x+enemyspeed;
			}
			else{
				this.position.x = this.position.x-enemyspeed;
			}
		}		

	}
	
	public boolean edgeHit(){
		if(this.position.x<=0){
			return true;
		}
		else if(this.position.x>parentWidth-width){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Rectangle getBounds(){
		return new Rectangle(position.x,position.y,width,height);
	}
	
	public Point getPosition(){
		return position;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void dropPosition() {
		this.position.y+=30;
	}
	public void changeDirection(){
		directionRight=!directionRight;
	}


	public boolean isActive(){
		return active;
	}
	
	public void destroy(){
		active = false;
	}
	

	//removes a life from the player
	public void removeLife(){
		lives--;
		if (lives<=0){
			GamePanel.score = GamePanel.score + value;
			active = false;
		}
	}
	

}