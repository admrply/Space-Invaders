import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


@SuppressWarnings("serial")
public class Meteor extends Enemy{
	
	@SuppressWarnings("unused")
	private int width;
	private int height;
	private int speed = 4+GamePanel.level;

	public Meteor(int parentWidth, int parentHeight, int x, int y) {
		super(parentWidth, parentHeight, x, y);
		lives = 1;
		setImg();
		value = 10;
		this.height = img.getHeight();
		this.width = img.getWidth();
	}
	
	@Override
	public void setImg(){
		try {
			if(GamePanel.konamiActive){
				img = ImageIO.read(getClass().getResource("/konamicat.png"));
			}
			else{
				img = ImageIO.read(getClass().getResource("/meteor.png"));
			}
			System.out.println("**METEOR-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ METEOR**");
			e.printStackTrace();
		}
	}
	
	@Override
	public void draw(Graphics g){
		super.draw(g);
		if(this.position.y>GamePanel.gameHeight+this.height){
			this.destroy();
			System.out.println("The meteor hit the ground");
		}
	}
	
	
	@Override
	public void move(){
		if(GamePanel.paused){}
		else{
			this.position.y += speed;
		}
	}

	@Override
	public boolean edgeHit(){
		if(GamePanel.konamiActive){
			return true;
		}
		else{
			return false;
		}
	}
	@Override
	public void dropPosition(){}
}
