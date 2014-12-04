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
public class Destroyer extends Boss implements ShootBack, Cloaking, ActionListener{
	
	private ArrayList<Bullet> enemyBulletList = new ArrayList<Bullet>();
	private int width;
	private int speed = 1;
	private boolean cloaked = false;
	Timer shotTimer;
	Timer cloakTimer;
	Timer frequencyRefresh;
	Random random = new Random();
	int temp = 0;

	public Destroyer(int parentWidth, int parentHeight, int x, int y) {
		super(parentWidth, parentHeight, x, y);
		lives = 2;
		setImg();
		value = 200;
		shotTimer = new Timer(random.nextInt(5000)+1000, this);
//		Creates a random timer somewhere between 1000 and 6000 (1 and 5 seconds)
		shotTimer.start();
		cloakTimer = new Timer(random.nextInt(4000)+1000, this);
//		Creates a random number somewhere between 1000 and 5000 (1 and 5 seconds)
		cloakTimer.start();
		enemyBulletList = new ArrayList<Bullet>();
	}
	
	@Override
	public void setImg(){
		//This is a separate method so it can be called after deserialisation
		try {
			img = ImageIO.read(getClass().getResource("/destroyer.png"));
			System.out.println("**DESTROYER-OK**");
		} catch (IOException e) {
			System.out.println("***************CAN'T READ FILE*******************");
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics g){
		if(!cloaked){
			//If it's not cloaked
			super.draw(g);
			//Draw the ship
		}
		//But if it is cloaked, always allow it to be:
		if(this.position.x<=0-this.width){
			this.destroy();
			//removed when it leaves the screen
		}
		//Paused...
		if(!GamePanel.paused&&shotTimer.isRunning()){}
		else if(!GamePanel.paused&&!shotTimer.isRunning()){
			shotTimer.restart();
		}
		else if(GamePanel.paused){
			shotTimer.stop();
		}
		//And always allow the bullets to be drawn
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
	
	@Override
	public void move(){
		if(!GamePanel.paused){
			this.position.x -= speed;
		}
	}

	@Override
	public void changeCloak() {
		cloaked = !cloaked;
	}

	@Override
	public boolean isCloaked() {
		return cloaked;
	}
	
	@Override
	public boolean edgeHit(){
		return false;
		//If edgeHit was the same, all the enemies would immediately drop off the screen because the ship
		//passes through the edge of the screen
	}
	@Override
	public void dropPosition(){}
	//Don't allow it to drop in position
	
	public int getEnemyBulletCount(){
		return enemyBulletList.size();
	}
	
	public Bullet getBullet(int i){
		return enemyBulletList.get(i);
	}
	
	public Rectangle getBulletBounds(int i){
		return enemyBulletList.get(i).getBounds();
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==shotTimer){
			shoot();
			shotTimer.setDelay(random.nextInt(5000)+1000);
			//When the shotTimer is fired, change the frequency for the next firing
		}
		if(e.getSource()==cloakTimer){
			changeCloak();
			cloakTimer.setDelay(random.nextInt(2500)+1000);
			//Same as above
		}
	}
	
	@Override
	public void shoot(){
		enemyBulletList.add(new Bullet(new Point(position.x, position.y),1,10,0));
	}

	@Override
	public void stopBullets() {
		shotTimer.stop();
	}

	@Override
	public void startBullets() {
		shotTimer.restart();
	}

	@Override
	public void stopCloakChange() {
		cloakTimer.stop();
	}

	@Override
	public void startCloakChange() {
		cloakTimer.restart();		
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

	//This will be used for shelter hit detection
	@Override
	public Point getBulletPosition(int i) {
		return null;
	}
	

}
