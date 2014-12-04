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


@SuppressWarnings("serial")
public class Mothership extends Boss implements ShootBack, ActionListener{
	
	private ArrayList<Bullet> enemyBulletList = new ArrayList<Bullet>();
	private int width;
	private int height;
	Timer timer;
	Random initialDelay = new Random();

	public Mothership(int parentWidth, int parentHeight, int x, int y) {
		super(parentWidth, parentHeight, x, y);
		lives = 3;
		setImg();
		value = 100;
		this.height = img.getHeight()/2;
		this.width = img.getWidth()/2;
		delayRandomiser();
		enemyBulletList = new ArrayList<Bullet>();
	}
	
	@Override
	public void setImg(){
		try {
			img = ImageIO.read(getClass().getResource("/mothership.png"));
			System.out.println("**MOTHERSHIP-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ MOTHERSHIP**");
			e.printStackTrace();
		}
//		Iterator<Bullet> iterator = enemyBulletList.iterator();
//		while (iterator.hasNext()) {
//		    Bullet b = iterator.next();
//		    if(b!=null && b.isActive()){
//		    	b.setImg();
//		    }
//		}
	}
	
	@Override
	public void draw(Graphics g){
		
		g.drawImage(img, position.x, position.y, width, height, null);
		
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
//		    	shotRandomiser();
		    	b.move();
		    	b.draw(g);
		    }
		    else{
		    	iterator.remove();
		    }
		}
	}
	
	public void delayRandomiser(){
		int iDelay = initialDelay.nextInt(1000);
		timer = new Timer(2000, this);
		timer.setInitialDelay(iDelay);
		timer.start();
	}
	
	@Override
	public void dropPosition(){
		super.dropPosition();
		enemyspeed = enemyspeed+1;
	}
	
	@Override
	public void shoot() {
		enemyBulletList.add(new Bullet(new Point(position.x+(width/2), position.y),1,10,height));
	}

	@Override
	public int getEnemyBulletCount() {
		return enemyBulletList.size();
	}

	@Override
	public Bullet getBullet(int i) {
		return enemyBulletList.get(i);
	}

	@Override
	public Rectangle getBulletBounds(int i) {
		return enemyBulletList.get(i).getBounds();
	}

	@Override
	public void stopBullets() {
		timer.stop();
	}

	@Override
	public void startBullets() {
		timer.restart();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		shoot();
	}

	@Override
	public void replaceBulletImage() {
		Iterator<Bullet> replaceLoadedImg = enemyBulletList.iterator();
		while(replaceLoadedImg.hasNext()){
			Bullet b = replaceLoadedImg.next();
			if(b!=null && b.isActive()){
				b.setImg();
			}
		}
	}

	@Override
	public Point getBulletPosition(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
