import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Player implements Serializable{
	
	private ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	private int parentWidth;
	private int parentHeight;
	
	private boolean active = true;
	private int width;
	private int height;
	private Point position;
	private transient BufferedImage img;
	private int playerspeed = 10;
	public static int lives = 3;
	
	
	public Player(int parentWidth, int parentHeight)
	{
		
		//this code loads the image for the player. The try catches are used to catch and file io error
		setImg();
		
		//the height and width of the player are set via the image size
		this.height = img.getHeight()/2;
		this.width = img.getWidth()/2;
		
		//this is the height and width of the game screen, passed through from or main application
		this.parentHeight = parentHeight;
		this.parentWidth = parentWidth;
		
		//we set the player to initially appear in the middle (Horizontally) 
		//and at the bottom (vertically) of the game screen 
		this.position = new Point(parentWidth/2,parentHeight - this.height);
		
		//finally we create the bullet list for the player
//		bulletList = new ArrayList<Bullet>();
		
	}
	
	public void setImg(){
		try{
			img = ImageIO.read(getClass().getResource("/Space-invaders.png"));
			System.out.println("***Spaceship reread***");
			Iterator<Bullet> iterator = bulletList.iterator();
			while (iterator.hasNext()) {
			    Bullet b = iterator.next();
			    if(b!=null && b.isActive()){
			    	b.setImg();
			    }
			}
		} catch (IOException e){
			System.out.println("***CAN'T READ SPACESHIP***");
			e.printStackTrace();
		}
	}
	
	//this method draws the player class to the screen
	public void draw(Graphics g) {
		
		//draws the player image in its current position
		g.drawImage(img, position.x, position.y, width, height, null);
		
		//this code uses an iterator to run through the bullets, if a bullet has bee maked as inactive it is removed,
		//otherwise the bullets move and draw methods are called to update it on the screen
		Iterator<Bullet> iterator = bulletList.iterator();
		while (iterator.hasNext()) {
		    Bullet b = iterator.next();
		    
		    if(b!=null && b.isActive()){
		    	b.move();
		    	b.draw(g);
		    }
		    else{
		    	iterator.remove();
		    }
		}
		
	}
	
	//the players move method.
	public void move(int x){
		if(active){
			//these lines of code keep the player within the gamescreen (parent width)
			//if the current position is less than 0 we bump the player back to zero
			if(position.x<0){
				position.x =0; 
			}
			//if the current position + the players width is greater than the gamescreen 
			//width we bump the player back to just before ots right hand side hits the right hand side of the parent
			else if(position.x>parentWidth-width){
				position.x=parentWidth - width;
			}
			//other wise its fine to moves based on the direction passed in (x) and the playerspeed
			else{
				this.position.x += (x * playerspeed);
			}
		}
		//By only moving the player when active it prevents invisible moving from the death screen

	}
	
	//adds a new bullet to the bullet list. The parameters passed through are where we want the ullet to appear. In this case in the top-middle of the player
	public void fire(){
		bulletList.add(new Bullet(new Point(position.x+(width/2), position.y),0,20,0));
	}
	
	//this returns a specific player bullet
	public Bullet getBullet(int i){
		return bulletList.get(i);
	}
	
	public Rectangle getBulletBounds(int i){
		return bulletList.get(i).getBounds();
	}
	
	//this returns the current number of bullets
	public int getBulletCount(){
		return bulletList.size();
	}
	//removes a life from the player
	public void removeLife(){
		lives--;
		System.out.println("You've been hit!");
		if(lives<=0){
			active = false;
			position.x=GamePanel.gameWidth/2;
		}
	}
	
	public Rectangle getBounds(){
		return new Rectangle(position.x,position.y,width,height);
	}
	
	public int getGameHeight(){
		return parentHeight;
	}
	
	public boolean isActive(){
		return active;
	}

	public void setActive() {
		active=true;
	}
	
	

}
