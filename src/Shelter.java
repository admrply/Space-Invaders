import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class Shelter {

	private int width;
	private int height;
	private int hitHeight;
	private int hitWidth;
	private transient BufferedImage img;
	private transient BufferedImage hitImg;
	private Point position;
	private boolean active = true;
	private ArrayList<Point> hitPoints = new ArrayList<Point>();
	
	public Shelter(int parentWidth, int parentHeight, int x, int y){

		setImg();
		this.height = img.getHeight()/3;
		this.width = img.getWidth()/3;
		hitHeight = 10;
		hitWidth = 10;
		this.position = new Point(x,y);
		
	}
	
	public void setImg(){
		try {
			img = ImageIO.read(getClass().getResource("/shelter.png"));
			hitImg = ImageIO.read(getClass().getResource("/shelterhit.png"));
			System.out.println("**SHELTER-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ SHELTER**");
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics g) {
		//draws the shelter image in its current position
		g.drawImage(img, position.x, position.y, width, height, null);
	}
	
	public void hitDraw(Graphics g){
		for(int i=0;i<getNumberOfHits();i++){
			//For each of the number of hits
			g.drawImage(hitImg, getHitCoord(i).x, getHitCoord(i).y+10, hitWidth, hitHeight, null);
			//draw a black hitImg square that is equal to the hit coordinate but ten lower
			System.out.println("Hit drawn at position: "+getHitCoord(i));
			System.out.println(hitPoints.size());
		}
	}
	
	public Point getHitCoord(int i){
		return hitPoints.get(i);
	}
	
	public int getNumberOfHits(){
		return hitPoints.size();
	}
	
	public void hit(Point hitCoord){
		hitPoints.add(hitCoord);
	}
	
	public void destroy(){
		active = false;
	}

	public Rectangle getBounds() {
		return new Rectangle(position.x,position.y,width,height);
	}
	
}
