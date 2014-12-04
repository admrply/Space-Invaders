import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Bullet implements Serializable{
	
	private Point position;
	private int height, width;
	private transient BufferedImage img;
	//Transient so that it doesn't attempt to get written during serialisation
	private int speed;
	private boolean active;
	private int bulletType;
	//This determines whether it was fired by a player or an enemy


	public Bullet(Point playerPosition, int type, int speed, int objectFiredFromHeight){
		//Type 0 for player bullet, type 1 for enemy bullet
		//objectFiredFromHeight allows the bullets to fall from the bottom of the enemy instead of the top
		this.speed = speed;
		this.bulletType = type;
		setImg();
		//same as the player class this simply loads the image for the bullet.
		//It is a method simply for code reuse when loading from serialisation

		height = img.getHeight();
		width = img.getWidth();
		//Sets bullet size

		switch(bulletType){
			//Checks for the type of bullet
			case 0: position = new Point(playerPosition.x - (width/2), playerPosition.y);
					//If it's a player bullet then there is no height adjustment. It fires from the top
					//of the player
					active = true;
					break;
			case 1: position = new Point(playerPosition.x - (width/2), playerPosition.y + objectFiredFromHeight);
					//If it's an enemy, it's adjusted to fire from the bottom
					active = true;
					break;
		}
		//We have to do a little offset with the bullet image so our bullet appears in the
		//(horizontal) centre of our player/enemy. That's what the (width/2) is for.
	}
	
	public void setImg(){
		try {
			switch(bulletType){
				case 0: img = ImageIO.read(getClass().getResource("/playerbullet.png"));
						break;
				case 1: img = ImageIO.read(getClass().getResource("/enemybullet.png"));
						break;
			}
			//Loads a red or a green bullet depending on the type of bullet.
		} catch (IOException e) {
			System.out.println("**CAN'T READ BULLET**");
			e.printStackTrace();
		}
	}

	//draws the bullet
	public void draw(Graphics g) {
		g.drawImage(img, position.x, position.y, width, height, null);
		//Draws the bullet image
	}

	public void move(){
		if(!GamePanel.paused){
			//If the game is not paused then the bullets will move
			switch(bulletType){
				case 0:	if(position.y > 0){
							position.y-=speed;
							//If the player bullet hasn't gone off the top of the screen then move it higher
						}
						else if(position.y<0){
							destroy();
							//If it has gone higher, destroy it
						} 
						break;
						
				case 1: if(position.y<GamePanel.gameHeight){
							position.y+=speed;
							//If the enemy bullet hasn't reached the bottom of the screen, move it lower
						}
						else if(position.y>GamePanel.gameHeight){
							destroy();
							//If it has, destroy it
						}
						break;
			}
		}

	}

	//sets the bullet as inactive
	public void destroy(){
		active = false;
	}

	//returns whether the bullet is active or not
	public boolean isActive(){
		return active;
	}

	//returns the bullets current position
	public Point getPosition() {
		return position;
	}

	//returns the bounds of the bullet as a rectange for collision detection
	public Rectangle getBounds(){
		return new Rectangle(position.x,position.y,width,height);
	}



}
