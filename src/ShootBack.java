import java.awt.Point;
import java.awt.Rectangle;


public interface ShootBack {

//	public void draw(Graphics g);
//	
	public void shoot();
	public int getEnemyBulletCount();
	public Bullet getBullet(int i);
	public Rectangle getBulletBounds(int i);
	public void stopBullets();
	public void startBullets();
	public void replaceBulletImage();
	Point getBulletPosition(int i);
}