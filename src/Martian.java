import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Martian extends Invader implements ShootBack, ActionListener{
	
	private ArrayList<Bullet> enemyBulletList = new ArrayList<Bullet>();
	private int width;
	private int height;
	Timer timer;
	Random random = new Random();
	
	
	public Martian(int parentWidth, int parentHeight, int x, int y){
		super(parentWidth, parentHeight, x, y);
		lives = 1;
		enemyBulletList = new ArrayList<Bullet>();
		setImg();
		value = 30;
		this.height = img.getHeight()/2;
		this.width = img.getWidth()/2;
		shotRandomiser();
		
	}
	
	@Override
	public void setImg(){
		try {
			img = ImageIO.read(getClass().getResource("/martian.png"));
			System.out.println("**MARTIAN-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ MARTIAN**");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void draw(Graphics g){
		super.draw(g);
		if(!GamePanel.paused&&timer.isRunning()){}
		else if(!GamePanel.paused&&!timer.isRunning()){
			timer.restart();
		}
		else if(GamePanel.paused){
			timer.stop();
		}
		
		Iterator<Bullet> iterator = enemyBulletList.iterator();
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
	
	public void shotRandomiser(){
		int freq = random.nextInt(4000)+1000;
//		int freq=500;
		timer = new Timer(freq, this);
//		Creates a random number somewhere between 1000 and 5000 (1 and 5 seconds)
		timer.start();
	}
	
	public void replaceBulletImage(){
		Iterator<Bullet> replaceLoadedImg = enemyBulletList.iterator();
		while(replaceLoadedImg.hasNext()){
			Bullet b = replaceLoadedImg.next();
			if(b!=null && b.isActive()){
				b.setImg();
			}
		}
	}
	
	@Override
	public void stopBullets(){
		timer.stop();
	}
	
	@Override
	public void startBullets(){
		timer.restart();
	}
	
	public int getEnemyBulletCount(){
		return enemyBulletList.size();
	}
	
	public Bullet getBullet(int i){
		return enemyBulletList.get(i);
	}
	
	public Rectangle getBulletBounds(int i){
		return enemyBulletList.get(i).getBounds();
	}
	
	@Override
	public Point getBulletPosition(int i){
		return enemyBulletList.get(i).getPosition();
	}
	
	public void actionPerformed(ActionEvent e){
		shoot();
	}
	
	@Override
	public void shoot(){
		enemyBulletList.add(new Bullet(new Point(position.x+(width/2), position.y),1,10,height));
	}
	
}
